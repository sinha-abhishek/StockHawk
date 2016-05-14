package com.sam_chordas.android.stockhawk.helpers;

import com.sam_chordas.android.stockhawk.models.StockDailyDetailModel;
import com.sam_chordas.android.stockhawk.models.StocksModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by abhishek on 15/05/16.
 */
public class StockDetailHelper {
    public List<StockDailyDetailModel> weekly = new ArrayList<>();
    public List<StockDailyDetailModel> monthly = new ArrayList<>();
    public List<StockDailyDetailModel> yearly = new ArrayList<>();

    private Date toDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedCurrentDate = sdf.parse(dateString);
        return convertedCurrentDate;
    }

    public StockDetailHelper(List<StockDailyDetailModel> models) {
        Collections.sort(models, new Comparator<StockDailyDetailModel>() {
            @Override
            public int compare(StockDailyDetailModel lhs, StockDailyDetailModel rhs) {
                try {
                    return toDate(lhs.date).compareTo(toDate(rhs.date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        Date cur = new Date();
        for (StockDailyDetailModel model:
            models ) {
            try {
                Date d = toDate(model.date);
                long diff = cur.getTime() - d.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if (days <= 7) {
                    weekly.add(model);
                    monthly.add(model);
                    yearly.add(model);
                } else if (days <=30) {
                    monthly.add(model);
                    yearly.add(model);
                } else if (days <= 365){
                    yearly.add(model);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
