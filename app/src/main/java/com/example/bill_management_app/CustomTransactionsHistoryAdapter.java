package com.example.bill_management_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomTransactionsHistoryAdapter extends BaseAdapter {

    Context context;
    String transactionId[];
    String status[];
    String transactionDate[];
    LayoutInflater inflater;

    public CustomTransactionsHistoryAdapter(Context appContext, String[] transactionId, String[] transactionDate, String[] status) {
        context = appContext;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.status = status;

        inflater = LayoutInflater.from(appContext);
    }
    @Override
    public int getCount() {
        // limit display to 5 items
        if (transactionId != null) {
            return Math.min(transactionId.length, 5);
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_cust_transactions,null);
        TextView tId = convertView.findViewById(R.id.transactionItem);
        TextView tDate = convertView.findViewById(R.id.dateItem);
        TextView st = convertView.findViewById(R.id.statusItem);


        tId.setText(transactionId[position]);
        tDate.setText(transactionDate[position]);
        st.setText(status[position]);

        return convertView;
    }
}
