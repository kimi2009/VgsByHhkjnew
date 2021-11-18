package com.hhkj.vgsbyhhkjnew.bean;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
public class BaseStarAdapter implements JsonSerializer<BaseStar>, JsonDeserializer<Void> {

    @Override
    public Void deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            // 指定包名+类名
            String thePackage = "com.hhkj.vgsbyhhkjnew.bean.";
            if (type.equals("0")) {
                return context.deserialize(element, Class.forName(thePackage + "BaseStar"));
            } else if (type.equals("1")) {
                return context.deserialize(element, Class.forName(thePackage + "Line"));
            } else if (type.equals("2")) {
                return context.deserialize(element, Class.forName(thePackage + "PolygonGeometry"));
            } else if (type.equals("3")) {
                return context.deserialize(element, Class.forName(thePackage + "RectGeometry"));
            }else if (type.equals("4")) {
                return context.deserialize(element, Class.forName(thePackage + "EllipseGeometry"));
            }else if (type.equals("5")) {
                return context.deserialize(element, Class.forName(thePackage + "TextGeometry"));
            }
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
        return null;
    }

    @Override
    public JsonElement serialize(BaseStar src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }
}
