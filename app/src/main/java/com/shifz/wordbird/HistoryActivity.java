package com.shifz.wordbird;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shifz.wordbird.adapters.RecentAdapter;
import com.shifz.wordbird.adapters.interfaces.RecentCallback;
import com.shifz.wordbird.models.RecentRequest;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements RecentCallback {

    private static final int RQ_CODE_MAIN = 1;
    private RecentAdapter adapter;
    private List<RecentRequest> recentRequestList;
    private List<RecentRequest> tempRecentRequestList = new ArrayList<>();
    private DBHelper dbHelper;
    private int lastRemovedRecentRequestPosition;
    private RecentRequest lastRemovedRecentRequest;
    private View fabRemoveAll;
    private View tvNoHistoryFoundV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabRemoveAll = findViewById(R.id.fabRemoveAll);
        tvNoHistoryFoundV = findViewById(R.id.tvNoHistoryFound);

        //Checking if there's any history exists
        dbHelper = DBHelper.getInstance(this);
        recentRequestList = dbHelper.getRecentRequests();

        if (recentRequestList == null) {
            //No history exists
            //Hiding fab
            fabRemoveAll.setVisibility(View.GONE);
            tvNoHistoryFoundV.setVisibility(View.VISIBLE);

        } else {
            //has history

            //Setting fab
            fabRemoveAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, R.string.clear_history, Snackbar.LENGTH_LONG)
                            .setAction(R.string.confirm, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeAllHistory();
                                }
                            })
                            .show();
                }
            });

            final RecyclerView rvRecent = (RecyclerView) findViewById(R.id.rvRecent);
            adapter = new RecentAdapter(recentRequestList, this);
            rvRecent.setAdapter(adapter);
            rvRecent.setLayoutManager(new LinearLayoutManager(this));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeAllHistory() {
        tempRecentRequestList.addAll(recentRequestList);
        recentRequestList.clear();
        adapter.notifyDataSetChanged();
        tvNoHistoryFoundV.setVisibility(View.VISIBLE);
        Snackbar.make(fabRemoveAll, R.string.all_history_cleared, Snackbar.LENGTH_SHORT)
                .setAction(R.string.Undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recentRequestList.addAll(tempRecentRequestList);
                        tempRecentRequestList.clear();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (tempRecentRequestList.size() > 0) {
                            //undo time expired so clear in db also
                            dbHelper.setAllRequestIsDelete(true);
                            tempRecentRequestList.clear();
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                })
                .show();
    }


    @Override
    public void onDeleteRecent(int position) {

        final RecentRequest delRecRequest = recentRequestList.get(position);

        //Saving to undo
        lastRemovedRecentRequestPosition = position;
        lastRemovedRecentRequest = delRecRequest;

        recentRequestList.remove(position);
        adapter.notifyItemRemoved(position);

        Snackbar.make(fabRemoveAll, R.string.history_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.Undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        undoDeleteRecent();
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (lastRemovedRecentRequest != null) {
                            dbHelper.setRequestIsDeleted(delRecRequest.getId(), true);
                            lastRemovedRecentRequest = null;
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                })
                .show();

        if (recentRequestList.size() == 0) {
            tvNoHistoryFoundV.setVisibility(View.VISIBLE);
        }
    }

    private void undoDeleteRecent() {

        if (lastRemovedRecentRequest != null) {

            recentRequestList.add(lastRemovedRecentRequestPosition, lastRemovedRecentRequest);
            adapter.notifyItemInserted(lastRemovedRecentRequestPosition);

            //Hiding no history tv cuz we've result here.
            if (tvNoHistoryFoundV.getVisibility() == View.VISIBLE) {
                tvNoHistoryFoundV.setVisibility(View.GONE);
            }

            lastRemovedRecentRequest = null;

        }
    }

    @Override
    public void onItemClicked(int position) {
        final Intent mainIntent = new Intent(HistoryActivity.this, MainActivity.class);
        final Bundle args = new Bundle(position);
        args.putSerializable(Request.KEY, recentRequestList.get(position));
        mainIntent.putExtras(args);
        startActivityForResult(mainIntent, RQ_CODE_MAIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQ_CODE_MAIN && resultCode == RESULT_OK) {
            //New history available
            recentRequestList.clear(); //Clearing old
            recentRequestList.addAll(dbHelper.getRecentRequests());
            adapter.notifyDataSetChanged(); //notified
        }
    }
}
