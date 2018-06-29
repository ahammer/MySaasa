package com.mysaasa.api;

import com.mysaasa.api.messages.NewMessageEvent;
import com.mysaasa.api.model.Message;
import com.mysaasa.api.model.User;
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
        return mySaasa.gateway.getMessageCount();
    }

    public Observable<SendMessageResponse> sendMessage(final String to_user,
                                                       final String title,
                                                       final String body,
                                                       final String name,
                                                       final String email,
                                                       final String phone) {
                return mySaasa.gateway.sendMessage(to_user, title, body, name, email, phone);
    }

    public Observable<Message> getMessageThread(final Message m) {
                return mySaasa.gateway.getThread(m.id).flatMapIterable(response->response.data);
    }


    public Observable<Message> getMessages() {
                return mySaasa.gateway.getMessages(0,100,"timeSent","DESC").flatMapIterable(response->response.data);
    }

    public Observable<ReplyMessageResponse> replyToMessage(final Message parent, final String s) {
                return mySaasa.gateway.replyMessage(parent.id,s);
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
