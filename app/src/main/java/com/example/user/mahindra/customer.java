package com.example.user.mahindra;

/**
 * Created by ETS-7 on 10/26/2017.
 */

public class customer {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    @com.google.gson.annotations.SerializedName("customer_name")
    private String cus_name;
    @com.google.gson.annotations.SerializedName("customer_contact")
    private String cus_contact;
    @com.google.gson.annotations.SerializedName("customer_address")
    private String cus_address;
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
    public customer() {

    }

    @Override
    public String toString() {
        return getCustomer();
    }


    public customer(String cus_name, String cus_contact, String cus_address) {

        this.setCus_name(cus_name);
        this.setCus_contact(cus_contact);
        this.setCus_address(cus_address);
//            this.setId(id);
    }

    /**
     * Returns the item text
     *
     */



    public String getCustomer()
    {
        return cus_name+"*"+cus_contact+"*"+cus_address ;
    }

    public String getCus_name()
    {
        return cus_name;
    }


    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public void setCus_name(String text) {
        cus_name = text;
    }

    public void setCus_contact(String text) {
        cus_contact= text;
    }

    public void setCus_address(String text) {
        cus_address= text;
    }
    /**
     * Returns the item id
     */

}

