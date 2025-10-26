package com.example.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class CurrencyAdapter extends BaseAdapter {
    public static class Item {
        public final String code;   // USD
        public final String symbol; // $
        public final String label;  // USD — US Dollar
        public Item(String code, String symbol, String label) {
            this.code = code; this.symbol = symbol; this.label = label;
        }
    }

    private final Context ctx;
    private final List<Item> data;
    private String selectedCode; // code hiện được chọn

    public CurrencyAdapter(Context ctx, List<Item> data, String selectedCode) {
        this.ctx = ctx;
        this.data = data;
        this.selectedCode = selectedCode;
    }

    @Override public int getCount() { return data.size(); }
    @Override public Item getItem(int position) { return data.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(ctx).inflate(R.layout.row_currency, parent, false);
        }
        TextView tvSymbol = v.findViewById(R.id.tvSymbol);
        TextView tvName   = v.findViewById(R.id.tvName);
        RadioButton rb    = v.findViewById(R.id.rbSelect);

        Item it = getItem(position);
        tvSymbol.setText(it.symbol);
        tvName.setText(it.label);
        rb.setChecked(it.code.equals(selectedCode));

        // click cả dòng hay radio đều chọn
        View.OnClickListener choose = vv -> {
            selectedCode = it.code;
            Prefs.setCurrency(ctx, it.code, it.symbol);
            notifyDataSetChanged();
        };
        v.setOnClickListener(choose);
        rb.setOnClickListener(choose);

        return v;
    }
}
