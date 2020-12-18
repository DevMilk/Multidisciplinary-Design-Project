package com.cdtp.seraserver.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GreenHouse {
    private Map<String,String> values;
    private String ip;
    private String name;
    private Boolean isDown;
    private long nextNotificationTime;
    public GreenHouse(Map<String, String> values, String ip) {
        this.values = values;
        this.ip = ip;
    }

    public GreenHouse() {}

    public GreenHouse(String ip){
        this.ip = ip;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public void changeValue(String valueName,String value){
        values.put(valueName,value);
    }

    public void updateNextNotificationTime(int millisecond){
        nextNotificationTime += millisecond;
    }
}
