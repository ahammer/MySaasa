package com.mysaasa.api.responses;

import com.mysaasa.api.model.BlogPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adam on 12/28/2014.
 */
public class GetBlogPostsResponse extends SimpleResponse {
        private ArrayList<BlogPost> data = new ArrayList<BlogPost>();
        public List<BlogPost> getData() {
                return Collections.unmodifiableList(data);
        }
}
