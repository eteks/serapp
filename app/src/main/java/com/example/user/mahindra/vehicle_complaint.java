package com.example.user.mahindra;

/**
 * Created by ETS-7 on 10/27/2017.
 */

public class vehicle_complaint {

        @com.google.gson.annotations.SerializedName("id")
        private String mId;
        @com.google.gson.annotations.SerializedName("vehicle_id")
        private int vehicle_id;
        @com.google.gson.annotations.SerializedName("complaint_id")
        private int complaint_id;


        public vehicle_complaint() {

        }

        @Override
        public String toString() {
            return "success";

        }

        public vehicle_complaint(int vehicle_id, int complaint_id) {
            this.setVehicle(vehicle_id);
            this.setComplaint(complaint_id);
//            this.setId(id);
        }

        public void setVehicle(int id){
            vehicle_id = id;
        }

        public void setComplaint(int id){
            complaint_id = id;
        }


        public String getId() {
            return mId;
        }


}
