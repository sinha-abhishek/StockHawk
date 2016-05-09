package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON, List<String> errorList){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          try {
            ContentProviderOperation operation = buildBatchOperation(jsonObject,errorList);
            if (operation != null)
              batchOperations.add(buildBatchOperation(jsonObject, errorList));
          } catch (NumberFormatException n) {

          }
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              ContentProviderOperation operation = buildBatchOperation(jsonObject,errorList);
              if (operation != null)
                batchOperations.add(buildBatchOperation(jsonObject, errorList));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0, 1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject, List<String> errors){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.Quotes.CONTENT_URI);
    String symbol = "";
    ContentProviderOperation operation = null;
    try {
      symbol = jsonObject.getString("symbol");
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }
      operation = builder.build();
    } catch (JSONException e){
      e.printStackTrace();
    } catch (NumberFormatException e) {
      String s = "Failed to add symbol "+ symbol;
      errors.add(s);
      e.printStackTrace();
    } catch (Exception e) {
      String s = "Failed to add symbol "+ symbol;
      errors.add(s);
      e.printStackTrace();
    } finally {
      return operation;
    }

  }

  public static boolean isNetworkAvailable(Context context) {
    boolean isNetworkConnected = false;
    try {
      ConnectivityManager connManager = (ConnectivityManager) context.getSystemService
              (Context.CONNECTIVITY_SERVICE);
      if (connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo()
              .isAvailable() && connManager.getActiveNetworkInfo().isConnected()) {
        isNetworkConnected = true;
      }
    } catch (Exception ex) {
      isNetworkConnected = false;
    }
    return isNetworkConnected;
  }
}
