package com.mysaasa.api;

import com.mysaasa.api.model.BlogComment;
import com.mysaasa.api.model.BlogPost;
import com.mysaasa.api.responses.GetBlogCommentsResponse;
import com.mysaasa.api.responses.PostCommentResponse;
import com.mysaasa.api.responses.PostReplyResponse;
import io.reactivex.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.mysaasa.api.Preconditions.checkNotNull;


/**
 * Created by Adam on 1/6/2015.
 */
public class CommentManager {
    private final MySaasaClient mySaasaClient;
    public Map<Long, BlogComment> blogCommentIdMap = new HashMap<Long, BlogComment>();
    public Map<BlogComment, List<BlogComment>> childLookup = new HashMap<BlogComment, List<BlogComment>>();


    public CommentManager(MySaasaClient mySaasaClient) {
        this.mySaasaClient = mySaasaClient;
    }


    public Observable<BlogComment> getBlogCommentsObservable(BlogPost post) {
        checkNotNull(post);
        return mySaasaClient.gateway.getBlogComments(post.id, 100).flatMapIterable(response->response.getData());
    }

    public Observable<PostCommentResponse> postBlogComment(final BlogPost post, final String text) {
        return mySaasaClient.gateway.postComment(post.id, text);
    }

    public Observable<PostReplyResponse> postCommentResponse(final BlogComment comment, final String text) {
        return mySaasaClient.gateway.postReply(comment.getId(), text);
    }

    public BlogComment lookupCommentById(long parent_id) {
        return blogCommentIdMap.get(parent_id);
    }

    public List<BlogComment> getChildren(BlogComment blogComment) {
        List<BlogComment> comments = childLookup.get(blogComment);
        return comments==null?Collections.EMPTY_LIST:comments;
    }
}
