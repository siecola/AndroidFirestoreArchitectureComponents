package br.com.siecola.firestorearchcomp.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Product implements Serializable {
    public static final String COLLECTION = "products";
    public static final String FIELD_userId = "userId";
    public static final String FIELD_name = "name";
    public static final String FIELD_description = "description";
    public static final String FIELD_code = "code";
    public static final String FIELD_price = "price";

    private String id;
    private String userId;
    private String name;
    private String description;
    private String code;
    private double price;

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}