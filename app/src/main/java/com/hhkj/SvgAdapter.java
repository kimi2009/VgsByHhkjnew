package com.hhkj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhkj.vgsbyhhkjnew.OnRecyclerViewItemClickListener;
import com.hhkj.vgsbyhhkjnew.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj
 * @ClassName: SvgAdapter
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/11/23 16:28
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class SvgAdapter extends RecyclerView.Adapter<SvgAdapter.SvgHolder> implements View.OnClickListener {
    protected List<svgInfo> mData;

    public SvgAdapter(List<svgInfo> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public SvgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.svg_item, parent, false);
        // 实例化viewholder
        SvgHolder viewHolder = new SvgAdapter.SvgHolder(v);
        v.setOnClickListener(this);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull SvgHolder holder, int position) {
        svgInfo d = mData.get(position);
        holder.svg_name.setText(d.showName);

        //给holder的itemview添加Tag为position
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onClick(v, (int) v.getTag(), mData.get((int) v.getTag()));
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    class SvgHolder extends RecyclerView.ViewHolder {
        View r;
        public TextView svg_name;

        public SvgHolder(View root) {
            super(root);
            this.r = root;
            svg_name = (TextView) r.findViewById(R.id.svg_name);

        }
    }


}
