package com.shifz.wordbird;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.shifz.wordbird.utils.App;
import com.shifz.wordbird.utils.CommonHelper;
import com.shifz.wordbird.utils.OkHttpHelper;
import com.shifz.wordbird.utils.PrefHelper;
import com.shifz.wordbird.ui.ProgressManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InActivity extends AppCompatActivity implements ProgressManager.Callback {

    private static final int DEFAULT_PROBLEM = 0;
    private static final String X = InActivity.class.getSimpleName();
    private static final String KEY_NAME = "name";
    private static final String KEY_IMEI = "imei";
    public static final String KEY_API_KEY = "api_key";
    private ProgressManager pm;
    private CommonHelper commonHelper;

    private final Request.Builder inRequestBuilder = new Request.Builder()
            .url(OkHttpHelper.BASE_URL + "/IN");
    private String ownerName;
    private String deviceImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in);

        //To manage the progress
        pm = new ProgressManager(
                (ViewGroup) getWindow().getDecorView().getRootView(),
                this
        );

        //Helper class to achieve common utility tasks
        commonHelper = new CommonHelper(this);

        //Getting owner name
        ownerName = commonHelper.getOwnerName();

        if (ownerName == null) {
            //Failed to find ownerName, so the email
            ownerName = commonHelper.getOwnerEmail();
            if (ownerName == null) {
                //Even the email can't be collected, so let it be Unknown
                ownerName = "Unknown";
            }
        }

        //Collecting device imei
        deviceImei = commonHelper.getDeviceImei();

        //finally adding collected values to request
        inRequestBuilder.post(new FormEncodingBuilder()
                        .add(KEY_NAME, ownerName)
                        .add(KEY_IMEI, deviceImei)
                        .build()
        );

        //We've data needed, so now start signInprocess
        startInProcess();
    }

    private void startInProcess() {

        Log.d(X, "Starting IN process...");

        //Checking if there's network
        if (commonHelper.isNetwork()) {

            pm.showProgress("Connecting as " + ownerName);

            //Adding parameter
            OkHttpHelper.getClient().newCall(inRequestBuilder.build()).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pm.showError(R.string.network_error_occurred, DEFAULT_PROBLEM,R.drawable.error_no_network);
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    final String resp = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject jResp = new JSONObject(resp);
                                final boolean hasError = jResp.getBoolean(OkHttpHelper.KEY_ERROR);
                                if(hasError){
                                    final String errorMessage = jResp.getString(OkHttpHelper.KEY_MESSAGE);
                                    pm.showError(errorMessage,DEFAULT_PROBLEM );
                                }else{
                                    final String apiKey = jResp.getString(KEY_API_KEY);
                                    PrefHelper.getInstance(InActivity.this).savePref(KEY_API_KEY,apiKey);
                                    App.apiKey = apiKey;
                                    startActivity(new Intent(InActivity.this,MenuActivity.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                pm.showError(R.string.server_error, DEFAULT_PROBLEM,R.drawable.error_default);
                            }
                        }
                    });
                }
            });


        } else {
            pm.showError(R.string.no_network_found, DEFAULT_PROBLEM, R.drawable.error_no_network);
        }
    }

    @Override
    public void onSolutionClicked(int problemType) {
        switch (problemType) {

            case DEFAULT_PROBLEM:
                startInProcess();
                break;

        }
    }
}
