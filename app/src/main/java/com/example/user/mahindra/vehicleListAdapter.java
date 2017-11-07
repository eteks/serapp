package com.example.user.mahindra;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.user.mahindra.R.id.checkBox;

/**
 * Created by ETS-7 on 10/27/2017.
 */


/**
 * Adapter to bind a ToDoItem List to a view
 */
public class vehicleListAdapter extends ArrayAdapter<vehicle>{

    /**
     * Adapter context
     */
    private Context mContext;
    public static  String[] vehicle_id = new String[100];
    /**
     * Adapter View layout
     */
    private int mLayoutResourceId;
    int index = 0;

    public vehicleListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final vehicle currentItem = getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }
//        System.out.println("Current item :"+currentItem);
        row.setTag(currentItem);
        final TextView text = (TextView)  row.findViewById(R.id.textView);
//        System.out.println(currentItem.getText());
        String [] title = currentItem.getText().split(",");
        text.setText(title[0]);
        final TextView vehicleStatus = (TextView)  row.findViewById(R.id.vehicleStatus);
        vehicle_id[position] = title[1];
        return row;
    }
}


