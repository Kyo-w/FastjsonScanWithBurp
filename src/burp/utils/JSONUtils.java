package burp.utils;

import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.util.StringUtils;

public class JSONUtils {

    public static boolean isJSONString(String context){
        if(context == null){
            return false;
        }
        if(!context.startsWith("{") || !context.endsWith("}")){
            return false;
        }
        try{
            JSONObject.parse(context);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean isJSONString(byte[] context){
        String body = getBody(context);
        if(body == null){
            return false;
        }
        if(!body.startsWith("{") || !body.endsWith("}")){
            return false;
        }
        try{
            JSONObject.parse(body);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static String getBody(byte[] body){
        String s = new String(body);
        String[] split = s.split("\r\n\r\n");
        return split[1];
    }
}
