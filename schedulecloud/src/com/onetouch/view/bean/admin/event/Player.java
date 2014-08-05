package com.onetouch.view.bean.admin.event;

import java.io.Serializable;
import java.util.Date;

public class Player implements Serializable {

    private String name;
   
    private int number;

    private String photo;
   
    private String position;
   
    private String nationality;
   
    private String height;
   
    private String weight;
   
    private Date birth;


   
    public Player() {}
   
    public Player(String name) {
            this.name = name;
    }

public Player(String name, int number, String photo) {
            this.name = name;
    this.number = number;
            this.photo = photo;
    }

public Player(String name, int number, String photo, String position) {
            this.name = name;
    this.number = number;
            this.photo = photo;
    this.position = position;
    }
   
    public String getHeight() {
            return height;
    }

    public void setHeight(String height) {
            this.height = height;
    }

    public String getWeight() {
            return weight;
    }

    public void setWeight(String weight) {
            this.weight = weight;
    }

    public Date getBirth() {
            return birth;
    }

    public void setBirth(Date birth) {
            this.birth = birth;
    }
   
    public Player(String name, int number) {
            this.name = name;
            this.number = number;
    }

    public String getName() {
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }

    public String getPhoto() {
            return photo;
    }

    public void setPhoto(String photo) {
            this.photo = photo;
    }
   
    public String getPosition() {
            return position;
    }

    public void setPosition(String position) {
            this.position = position;
    }

    public String getNationality() {
            return nationality;
    }

    public void setNationality(String nationality) {
            this.nationality = nationality;
    }
   

    public int getNumber() {
            return number;
    }

    public void setNumber(int number) {
            this.number = number;
    }







    @Override
    public boolean equals(Object obj) {
            if(obj == null)
                    return false;
            if(!(obj instanceof Player))
                    return false;
           
            return ((Player)obj).getNumber() == this.number;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        return hash * 31 + name.hashCode();
    }

    @Override
    public String toString() {
            return name;
    }
}

