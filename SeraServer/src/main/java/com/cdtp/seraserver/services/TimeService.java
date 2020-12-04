package com.cdtp.seraserver.services;

import java.sql.Timestamp;

public class TimeService {

    public static long getTime(){
        return (new Timestamp(System.currentTimeMillis())).getTime();
    }
}
