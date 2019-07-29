package com.piniti.platform;

/**
 * Created by Animash on 5/12/2019.
 */

public class UserAdapter {

    private String id, name, email, profession, address, number, gender, image;

    public UserAdapter(){

    }

    public UserAdapter(String id, String name, String email, String profession, String address, String number, String gender, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profession = profession;
        this.address = address;
        this.number = number;
        this.gender = gender;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
