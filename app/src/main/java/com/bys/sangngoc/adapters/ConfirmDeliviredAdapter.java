package com.bys.sangngoc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bys.sangngoc.R;
import com.bys.sangngoc.models.DeliveryItem;
import com.bys.sangngoc.models.KeyValue;
import com.bys.sangngoc.models.ProductQtyOfTypes;
import com.bys.sangngoc.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bys.sangngoc.R.drawable;
import static com.bys.sangngoc.R.id;

public class ConfirmDeliviredAdapter extends RecyclerView.Adapter<ConfirmDeliviredAdapter.RecyclerViewHolder> {

    private Context context;
    private List<DeliveryItem> items = new ArrayList<>();
    private IOnItemClickedListener mIOnItemClickedListener;
    private double total;
    private boolean checkGood = true;
    private boolean checkBad = true;
    private boolean checkError = true;
    private boolean check = false;
    private ArrayList<String> mValuesData = new ArrayList<>();
    private ArrayList<KeyValue> mKeyValues = new ArrayList<>();

    public ConfirmDeliviredAdapter(Context context, List<DeliveryItem> DeliveryItem) {
        this.context = context;
        this.items = DeliveryItem;
    }

    public ConfirmDeliviredAdapter(Context context, List<DeliveryItem> DeliveryItem, ArrayList<String> mValuesData, ArrayList<KeyValue> mKeyValues) {
        this.context = context;
        this.items = DeliveryItem;
        this.mValuesData = mValuesData;
        this.mKeyValues = mKeyValues;
    }

    public ConfirmDeliviredAdapter(List<DeliveryItem> DeliveryItem) {
        this.items = DeliveryItem;
    }

