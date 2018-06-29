package com.mysaasa.api;

import com.mysaasa.api.model.BlogComment;
import com.mysaasa.api.model.BlogPost;
import com.mysaasa.api.model.Category;
import com.mysaasa.api.responses.PostToBlogResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 3/3/2016.
 */
public class BlogManager {
    private final MySaasaClient mySaasa;

    private Map<Category, Observable<BlogPost>> mBlogPostCache = new HashMap<>();
    private Map<BlogPost, Observable<BlogComment>> mBlogCommentCache = new HashMap<>();

    public BlogManager(MySaasaClient mySaasaClient) {
        this.mySaasa = mySaasaClient;
    }


    /**
     * Returns a cached observable object for these blogposts
     * @param c
     * @return
     */
    public Observable<BlogPost> getBlogPosts(final Category c) {
        return mySaasa.gateway
                .getBlogPosts(c.name, 0, 20, "date", "desc")
                .flatMapIterable(response->response.getData());
    }


    public Observable<PostToBlogResponse> postToBlog(final String title,
                                                     final String subtitle,
                                                     final String summary,
                                                     final String body,
                                                     final String category) {
        return mySaasa.gateway.postToBlog(title, subtitle, summary, body, category);
    }
}
