package ish.oncourse.portal.services.timetable;

import java.util.Date;

/**
 * User: artem
 * Date: 10/14/13
 * Time: 3:37 PM
 */
public class CalendarEvent {
    private Date date;
    private String content;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
