package com.mysaasa.api.observables;

import com.mysaasa.api.MySaasaClient;
import com.mysaasa.api.responses.SimpleResponse;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Adam on 3/26/2016.
 */
public abstract class  StandardMySaasaObservable<T extends SimpleResponse> implements Observable.OnSubscribe<T>{
    private final MySaasaClient mySaasa;  
    private final boolean authenticate;
    private T response;


    protected StandardMySaasaObservable(MySaasaClient mySaasa, boolean authenticate) {
        this.mySaasa = mySaasa;
        this.authenticate = authenticate;
    }



    @Override
    public void call(Subscriber<? super T> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            Call<T> call = getNetworkCall();

            try {
                mySaasa.startNetwork();
                if (authenticate) mySaasa.getAuthenticationManager().refreshIfNecessary();
                response = call.execute().body();
                handleResponse(subscriber);
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

    private void handleResponse(Subscriber<? super T> subscriber) {
        if (response.isSuccess()) {
            subscriber.onNext(response);
            if (postResponse(response)) subscriber.onCompleted();
            else subscriber.onError(new RuntimeException("postResponse returned false in "+this.getClass().getName()));
        } else {
            subscriber.onError(new RuntimeException(response.getMessage()));
        }
    }

    public boolean postResponse(T response) {
     return true;
    }

    protected abstract Call<T> getNetworkCall();

    public MySaasaClient getMySaasa() {
        return mySaasa;
    }

    public T getResponse() {
        return response;
    }
}
