package com.bailey.web.lighter.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Gson 管理工具类
 */
public class GsonUtil {
    public static Gson getGsonInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        GsonUTCDateAdapter gsonUTCDateAdapter = new GsonUTCDateAdapter();
        gsonBuilder.registerTypeAdapter(Date.class, gsonUTCDateAdapter);
        gsonBuilder.registerTypeAdapter(java.sql.Date.class, gsonUTCDateAdapter);
        gsonBuilder.registerTypeAdapter(Timestamp.class, gsonUTCDateAdapter);
        return gsonBuilder.create();
    }
}
