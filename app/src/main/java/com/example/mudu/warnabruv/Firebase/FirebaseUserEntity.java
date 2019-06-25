package com.example.mudu.warnabruv.Firebase;

public class FirebaseUserEntity {
    public String uId, email, password, name, phone, profileImage;

    public FirebaseUserEntity(){
    }

    public FirebaseUserEntity(String uId, String email, String password, String name, String phone, String country) {
        this.uId = uId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.profileImage = profileImage;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
