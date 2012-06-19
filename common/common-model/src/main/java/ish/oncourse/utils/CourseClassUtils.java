package ish.oncourse.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;

/**
 * Utility class s to evaluate course class 
 * @author vdavidovich
 *
 */
public class CourseClassUtils {
	/**
	 * Haversine formula: R = earth�s radius (mean radius = 6,371km) dLat =
	 * lat2 - lat1 dLon = lon2 - lon1 a = (sin(dLat/2))^2 +
	 * cos(lat1)*cos(lat2)*(sin(dLat/2))^2 c = 2*atan2(sqrt(a), sqrt(1-a)) d =
	 * R*c
	 * 
	 * @param nearLatitude
	 * @param nearLongitude
	 * @return
	 */
	public static final double evaluateDistanceForCourseClassSiteAndLocation(final CourseClass courseClass, final Double nearLatitude, 
		final Double nearLongitude) {
		if (nearLatitude != null && nearLongitude != null && courseClass.getRoom() != null && courseClass.getRoom().getSite() != null
				&&courseClass.getRoom().getSite().getIsWebVisible() && courseClass.getRoom().getSite().isHasCoordinates()) {
			Site site = courseClass.getRoom().getSite();

			double earthRadius = 6371; // km

			double lat1 = Math.toRadians(site.getLatitude().doubleValue());
			double lon1 = Math.toRadians(site.getLongitude().doubleValue());
			double lat2 = Math.toRadians(nearLatitude);
			double lon2 = Math.toRadians(nearLongitude);

			double dLat = lat2 - lat1;
			double dLon = lon2 - lon1;
			double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2)
					* Math.sin(dLon / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

			double distance = earthRadius * c;
			return distance;
			}
		return -1d;
	}
	
	private static float focusMatchForNear(final CourseClass courseClass, final Double nearLatitude, final Double nearLongitude) {
		float result = 0.0f;
		double distance = evaluateDistanceForCourseClassSiteAndLocation(courseClass, nearLatitude, nearLongitude);
		if (distance > -1d) {
			float searchKilometers = 10.0f;
			if (distance >= 5d * searchKilometers) {
				result = 0f;
			} else if (distance <= searchKilometers) {
				result = 1f;
			} else {
				result = 1 - ((float) distance - searchKilometers) / (4f * searchKilometers);
			}
		}
		return result;
	}
	
	private static float focusMatchForPrice(final CourseClass courseClass, final Float price) {
		float result = 0.0f;
		float maxPrice = price;
		if (courseClass.hasFeeIncTax()) {
			if (courseClass.getFeeIncGst().floatValue() > maxPrice) {
				result = 0.75f - (courseClass.getFeeIncGst().floatValue() - maxPrice) / maxPrice * 0.25f;
				if (result < 0.25f) {
					result = 0.25f;
				}
			}
		}
		return result;
	}
	
	private static float focusMatchForTime(final CourseClass courseClass, final String time) {
		float result = 0.0f;

		Integer latestHour = getLatestSessionStartHour(courseClass);
		Integer earliestHour = courseClass.getEarliestSessionStartHour();
		// much discussion about what day and evening mean...
		// current definitions is that any session that starts before 5 pm is
		// daytime, any that starts after 5pm is evening
		boolean isEvening = latestHour != null && latestHour >= 17;
		boolean isDaytime = earliestHour != null && earliestHour < 17;
		if (isEvening && isDaytime || isDaytime && "daytime".equalsIgnoreCase(time) || isEvening
				&& "evening".equalsIgnoreCase(time)) {
			result = 1.0f;
		}
		return result;

	}
	
	private static Integer getLatestSessionStartHour(final CourseClass courseClass) {
		Integer latest = null;
		for (Session session : courseClass.getSessions()) {
			Calendar end = Calendar.getInstance();
			end.setTime(session.getStartDate());
			Integer sessionStartHour = end.get(Calendar.HOUR_OF_DAY);
			if (latest == null || sessionStartHour > latest) {
				latest = sessionStartHour;
			}
		}
		return latest;
	}
	
	private static float focusMatchForDays(final CourseClass courseClass, final String searchDay) {
		float result = 0.0f;
		if (courseClass.getDaysOfWeek() != null) {
			List<String> uniqueDays = new ArrayList<String>();
			uniqueDays.addAll(courseClass.getDaysOfWeek());
			for (String day : uniqueDays) {
				String lowerDay = day.toLowerCase();
				if (TimestampUtilities.DaysOfWeekNamesLowerCase.contains(lowerDay) && lowerDay.equalsIgnoreCase(day)) {
					result = 1.0f;
					break;
				}
				if (TimestampUtilities.DaysOfWorkingWeekNamesLowerCase.contains(lowerDay)
						&& "weekday".equalsIgnoreCase(day)) {
					result = 1.0f;
					break;
				}
				if (TimestampUtilities.DaysOfWeekendNamesLowerCase.contains(lowerDay)
						&& "weekend".equalsIgnoreCase(day)) {
					result = 1.0f;
					break;
				}
			}
		}
		return result;
	}
	
	public static float focusMatchForClass(final CourseClass courseClass, final Double locatonLat, final Double locationLong, 
		final Map<SearchParam, Object> searchParams) {
		float bestFocusMatch = -1.0f;

		if (!searchParams.isEmpty()) {

			float daysMatch = 1.0f;
			if (searchParams.containsKey(SearchParam.day)) {
				daysMatch = CourseClassUtils.focusMatchForDays(courseClass, (String) searchParams.get(SearchParam.day));
			}

			float timeMatch = 1.0f;
			if (searchParams.containsKey(SearchParam.time)) {
				timeMatch = CourseClassUtils.focusMatchForTime(courseClass, (String) searchParams.get(SearchParam.time));
			}

			float priceMatch = 1.0f;
			if (searchParams.containsKey(SearchParam.price)) {
				try {
					Float price = Float.parseFloat((String) searchParams.get(SearchParam.price));
					priceMatch = CourseClassUtils.focusMatchForPrice(courseClass, price);
				} catch (NumberFormatException e) {
				}
			}

			float nearMatch = 1.0f;
			if (locatonLat != null && locationLong != null) {
				nearMatch = CourseClassUtils.focusMatchForNear(courseClass, locatonLat, locationLong);
			}
			
			float result = daysMatch * timeMatch * priceMatch * nearMatch;
			return result;
		}

		return bestFocusMatch;

	}
}
