package com.mysaasa.api.responses;

import com.mysaasa.api.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 2014-10-16.
 */
public class GetCategoriesResponse extends SimpleResponse {
    public  List<Category> results = new ArrayList();
}
