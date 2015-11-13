package com.shifz.wordbird.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shifz.wordbird.R;
import com.shifz.wordbird.adapters.interfaces.CallBack;
import com.shifz.wordbird.models.Menu;

import java.util.List;

/**
 * Created by Shifar Shifz on 10/24/2015 10:08 AM.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {


    private final CallBack callback;
    private final List<Menu> menuList;

    public MenuAdapter(final List<Menu> menuList, final CallBack callback) {
        this.menuList = menuList;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View menuRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_row, parent, false);
        return new ViewHolder(menuRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Menu menu = menuList.get(position);
        holder.tvMenuTitle.setText(menu.getTitle());
        holder.tvMenuDescr.setText(menu.getDescr());

    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvMenuTitle, tvMenuDescr;

        public ViewHolder(View v) {
            super(v);
            this.tvMenuTitle = (TextView) v.findViewById(R.id.tvMenuTitle);
            this.tvMenuDescr = (TextView) v.findViewById(R.id.tvMenuDescr);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClicked(getLayoutPosition());
                }
            });
        }
    }

}
