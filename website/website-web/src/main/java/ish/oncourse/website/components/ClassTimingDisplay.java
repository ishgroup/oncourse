package ish.oncourse.website.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.utils.TimestampUtilities;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ClassTimingDisplay {
	@Parameter
	@Property
	private CourseClass courseClass;

	@Property
	private String stringListItem;

	public List<String> getWeekdays() {
		return TimestampUtilities.DaysOfWorkingWeekNames;
	}

	public List<String> getWeekendDays() {
		return TimestampUtilities.DaysOfWeekendNames;
	}

	public String getWeekdayListItemClass() {
		return "timing-weekday timing-"
				+ this.stringListItem
				+ " match-8 match-"
				+ (TimestampUtilities.DaysOfWorkingWeekNames
						.indexOf(stringListItem) + 1)
				+ " timing-"
				+ cssClassSuffix(courseClass.getDaysOfWeek(), false, false/*
																		 * TODO
																		 * implement
																		 * methods
																		 * from
																		 * AbstractSearchListingView
																		 * :
																		 * isSearchingWeekday
																		 * (),
																		 * isSearchingWeekend
																		 * ()
																		 */,
						this.stringListItem);
	}

	public String getWeekendListItemClass() {
		return "timing-weekend timing-"
				+ stringListItem
				+ " match-9 match-"
				+ (TimestampUtilities.DaysOfWeekendNames
						.indexOf(stringListItem) + 6)
				+ " timing-"
				+ cssClassSuffix(courseClass.getDaysOfWeek(), false, false/*
																		 * TODO
																		 * implement
																		 * methods
																		 * from
																		 * AbstractSearchListingView
																		 * :
																		 * isSearchingWeekend
																		 * (),
																		 * isSearchingWeekday
																		 * ()
																		 */,
						this.stringListItem);
	}

	public String getDayListItemLabel() {
		return this.stringListItem.substring(0, 2);
	}

	private static String cssClassSuffix(boolean eoFlag,
			boolean primeSearchCriteria, boolean alternateSearchCriteria) {
		String suffix = null;
		if (!eoFlag) {
			suffix = "no";
		} else if (primeSearchCriteria || alternateSearchCriteria) {
			if (!primeSearchCriteria) {
				if (alternateSearchCriteria) {
					suffix = "fail";
				} else {
					suffix = "no";
				}
			} else {
				suffix = "match";
			}
		} else {
			suffix = "yes";
		}
		return suffix;
	}

	private static String cssClassSuffix(Collection<String> items,
			boolean primeSearchCriteria, boolean alternateSearchCriteria,
			String item) {
		return cssClassSuffix(items.contains(item), primeSearchCriteria,
				alternateSearchCriteria);
	}

	public String getDaytimeClass() {
		boolean flag = Boolean.TRUE.equals(courseClass.isDaytime());
		return "timing-daytime match-10 timing-daytime-"
				+ cssClassSuffix(flag, false, false/*
													 * TODO implement methods
													 * from
													 * AbstractSearchListingView
													 * : isSearchingDayTime(),
													 * isSearchingEveningTime()
													 */);
	}

	public String getEveningClass() {
		boolean flag = Boolean.TRUE.equals(courseClass.isEvening());
		return "timing-evening match-10 timing-evening-"
				+ cssClassSuffix(flag, false, false/*
													 * TODO implement methods
													 * from
													 * AbstractSearchListingView
													 * :
													 * isSearchingEveningTime(),
													 * isSearchingDayTime()
													 */);
	}
}
