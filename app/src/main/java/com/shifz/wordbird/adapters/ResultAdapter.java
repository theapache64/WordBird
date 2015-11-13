package com.shifz.wordbird.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shifz.wordbird.MenuActivity;
import com.shifz.wordbird.R;
import com.shifz.wordbird.adapters.interfaces.CallBack;
import com.shifz.wordbird.models.Menu;
import com.shifz.wordbird.models.result.Head;
import com.shifz.wordbird.models.result.Result;

import java.util.List;

/**
 * Created by Shifar Shifz on 10/24/2015 12:13 PM.
 */
public class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int VIEW_TYPE_HEAD = 0;
    private static final int VIEW_TYPE_BODY = 1;
    private final List<Result> resultList;
    private final CallBack callback;
    private final boolean isLoopable;

    public ResultAdapter(final List<Result> resultList, final boolean isLoopable, final CallBack callback) {
        this.resultList = resultList;
        this.callback = callback;
        this.isLoopable = isLoopable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_HEAD) {
            final View rootView = inflater.inflate(R.layout.head_row, parent, false);
            return new HeadViewHolder(rootView);
        } else {
            final View rootView = inflater.inflate(R.layout.body_row, parent, false);
            return new BodyViewHolder(rootView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Result result = resultList.get(position);

        if (holder instanceof HeadViewHolder) {
            ((HeadViewHolder) holder).tvHead.setText(result.string());
        } else {
            ((BodyViewHolder) holder).tvBody.setText(Html.fromHtml(result.string()));
        }

    }

    @Override
    public int getItemViewType(int position) {
        final Result result = resultList.get(position);
        if (result instanceof Head) {
            return VIEW_TYPE_HEAD;
        } else {
            return VIEW_TYPE_BODY;
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    protected class HeadViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvHead;

        public HeadViewHolder(View itemView) {
            super(itemView);
            this.tvHead = (TextView) itemView.findViewById(R.id.tvHead);
        }
    }

    protected class BodyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        protected TextView tvBody;

        public BodyViewHolder(View itemView) {
            super(itemView);
            this.tvBody = (TextView) itemView.findViewById(R.id.tvBody);

            if (isLoopable) {

                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callback.onItemClicked(getLayoutPosition());
                    }
                });

                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            for (int i = 0; i < MenuActivity.menuList.size(); i++) {
                final Menu maMenu = MenuActivity.menuList.get(i);

                if (i <= 8 && i!=4) {
                    menu.add(
                            getLayoutPosition(), i, getLayoutPosition(), String.format("%s of %s", maMenu.getCode(), resultList.get(getLayoutPosition()).string())
                    );
                } else {
                    menu.add(
                            getLayoutPosition(), i, getLayoutPosition(), String.format("%ss with %s", maMenu.getCode(), resultList.get(getLayoutPosition()).string())
                    );
                }

            }
        }
    }
}
