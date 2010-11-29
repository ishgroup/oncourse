package ish.oncourse.services.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.visitor.BaseVisitor;

import java.util.Date;

import org.apache.tapestry5.ioc.Messages;

public class LastEditedVisitor extends BaseVisitor<String> {

	private Messages messages;

	public LastEditedVisitor(Messages messages) {
		super();
		this.messages = messages;
	}

	public String visitWebNode(WebNode node) {
		return getLastEdited(node.getModified());
	}
	
	public String visitWebContent(WebContent block) {
		return getLastEdited(block.getModified());
	}
	
	public String visitWebNodeType(WebNodeType type) {
		return getLastEdited(type.getModified());
	}

	private String getLastEdited(Date modifiedDate) {

		long passedSeconds = ((new Date()).getTime() - modifiedDate.getTime()) / 1000;

		String message;

		if (passedSeconds < 60) {
			message = messages.format("lastedited", passedSeconds,
					passedSeconds > 1 ? "seconds" : "second");

		} else if (passedSeconds < 3600) {
			long passedMinutes = passedSeconds / 60;

			message = messages.format("lastedited", passedMinutes,
					passedMinutes > 1 ? "minutes" : "minute");

		} else if (passedSeconds < 3600 * 60 * 24) {
			long passedHours = passedSeconds / (3600 * 60);

			message = messages.format("lastedited", passedHours,
					passedHours > 1 ? "hours" : "hour");

		} else {
			long passedDays = passedSeconds / (3600 * 60 * 24);

			message = messages.format("lastedited", passedDays,
					passedDays > 1 ? "days" : "day");
		}

		return message;
	}

}
