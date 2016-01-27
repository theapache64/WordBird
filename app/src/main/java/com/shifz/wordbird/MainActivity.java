package com.shifz.wordbird;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.shifz.wordbird.fragments.WordBirdFragment;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.ui.DialogHelper;

public class MainActivity extends AppCompatActivity implements WordBirdFragment.WordBirdFragmentCallback, FragmentManager.OnBackStackChangedListener {


    private DialogHelper dialogHelper;
    private Request request;
    private ActionBar actionBar;
    private int requestCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        request = (Request) getIntent().getExtras().getSerializable(Request.KEY);

        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }


        dialogHelper = new DialogHelper(this);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        //finally showing the result
        onNewRequest(request);
    }

    private void showDialog() {

        dialogHelper.showInputDialog(R.string.try_another, R.string.try_another_word, new DialogHelper.DialogInputCallback() {
            @Override
            public void onInput(String input) {
                if (input.trim().isEmpty()) {
                    //Empty input
                    Snackbar.make(findViewById(android.R.id.content), "Invalid word!", Snackbar.LENGTH_LONG)
                            .setAction(R.string.try_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Explicitly calling
                                    showDialog();
                                }
                            })
                            .show();
                } else {
                    //Valid input
                    final Request newRequest = new Request(input, request.getType());
                    onNewRequest(newRequest);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewRequest(Request request) {

        requestCount++;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentContainer, WordBirdFragment.newInstance(request), WordBirdFragment.TAG)
                .addToBackStack(WordBirdFragment.TAG)
                .commit();

        if (requestCount == 2) {
            //Setting historyactivity flag
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    @Override
    public void onBackStackChanged() {

        final WordBirdFragment wbFragment = (WordBirdFragment) getSupportFragmentManager().findFragmentByTag(WordBirdFragment.TAG);

        if (wbFragment != null) {
            //Setting actionbar title as currently handling
            actionBar.setTitle(wbFragment.getRequest().getType());
            actionBar.setSubtitle(wbFragment.getRequest().getWord());
        }


    }


}