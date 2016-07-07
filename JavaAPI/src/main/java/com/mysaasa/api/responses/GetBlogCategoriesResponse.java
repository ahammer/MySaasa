package com.mysaasa.api.responses;

import com.mysaasa.api.model.Category;

import java.util.List;

/**
 * Created by Adam on 2/29/2016.
 */
public class GetBlogCategoriesResponse {
    List<Category> data;

    public List<Category> getData() {
        return data;
    }
}
