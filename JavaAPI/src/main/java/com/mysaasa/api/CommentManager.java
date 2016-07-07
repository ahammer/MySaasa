package com.mysaasa.api;

import com.mysaasa.api.model.BlogComment;
import com.mysaasa.api.model.BlogPost;
import com.mysaasa.api.responses.GetBlogCommentsResponse;
import com.mysaasa.api.responses.PostCommentResponse;
import com.mysaasa.api.responses.PostReplyResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

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
        final BlogPost post1 = post;
        final com.mysaasa.api.MySaasaGateway gateway1 = mySaasaClient.gateway;
        Observable<BlogComment> observable = Observable.create(new BlogCommentsObservable(this, post1));
        return observable.subscribeOn(Schedulers.io()).onBackpressureBuffer();
    }

    public Observable<PostCommentResponse> postBlogComment(final BlogPost post, final String text) {
        Observable<PostCommentResponse> observable = Observable.create(new Observable.OnSubscribe<PostCommentResponse>() {
            @Override
            public void call(Subscriber<? super PostCommentResponse> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    Call<PostCommentResponse> call = mySaasaClient.gateway.postComment(post.id, text);
                    try {
                        Response<PostCommentResponse> response = call.execute();
                        subscriber.onNext(response.body());
                        subscriber.onCompleted();
                        System.out.println(response.toString());
                    } catch (IOException e) {
                        subscriber.onError(e);
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }
        });
        return observable.subscribeOn(Schedulers.io());

    }

    public Observable<PostReplyResponse> postCommentResponse(final BlogComment comment, final String text) {
        Observable<PostReplyResponse> observable = Observable.create(new Observable.OnSubscribe<PostReplyResponse>() {
            @Override
            public void call(Subscriber<? super PostReplyResponse> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    Call<PostReplyResponse> call = mySaasaClient.gateway.postReply(comment.getId(), text);
                    try {
                        Response<PostReplyResponse> response = call.execute();
                        subscriber.onNext(response.body());
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }
        });
        return observable.subscribeOn(Schedulers.io());

    }

    public BlogComment lookupCommentById(long parent_id) {
        return blogCommentIdMap.get(parent_id);
    }

    public List<BlogComment> getChildren(BlogComment blogComment) {
        List<BlogComment> comments = childLookup.get(blogComment);
        return comments==null?Collections.EMPTY_LIST:comments;
    }

    public com.mysaasa.api.MySaasaGateway getGateway() {
        return mySaasaClient.getGateway();
    }

    private static class BlogCommentsObservable implements Observable.OnSubscribe<BlogComment> {
        private final com.mysaasa.api.MySaasaGateway gateway;
        private final BlogPost post;
        private final CommentManager commentManager;

        public BlogCommentsObservable(CommentManager commentManager, BlogPost post) {
            this.commentManager = commentManager;
            this.gateway = commentManager.getGateway();

            this.post = post;
        }

        @Override
        public void call(Subscriber<? super BlogComment> subscriber) {
            if (!subscriber.isUnsubscribed()) {
                Call<GetBlogCommentsResponse> call = gateway.getBlogComments(post.id, 100);
                try {
                    Response<GetBlogCommentsResponse> response = call.execute();

                    //Register ID's

                    commentManager.registerComments(response.body().getData());
                    commentManager.scanForParents();


                    for (BlogComment bc : response.body().getData()) {
                        if (bc.calculateDepth(commentManager) == 0) {
                            subscriber.onNext(bc);
                        }
                    }

                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }
    }

    private void registerComments(List<BlogComment> blogCommentList) {
        for (BlogComment blogComment:blogCommentList) registerComment(blogComment);
    }

    private void scanForParents() {
        for (BlogComment bc: blogCommentIdMap.values()) {
            BlogComment parent = bc.getParent(this);
            registerAsChild(parent, bc);
        }
    }

    private void registerAsChild(BlogComment parent, BlogComment bc) {
        List<BlogComment> children = childLookup.get(parent);
        if (children == null) children = new ArrayList<BlogComment>();
        if (!children.contains(bc)) children.add(bc);
        childLookup.put(parent,children);
    }

    private void registerComment(BlogComment blogComment) {
        blogCommentIdMap.put(blogComment.getId(), blogComment);
    }
}
