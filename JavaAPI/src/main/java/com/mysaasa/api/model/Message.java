package com.mysaasa.api.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.Date;



/**
 * Created by adam on 15-02-15.
 */
public class Message implements Serializable{
    public final long id;

    public final User recipient;
    public final User sender;
    public final String title;
    public final String body;
    public final String data;
    public final Date timeSent;
    public final boolean read;
    public final ContactInfo senderContactInfo;

    public final Message messageThreadRoot;

    private final Object dataObj;
    private com.mysaasa.api.model.MessageType type = com.mysaasa.api.model.MessageType.Unknown;

    public Message(long id, ContactInfo senderContactInfo, com.mysaasa.api.model.User recipient, com.mysaasa.api.model.User sender, String title, String body, String data, Date timeSent, boolean read)  {
        this.id = id;
        this.senderContactInfo = senderContactInfo;
        this.recipient = recipient;
        this.title = title;
        this.body = body;
        this.timeSent = timeSent;
        this.read = read;
        this.data = data;
        this.sender = sender;
        this.messageThreadRoot = null;


        if (data != null && !data.trim().equals("")) {
            this.dataObj = parseData();
        } else {
            this.dataObj = null;
        }
    }

    public Object getDataObj() {
        return dataObj;
    }

    public com.mysaasa.api.model.MessageType getType() {
        return type;
    }

    private Object parseData()  {
        try {
            //Parse the type string, then find the enum and pass it on.
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(data);
            String type = jo.get("type").getAsString();
            this.type = com.mysaasa.api.model.MessageType.valueOf(type);
            return this.type.parse(jo);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != message.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    static Gson gson = new GsonBuilder().create();
    public static Message from(JsonObject o) {
            return gson.fromJson(o, Message.class);
    }


    @Override
    public String toString() {
        return title+"\n"+body;
    }

    public String toDebugString() {
        return "Message{" +
                "id=" + id +
                ", recipient=" + recipient +
                ", sender=" + sender +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", data='" + data + '\'' +
                ", timeSent=" + timeSent +
                ", read=" + read +
                ", senderContactInfo=" + senderContactInfo +
                ", dataObj=" + dataObj +
                ", type=" + type +
                '}';
    }

    /**
     * Shortcut to the Recipient ID
     * Null if none
     * @return the Recipient.id
     */
    public Long getRecipientId() {
        if (recipient != null) return recipient.id;
        return null;
    }

    /**
     * Shortcut to the Sender ID
     * Doesn't throw NPEs
     * @return the sender ID, if any, null if not
     */
    public Long getSenderId() {
        if (sender != null) return sender.id;
        return null;
    }

    /**
     * Shortcut to the senderContactInfo.id
     * Does a null check to avoid NPE's
     *
     * @return the ID of the Contact Info
     */
    public Long getSenderContactInfoId() {
        if (senderContactInfo != null) return senderContactInfo.id;
        return null;
    }
}
