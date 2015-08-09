package ish.oncourse.textile.pages;

import org.junit.Test;

import java.util.regex.Matcher;
import static org.junit.Assert.*;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TextileLocationTest {

    @Test
    public void testNameParser() {
//        ** "Sydney":sydney-2000/100
//
//                ** "Newcastle":newcastle-2100/300
        Matcher matcher = TextileLocation.NAME_PATTERN.matcher("Sydney:sydney-2000/100");
        //matcher = TextileLocation.NAME_PATTERN.matcher("Sydney:sydney-2000");
        assertTrue(matcher.find());

        assertEquals("Sydney", matcher.group("name"));
        assertEquals("sydney", matcher.group("subrb"));
        assertEquals("2000", matcher.group("post"));
        assertEquals("100", matcher.group("dist"));
    }

}
