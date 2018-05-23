package com.pr.web.lighter.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Gson 日期型数据适配器
 *
 * @author Bailey
 */
public class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    final public static String     UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
    final private       DateFormat dateFormat;

    public GsonUTCDateAdapter() {
        dateFormat = new SimpleDateFormat(UTC_FORMAT, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return dateFormat.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateFormat.format(date));
    }
}