    public void setOnItemClickListener(IOnItemClickedListener listener) {
        mIOnItemClickedListener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.confirm_delivired_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        DeliveryItem item = items.get(position);
        holder.tvNote.setVisibility(View.GONE);
        holder.tvAmount.setText(item.getQuantityTmp() + " " + item.getMeasureUnit().getValuee());
        holder.tvRealAmount.setText(item.getDeliveredActualQty() + " " + item.getMeasureUnit().getValuee());
        holder.idDelivered.setText(item.getProductNo());
        holder.tvName.setText(item.getProductDesc());
        holder.imgLineDotted.setVisibility(View.VISIBLE);
        holder.layoutNote.setVisibility(View.VISIBLE);
        if (item.getBarrelQty() == 0) {
            holder.tvBarrel.setVisibility(View.GONE);
        } else {
            holder.tvBarrel.setText("(" + item.getBarrelQty() + " Thùng)");
        }

        holder.tvGoodMeasureUnit.setText(item.getMeasureUnit().getValuee());
        holder.tvBadMeasureUnit.setText(item.getMeasureUnit().getValuee());
        holder.tvErrorMeasureUnit.setText(item.getMeasureUnit().getValuee());

        holder.etGoodType.setText(item.getDeliveredGoodQty() + "");
        holder.etBadType.setText(item.getDeliveredBadQty() + "");
        holder.etErrorType.setText(item.getDeliveredErrorQty() + "");
        holder.tvQuantity.setText(item.getDeliveredActualQty() + "");
        holder.tvQuantityMeasureunit.setText(item.getMeasureUnit().getValuee());

        if (!item.isStatus()) {
            holder.imgStatus.setBackgroundResource(R.drawable.bg_status_no_confirm);
            holder.img.setImageResource(drawable.ic_no_confirm);
            holder.layoutReason.setVisibility(View.GONE);
            item.setChanged(false);
            item.setPositionReason(0);
            holder.imgEdit.setVisibility(View.GONE);
            holder.layoutEditQuantity.setVisibility(View.GONE);
        } else {
            holder.imgStatus.setBackgroundResource(R.drawable.bg_status_confirm);
            holder.img.setImageResource(drawable.ic_confirm);
            holder.tvRealAmount.setKeyListener(new AppCompatEditText(context).getKeyListener());
            if (item.isEdited()) {
                holder.imgEdit.setVisibility(View.GONE);
                holder.layoutEditQuantity.setVisibility(View.VISIBLE);
                if (item.getDeliveredGoodQty() != 0.0 && item.getDeliveredBadQty() == 0.0 && item.getDeliveredErrorQty() == 0.0)
                    item.setType("good");
                if (item.getDeliveredGoodQty() == 0.0 && item.getDeliveredBadQty() != 0.0 && item.getDeliveredErrorQty() == 0.0)
                    item.setType("bad");
                if (item.getDeliveredGoodQty() == 0.0 && item.getDeliveredBadQty() == 0.0 && item.getDeliveredErrorQty() != 0.0)
                    item.setType("error");
            } else {
                holder.imgEdit.setVisibility(View.VISIBLE);
                holder.layoutEditQuantity.setVisibility(View.GONE);
            }
        }
        if (item.isChanged()) {
            holder.layoutReason.setVisibility(View.VISIBLE);

        } else {
            holder.layoutReason.setVisibility(View.GONE);
        }

        holder.etGoodType.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        holder.etBadType.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        holder.etErrorType.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        holder.spReason.setAdapter(new SpinnerAdapter(context, mValuesData));
        holder.spReason.setSelection(item.getPositionReason());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView idDelivered, tvAmount, tvName, tvNote, tvRealAmount, tvQuantity;
        TextView tvGoodMeasureUnit, tvBadMeasureUnit, tvErrorMeasureUnit, tvQuantityMeasureunit;
        EditText etGoodType, etBadType, etErrorType;
        EditText edtNote;
        LinearLayout imgStatus, layoutNote, imgEdit, layoutEditQuantity, layoutApprovedQuantity;
        LinearLayout layoutGood, layoutBad, layoutError, layoutReason;
        ImageView img, imgLineDotted, imgApprovedQuantity;
        TextView tvPriceGood, tvPriceBad, tvPriceError;
        TextView tvBarrel;
        Spinner spReason;

        public double getTotalItem() {
            double good = Double.parseDouble(StringUtils.isEmptyDot(etGoodType.getText().toString()) ? "0" : etGoodType.getText().toString());
            double bad = Double.parseDouble(StringUtils.isEmptyDot(etBadType.getText().toString()) ? "0" : etBadType.getText().toString());
            double error = Double.parseDouble(StringUtils.isEmptyDot(etErrorType.getText().toString()) ? "0" : etErrorType.getText().toString());
            return Double.parseDouble(StringUtils.formatPrice3DecimalPlaces(good + bad + error));
        }

        public RecyclerViewHolder(final View itemView) {
            super(itemView);
            idDelivered = itemView.findViewById(R.id.id_delivered);
            tvAmount = itemView.findViewById(R.id.amount);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRealAmount = itemView.findViewById(R.id.realAmount);
            edtNote = itemView.findViewById(R.id.edt_note);
            imgStatus = itemView.findViewById(R.id.img_confirm);
            imgEdit = itemView.findViewById(id.img_edit);
            layoutNote = itemView.findViewById(R.id.layout_note);
            img = itemView.findViewById(R.id.img);
            imgLineDotted = itemView.findViewById(R.id.line_dotted);
            tvNote = itemView.findViewById(R.id.tvNote);
            imgApprovedQuantity = itemView.findViewById(R.id.img_approved_quantity);
            layoutEditQuantity = itemView.findViewById(R.id.layout_edit_quantity);
            layoutApprovedQuantity = itemView.findViewById(R.id.layout_approved_quantity);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            etGoodType = itemView.findViewById(R.id.et_good_type);
            etBadType = itemView.findViewById(R.id.et_bad_type);
            etErrorType = itemView.findViewById(R.id.et_error_type);
            tvGoodMeasureUnit = itemView.findViewById(R.id.tv_good_measureunit);
            tvBadMeasureUnit = itemView.findViewById(R.id.tv_bad_measureunit);
            tvErrorMeasureUnit = itemView.findViewById(R.id.tv_error_measureunit);
            layoutGood = itemView.findViewById(id.layout_good_type);
            layoutBad = itemView.findViewById(id.layout_bad_type);
            layoutError = itemView.findViewById(id.layout_error_type);
            tvQuantityMeasureunit = itemView.findViewById(R.id.tv_quantity_measureunit);
            tvPriceGood = itemView.findViewById(R.id.tv_price_good);
            tvPriceBad = itemView.findViewById(id.tv_price_bad);
            tvPriceError = itemView.findViewById(id.tv_price_error);
            tvBarrel = itemView.findViewById(id.tv_barrel);
            spReason = itemView.findViewById(id.sp_reason);
            layoutReason = itemView.findViewById(R.id.layout_reason);

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.get(getLayoutPosition()).setEdited(true);
                    notifyDataSetChanged();
                }
            });

            imgStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (items.get(getLayoutPosition()).isStatus()) {
                        items.get(getLayoutPosition()).setStatus(false);
                        items.get(getLayoutPosition()).setEdited(false);
                        items.get(getLayoutPosition()).setDeliveredActualQty(0);
                        items.get(getLayoutPosition()).setDeliveredGoodQty(0);
                        items.get(getLayoutPosition()).setDeliveredBadQty(0);
                        items.get(getLayoutPosition()).setDeliveredErrorQty(0);
                        notifyDataSetChanged();
                    } else {
                        items.get(getLayoutPosition()).setStatus(true);
                        if (items.get(getLayoutPosition()).getProductQtyOfTypes() != null) {
                            for (ProductQtyOfTypes productQtyOfTypes : items.get(getLayoutPosition()).getProductQtyOfTypes()) {
                                if (productQtyOfTypes.getProductDeliveredQualityNo().equalsIgnoreCase("Good")) {
                                    items.get(getLayoutPosition()).setDeliveredGoodQty(productQtyOfTypes.getProductDeliveryQty());
                                }
                                if (productQtyOfTypes.getProductDeliveredQualityNo().equalsIgnoreCase("Ugly")) {
                                    items.get(getLayoutPosition()).setDeliveredBadQty(productQtyOfTypes.getProductDeliveryQty());
                                }
                                if (productQtyOfTypes.getProductDeliveredQualityNo().equalsIgnoreCase("Error")) {
                                    items.get(getLayoutPosition()).setDeliveredErrorQty(productQtyOfTypes.getProductDeliveryQty());
                                }
                            }
                        }
                        items.get(getLayoutPosition()).setDeliveredActualQty(items.get(getLayoutPosition()).getQuantityTmp());
                        notifyDataSetChanged();
                    }
                    if (mIOnItemClickedListener != null) {
                        mIOnItemClickedListener.canCheckedData();
                    }
                }
            });


            layoutApprovedQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etGoodType.getText().toString().trim().equalsIgnoreCase(".") || etBadType.getText().toString().trim().equalsIgnoreCase(".") || etErrorType.getText().toString().trim().equalsIgnoreCase(".")) {
                        Toast.makeText(context, context.getString(R.string.txt_data_invalid), Toast.LENGTH_LONG).show();
                        return;
                    }
                    total = getTotalItem();
