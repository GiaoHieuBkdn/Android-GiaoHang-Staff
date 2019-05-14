package com.bys.sangngoc.views;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by SDC on 10/4/2016.
 */

public class NumberTextWatcher implements TextWatcher {

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";
    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;
    private EditText et;
    private ICustomTextChangeListener mICustomTextChangeListener;


    public NumberTextWatcher(EditText et) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        df = new DecimalFormat("#,###.##",otherSymbols);
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###",otherSymbols);
        this.et = et;
        hasFractionalPart = false;
    }

    public void setICustomTextChangeListener(ICustomTextChangeListener listener){
        mICustomTextChangeListener = listener;
    }

    private boolean isAddDolla;
    public void afterTextChanged(Editable s) {
        if(isAddDolla){
            isAddDolla = false;
            return;
        }
        et.removeTextChangedListener(this);
        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = df.parse(v);
            int cp = et.getSelectionStart();
            isAddDolla = true;
            if (hasFractionalPart) {
                et.setText(df.format(n));
            } else {
                et.setText(dfnd.format(n));
            }
            endlen = et.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et.getText().length()) {
                et.setSelection(sel);
            } else {
                // place cursor at the end?
                et.setSelection(et.getText().length() - 1);
            }
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

        et.addTextChangedListener(this);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
        if(mICustomTextChangeListener != null){
            mICustomTextChangeListener.onTextChanged(s, start, before, count);
        }
    }

    public interface ICustomTextChangeListener{
        void onTextChanged(CharSequence charSequence, int i, int i1, int i2);
    }
}