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
		String[] lastEdited = getLastEdited(node.getModified());
		return messages.format("lastedited", lastEdited[0], lastEdited[1]);
	}

	public String visitWebContent(WebContent block) {
		String[] lastEdited = getLastEdited(block.getModified());
		return messages.format("lastedited", lastEdited[0], lastEdited[1]);
	}

	public String visitWebNodeType(WebNodeType type) {
		String[] lastEdited = getLastEdited(type.getModified());
		return messages.format("lastedited", lastEdited[0], lastEdited[1]);
	}

	String[] getLastEdited(Date modifiedDate) {

		Date today = new Date();
		
		long passedSeconds = (today.getTime() - modifiedDate.getTime()) / 1000;
		
		long passedTime;
		String passedDesc;

		if (passedSeconds < 60) {
			passedTime = passedSeconds;
			passedDesc = passedTime > 1 ? "seconds" : "second";
		} else if (passedSeconds < 3600) {
			passedTime = passedSeconds / 60;
			passedDesc = passedTime > 1 ? "minutes" : "minute";
		} else if (passedSeconds < 3600 * 24) {
			passedTime = passedSeconds / 3600;
			passedDesc = passedTime > 1 ? "hours" : "hour";
		} else {
			passedTime = passedSeconds / (3600 * 24);
			passedDesc = passedTime > 1 ? "days" : "day";
		}

		return new String[] { String.valueOf(passedTime), passedDesc };
	}

}
