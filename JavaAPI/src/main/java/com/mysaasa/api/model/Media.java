package com.mysaasa.api.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 *  "id": 70,
 "type": "IMAGE",
 "format": "JPEG",
 "filename": "DSC00191.jpg",
 "uid": "60e4155d1493fc7d2b47fff"

 * Created by adam on 2014-10-31.
 */
public class Media implements Serializable{
    public final long id;
    public final String format;
    public final String type;
    public final String filename;
    public final String uid;

    public Media(JsonObject obj) {
        id = obj.get("id").getAsLong();
        format = obj.get("format").getAsString();
        type = obj.get("type").getAsString();
        filename = obj.get("filename").getAsString();
        uid = obj.get("uid").getAsString();
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", format='" + format + '\'' +
                ", type='" + type + '\'' +
                ", filename='" + filename + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

}
