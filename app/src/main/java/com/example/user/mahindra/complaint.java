package com.example.user.mahindra;

/**
 * Created by ETS-7 on 10/26/2017.
 */

public class complaint {
    @com.google.gson.annotations.SerializedName("complaint_name")
    private String c_name;
    @com.google.gson.annotations.SerializedName("complaint_id")
    private String c_id;

    @com.google.gson.annotations.SerializedName("id")
    private String mId;


    public complaint() {

    }

    @Override
    public String toString() {
        return getText();

    }

    public complaint(String text, String id,String c_id) {
        this.setText(text);
        this.setText(c_id);
//            this.setId(id);
    }

    public String getText() {
        return c_name;

    }
    public String getC_id()
    {
        return c_id;
    }

    public final void setText(String text) {
        c_name = text;
        c_id=text;
    }

    public String getId() {
        return mId;
    }


}

