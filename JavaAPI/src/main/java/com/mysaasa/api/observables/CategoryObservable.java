package com.mysaasa.api.observables;

import com.mysaasa.api.MySaasaGateway;
import com.mysaasa.api.responses.GetBlogCategoriesResponse;
import com.mysaasa.api.model.Category;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

import static com.mysaasa.api.Preconditions.checkNotNull;


/**
 * Get's the Categories from the server.
 */
public class CategoryObservable implements Observable.OnSubscribe<Category> {
    private final com.mysaasa.api.MySaasaGateway gateway;

    public CategoryObservable(com.mysaasa.api.MySaasaGateway gateway) {
        checkNotNull(gateway);
        this.gateway = gateway;
    }

    @Override
    public void call(Subscriber<? super Category> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            Call<GetBlogCategoriesResponse> blogCategoriesRequest = gateway.getBlogCategories();
            try {
                Response<GetBlogCategoriesResponse> response = blogCategoriesRequest.execute();
                for (Category c:response.body().getData()) {
                  subscriber.onNext(c);
                }
            } catch (IOException e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        }
    }
}
