package ish.oncourse.model;

import ish.oncourse.model.auto._DiscountCourseClass;

public class DiscountCourseClass extends _DiscountCourseClass implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(DISCOUNT_ID_PK_COLUMN) : null;
	}

	public College getCollege() {
		return getCourseClass().getCollege();
	}
	
}
