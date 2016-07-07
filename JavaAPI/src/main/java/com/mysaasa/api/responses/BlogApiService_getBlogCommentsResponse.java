package com.mysaasa.api.responses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 1/5/2015.
 */
public class BlogApiService_getBlogCommentsResponse extends SimpleResponse {
    public List<com.mysaasa.api.model.BlogComment> comments = new ArrayList();
}
