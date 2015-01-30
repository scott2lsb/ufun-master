
package com.shengshi.http.entities;

import com.google.gson.stream.JsonReader;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.AppException.ExceptionStatus;

import java.io.IOException;


public class ModuleEntity extends BaseEntity {

    private String groupId;
    private String moduleName;
    private String iconUrl;
    private String point;
    private String description;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void readFromJson(JsonReader reader) throws AppException {
        try {
            String tag = null;
            reader.beginObject();
            while (reader.hasNext()) {
                tag = reader.nextName();
                if ("moduleName".equalsIgnoreCase(tag)) {
                    moduleName = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.ParseJsonException, e.getMessage());
        }
    }

}
