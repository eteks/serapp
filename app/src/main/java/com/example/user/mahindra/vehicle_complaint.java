package com.example.user.mahindra;

/**
 * Created by ETS-7 on 10/27/2017.
 */

public class vehicle_complaint {
        @com.google.gson.annotations.SerializedName("complaint_name")
        private String c_name;
        @com.google.gson.annotations.SerializedName("complaint_id")
        private String com_id;

        @com.google.gson.annotations.SerializedName("id")
        private String mId;


        public vehicle_complaint() {

        }

        @Override
        public String toString() {
            return getText();

        }

        public vehicle_complaint(String text, String id,String c_id) {
            this.setText(text);
            this.setText(c_id);
//            this.setId(id);
        }

        public String getText() {
            return c_name;

        }
        public String getC_id()
        {
            return com_id;
        }

        public final void setText(String text) {
            c_name = text;
            com_id=text;
        }

        public String getId() {
            return mId;
        }


}
