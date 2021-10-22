package com.hhkj.vgsbyhhkjnew;

import java.util.HashMap;

public class Para {
    private String TypeId;
    HashMap<String, String> Properties = new HashMap<String, String>();
    HashMap<String, String> UserProperties = new HashMap<String, String>();

    public String getTypeId() {
        return TypeId;
    }

    public void setTypeId(String typeId) {
        TypeId = typeId;
    }

    public HashMap<String, String> getProperties() {
        return Properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        Properties = properties;
    }

    public HashMap<String, String> getUserProperties() {
        return UserProperties;
    }

    public void setUserProperties(HashMap<String, String> userProperties) {
        UserProperties = userProperties;
    }
}
