package com.shifz.wordbird.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shifz.wordbird.MenuActivity;
import com.shifz.wordbird.R;
import com.shifz.wordbird.adapters.interfaces.CallBack;
import com.shifz.wordbird.adapters.ResultAdapter;
import com.shifz.wordbird.models.Menu;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.models.result.Body;
import com.shifz.wordbird.models.result.Head;
import com.shifz.wordbird.models.result.Result;
import com.shifz.wordbird.utils.CommonHelper;
import com.shifz.wordbird.utils.DBHelper;
import com.shifz.wordbird.ui.DialogHelper;
import com.shifz.wordbird.utils.OkHttpHelper;
import com.shifz.wordbird.ui.ProgressManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordBirdFragment extends Fragment implements ProgressManager.Callback, CallBack {


    public static final String TAG = WordBirdFragment.class.getSimpleName();
    private static final int PROBLEM_TYPE_INVALID_WORD = 0;
    private static final String KEY_HEAD = "head";
    private static final String KEY_BODY = "body";
    private static final String KEY_RESULTS = "result";
    private ProgressManager pm;
    private List<Result> resultList;
    private ResultAdapter adapter;
    private Request request;
    private DialogHelper dialogHelper;
    private DBHelper dbHelper;
    private WordBirdFragmentCallback callback;

    public WordBirdFragment() {

    }

    public static WordBirdFragment newInstance(final Request request) {
        final WordBirdFragment wordBirdFragment = new WordBirdFragment();
        final Bundle args = new Bundle();
        args.putSerializable(Request.KEY, request);
        wordBirdFragment.setArguments(args);
        return wordBirdFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (WordBirdFragmentCallback) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View parent = inflater.inflate(R.layout.fragment_word_bird, container, false);

        request = (Request) getArguments().getSerializable(Request.KEY);

        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }

        final boolean isLoopable = Menu.getLoopableSet().contains(request.getType());

        pm = new ProgressManager(
                (ViewGroup) parent, this, R.id.rvResult
        );

        dialogHelper = new DialogHelper(getActivity());

        resultList = new ArrayList<>();

        dbHelper = DBHelper.getInstance(getActivity());

        adapter = new ResultAdapter(resultList, isLoopable, this);
        final RecyclerView rvResult = (RecyclerView) parent.findViewById(R.id.rvResult);
        rvResult.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvResult.setAdapter(adapter);

        registerForContextMenu(rvResult);

        downloadData();

        return parent;
    }

    //Checking if the result could be clickable or not
    private boolean getIsLoopable(String type) {
        switch (type) {

            case MenuActivity.Codes.CODE_MEANING:
                return false;

            default:
                return true;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final Menu clickedMenu = MenuActivity.menuList.get(item.getItemId());
        final int clickedItemPosition = item.getGroupId();
        Log.d(TAG, "Item position :" + clickedItemPosition);
        final String selectedWord = resultList.get(clickedItemPosition).string();
        Log.d(TAG, "Selected word : " + selectedWord);
        final Request newRequest = new Request(selectedWord, clickedMenu.getCode());
        callback.onNewRequest(newRequest);
        return true;

    }

    private void downloadData() {

        //Showing progress
        pm.showProgress(String.format("Finding %s for %s", request.getType(), request.getWord()));

        final Request dbRequest = dbHelper.getRequest(request);


        if (dbRequest != null) {

            Log.d(TAG, "Request already processed :)");

            final String historyResult = dbRequest.getResult();
            if (historyResult != null) {

                try {
                    resultList = parseResultList(new JSONArray(historyResult));
                    adapter.notifyDataSetChanged();
                    pm.showMainLayout();
                } catch (JSONException e) {
                    e.printStackTrace();
                    pm.showError("Failed to load from local", 1);
                }

            } else {

                //The word is invalid
                pm.showError("Invalid word", PROBLEM_TYPE_INVALID_WORD, "Try another");

            }

            return;
        }

        Log.d(TAG, "New request, so check the network");

        if (CommonHelper.isNetwork(getActivity())) {

            //Available network
            OkHttpHelper.getResult(request, new Callback() {
                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    final Activity act = getActivity();
                    if (act == null) {
                        Log.d(TAG, "Activity closed onFailure");
                        return;
                    }
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pm.showError(R.string.network_error_occurred, ProgressManager.PROBLEM_TYPE_NETWORK_ERROR, R.drawable.error_no_network);
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    final String resp = response.body().string();
                    Log.d(TAG, "Response : " + resp);
                    final Activity act = getActivity();
                    if (act == null) {
                        Log.d(TAG, "Activity closed onResponse");
                        return;
                    }
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject jResp = new JSONObject(resp);
                                final boolean hasError = jResp.getBoolean(OkHttpHelper.KEY_ERROR);
                                if (hasError) {
                                    final String message = jResp.getString(OkHttpHelper.KEY_MESSAGE);
                                    pm.showError(message, PROBLEM_TYPE_INVALID_WORD, getString(R.string.try_another));
                                    request.setResult(null);

                                } else {
                                    final JSONArray result = jResp.getJSONArray(KEY_RESULTS);
                                    request.setResult(result.toString());
                                    resultList = parseResultList(result);
                                    adapter.notifyDataSetChanged();
                                    pm.showMainLayout();
                                }

                                dbHelper.addRequest(request);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        } else {
            //Network not available
            pm.showError(R.string.no_network_found, ProgressManager.PROBLEM_TYPE_NETWORK_ERROR, R.drawable.error_no_network);
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "Cancelling request onDestroyView");
        OkHttpHelper.getClient().cancel(Request.KEY);
        super.onDestroyView();
    }

    //Used to convert jsonarray to List<Result>
    private List<Result> parseResultList(JSONArray jResult) {

        try {

            for (int i = 0; i < jResult.length(); i++) {
                final JSONObject jNode = jResult.getJSONObject(i);
                resultList.add(new Head(jNode.getString(KEY_HEAD)));
                final JSONArray jBody = jNode.getJSONArray(KEY_BODY);
                for (int j = 0; j < jBody.length(); j++) {
                    resultList.add(new Body(jBody.getString(j)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final int totalResultCount = resultList.size() - jResult.length();
        final String messageFormat = totalResultCount == 1 ? "%s result found" : "%s results found";

        Toast.makeText(getActivity(), String.format(messageFormat, totalResultCount), Toast.LENGTH_SHORT).show();

        return resultList;
    }


    @Override
    public void onSolutionClicked(final int problemType) {

        switch (problemType) {
            case PROBLEM_TYPE_INVALID_WORD:
                dialogHelper.showInputDialog(R.string.try_another, R.string.try_another_word, new DialogHelper.DialogInputCallback() {
                    @Override
                    public void onInput(String input) {
                        if (input.trim().isEmpty()) {
                            //Empty input
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Invalid word!", Snackbar.LENGTH_LONG)
                                    .setAction(R.string.try_again, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Explicitly calling
                                            onSolutionClicked(problemType);
                                        }
                                    })
                                    .show();
                        } else {
                            //Valid input
                            request.setWord(input);
                            downloadData();
                        }
                    }
                });
                break;

            case ProgressManager.PROBLEM_TYPE_NETWORK_ERROR:
                downloadData();
                break;
        }
    }

    @Override
    public void onItemClicked(int position) {

        final String clickedWord = resultList.get(position).string();
        final String type = request.getType();

        final Request newRequest = new Request(clickedWord, type);
        callback.onNewRequest(newRequest);

    }

    public interface WordBirdFragmentCallback {
        void onNewRequest(final Request request);
    }

    public Request getRequest() {
        return request;
    }
}
