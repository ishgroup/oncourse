/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.sms;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

public class ClicatelApiUrlBuilderTest {

    
    @Test
    public void testIsStudentLoggedIn() throws MalformedURLException, UnsupportedEncodingException {
        
        String urlString = ClicatelApiUrlBuilder.valueOf("123", "0612343563", "0612343563", "Hey Jude", "https://api.clickatell.com/http/").build().toString();
        assertEquals("https://api.clickatell.com/http/sendmsg?session_id=123&to=+61612343563&from=0612343563&text=Hey+Jude",  urlString);
        
        urlString = ClicatelApiUrlBuilder.valueOf("123", "0612343563", "Any Address", "Hey Jude", "https://api.clickatell.com/http/").build().toString();
        assertEquals("https://api.clickatell.com/http/sendmsg?session_id=123&to=+61612343563&from=Any.Address&text=Hey+Jude",  urlString);

        urlString = ClicatelApiUrlBuilder.valueOf("123", "0612343563", "Any    Address  with    white spaces", "Hey Jude", "https://api.clickatell.com/http/").build().toString();
        assertEquals("https://api.clickatell.com/http/sendmsg?session_id=123&to=+61612343563&from=Any.Address.with.white.spaces&text=Hey+Jude",  urlString);

        urlString = ClicatelApiUrlBuilder.valueOf("123", "0612343563", "Any Address with brake \n\r lines", "Hey Jude", "https://api.clickatell.com/http/").build().toString();
        assertEquals("https://api.clickatell.com/http/sendmsg?session_id=123&to=+61612343563&from=Any.Address.with.brake.lines&text=Hey+Jude",  urlString);

        urlString = ClicatelApiUrlBuilder.valueOf("123", "0612343563", "\ttab in front", "Hey Jude", "https://api.clickatell.com/http/").build().toString();
        assertEquals("https://api.clickatell.com/http/sendmsg?session_id=123&to=+61612343563&from=tab.in.front&text=Hey+Jude",  urlString);


    }
}
