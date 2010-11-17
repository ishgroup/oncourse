package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;

import java.util.Date;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageInfo {

	@Parameter
	@Property
	private WebNode node;

	@Inject
	private Messages messages;

	@Property
	@Component
	private Zone inspectorZone;

	public String getPageStatus() {
		String key = "page.status."
				+ ((node.isPublished()) ? "published" : "nopublished");
		return messages.get(key);
	}

	public String getLastEdited() {

		long passedSeconds = ((new Date()).getTime() - node.getModified()
				.getTime()) / 1000;

		String message;

		if (passedSeconds < 60) {

			message = messages.format("page.lastedited", passedSeconds,
					passedSeconds > 1 ? "seconds" : "second");

		} else if (passedSeconds < 3600) {
			long passedMinutes = passedSeconds / 60;
			
			message = messages.format("page.lastedited", passedMinutes,
					passedMinutes > 1 ? "minutes" : "minute");

		} else if (passedSeconds < 3600 * 60 * 24) {
			long passedHours = passedSeconds / (3600 * 60);
			
			message = messages.format("page.lastedited", passedHours,
					passedHours > 1 ? "hours" : "hour");

		} else {
			long passedDays = passedSeconds / (3600 * 60 * 24);
			
			message = messages.format("page.lastedited", passedDays,
					passedDays > 1 ? "days" : "day");
		}

		return message;
	}
}
