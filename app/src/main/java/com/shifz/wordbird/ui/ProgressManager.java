package com.shifz.wordbird.ui;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shifz.wordbird.R;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class ProgressManager {

    public static final int PROBLEM_TYPE_NETWORK_ERROR = 9;
    private ViewGroup root;
    private View llProgressLayout;
    private View mainLayout;
    private TextView tvStatus;
    private View pbProgress;
    private Button bSolution;
    private Callback callback;

    public ProgressManager(final ViewGroup root,Callback callback) {
        this.root = root;
        this.callback = callback;
        setup();
    }

    public ProgressManager(final ViewGroup root,Callback callback,final int mainLayout){
        this.root = root;
        this.callback = callback;
        this.mainLayout = root.findViewById(mainLayout);
        setup();
    }

    private void setup() {
        this.root = (ViewGroup) LayoutInflater.from(this.root.getContext()).inflate(R.layout.progress_layout,this.root,true);
        this.llProgressLayout = this.root.findViewById(R.id.llProgressLayout);
        this.tvStatus = (TextView) this.llProgressLayout.findViewById(R.id.tvStatus);
        this.pbProgress = this.llProgressLayout.findViewById(R.id.pbProgress);
        this.bSolution = (Button) this.llProgressLayout.findViewById(R.id.bSolution);
        this.bSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSolutionClicked(Integer.parseInt(v.getTag().toString()));
            }
        });
    }

    public void showProgress(@StringRes final int message){
        if(mainLayout!=null){
            mainLayout.setVisibility(View.GONE); //Hiding main layout
        }

        this.llProgressLayout.setVisibility(View.VISIBLE);
        this.pbProgress.setVisibility(View.VISIBLE);
        this.tvStatus.setVisibility(View.VISIBLE);
        this.tvStatus.setText(message);
        this.bSolution.setVisibility(View.GONE);
        this.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void showProgress(final String message){
        if(mainLayout!=null){
            mainLayout.setVisibility(View.GONE); //Hiding main layout
        }

        this.llProgressLayout.setVisibility(View.VISIBLE);
        this.pbProgress.setVisibility(View.VISIBLE);
        this.tvStatus.setVisibility(View.VISIBLE);
        this.tvStatus.setText(message);
        this.bSolution.setVisibility(View.GONE);
        this.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void showError(@StringRes final int errorMessage,final int problemType,@DrawableRes final int errorDrawable){

        if(mainLayout!=null){
            mainLayout.setVisibility(View.GONE); //Hiding main layout
        }

        this.llProgressLayout.setVisibility(View.VISIBLE);
        this.pbProgress.setVisibility(View.GONE);
        this.tvStatus.setVisibility(View.VISIBLE);
        this.tvStatus.setText(errorMessage);
        this.bSolution.setVisibility(View.VISIBLE);
        this.bSolution.setText(R.string.try_again);
        this.bSolution.setTag(problemType);
        this.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0,errorDrawable,0,0);
    }

    public void showError(final String errorMessage,final int problemType){

        if(mainLayout!=null){
            mainLayout.setVisibility(View.GONE); //Hiding main layout
        }

        this.llProgressLayout.setVisibility(View.VISIBLE);
        this.pbProgress.setVisibility(View.GONE);
        this.tvStatus.setVisibility(View.VISIBLE);
        this.tvStatus.setText(errorMessage);
        this.bSolution.setVisibility(View.VISIBLE);
        this.bSolution.setText(R.string.try_again);
        this.bSolution.setTag(problemType);
        this.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.error_default, 0, 0);
    }

    public void showError(final String errorMessage,final int problemType,final String solutionText){

        if(mainLayout!=null){
            mainLayout.setVisibility(View.GONE); //Hiding main layout
        }

        this.llProgressLayout.setVisibility(View.VISIBLE);
        this.pbProgress.setVisibility(View.GONE);
        this.tvStatus.setVisibility(View.VISIBLE);
        this.tvStatus.setText(errorMessage);
        this.bSolution.setVisibility(View.VISIBLE);
        this.bSolution.setText(solutionText);
        this.bSolution.setTag(problemType);
        this.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.error_default, 0, 0);
    }

    public void showMainLayout(){
        if(mainLayout!=null){
            mainLayout.setVisibility(View.VISIBLE);
        }

        this.llProgressLayout.setVisibility(View.GONE);
    }

    public interface Callback{
        void onSolutionClicked(final int problemType);
    }

}
