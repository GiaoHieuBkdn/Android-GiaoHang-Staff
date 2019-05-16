package com.bys.sangngoc.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.bys.sangngoc.R;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.StringUtils;

/**
 * Created by Admin on 11/14/16.
 */

public class DeliveryPointsAdapter extends RecyclerView.Adapter<DeliveryPointsAdapter.ViewHolder> {
    private List<DeliveryPoint> mData;
    private Context mContext;
    private IOnDeliveryPointClicklistener mOnClickListener;
    private boolean mIsFromHistory;

    public DeliveryPointsAdapter(Context context, List<DeliveryPoint> data, boolean isFromHistory) {
        this.mData = data;
        this.mContext = context;
        this.mIsFromHistory = isFromHistory;
    }

    public void setItemListener(IOnDeliveryPointClicklistener listener) {
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delivery_point_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final DeliveryPoint item = mData.get(position);
        holder.tvPointNo.setText(StringUtils.getString(mContext, item.getDeliveryPointNo()));
        holder.tvCustomerName.setText(StringUtils.getString(mContext, item.getDeliveryAddressInfo() != null ? item.getDeliveryAddressInfo().getContactName() : ""));
        holder.tvDate.setText(StringUtils.getFullDate2StringFromTimestamp(item.getActualDeliveryTime() != 0 && mIsFromHistory ? item.getActualDeliveryTime() * 1000 : item.getExpectedDeliveryTime() * 1000));
        holder.tvAddress.setText(StringUtils.getString(mContext, item.getDeliveryAddressInfo() != null ? item.getDeliveryAddressInfo().getAddress() : ""));
        if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED)) {
            holder.tvNote.setText(StringUtils.getString(mContext, item.getFeedback()));
        } else {
            holder.tvNote.setText(StringUtils.getString(mContext, item.getNotes()));
        }
        if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_blue);
            holder.tvStatus.setText(mContext.getString(R.string.txt_status_progressing));
            holder.viewStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_status_blue));
            holder.tvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_blue));
        } else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_orange);
            holder.tvStatus.setText(mContext.getString(R.string.txt_status_dont_progressing));
            holder.viewStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_status_orange));
            holder.tvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_orange));
        } else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_dark);
            holder.tvStatus.setText(mContext.getString(R.string.txt_status_cancel));
            holder.viewStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_status_dark));
            holder.tvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_dark));
        } else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_red);
            holder.tvStatus.setText(mContext.getString(R.string.txt_status_trouble));
            holder.viewStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_status_red));
            holder.tvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_red));
        }  else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_FAILDED)) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_dark);
            holder.tvStatus.setText(mContext.getString(R.string.txt_status_failed));
            holder.viewStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_status_dark));
            holder.tvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_dark));
        }  else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
            if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED)) {
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_completed));
            } else  if (item.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT)) {
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_completed_need_accepted));
            } else {
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_completed_accepted));
            }
            holder.viewStatus.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_status_green));
            holder.tvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_green));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.onItemClick(position);
                }
            }
        });
        if (position % 2 == 0) {
            holder.viewGroup.setSelected(true);
        } else {
            holder.viewGroup.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPointNo;
        private TextView tvCustomerName;
        private TextView tvDate;
        private TextView tvAddress;
        private TextView tvNote;
        private TextView tvStatus;
        private View viewStatus;
        private View viewGroup;

        public ViewHolder(View view) {
            super(view);
            tvPointNo = (TextView) view.findViewById(R.id.tv_point_no);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvCustomerName = (TextView) view.findViewById(R.id.tv_customer_name);
            tvAddress = (TextView) view.findViewById(R.id.tv_address);
            tvNote = (TextView) view.findViewById(R.id.tv_note);
            tvStatus = (TextView) view.findViewById(R.id.tv_status);
            tvStatus.setSelected(true);
            viewStatus = view.findViewById(R.id.view_status);
            viewGroup = view.findViewById(R.id.view_group);
        }
    }


    public interface IOnDeliveryPointClicklistener {
        void onItemClick(int position);
    }
}
