package com.bys.sangngoc.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.bys.sangngoc.R;
import com.bys.sangngoc.models.DeliveryItem;
import com.bys.sangngoc.models.DeliveryItems;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.InforItem;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.FontUtils;
import com.bys.sangngoc.utils.StringUtils;
import com.bys.sangngoc.views.CustomDialogListProductConfirm;
import com.bys.sangngoc.views.CustomDialogListRealProductConfirm;

/**
 * Created by Admin on 11/14/16.
 */

public class DetailDriveDeliveryAdapter extends RecyclerView.Adapter<DetailDriveDeliveryAdapter.ViewHolder> {
    private List<InforItem> mData;
    private Context mContext;
    private IOnInforItemClicklistener mOnClickListener;
    private DeliveryPoint mDeliveryPoint;

    public DetailDriveDeliveryAdapter(Context context, List<InforItem> data) {
        this.mData = data;
        this.mContext = context;
    }

    public void setDeliveryPoint(DeliveryPoint deliveryPoint) {
        mDeliveryPoint = deliveryPoint;
    }

    public void setItemListener(IOnInforItemClicklistener listener) {
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_driver_delivery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final InforItem item = mData.get(position);
        holder.tvName.setText(item.getName());
        holder.tvValues.setText(item.getValue());
        holder.tvValues.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        holder.tvValues.setSingleLine(false);
        if (position == 0) {
            if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_blue_top);
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_progressing));
                holder.tvValues.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_blue));
            } else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_orange_top);
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_dont_progressing));
                holder.tvValues.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_orange));
            } else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_dark_top);
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_cancel));
                holder.tvValues.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_dark));
            } else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_red_top);
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_trouble));
                holder.tvValues.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_red));
            } else if (item.getStatus().equalsIgnoreCase(Constants.STATUS_FAILDED)) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_dark_top);
                holder.tvStatus.setText(mContext.getString(R.string.txt_status_failed));
                holder.tvValues.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_dark));
            } else {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green_top);
                if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED)) {
                    holder.tvStatus.setText(mContext.getString(R.string.txt_status_completed));
                } else  if (item.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT)) {
                    holder.tvStatus.setText(mContext.getString(R.string.txt_status_completed_need_accepted));
                } else {
                    holder.tvStatus.setText(mContext.getString(R.string.txt_status_completed_accepted));
                }
                holder.tvValues.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_green));
            }
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.viewAction.setVisibility(View.GONE);
            holder.tvValues.setTypeface(FontUtils.getTypeface(mContext, "fonts/sf_pro_display_medium.otf"));
        } else {
            holder.tvValues.setTypeface(FontUtils.getTypeface(mContext, "fonts/sf_pro_display_light.otf"));
            holder.tvValues.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_grey));
            holder.tvStatus.setVisibility(View.GONE);
            if (item.getAction() == InforItem.Action.CALL) {
//                String value = holder.tvValues.getText().toString();
//                int length = value.length();
//                for (int i = length; i <= 16; i++){
//                    value += "&#160;";
//                }
//                holder.tvValues.setText(value);
                holder.tvValues.getLayoutParams().width = mContext.getResources().getInteger(R.integer.width_call_list);
                holder.tvValues.setSingleLine(true);
                holder.tvValues.setEllipsize(TextUtils.TruncateAt.END);

                holder.tvAction.setText(mContext.getText(R.string.txt_call));
                holder.imvAction.setImageResource(R.drawable.ic_call_white);
                if (item.getValue().equalsIgnoreCase(mContext.getString(R.string.txt_no_text))) {
                    holder.viewAction.setVisibility(View.GONE);
                } else {
                    holder.viewAction.setVisibility(View.VISIBLE);
                }
            } else if (item.getAction() == InforItem.Action.LIST) {
//                String value = holder.tvValues.getText().toString();
//                int length = value.length();
//                for (int i = length; i <= 16; i++){
//                    value += "&#160;";
//                }
//                holder.tvValues.setText(value);
                holder.tvValues.getLayoutParams().width = mContext.getResources().getInteger(R.integer.width_call_list);
                ;
                holder.tvValues.setSingleLine(true);
                holder.tvValues.setEllipsize(TextUtils.TruncateAt.END);

                holder.tvAction.setText(mContext.getText(R.string.txt_view_list));
                holder.imvAction.setImageResource(R.drawable.ic_list_white);
                if (item.getValue().equalsIgnoreCase(mContext.getString(R.string.txt_no_text))) {
                    holder.viewAction.setVisibility(View.GONE);
                } else {
                    holder.viewAction.setVisibility(View.VISIBLE);
                }
            } else {
                holder.viewAction.setVisibility(View.GONE);
            }
            holder.viewAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getAction() == InforItem.Action.CALL) {
                        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                            Toast.makeText(mContext, mContext.getString(R.string.txt_device_not_support_call), Toast.LENGTH_SHORT).show();
                        } else {
                            if (mDeliveryPoint.getDeliveryAddressInfo() != null && mDeliveryPoint.getDeliveryAddressInfo().getContactPhone() != null && mDeliveryPoint.getDeliveryAddressInfo().getContactPhone().length() > 0) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + mDeliveryPoint.getDeliveryAddressInfo().getContactPhone()));
                                mContext.startActivity(intent);
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.txt_not_have_phone), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (item.getAction() == InforItem.Action.LIST) {
                        if (mDeliveryPoint.getDeliveryItems() != null && mDeliveryPoint.getDeliveryItems().size() > 0) {
                            if (mData.get(0).getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED)
                                    || mData.get(0).getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_ACCEPTED)
                                    || mData.get(0).getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT)) {
                                ArrayList<DeliveryItem> products = new ArrayList<>();
                                for (DeliveryItem item : mDeliveryPoint.getDeliveryItems()) {
                                    products.add(item);
                                }
                                CustomDialogListRealProductConfirm dialog = new CustomDialogListRealProductConfirm(mContext, true);
                                dialog.show();
                                dialog.setTitle(mContext.getString(R.string.txt_product_list));
                                dialog.loadData(products);
                            } else {
                                ArrayList<String> products = new ArrayList<>();
                                for (DeliveryItem item : mDeliveryPoint.getDeliveryItems()) {
                                    String tmp = "";
                                    Double numTmp = item.getProductQtyOfTypes().get(0).getProductDeliveryQty()
                                            + item.getProductQtyOfTypes().get(1).getProductDeliveryQty()
                                            +item.getProductQtyOfTypes().get(2).getProductDeliveryQty();
                                    if (numTmp % 1 == 0) {
                                        tmp = StringUtils.formatPriceByDouble(numTmp) + " " + item.getMeasureUnit().getValuee() + " " + item.getProductDesc();
                                    } else {
                                        tmp = StringUtils.formatPrice3DecimalPlaces(numTmp) + " " + item.getMeasureUnit().getValuee() + " " + item.getProductDesc();
                                    }
                                    products.add(tmp);
                                }
                                CustomDialogListProductConfirm dialog = new CustomDialogListProductConfirm(mContext);
                                dialog.show();
                                dialog.setTitle(mContext.getString(R.string.txt_product_list_processing));
                                dialog.loadData(products);
                            }
                        }

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvValues;
        private View viewAction;
        private TextView tvAction;
        private ImageView imvAction;
        private TextView tvStatus;


        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvValues = (TextView) view.findViewById(R.id.tv_values);
            tvValues.setSelected(true);
            viewAction = view.findViewById(R.id.view_action);
            tvAction = (TextView) view.findViewById(R.id.tv_action);
            imvAction = (ImageView) view.findViewById(R.id.imv_action);
            tvStatus = (TextView) view.findViewById(R.id.tv_status);
            tvStatus.setSelected(true);
        }
    }


    public interface IOnInforItemClicklistener {
        void onItemClick(int position);
    }
}
