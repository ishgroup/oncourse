package ish.oncourse.services.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.visitor.BaseVisitor;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Date;


public class LastEditedVisitor extends BaseVisitor<String> {

	@Override
	public String visitWebNode(WebNode node) {
		return getLastEditedMessage(node.getModified());
	}

	@Override
	public String visitWebContent(WebContent block) {
		return getLastEditedMessage(block.getModified());
	}

	@Override
	public String visitWebNodeType(WebNodeType type) {
		return getLastEditedMessage(type.getModified());
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

	private String getLastEditedMessage(Date modifiedDate) {
		String[] lastEdited=getLastEdited(modifiedDate);
		return String.format("Last edited %s %s ago", lastEdited[0], lastEdited[1]);
	}

}
