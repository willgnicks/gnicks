package com.gnicks.designs.pc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintUtil {

    public static void inform(String role, String threadName,Object...msg){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(getDate());
        stringBuffer.append("]");
        stringBuffer.append("  ");
        stringBuffer.append(role);
        stringBuffer.append("  ");
        stringBuffer.append(threadName);
        for (Object obj: msg ) {
            stringBuffer.append("  ");
            stringBuffer.append(obj.toString());
        }
        System.out.println(new String(stringBuffer));
    }

    private static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static long timeCost(long start){
        return System.currentTimeMillis() - start;
    }

}
