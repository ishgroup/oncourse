package ish.oncourse.model;

import ish.oncourse.common.field.*;
import ish.oncourse.model.auto._WaitingList;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.SecurityUtil;

import static ish.oncourse.common.field.PropertyGetSetFactory.GET;
import static ish.oncourse.common.field.PropertyGetSetFactory.SET;

@Type(value = ContextType.WAITING_LIST)
public class WaitingList extends _WaitingList implements Queueable {
	
	private static final long serialVersionUID = 8659761513629004303L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
	
	@Override
	protected void onPrePersist() {
		if (getPotentialStudents() == null) {
			setPotentialStudents(1);
		}
	}
	
	@Override
	@Property(value = FieldProperty.CUSTOM_FIELD_WAITING_LIST, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, WaitingListCustomField.class);
	}
	
	@Property(value = FieldProperty.STUDENTS_COUNT, type = SET, params = {Integer.class})
	@Override
	public void setPotentialStudents(Integer potentialStudents) {
		super.setPotentialStudents(potentialStudents);
	}

	@Property(value = FieldProperty.STUDENTS_COUNT, type = GET, params = {})
	@Override
	public Integer getPotentialStudents() {
		return super.getPotentialStudents();
	}

	@Property(value = FieldProperty.DETAIL, type = SET, params = {String.class})
	@Override
	public void setDetail(String detail) {
		super.setDetail(detail);
	}

	@Property(value = FieldProperty.DETAIL, type = GET, params = {})
	@Override
	public String getDetail() {
		return super.getDetail();
	}
}
