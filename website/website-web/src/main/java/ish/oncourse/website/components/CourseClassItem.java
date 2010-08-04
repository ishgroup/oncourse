package ish.oncourse.website.components;

import java.text.DecimalFormat;
import java.text.Format;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.TutorRole;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class CourseClassItem {
	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private boolean excludePrice;

	@Parameter
	@Property
	private boolean excludeShortlistControl;

	@Property
	private TutorRole tutorRole;

	@Property
	private int index;

	private Format hoursFormat;

	@SetupRender
	public void beforeRender() {
		this.hoursFormat = new DecimalFormat("0.#");
	}

	public boolean isHasSite() {
		return courseClass.getRoom() != null
				&& courseClass.getRoom().getSite() != null;
	}

	public boolean isHasTutorRoles() {
		return courseClass.getTutorRoles().size() > 0;
	}

	public boolean isHasLinkToLocation() {
		return false; // ~linkToLocationsMap and isListPage
	}

	public boolean isHasSiteSuburb() {
		return courseClass.getRoom() != null
				&& courseClass.getRoom().getSite() != null
				&& courseClass.getRoom().getSite().getSuburb() != null
				&& !"".equals(courseClass.getRoom().getSite().getSuburb());
	}

	public boolean isTutorPortal() {
		return false;
	}

	public String getSessionForClass() {
		return (isTutorPortal()) ? "" : "hidden";
	}

	public Format getHoursFormat() {
		return this.hoursFormat;
	}

	public boolean isHasSiteName() {
		return isHasSite() && courseClass.getRoom().getSite().getName() != null
				&& !"online".equals(courseClass.getRoom().getSite().getName());
	}
}
