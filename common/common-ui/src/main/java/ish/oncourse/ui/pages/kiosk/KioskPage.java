package ish.oncourse.ui.pages.kiosk;

import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static ish.oncourse.model.auto._Session.ROOM;

public class KioskPage {

    @Inject
    private Request request;

    @Inject
    private ICayenneService cayenneService;

    @Property
    private Site site;

    @Property
    private Room room;

    @Property
    private List<Session> sessions;

    @Property
    private Session session;

    @SetupRender
    public void beforeRender() {
        site = (ish.oncourse.model.Site) request.getAttribute(Site.class.getSimpleName());
        room = (ish.oncourse.model.Room) request.getAttribute(Room.class.getSimpleName());

        TimeZone timeZone = TimeZone.getTimeZone(site.getCollege().getTimeZone());

        Date start = Calendar.getInstance(timeZone).getTime();
        Date end = DateUtils.addDays(start, 1);

        if (room != null) {
            sessions = ObjectSelect.query(Session.class).where(ROOM.eq(room))
                    .and(Session.END_DATE.gte(start))
                    .and(Session.END_DATE.lt(end))
                    .orderBy(Session.END_DATE.asc(), Session.COURSE_CLASS.asc()).limit(20).select(cayenneService.sharedContext());
        } else {
            sessions = ObjectSelect.query(Session.class).where(ROOM.dot(Room.SITE).eq(site))
                    .and(Session.END_DATE.gte(start))
                    .and(Session.END_DATE.lt(end))
                    .orderBy(Session.END_DATE.asc(), Session.COURSE_CLASS.asc()).limit(20).select(cayenneService.sharedContext());
        }
    }
}
