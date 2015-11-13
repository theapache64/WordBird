package com.shifz.wordbird.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shifz.wordbird.R;
import com.shifz.wordbird.adapters.interfaces.RecentCallback;
import com.shifz.wordbird.models.RecentRequest;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.utils.CommonHelper;

import java.util.List;

/**
 * Created by Shifar Shifz on 10/25/2015.
 */
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private final List<RecentRequest> requestList;
    private final RecentCallback callback;

    public RecentAdapter(List<RecentRequest> requestList, RecentCallback callback) {
        this.requestList = requestList;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View recentRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_row, parent, false);
        return new ViewHolder(recentRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RecentRequest curRequest = requestList.get(position);
        holder.tvWord.setText(CommonHelper.firstCharUp(curRequest.getWord()));
        holder.tvType.setText(curRequest.getType());
        holder.tvTime.setText(curRequest.getTimeSpan());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView tvWord, tvType, tvTime;

        public ViewHolder(View v) {
            super(v);

            tvWord = (TextView) v.findViewById(R.id.tvWord);
            tvType = (TextView) v.findViewById(R.id.tvType);
            tvTime = (TextView) v.findViewById(R.id.tvTime);
            v.findViewById(R.id.ibRemove).setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.ibRemove:
                    callback.onDeleteRecent(getLayoutPosition());
                    break;
                default:
                    callback.onItemClicked(getLayoutPosition());
            }
        }
    }
}
