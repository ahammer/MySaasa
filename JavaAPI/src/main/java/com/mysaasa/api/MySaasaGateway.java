package com.mysaasa.api;

import com.mysaasa.api.responses.*;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * MySaasaGateway, Retrofit
 *
 * Created by adam on 2014-09-29.
 */
public interface MySaasaGateway {


    @FormUrlEncoded
    @POST("BlogApiService/postToBlog")
    Observable<PostToBlogResponse> postToBlog(@Field("title")String title, @Field("subtitle")String subtitle, @Field("summary")String summary, @Field("body")String body, @Field("category")String category);


    @FormUrlEncoded
    @POST("MessagingApiService/getThread")
    Observable<GetThreadResponse> getThread(@Field("message_id")long message_id);


    @FormUrlEncoded
    @POST("TestService/addTwo")
    Observable<AddTwoResponse> addTwo(@Field("a")int a, @Field("b")int b);


    @FormUrlEncoded
    @POST("BlogApiService/getBlogPostById")
    Observable<GetBlogPostByIdResponse> getBlogPostById(@Field("id")long id);


    @FormUrlEncoded
    @POST("UserApiService/createUser")
    Observable<CreateUserResponse> createUser(@Field("identifier")String identifier, @Field("password")String password);


    @FormUrlEncoded
    @POST("UserApiService/Website")
    Observable<WebsiteResponse> Website();


    @FormUrlEncoded
    @POST("BlogApiService/deleteComment")
    Observable<DeleteCommentResponse> deleteComment(@Field("comment_id")long comment_id);


    @FormUrlEncoded
    @POST("MarketingApiService/addReferral")
    Observable<AddReferralResponse> addReferral(@Field("parentId")long parentId, @Field("childId")long childId);


    @FormUrlEncoded
    @POST("BlogApiService/postReply")
    Observable<PostReplyResponse> postReply(@Field("parent_comment_id")long parent_comment_id, @Field("comment")String comment);


    @FormUrlEncoded
    @POST("UserApiService/WebsiteTest")
    Observable<WebsiteTestResponse> WebsiteTest();


    @FormUrlEncoded
    @POST("UserApiService/getSession")
    Observable<GetSessionResponse> getSession();


    @FormUrlEncoded
    @POST("MessagingApiService/replyMessage")
    Observable<ReplyMessageResponse> replyMessage(@Field("message_id")long message_id, @Field("message")String message);


    @FormUrlEncoded
    @POST("BlogApiService/deleteBlogPost")
    Observable<DeleteBlogPostResponse> deleteBlogPost(@Field("post_id")long post_id);


    @FormUrlEncoded
    @POST("MediaApiServiceImpl/getAllMedia")
    Observable<GetAllMediaResponse> getAllMedia();


    @FormUrlEncoded
    @POST("CategoryApiService/getProductCategories")
    Observable<GetProductCategoriesResponse> getProductCategories();


    @FormUrlEncoded
    @POST("MessagingApiService/getMessages")
    Observable<GetMessagesResponse> getMessages(@Field("page")long page, @Field("page_size")long page_size, @Field("order")String order, @Field("direction")String direction);


    @FormUrlEncoded
    @POST("MessagingApiService/getMessageById")
    Observable<GetMessageByIdResponse> getMessageById(@Field("message_id")long message_id);


    @FormUrlEncoded
    @POST("MessagingApiService/getMessageCount")
    Observable<GetMessageCountResponse> getMessageCount();


    @FormUrlEncoded
    @POST("CategoryApiService/getBlogCategories")
    Observable<GetBlogCategoriesResponse> getBlogCategories();


    @FormUrlEncoded
    @POST("MarketingApiService/test")
    Observable<TestResponse> test();


    @FormUrlEncoded
    @POST("TestService/throwsUp")
    Observable<ThrowsUpResponse> throwsUp();


    @FormUrlEncoded
    @POST("UserApiService/loginUser")
    Observable<LoginUserResponse> loginUser(@Field("identifier")String identifier, @Field("password")String password);


    @FormUrlEncoded
    @POST("UserApiService/Media")
    Observable<MediaResponse> Media();


    @FormUrlEncoded
    @POST("BlogApiService/getBlogComments")
    Observable<GetBlogCommentsResponse> getBlogComments(@Field("post_id")long post_id, @Field("count")int count);


    @FormUrlEncoded
    @POST("UserApiService/ping")
    Observable<PingResponse> ping();


    @FormUrlEncoded
    @POST("UserApiService/registerGcmKey")
    Observable<RegisterGcmKeyResponse> registerGcmKey(@Field("gc_reg_id")String gc_reg_id);


    @FormUrlEncoded
    @POST("UserApiService/BasicTest")
    Observable<BasicTestResponse> BasicTest();


    @FormUrlEncoded
    @POST("BlogApiService/getBlogPosts")
    Observable<GetBlogPostsResponse> getBlogPosts(@Field("category")String category, @Field("page")int page, @Field("take")int take, @Field("order")String order, @Field("direction")String direction);


    @FormUrlEncoded
    @POST("MessagingApiService/sendMessage")
    Observable<SendMessageResponse> sendMessage(@Field("to_user")String to_user, @Field("title")String title, @Field("body")String body, @Field("name")String name, @Field("email")String email, @Field("phone")String phone);


    @POST("UserApiService/logout")
    Observable<LogoutResponse> logout();


    @FormUrlEncoded
    @POST("TestService/throwsUpWithNullMessage")
    Observable<ThrowsUpWithNullMessageResponse> throwsUpWithNullMessage();


    @FormUrlEncoded
    @POST("TestService/stringStub")
    Observable<StringStubResponse> stringStub(@Field("string")String string);


    @FormUrlEncoded
    @POST("BlogApiService/getTopLevelBlogComments")
    Observable<GetTopLevelBlogCommentsResponse> getTopLevelBlogComments(@Field("id")long id, @Field("count")int count);


    @FormUrlEncoded
    @POST("BlogApiService/postComment")
    Observable<PostCommentResponse> postComment(@Field("post_id")long post_id, @Field("comment")String comment);


    @FormUrlEncoded
    @POST("UserApiService/AddTwo")
    Observable<AddTwoResponse> AddTwo(@Field("a")int a, @Field("b")int b);


    @FormUrlEncoded
    @POST("BlogApiService/updateBlogPost")
    Observable<UpdateBlogPostResponse> updateBlogPost(@Field("id")long id, @Field("title")String title, @Field("subtitle")String subtitle, @Field("summary")String summary, @Field("body")String body);


    @FormUrlEncoded
    @POST("BlogApiService/updateComment")
    Observable<UpdateCommentResponse> updateComment(@Field("comment_id")long comment_id, @Field("comment")String comment);

    @POST("MarketingApiService/getUserReferralData")
    Observable<UserReferralDataResponse> getUserReferralData();
}