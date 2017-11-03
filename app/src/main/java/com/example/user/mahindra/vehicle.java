package com.example.user.mahindra;

/**
 * Created by ETS-7 on 10/26/2017.
 */

public class vehicle {


    /**
     * Represents an item in a ToDo list
     */


    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    @com.google.gson.annotations.SerializedName("engine_no")
    private String e_no;
    @com.google.gson.annotations.SerializedName("vehicle_reg_no")
    private String reg_no;
    @com.google.gson.annotations.SerializedName("color_code")
    private String col_code;
    @com.google.gson.annotations.SerializedName("vehicle_id")
    private String v_id;
    /**
     * Item Id
     */


    /**
     * Indicates if the item is completed
     */
//        @com.google.gson.annotations.SerializedName("complete")
//        private boolean mComplete;

    /**
     * ToDoItem constructor
     */
    public vehicle() {

    }
    @Override
    public String toString() {
        return getReg_no();
    }


    public vehicle(String e_no,String col_code,String reg_no,String v_id) {

        this.setEngineNo(e_no);
        this.setColourCode(col_code);
        this.setRegNo(reg_no);
        this.setV_id(v_id);
//            this.setId(id);
    }

    /**
     * Returns the item text
     *
     */



    public String getReg_no()
    {

        return v_id+","+e_no+","+col_code+","+reg_no;
    }
    public String getCol_code()
    {
        return col_code;
    }

    public String getText(){
        return reg_no;
    }
    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public void setEngineNo(String text) {
        e_no = text;
    }

    public void setColourCode(String text) {
        col_code= text;
    }

    public void setRegNo(String text) {
        reg_no= text;
    }
    public void setV_id(String text) {
        v_id= text;
    }
    /**
     * Returns the item id
     */

}

