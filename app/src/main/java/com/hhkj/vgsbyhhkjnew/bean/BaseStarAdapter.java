package com.hhkj.vgsbyhhkjnew.bean;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hhkj.vgsbyhhkjnew.Shape;

import java.lang.reflect.Type;

/**
 * @ProjectName: VgsByHhkjnew
 * @Package: com.hhkj.vgsbyhhkjnew.bean
 * @ClassName: BaseBoAdapter
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/11/17 16:32
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class BaseStarAdapter implements JsonSerializer<BaseStar>, JsonDeserializer<BaseStar> {

    @Override
    public BaseStar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            // 指定包名+类名
            String thePackage = "com.hhkj.vgsbyhhkjnew.bean.";
            return context.deserialize(element, Class.forName(thePackage + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }

    @Override
    public JsonElement serialize(BaseStar src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }
}
