package com.mysaasa.api.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * A category of a BlogTemplateService
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    public final int id;
    public final String name;

    public Category(JsonObject object) {
        id= object.get("id").getAsInt();
        name = object.get("name").getAsString();
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String blog) {
        name = blog;
        id = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!name.equals(category.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
