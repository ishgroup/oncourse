package ish.oncourse.ui.pages.kiosk;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.exp.Expression;
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
    @Property
    private Request request;

    @Inject
    private IRichtextConverter textileConverter;

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
        Date end = DateUtils.truncate(DateUtils.addDays(start, 1), Calendar.DAY_OF_MONTH);
        Expression courseClassExpression = Session.COURSE_CLASS.dot(CourseClass.CANCELLED).eq(Boolean.FALSE)
                .andExp(Session.END_DATE.gte(start))
                .andExp(Session.END_DATE.lt(end));

        if (room != null) {
            sessions = ObjectSelect.query(Session.class).where(ROOM.eq(room))
                    .and(courseClassExpression)
                    .orderBy(Session.START_DATE.asc()).limit(20).select(cayenneService.sharedContext());
        } else {
            sessions = ObjectSelect.query(Session.class).where(ROOM.dot(Room.SITE).eq(site))
                    .and(courseClassExpression)
                    .orderBy(Session.START_DATE.asc()).limit(20).select(cayenneService.sharedContext());
        }
    }

    public String getSpecialInstructions() {
         return textileConverter.convertCustomText(site.getSpecialInstructions(), new ValidationErrors());
    }
}
