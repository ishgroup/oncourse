/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.sms;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ClicatelApiUrlBuilder {
    
    private static final String EncodeType = "UTF-8";
    private  String sessionId;
    private  String to;
    private  String from;
    private  String text;
    private  String clicatelAddress;

    private ClicatelApiUrlBuilder(){}
    
    public static ClicatelApiUrlBuilder valueOf(String sessionId, String to, String from, String text,  String clicatelAddress) {
        ClicatelApiUrlBuilder builder = new ClicatelApiUrlBuilder();
    
        builder.sessionId = sessionId;
        builder.to = to;
        builder.from = from;
        builder.text = text;
        builder.clicatelAddress = clicatelAddress;
        return builder;
    }
   
   
   public URL build() throws MalformedURLException, UnsupportedEncodingException {
       String toEncoded = to.replaceAll("\\D", "");

       if (toEncoded.startsWith("0")) {
           toEncoded = toEncoded.replaceFirst("0", "+61");
       }

       String fromEncoded = URLEncoder.encode(from.trim().replaceAll("(\\s)+", "."), EncodeType);
       String textEncoded = URLEncoder.encode(text.trim(), EncodeType);

       return new URL(String.format("%ssendmsg?session_id=%s&to=%s&from=%s&text=%s", clicatelAddress, sessionId, toEncoded,fromEncoded, textEncoded));

   }
}
