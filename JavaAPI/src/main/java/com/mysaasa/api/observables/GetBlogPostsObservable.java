package com.mysaasa.api.observables;

import com.mysaasa.api.model.BlogPost;
import com.mysaasa.api.model.Category;
import com.mysaasa.api.responses.GetBlogPostsResponse;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Adam on 3/3/2016.
 */
public class GetBlogPostsObservable implements Observable.OnSubscribe<BlogPost> {
    private final Category category;
    private final com.mysaasa.api.MySaasaGateway gateway;
    private final com.mysaasa.api.MySaasaClient client;
    private GetBlogPostsResponse response;

    public GetBlogPostsObservable(Category category, com.mysaasa.api.MySaasaClient client) {
        this.category = category;
        this.gateway = client.getGateway();
        this.client = client;
    }

    @Override
    public void call(Subscriber<? super BlogPost> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            try {
                client.startNetwork();
                if (response == null) {
                    Call<GetBlogPostsResponse> call = gateway.getBlogPosts(category.name, 0, 100, "priority", "DESC");
                    this.response = call.execute().body();
                }

                if (!this.response.isSuccess()) {
                    subscriber.onError(new MySaasaServerException(response.getMessage()));
                }
                for (BlogPost bp : response.getData()) {
                    subscriber.onNext(bp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.stopNetwork();
            }
            subscriber.onCompleted();
        }
    }
}
