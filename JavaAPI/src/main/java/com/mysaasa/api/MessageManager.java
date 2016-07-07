package com.mysaasa.api;

import com.mysaasa.api.messages.NewMessageEvent;
import com.mysaasa.api.model.Message;
import com.mysaasa.api.model.User;
import com.mysaasa.api.observables.ModelMySaasaObservable;
import com.mysaasa.api.responses.GetMessageCountResponse;
import com.mysaasa.api.responses.GetMessagesResponse;
import com.mysaasa.api.responses.GetThreadResponse;
import com.mysaasa.api.responses.ReplyMessageResponse;
import com.mysaasa.api.responses.SendMessageResponse;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 4/6/2015.
 */
public class MessageManager {
    private final MySaasaClient mySaasa;
    private final MySaasaMessageStorage messageStore;
    private final MessageEventEmitter emitter;
    private final Observable<NewMessageEvent> messageEventObservable = Observable.create(emitter = new MessageEventEmitter());

    public MessageManager(MySaasaClient mySaasaClient) {
        this.mySaasa = mySaasaClient;
        messageStore = new InMemoryMessageStorage(mySaasaClient);
    }

    public void start() {
        mySaasa.bus.register(emitter);
    }

    public void stop() {
        mySaasa.bus.unregister(emitter);
    }


    public Observable<GetMessageCountResponse> getMessageCount() {
        return Observable.create(new com.mysaasa.api.observables.StandardMySaasaObservable<GetMessageCountResponse>(mySaasa, true) {
            @Override
            protected Call<GetMessageCountResponse> getNetworkCall() {
                return mySaasa.gateway.getMessageCount();
            }
        });
    }

    public Observable<SendMessageResponse> sendMessage(final String to_user,
                                                       final String title,
                                                       final String body,
                                                       final String name,
                                                       final String email,
                                                       final String phone) {
        return Observable.create(new com.mysaasa.api.observables.StandardMySaasaObservable<SendMessageResponse>(mySaasa, true) {
            @Override
            protected Call<SendMessageResponse> getNetworkCall() {
                return mySaasa.gateway.sendMessage(to_user, title, body, name, email, phone);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Message> getMessageThread(final Message m) {
        return Observable.create(new ModelMySaasaObservable<Message, com.mysaasa.api.responses.GetThreadResponse>(mySaasa, true) {
            @Override
            public void processItems(GetThreadResponse response, Subscriber<? super Message> subscriber) {
                for (Message m:response.data) messageStore.storeMessage(m);
                for (Message message : response.data) subscriber.onNext(message);
                subscriber.onCompleted();
            }

            @Override
            protected Call<com.mysaasa.api.responses.GetThreadResponse> getNetworkCall() {
                return mySaasa.gateway.getThread(m.id);
            }
        }).subscribeOn(Schedulers.io()).onBackpressureBuffer();
    }


    public Observable<Message> getMessages() {
        return Observable.create(new ModelMySaasaObservable<Message, GetMessagesResponse>(mySaasa, true) {
            @Override
            protected Call<GetMessagesResponse> getNetworkCall() {
                return mySaasa.gateway.getMessages(0,100,"timeSent","DESC");
            }

            @Override
            public void processItems(GetMessagesResponse response, Subscriber<? super Message> subscriber) {

                User user = mySaasa
                        .getAuthenticationManager()
                        .getAuthenticatedUser();

                messageStore.storeMessages(response.data);


                for (Message m : messageStore.getRootMessages(user))
                    subscriber.onNext(m);

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).onBackpressureBuffer();
    }

    public Observable<com.mysaasa.api.responses.ReplyMessageResponse> replyToMessage(final Message parent, final String s) {
        return Observable.create(new com.mysaasa.api.observables.StandardMySaasaObservable<ReplyMessageResponse>(mySaasa, true ) {
            @Override
            protected Call<com.mysaasa.api.responses.ReplyMessageResponse> getNetworkCall() {
                return mySaasa.gateway.replyMessage(parent.id,s);
            }
        }).subscribeOn(Schedulers.io() );
    }


    public Observable<NewMessageEvent> getMessagesObservable() {
        return messageEventObservable;
    }



    private static class MessageEventEmitter implements Observable.OnSubscribe<NewMessageEvent> {
        List<Subscriber> subscriberList = new ArrayList<>();

        @Override
        public void call(Subscriber<? super NewMessageEvent> subscriber) {
            subscriberList.add(subscriber);
        }

        @Subscribe
        public void onNewMessage(NewMessageEvent event) {
            for (int i=subscriberList.size()-1;i>=0;i--) {
                if (subscriberList.get(i).isUnsubscribed()) {
                    subscriberList.remove(i);
                } else {
                    subscriberList.get(i).onNext(event);
                }
            }
        }


    }
}
