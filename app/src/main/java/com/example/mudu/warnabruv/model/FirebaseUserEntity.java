package com.example.mudu.warnabruv.model;

public class FirebaseUserEntity {
    private String uId, email, password, name, phone, country;

    public FirebaseUserEntity(){
    }

    public FirebaseUserEntity(String uId, String email, String password, String name, String phone, String country) {
        this.uId = uId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.country = country;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
