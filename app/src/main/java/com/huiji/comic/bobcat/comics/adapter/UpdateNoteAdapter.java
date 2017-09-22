package com.huiji.comic.bobcat.comics.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huiji.comic.bobcat.comics.R;
import com.huiji.comic.bobcat.comics.bean.UpdateNoteBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeYongchen on 2017/8/17.
 */

public class UpdateNoteAdapter extends RecyclerView.Adapter<UpdateNoteAdapter.RvViewHolder> {

    private Context mContext;
    private List<UpdateNoteBean> mDateList;

    public UpdateNoteAdapter(Context context, List<UpdateNoteBean> list) {
        mContext = context;
        mDateList = list;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RvViewHolder holder = new RvViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_update_note, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, int position) {
        holder.tvUpdateNoteVersion.setText(String.format("v%s", mDateList.get(position).getVersion()));
        holder.tvUpdateNoteMsg.setText(mDateList.get(position).getMsg());
    }

    @Override
    public int getItemCount() {
        return mDateList.size();
    }

    static class RvViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_update_note_version)
        TextView tvUpdateNoteVersion;
        @BindView(R.id.tv_update_note_msg)
        TextView tvUpdateNoteMsg;

        RvViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
