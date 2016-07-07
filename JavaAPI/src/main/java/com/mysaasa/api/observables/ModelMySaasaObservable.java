package com.mysaasa.api.observables;

import com.mysaasa.api.responses.SimpleResponse;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;

/**
 * This is used to create observables that emit a Model item, but is 2 way generic
 * you start by defining the type you want to emit, and the type of the network response
 * you implement a function that returns the network call, and another function that
 * parses the response and emits items to the subscriber.
 */
public abstract class ModelMySaasaObservable <T, V extends SimpleResponse> implements Observable.OnSubscribe<T> {
    private final com.mysaasa.api.MySaasaClient mySaasa;
    private final boolean authenticate;


    public ModelMySaasaObservable(com.mysaasa.api.MySaasaClient mySaasa, boolean authenticate) {
        this.mySaasa = mySaasa;
        this.authenticate = authenticate;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            try {
                mySaasa.startNetwork();
                if (authenticate) {     //If authentication required, clear cache
                    mySaasa.getAuthenticationManager().refreshIfNecessary();
                }
            } catch (Exception e) {
                subscriber.onError(e);
                return;
            } finally {
                mySaasa.stopNetwork();

            }

            Call<V> call = getNetworkCall();

            try {
                mySaasa.startNetwork();
                V response = call.execute().body();
                handleResponse(response, subscriber);
            } catch (Exception e) {
                onError(e);
                subscriber.onError(e);
            } finally {
                mySaasa.stopNetwork();
            }
        }
    }

    protected void onError( Exception e) {
        //Reserved for over-ride
    }

    private void handleResponse(V response, Subscriber<? super T> subscriber) {
        if (response.isSuccess()) {
            processItems(response, subscriber);
        } else {
            subscriber.onError(new RuntimeException(response.getMessage()));
        }
    }

    public abstract void processItems(V response, Subscriber<? super T> subscriber);



    protected abstract Call<V> getNetworkCall();

    public com.mysaasa.api.MySaasaClient getMySaasa() {
        return mySaasa;
    }

}
