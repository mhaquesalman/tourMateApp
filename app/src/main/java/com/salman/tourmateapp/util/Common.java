package com.salman.tourmateapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy K:mm a");
        String formatted = sdf.format(date);
        return formatted;
    }
}
