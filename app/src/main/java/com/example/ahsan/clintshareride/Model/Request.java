package com.example.ahsan.clintshareride.Model;

import java.util.List;

/**
 * Created by ahsan on 12/26/18.
 */

public class Request {

    private String phone;
    private String name;
    private String address;
    private String total;
    //private String status;
    private String comment;
    //private List<Order> foods;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String comment) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.comment = comment;
        //this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
