package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.mvp.ModelCollectionManager;
import com.sam_chordas.android.stockhawk.mvp.StocksModelCollection;
import com.sam_chordas.android.stockhawk.rest.CustomRecyclerView;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 06/05/16.
 */
public class MainFragment extends Fragment {
    public static String SELECTED = "selected";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    com.melnykov.fab.FloatingActionButton floatingActionButton;

    @BindView(R.id.emptyView)
    TextView emptyView;


    private StocksModelCollection stocksModelCollection;
    private QuoteCursorAdapter mCursorAdapter;
    private Context context;
    private CharSequence mTitle;
    private Intent mServiceIntent;
    private ItemTouchHelper mItemTouchHelper;
    private FragmentCallback fragmentCallback;
    private int mActivePosition = 0;
    private LinearLayoutManager layoutManager;


    public MainFragment() {
        //recyclerView.getLayoutManager().
    }

    public interface FragmentCallback {
        void showSpinner();
        void hideSpinner();
        void restoreActionBar();
        void showNoContent();
        void onItemSelected(String symbol);
        void onStocksLoaded(RecyclerView view, int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        fragmentCallback = (FragmentCallback)getActivity();
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(context,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        //TODO:
                        // do something on item click
                        Intent i = new Intent(context, DetailActivity.class);
                        String symbol = mCursorAdapter.getItemAtPosition(position);
                        //i.putExtra(SELECTED, symbol);
                        //startActivity(i);
                        fragmentCallback.onItemSelected(symbol);
                    }
                }));
        //recyclerView.
        mCursorAdapter = new QuoteCursorAdapter(context, null);
        recyclerView.setAdapter(mCursorAdapter);
        floatingActionButton.attachToRecyclerView(recyclerView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(context)) {
                    new MaterialDialog.Builder(context).title(R.string.symbol_search)
                            .content(R.string.content_test)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    stocksModelCollection.handleAdd(input.toString());
                                }
                            })
                            .show();
                } else {
                    networkToast();
                }

            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        mTitle = getActivity().getTitle();
        stocksModelCollection.BindView(this);
//        if(savedInstanceState !=null) {
//            mActivePosition = savedInstanceState.getInt("pos");
//            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("myState"));
//            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(mActivePosition);
//        }
        return view;
    }

//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        if (savedInstanceState == null) {
            stocksModelCollection = new StocksModelCollection(getActivity(),getActivity().getLoaderManager());
        } else {
            stocksModelCollection =  ModelCollectionManager.getInstance().restoreModels(savedInstanceState);
            if(stocksModelCollection == null) {
                stocksModelCollection = new StocksModelCollection(getActivity(),getActivity().getLoaderManager());
            }
        }
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_stocks, menu);

        ((FragmentCallback)getActivity()).restoreActionBar();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void UpdateAdapter(Cursor cursor) {
        fragmentCallback.hideSpinner();
        mCursorAdapter.swapCursor(cursor);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        ((FragmentCallback)getActivity()).onStocksLoaded(recyclerView, mActivePosition);
    }

    public void showError(String message) {
        //fragmentCallback.showNoContent();
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText(message);
        fragmentCallback.hideSpinner();
    }

    public void showErrorToast(String message) {
        Toast toast =
                Toast.makeText(context, message,
                        Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
        toast.show();
    }

    public void ShowSaved() {
        Toast toast =
                Toast.makeText(context, "This stock is already saved!",
                        Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        stocksModelCollection.onResume();
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_change_units){
            // this is for changing stock changes from percent value to dollar value
            Utils.showPercent = !Utils.showPercent;
            stocksModelCollection.onOptionsChange();
            //this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        ModelCollectionManager.getInstance().saveModels(stocksModelCollection, outState);
        outState.putParcelable("myState", layoutManager.onSaveInstanceState());
//        mActivePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//        outState.putInt("pos",mActivePosition);
        //layoutManager.computeVerticalScrollOffset(recyclerView.);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    public void ShowUpdating() {
        fragmentCallback.showSpinner();
    }

    public void networkToast(){
        fragmentCallback.hideSpinner();
        Toast.makeText(getActivity(), getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
    }
}
