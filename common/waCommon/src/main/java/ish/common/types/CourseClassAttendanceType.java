package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @author vdavidovich
 */
public enum CourseClassAttendanceType implements DisplayableExtendedEnumeration<Integer> {
	OUA_AND_NOT_HIGHER_DEGREE_REASEARCH_STUDENT_USE(0, "For OUA and non-higher degree research student use only",
		"For OUA and non-higher degree research student use only"),
	FULL_TIME_ATTENDANCE(1, "Full-time attendance",
		"Full-time attendance for: higher degree research student , higher education course completions for all full time students and VET student"),
	PART_TIME_ATTENDANCE(2, "Part-time attendance",
		"Part-time attendance for: higher degree research student, higher education course completions for all part time students and VET student"),
	NO_INFORMATION(9, "No information", "No information");

	private String displayValue;
	private String description;
	private int value;

	private CourseClassAttendanceType(int value, String displayValue, String description) {
		this.displayValue = displayValue;
		this.description = description;
		this.value = value;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	@Override
	public String getDisplayName() {
		return displayValue;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
