package com.example.ahsan.clintshareride.Model;

/**
 * Created by ahsan on 1/13/19.
 */

public class Order {

    private String PersonName;
    private String PersonMobile;
    private String Title;
    private String Details;

    public Order() {
    }

    public Order(String personName, String personMobile, String title, String details) {
        PersonName = personName;
        PersonMobile = personMobile;
        Title = title;
        Details = details;
    }

    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }

    public String getPersonMobile() {
        return PersonMobile;
    }

    public void setPersonMobile(String personMobile) {
        PersonMobile = personMobile;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }
}
