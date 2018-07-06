package com.mysaasa.api;

import com.mysaasa.api.messages.NetworkStateChange;

import org.greenrobot.eventbus.EventBus;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.JavaNetCookieJar;
import retrofit2.Retrofit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by administrator on 2014-06-29.
 */

/**
 * Some logic and state/threading/messaging can be built into this layer
 *
 * Once things are downloaded, they are stored in state.
 *
 * When authentication fails, it should automatically re-authenticate now
 *
 */
public class MySaasaClient {
    final MySaasaGateway gateway;



    private final BlogManager blogManager;
    private final AuthenticationManager authenticationManager;
    private final MessageManager messagesManager;
    private final CategoryManager categoryManager;
    private int callDepth = 0;
    private CommentManager commentManager;


    //Observables





    /**
     * Construct the service on a domain/port.
     *
     * Point it to a Simple platform Domain, and the running port, and the rest should work.
     *
     * @param domain
     * @param port
     */
    public MySaasaClient(String domain, int port, String scheme) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(CookieHandler.getDefault()))
                .addInterceptor(interceptor).build();


        gateway = new Retrofit.Builder()
                .baseUrl(scheme+"://"+domain+":"+port)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client )
                .build()
                .create(MySaasaGateway.class);

        categoryManager = new com.mysaasa.api.CategoryManager(this);
        blogManager = new BlogManager(this);
        authenticationManager = new com.mysaasa.api.AuthenticationManager(this);
        messagesManager = new com.mysaasa.api.MessageManager(this);
        commentManager = new CommentManager(this);
    }

    public BlogManager getBlogManager() {
        return blogManager;
    }

    public com.mysaasa.api.AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public com.mysaasa.api.MessageManager getMessagesManager() {
        return messagesManager;
    }

    public com.mysaasa.api.CategoryManager getCategoryManager() {
        return categoryManager;
    }


    public boolean isNetworkBusy() {
        return callDepth > 0;
    }

    public MySaasaGateway getGateway() {
        return gateway;
    }

    public CommentManager getCommentManager() {
        return commentManager;
    }
}
