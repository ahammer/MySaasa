package com.mysaasa.api;

import com.mysaasa.api.responses.AddTwoResponse;
import com.mysaasa.api.responses.BasicTestResponse;
import com.mysaasa.api.responses.CreateUserResponse;
import com.mysaasa.api.responses.DeleteBlogPostResponse;
import com.mysaasa.api.responses.DeleteCommentResponse;
import com.mysaasa.api.responses.GetAllMediaResponse;
import com.mysaasa.api.responses.GetBlogCategoriesResponse;
import com.mysaasa.api.responses.GetBlogCommentsResponse;
import com.mysaasa.api.responses.GetBlogPostByIdResponse;
import com.mysaasa.api.responses.GetBlogPostsResponse;
import com.mysaasa.api.responses.GetMessageByIdResponse;
import com.mysaasa.api.responses.GetMessageCountResponse;
import com.mysaasa.api.responses.GetMessagesResponse;
import com.mysaasa.api.responses.GetProductCategoriesResponse;
import com.mysaasa.api.responses.GetSessionResponse;
import com.mysaasa.api.responses.GetThreadResponse;
import com.mysaasa.api.responses.GetTopLevelBlogCommentsResponse;
import com.mysaasa.api.responses.LoginUserResponse;
import com.mysaasa.api.responses.LogoutResponse;
import com.mysaasa.api.responses.MediaResponse;
import com.mysaasa.api.responses.PingResponse;
import com.mysaasa.api.responses.PostCommentResponse;
import com.mysaasa.api.responses.PostReplyResponse;
import com.mysaasa.api.responses.PostToBlogResponse;
import com.mysaasa.api.responses.RegisterGcmKeyResponse;
import com.mysaasa.api.responses.ReplyMessageResponse;
import com.mysaasa.api.responses.SendMessageResponse;
import com.mysaasa.api.responses.UpdateBlogPostResponse;
import com.mysaasa.api.responses.UpdateCommentResponse;
import com.mysaasa.api.responses.WebsiteResponse;
import com.mysaasa.api.responses.WebsiteTestResponse;

import retrofit2.Call;
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
    Call<PostToBlogResponse> postToBlog(@Field("title") String title, @Field("subtitle") String subtitle, @Field("summary") String summary, @Field("body") String body, @Field("category") String category);


    @FormUrlEncoded
    @POST("MessagingApiService/getThread")
    Call<GetThreadResponse> getThread(@Field("message_id") long message_id);


    @FormUrlEncoded
    @POST("BlogApiService/getBlogPostById")
    Call<GetBlogPostByIdResponse> getBlogPostById(@Field("id") long id);


    @FormUrlEncoded
    @POST("UserApiService/createUser")
    Call<CreateUserResponse> createUser(@Field("identifier") String identifier, @Field("password") String password);


    @FormUrlEncoded
    @POST("UserApiService/Website")
    Call<WebsiteResponse> Website();


    @FormUrlEncoded
    @POST("BlogApiService/deleteComment")
    Call<DeleteCommentResponse> deleteComment(@Field("comment_id") long comment_id);


    @FormUrlEncoded
    @POST("BlogApiService/postReply")
    Call<PostReplyResponse> postReply(@Field("parent_comment_id") long parent_comment_id, @Field("comment") String comment);


    @FormUrlEncoded
    @POST("UserApiService/WebsiteTest")
    Call<WebsiteTestResponse> WebsiteTest();


    @FormUrlEncoded
    @POST("UserApiService/getSession")
    Call<GetSessionResponse> getSession();


    @FormUrlEncoded
    @POST("MessagingApiService/replyMessage")
    Call<ReplyMessageResponse> replyMessage(@Field("message_id") long message_id, @Field("message") String message);


    @FormUrlEncoded
    @POST("BlogApiService/deleteBlogPost")
    Call<DeleteBlogPostResponse> deleteBlogPost(@Field("post_id") long post_id);


    @FormUrlEncoded
    @POST("MediaApiServiceImpl/getAllMedia")
    Call<GetAllMediaResponse> getAllMedia();


    @FormUrlEncoded
    @POST("CategoryApiService/getProductCategories")
    Call<GetProductCategoriesResponse> getProductCategories();


    @FormUrlEncoded
    @POST("MessagingApiService/getMessageCount")
    Call<GetMessageCountResponse> getMessageCount();


    @FormUrlEncoded
    @POST("MessagingApiService/getMessageById")
    Call<GetMessageByIdResponse> getMessageById(@Field("message_id") long message_id);


    @FormUrlEncoded
    @POST("MessagingApiService/getMessages")
    Call<GetMessagesResponse> getMessages(@Field("page") long page, @Field("page_size") long page_size, @Field("order") String order, @Field("direction") String direction);


    @FormUrlEncoded
    @POST("CategoryApiService/getBlogCategories")
    Call<GetBlogCategoriesResponse> getBlogCategories();


    @FormUrlEncoded
    @POST("UserApiService/loginUser")
    Call<LoginUserResponse> loginUser(@Field("identifier") String identifier, @Field("password") String password);


    @FormUrlEncoded
    @POST("UserApiService/Media")
    Call<MediaResponse> Media();


    @FormUrlEncoded
    @POST("BlogApiService/getBlogComments")
    Call<GetBlogCommentsResponse> getBlogComments(@Field("post_id") long post_id, @Field("count") int count);


    @FormUrlEncoded
    @POST("UserApiService/ping")
    Call<PingResponse> ping();


    @FormUrlEncoded
    @POST("UserApiService/registerGcmKey")
    Call<RegisterGcmKeyResponse> registerGcmKey(@Field("gc_reg_id") String gc_reg_id);


    @FormUrlEncoded
    @POST("UserApiService/BasicTest")
    Call<BasicTestResponse> BasicTest();


    @FormUrlEncoded
    @POST("BlogApiService/getBlogPosts")
    Call<GetBlogPostsResponse> getBlogPosts(@Field("category") String category, @Field("page") int page, @Field("take") int take, @Field("order") String order, @Field("direction") String direction);


    @FormUrlEncoded
    @POST("MessagingApiService/sendMessage")
    Call<SendMessageResponse> sendMessage(@Field("to_user") String to_user, @Field("title") String title, @Field("body") String body, @Field("name") String name, @Field("email") String email, @Field("phone") String phone);


    @FormUrlEncoded
    @POST("UserApiService/logout")
    Call<LogoutResponse> logout();


    @FormUrlEncoded
    @POST("BlogApiService/getTopLevelBlogComments")
    Call<GetTopLevelBlogCommentsResponse> getTopLevelBlogComments(@Field("id") long id, @Field("count") int count);


    @FormUrlEncoded
    @POST("BlogApiService/postComment")
    Call<PostCommentResponse> postComment(@Field("post_id") long post_id, @Field("comment") String comment);


    @FormUrlEncoded
    @POST("UserApiService/AddTwo")
    Call<AddTwoResponse> AddTwo(@Field("a") int a, @Field("b") int b);


    @FormUrlEncoded
    @POST("BlogApiService/updateBlogPost")
    Call<UpdateBlogPostResponse> updateBlogPost(@Field("id") long id, @Field("title") String title, @Field("subtitle") String subtitle, @Field("summary") String summary, @Field("body") String body);


    @FormUrlEncoded
    @POST("BlogApiService/updateComment")
    Call<UpdateCommentResponse> updateComment(@Field("comment_id") long comment_id, @Field("comment") String comment);
}
