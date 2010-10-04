package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;

import java.util.List;

import org.apache.tapestry5.annotations.Property;

/**
 * Based on DynamicCookieView
 * 
 * @author ksenia
 * 
 */
public class ShortList {

	@Property
	private List<CourseClass> ordered;

	public Integer getOrderedCount() {
		if (ordered == null) {
			return 0;
		}
		return ordered.size();
	}

	public String getSelectedMessage() {
		return "course" + (ordered == null || ordered.size() != 1 ? "s" : "")
				+ " selected";
	}

	public boolean isHasObjects() {
		return ordered != null && !ordered.isEmpty();
	}
}
