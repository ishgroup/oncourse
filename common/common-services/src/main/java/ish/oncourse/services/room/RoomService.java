package ish.oncourse.services.room;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class RoomService implements IRoomService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@SuppressWarnings("unchecked")
	public Room getRoom(String searchProperty, Object value) {
		SelectQuery q = new SelectQuery(Room.class);
		q.andQualifier(getSiteQualifier());
		if (searchProperty != null) {
			q.andQualifier(ExpressionFactory.matchExp(searchProperty, value));
		}
		List<Room> result = cayenneService.sharedContext().performQuery(q);
		return !result.isEmpty() ? result.get(0) : null;
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Room.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()).andExp(
				ExpressionFactory.matchExp(Room.SITE_PROPERTY + "." + Site.IS_WEB_VISIBLE_PROPERTY, true));
	}

}
