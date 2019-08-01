package com.piniti.platform.Models;

/**
 * Created by Animash on 5/22/2019.
 */

public class AddPeople {

    private String id;
    private String name;
    private String profession;
    private String number;
    private String relation;
    private String category;
    private String image;
    private String thumb_image;

    public AddPeople(){

    }

    public AddPeople(String id, String name, String profession, String number,String relation, String category, String image, String thumb_image) {
        this.id = id;
        this.name = name;
        this.profession = profession;
        this.number = number;
        this.category = category;
        this.image = image;
        this.thumb_image = thumb_image;
        this.relation = relation;
    }

    public String getRelation() {

        return relation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
