package com.bailey.web.lighter.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

/**
 * Gson 日期型数据适配器
 *
 * @author Bailey
 */
public class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return DateParser.UTC_FORMAT.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(DateParser.UTC_FORMAT.format(date));
    }
}
