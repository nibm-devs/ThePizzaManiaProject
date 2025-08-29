package com.example.thepizzamaniaproject.Domain;

public class UserDomain {

    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String gender;
    private String profileImage;


    // constructor
    public UserDomain(int id, String name, String email, String password, String phone, String address, String gender, String profileImage)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.profileImage = profileImage;
    }


    // getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getProfileImage() {
        return profileImage;
    }


    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
