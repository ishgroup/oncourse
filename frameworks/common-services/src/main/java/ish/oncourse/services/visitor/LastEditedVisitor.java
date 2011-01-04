package ish.oncourse.services.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.visitor.BaseVisitor;

import java.util.Date;

import org.apache.tapestry5.ioc.Messages;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;


public class LastEditedVisitor extends BaseVisitor<String> {

	private Messages messages;

	public LastEditedVisitor(Messages messages) {
		super();
		this.messages = messages;
	}

	@Override
	public String visitWebNode(WebNode node) {
		String[] lastEdited = getLastEdited(node.getModified());
		return messages.format("lastedited", lastEdited[0], lastEdited[1]);
	}

	@Override
	public String visitWebContent(WebContent block) {
		String[] lastEdited = getLastEdited(block.getModified());
		return messages.format("lastedited", lastEdited[0], lastEdited[1]);
	}

	@Override
	public String visitWebNodeType(WebNodeType type) {
		String[] lastEdited = getLastEdited(type.getModified());
		return messages.format("lastedited", lastEdited[0], lastEdited[1]);
	}

	String[] getLastEdited(Date modifiedDate) {

		DateTime start = new DateTime(modifiedDate);
		DateTime today = new DateTime();
		Period p = new Period(start, today, PeriodType.forFields(
				new DurationFieldType[] { 
						DurationFieldType.days(), 
						DurationFieldType.hours(),
						DurationFieldType.minutes(),
						DurationFieldType.seconds()
				}));

		long passedTime;
		String passedDesc;

		if (p.getDays() > 0) {
			passedTime = p.getDays();
			passedDesc = passedTime > 1 ? "days" : "day";
		} else if (p.getHours() > 0) {
			passedTime = p.getHours();
			passedDesc = passedTime > 1 ? "hours" : "hour";
		} else if (p.getMinutes() > 0) {
			passedTime = p.getMinutes();
			passedDesc = passedTime > 1 ? "minutes" : "minute";
		} else {
			passedTime = p.getSeconds();
			passedDesc = passedTime > 1 ? "seconds" : "second";
		}

		return new String[] { String.valueOf(passedTime), passedDesc };
	}

}
