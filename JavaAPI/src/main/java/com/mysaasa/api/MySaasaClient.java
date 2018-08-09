package com.mysaasa.api;

import com.mysaasa.api.responses.*;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.reactivex.subjects.Subject;
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
public class MySaasaClient implements MySaasaGateway {
    final MySaasaGateway retrofitGateway;

    final BehaviorSubject<SessionSummary> currentSessionSubject = BehaviorSubject.createDefault(SessionSummary.NO_SESSION);


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


        retrofitGateway = new Retrofit.Builder()
                .baseUrl(scheme+"://"+domain+":"+port)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client )
                .build()
                .create(MySaasaGateway.class);

    }


    public Observable<SessionSummary> observeSessionSummary() {
        return currentSessionSubject;
    };

    @Override
    public Observable<PostToBlogResponse> postToBlog(String title, String subtitle, String summary, String body, String category) {
        return retrofitGateway.postToBlog(title, subtitle, summary, body, category)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    @Override
    public Observable<GetThreadResponse> getThread(long message_id) {
        return retrofitGateway.getThread(message_id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<AddTwoResponse> addTwo(int a, int b) {
        return retrofitGateway.addTwo(a, b)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    @Override
    public Observable<GetBlogPostByIdResponse> getBlogPostById(long id) {
        return retrofitGateway.getBlogPostById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    @Override
    public Observable<CreateUserResponse> createUser(String identifier, String password) {
        AsyncSubject<CreateUserResponse> subject = AsyncSubject.create();
        retrofitGateway.createUser(identifier, password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subject);

        subject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(result->{

                    if (result.isSuccess()) {
                        currentSessionSubject.onNext(result.getData());
                    }
        });

        return subject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    @Override
    public Observable<WebsiteResponse> Website() {
        return retrofitGateway.Website()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<DeleteCommentResponse> deleteComment(long comment_id) {
        return retrofitGateway.deleteComment(comment_id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<AddReferralResponse> addReferral(long parentId, long childId) {
        return retrofitGateway.addReferral(parentId, childId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<PostReplyResponse> postReply(long parent_comment_id, String comment) {
        return retrofitGateway.postReply(parent_comment_id, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<WebsiteTestResponse> WebsiteTest() {
        return retrofitGateway.WebsiteTest()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<GetSessionResponse> getSession() {
        return null;
    }

    @Override
    public Observable<ReplyMessageResponse> replyMessage(long message_id, String message) {
        return null;
    }

    @Override
    public Observable<DeleteBlogPostResponse> deleteBlogPost(long post_id) {
        return null;
    }

    @Override
    public Observable<GetAllMediaResponse> getAllMedia() {
        return null;
    }

    @Override
    public Observable<GetProductCategoriesResponse> getProductCategories() {
        return null;
    }

    @Override
    public Observable<GetMessagesResponse> getMessages(long page, long page_size, String order, String direction) {
        return null;
    }

    @Override
    public Observable<GetMessageByIdResponse> getMessageById(long message_id) {
        return null;
    }

    @Override
    public Observable<GetMessageCountResponse> getMessageCount() {
        return null;
    }

    @Override
    public Observable<GetBlogCategoriesResponse> getBlogCategories() {
        return null;
    }

    @Override
    public Observable<TestResponse> test() {
        return null;
    }

    @Override
    public Observable<ThrowsUpResponse> throwsUp() {
        return null;
    }

    @Override
    public Observable<LoginUserResponse> loginUser(String identifier, String password) {
        AsyncSubject<LoginUserResponse> subject = AsyncSubject.create();
        retrofitGateway.loginUser(identifier, password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subject);

        subject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(result->{
           if (result.isSuccess()) {
               currentSessionSubject.onNext(result.getData());
           }
        }, Throwable::printStackTrace);

        return subject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<MediaResponse> Media() {
        return retrofitGateway.Media()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<GetBlogCommentsResponse> getBlogComments(long post_id, int count) {
        return retrofitGateway.getBlogComments(post_id, count)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<PingResponse> ping() {
        return retrofitGateway.ping()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<RegisterGcmKeyResponse> registerGcmKey(String gc_reg_id) {
        return retrofitGateway.registerGcmKey(gc_reg_id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());

    }

    @Override
    public Observable<BasicTestResponse> BasicTest() {
        return null;
    }

    @Override
    public Observable<GetBlogPostsResponse> getBlogPosts(String category, int page, int take, String order, String direction) {
        return null;
    }

    @Override
    public Observable<SendMessageResponse> sendMessage(String to_user, String title, String body, String name, String email, String phone) {
        return null;
    }

    @Override
    public Observable<LogoutResponse> logout() {
        AsyncSubject<LogoutResponse> subject = AsyncSubject.create();
        retrofitGateway.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subject);

        subject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(result->{
            if (result.isSuccess()) {
                currentSessionSubject.onNext(SessionSummary.NO_SESSION);
            }
        });

        return subject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    @Override
    public Observable<ThrowsUpWithNullMessageResponse> throwsUpWithNullMessage() {
        return null;
    }

    @Override
    public Observable<StringStubResponse> stringStub(String string) {
        return null;
    }

    @Override
    public Observable<GetTopLevelBlogCommentsResponse> getTopLevelBlogComments(long id, int count) {
        return null;
    }

    @Override
    public Observable<PostCommentResponse> postComment(long post_id, String comment) {
        return null;
    }

    @Override
    public Observable<AddTwoResponse> AddTwo(int a, int b) {
        return null;
    }

    @Override
    public Observable<UpdateBlogPostResponse> updateBlogPost(long id, String title, String subtitle, String summary, String body) {
        return null;
    }

    @Override
    public Observable<UpdateCommentResponse> updateComment(long comment_id, String comment) {
        return null;
    }

    @Override
    public Observable<UserReferralDataResponse> getUserReferralData() {
        return retrofitGateway.getUserReferralData()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }
}
