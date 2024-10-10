/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.impl.converter;

import ish.oncourse.aql.impl.DateTimeInterval;
import ish.oncourse.aql.impl.Op;
import ish.oncourse.server.cayenne.Banking;
import ish.util.DateFormatter;
import ish.util.LocalDateUtils;
import org.apache.cayenne.exp.parser.ASTScalar;
import org.apache.cayenne.exp.parser.SimpleNode;

import java.util.TimeZone;

public class LazyBankingComparisonNode extends LazyEntityComparisonNode {

	LazyBankingComparisonNode(Op op) {
		super(op);
	}

	@Override
	protected SimpleNode createNode() {
		var interval = getInterval();
		if(interval == null) {
			return null;
		}
		var start = interval.getStart().toLocalDate();
		var end   = interval.getEnd().toLocalDate();
		if(start.equals(end)) {
			return (SimpleNode) Banking.SETTLEMENT_DATE.eq(start);
		} else {
			return (SimpleNode) Banking.SETTLEMENT_DATE.between(start, end);
		}
	}

	private DateTimeInterval getInterval() {
		if (jjtGetNumChildren() < 2) {
			return null;
		}

		var child = jjtGetChild(1);
		if(child instanceof LazyDateTimeScalar) {
			return ((LazyDateTimeScalar) child).getInterval();
		}

		if(child instanceof ASTScalar) {
			var searchString = ((ASTScalar) this.jjtGetChild(1)).getValue().toString();
			return searchStringToInterval(searchString);
		}

		return null;
	}

	private DateTimeInterval searchStringToInterval(String searchString) {
		if(searchString == null) {
			return null;
		}

		searchString = searchString.replace("%", "");
		var interval = DateTimeInterval.of(searchString);
		if(interval != null) {
			return interval;
		}

		var calendar = DateFormatter.parseDateToCal(searchString, TimeZone.getDefault());
		if(calendar != null) {
			return DateTimeInterval.of(LocalDateUtils.calendarToValue(calendar));
		}

		return null;
	}
}