//                    if (total > items.get(getLayoutPosition()).getQuantity()) {
//                        Toast.makeText(context, context.getString(R.string.txt_total_all_type_incorrect), Toast.LENGTH_LONG).show();
//                        return;
//                    } else {
                    items.get(getLayoutPosition()).setEdited(false);
                    //Save data of types
                    items.get(getLayoutPosition()).setDeliveredGoodQty(Double.parseDouble(StringUtils.isEmptyDot(etGoodType.getText().toString()) ? "0" : etGoodType.getText().toString()));
                    items.get(getLayoutPosition()).setDeliveredBadQty(Double.parseDouble(StringUtils.isEmptyDot(etBadType.getText().toString()) ? "0" : etBadType.getText().toString()));
                    items.get(getLayoutPosition()).setDeliveredErrorQty(Double.parseDouble(StringUtils.isEmptyDot(etErrorType.getText().toString()) ? "0" : etErrorType.getText().toString()));
                    items.get(getLayoutPosition()).setDeliveredActualQty(total);
                    items.get(getLayoutPosition()).setType("");
                    if ((items.get(getLayoutPosition()).getDeliveredGoodQty() !=  items.get(getLayoutPosition()).getDeliveredGoodQtyTmp())
                            || (items.get(getLayoutPosition()).getDeliveredBadQty() !=  items.get(getLayoutPosition()).getDeliveredBadQtyTmp())
                            || (items.get(getLayoutPosition()).getDeliveredErrorQty() !=  items.get(getLayoutPosition()).getDeliveredErrorQtyTmp())) {
                        items.get(getLayoutPosition()).setChanged(true);
                    } else {
                        items.get(getLayoutPosition()).setChanged(false);
                    }
                    checkBad = true;
                    checkError = true;
                    checkGood = true;
                    check = true;
                    notifyDataSetChanged();
                    hideKeyBoard();
                    if (mIOnItemClickedListener != null) {
                        mIOnItemClickedListener.canCheckedData();
                    }
