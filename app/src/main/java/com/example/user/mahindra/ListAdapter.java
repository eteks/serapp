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
    public class ListAdapter extends ArrayAdapter<complaint> {

        /**
         * Adapter context
         */
        Context mContext;

        /**
         * Adapter View layout
         */
        int mLayoutResourceId;


        public ListAdapter(Context context, int layoutResourceId) {
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
            final complaint currentItem = getItem(position);
            if (row == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                row = inflater.inflate(mLayoutResourceId, parent, false);
            }

            row.setTag(currentItem);
            //final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkToDoItem);
            //final ListView listViews = (ListView) row.findViewById(R.id.)
            final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkBox);
            checkBox.setText(currentItem.getText());
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
            final int index = 0;
            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (checkBox.isChecked()) {
                        if (mContext instanceof Complaints) {
                            Complaints activity = (Complaints) mContext;
                            activity.insertItem(position);
                        }
                    }
                }
            });
            return row;
        }



    }