//                    }
                }
            });


            etGoodType.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    return;
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    double good = Double.parseDouble(StringUtils.isEmptyDot(etGoodType.getText().toString()) ? "0" : etGoodType.getText().toString());
                    double bad = Double.parseDouble(StringUtils.isEmptyDot(etBadType.getText().toString()) ? "0" : etBadType.getText().toString());
                    double error = Double.parseDouble(StringUtils.isEmptyDot(etErrorType.getText().toString()) ? "0" : etErrorType.getText().toString());
                    if (items.get(getLayoutPosition()).getType() == "bad") {
                        double tmpBad = items.get(getLayoutPosition()).getDeliveredActualQty() - good - error;
                        if (bad <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                            if (tmpBad > 0) {
                                etBadType.setText("" + StringUtils.formatPrice3DecimalPlaces(tmpBad));
                            } else if (tmpBad < 0) {
                                etBadType.setText("0.0");
                                tvPriceBad.setTextColor(Color.WHITE);
                            }
                        }
                    }
                    if (items.get(getLayoutPosition()).getType() == "error") {
                        double tmpError = items.get(getLayoutPosition()).getDeliveredActualQty() - good - bad;
                        if (error <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                            if (tmpError > 0) {
                                etErrorType.setText("" + StringUtils.formatPrice3DecimalPlaces(tmpError));
                            } else if (tmpError < 0) {
                                etErrorType.setText("0.0");
                                tvPriceError.setTextColor(Color.WHITE);
                            }
                        }
                    }
                    tvPriceGood.setText(StringUtils.formatPriceByDouble(good * items.get(getLayoutPosition()).getProductQtyOfTypes().get(0).getProductDeliveredUnitPrice()));
                    tvQuantity.setText(getTotalItem() + "");
                    if (good == items.get(getLayoutPosition()).getDeliveredGoodQtyTmp()) {
                        tvPriceGood.setTextColor(Color.WHITE);
                    } else {
                        tvPriceGood.setTextColor(ContextCompat.getColor(context, R.color.color_grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }

            });

            etBadType.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    double good = Double.parseDouble(StringUtils.isEmptyDot(etGoodType.getText().toString()) ? "0" : etGoodType.getText().toString());
                    double bad = Double.parseDouble(StringUtils.isEmptyDot(etBadType.getText().toString()) ? "0" : etBadType.getText().toString());
                    double error = Double.parseDouble(StringUtils.isEmptyDot(etErrorType.getText().toString()) ? "0" : etErrorType.getText().toString());
                    if (items.get(getLayoutPosition()).getType() == "error") {
                        if (error <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                            double tmpError = items.get(getLayoutPosition()).getDeliveredActualQty() - good - bad;
                            if (tmpError > 0) {
                                etErrorType.setText("" + StringUtils.formatPrice3DecimalPlaces(tmpError));
                            } else if (tmpError < 0 && error <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                                etErrorType.setText("0.0");
                                tvPriceError.setTextColor(Color.WHITE);
                            }
                        }
                    }
                    if (items.get(getLayoutPosition()).getType() == "good") {
                        double tmpGood = items.get(getLayoutPosition()).getDeliveredActualQty() - error - bad;
                        if (good <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                            if (tmpGood > 0) {
                                etGoodType.setText("" + StringUtils.formatPrice3DecimalPlaces(tmpGood));
                            } else if (tmpGood < 0 && good <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                                etGoodType.setText("0.0");
                                tvPriceGood.setTextColor(Color.WHITE);
                            }
                        }
                    }
                    tvPriceBad.setText(StringUtils.formatPriceByDouble(good * items.get(getLayoutPosition()).getProductQtyOfTypes().get(1).getProductDeliveredUnitPrice()));
                    tvQuantity.setText(getTotalItem() + "");
                    if (bad == items.get(getLayoutPosition()).getDeliveredBadQtyTmp()) {
                        tvPriceBad.setTextColor(Color.WHITE);
                    } else {
                        tvPriceBad.setTextColor(ContextCompat.getColor(context, R.color.color_grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            etErrorType.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    double error = Double.parseDouble(StringUtils.isEmptyDot(etErrorType.getText().toString()) ? "0" : etErrorType.getText().toString());
                    double good = Double.parseDouble(StringUtils.isEmptyDot(etGoodType.getText().toString()) ? "0" : etGoodType.getText().toString());
                    double bad = Double.parseDouble(StringUtils.isEmptyDot(etBadType.getText().toString()) ? "0" : etBadType.getText().toString());
                    if (items.get(getLayoutPosition()).getType() == "bad") {
                        double tmpBad = items.get(getLayoutPosition()).getDeliveredActualQty() - good - error;
                        if (bad <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                            if (tmpBad > 0) {
                                etBadType.setText("" + StringUtils.formatPrice3DecimalPlaces(tmpBad));
                            } else if (tmpBad < 0 && bad <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                                etBadType.setText("0.0");
                                tvPriceBad.setTextColor(Color.WHITE);
                            }
                        }
                    }
                    if (items.get(getLayoutPosition()).getType() == "good") {
                        double tmpGood = items.get(getLayoutPosition()).getDeliveredActualQty() - error - bad;
                        if (good <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                            if (tmpGood > 0) {
                                etGoodType.setText("" + StringUtils.formatPrice3DecimalPlaces(tmpGood));
                            } else if (tmpGood < 0 && good <= items.get(getLayoutPosition()).getDeliveredActualQty()) {
                                etGoodType.setText("0.0");
                                tvPriceGood.setTextColor(Color.WHITE);
                            }
                        }
                    }
                    tvPriceError.setText(StringUtils.formatPriceByDouble(good * items.get(getLayoutPosition()).getProductQtyOfTypes().get(2).getProductDeliveredUnitPrice()));
                    tvQuantity.setText(getTotalItem() + "");
                    if (error == items.get(getLayoutPosition()).getDeliveredErrorQtyTmp()) {
                        tvPriceError.setTextColor(Color.WHITE);
                    } else {
                        tvPriceError.setTextColor(ContextCompat.getColor(context, R.color.color_grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            edtNote.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    items.get(getLayoutPosition()).setNoteItem(editable.toString());
                }
            });

            spReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    items.get(getLayoutPosition()).setPositionReason(i);
                    if (i == 0) {
                        items.get(getLayoutPosition()).setKeyReason(0);
                    } else {
                        items.get(getLayoutPosition()).setKeyReason(Integer.parseInt(mKeyValues.get(i - 1).key));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }

    }

    public interface IOnItemClickedListener {
        void canCheckedData();
    }

    protected void hideKeyBoard() {
        try {
            ((Activity) context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        InputMethodManager inputManager = (InputMethodManager) context
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(
                                ((Activity) context).getCurrentFocus().getApplicationWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (IllegalStateException e) {
                    } catch (Exception e) {
                    }
                }
            });

        } catch (IllegalStateException e) {
            // TODO: handle exception
        } catch (Exception e) {
        }
    }

    private void validateDecimal(String s) {
        if (s.contains(".")) {
            String subStr = s.substring(s.indexOf(".") + 1, s.length());
            if (subStr.length() > 3) {
                return;
            }
        }
    }

}

class DecimalDigitsInputFilter implements InputFilter {
    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }

}