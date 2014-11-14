package ish.oncourse.services.search;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.services.search.SearchParamsParser.*;

/**
 * Utility class s to evaluate course class 
 * @author vdavidovich
 *
 */
public class CourseClassUtils {
	/**
	 * Haversine formula: R = earth's radius (mean radius = 6,371km) dLat =
	 * lat2 - lat1 dLon = lon2 - lon1 a = (sin(dLat/2))^2 +
	 * cos(lat1)*cos(lat2)*(sin(dLat/2))^2 c = 2*atan2(sqrt(a), sqrt(1-a)) d =
	 * R*c
	 * 
	 * @param nearLatitude
	 * @param nearLongitude
	 * @return
	 */
	public static double evaluateDistanceForCourseClassSiteAndLocation(final CourseClass courseClass, final Double nearLatitude,
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

    public static boolean isCourseClassMatchBy(CourseClass courseClass, String postcode, Double km , Double latitude, Double longitude)
    {
        Room room = courseClass.getRoom();
        if (room != null && room.getSite() != null) {
            Site site = room.getSite();
            if (site.getIsWebVisible() && site.isHasCoordinates() && StringUtils.trimToNull(site.getPostcode()) != null)
            {
                if (postcode != null && postcode.equals(site.getPostcode()))
                {
                        return true;
                }

                if (km != null && latitude != null && longitude != null)
                {
                    double distance = CourseClassUtils.evaluateDistanceForCourseClassSiteAndLocation(courseClass, latitude, longitude);
                    if (distance <= km) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    private static float focusMatchForNear(CourseClass courseClass, Double nearLatitude, Double nearLongitude, Double distance) {
		float result = 0.0f;
		double classDistance = evaluateDistanceForCourseClassSiteAndLocation(courseClass, nearLatitude, nearLongitude);
		if (classDistance > -1d) {
			if (classDistance >= 5d * distance) {
				result = 0f;
			} else if (classDistance <= distance) {
				result = 1f;
			} else {
				result = 1 - ((float) classDistance - distance.floatValue()) / (4f * distance.floatValue());
			}
		}
		return result;
	}
	
	private static float focusMatchForPrice(final CourseClass courseClass, Float price) {
		float result = 0.0f;
		if (courseClass.hasFeeIncTax()) {
            if (courseClass.getFeeIncGst().floatValue() <= price){
                result =  1.0f;
            } else if (courseClass.getFeeIncGst().floatValue() > price) {
				result = 0.75f - (courseClass.getFeeIncGst().floatValue() - price) / price * 0.25f;
				if (result < 0.25f) {
					result = 0.25f;
				}
			}

		}
		return result;
	}

    /**
     * Returns 1 if the class is self paced or time of the class matchs to the <source>time</source> param.
     * @param time - accepts 'evening' or 'daytime' value. 'evening' means time between 18:00 and 6:00,
     *             daytime means time between 6:00 and 18:00
     */
	public static float focusMatchForTime(final CourseClass courseClass, final String time) {
		float result = 0.0f;

		boolean isEvening = courseClass.isEvening();
		boolean isDaytime = courseClass.isDaytime();
		if (isEvening && isDaytime ||
                isDaytime && PARAM_VALUE_daytime.equalsIgnoreCase(time) ||
                isEvening && PARAM_VALUE_evening.equalsIgnoreCase(time) ||
                courseClass.getIsDistantLearningCourse()) {
			result = 1.0f;
		}
		return result;

	}
	
	private static float focusMatchForDays(final CourseClass courseClass, final String searchDay) {
		float result = 0.0f;

        if (courseClass.getIsDistantLearningCourse())
            return 1.0f;
		if (courseClass.getDaysOfWeek() != null) {
			List<String> uniqueDays = new ArrayList<>();
			uniqueDays.addAll(courseClass.getDaysOfWeek());
			for (String day : uniqueDays) {
                if (PARAM_VALUE_weekday.equalsIgnoreCase(searchDay) && TimestampUtilities.DaysOfWorkingWeekNamesLowerCase.contains(day.toLowerCase()))
                {
                    result = 1.0f;
                    break;
                }
                if (PARAM_VALUE_weekend.equalsIgnoreCase(searchDay) && TimestampUtilities.DaysOfWeekendNamesLowerCase.contains(day.toLowerCase()))
                {
                    result = 1.0f;
                    break;
                }
                if (day.equalsIgnoreCase(searchDay))
                {
                    result = 1.0f;
                    break;
                }
			}
		}
		return result;
	}
	
	public static float focusMatchForClass(CourseClass courseClass,
		 SearchParams searchParams) {
		float bestFocusMatch = -1.0f;

		if (searchParams != null) {

			float daysMatch = 1.0f;
			if (searchParams.getDay() != null) {
				daysMatch = CourseClassUtils.focusMatchForDays(courseClass, searchParams.getDay());
			}

			float timeMatch = 1.0f;
			if (searchParams.getTime() != null) {
				timeMatch = CourseClassUtils.focusMatchForTime(courseClass, searchParams.getTime());
			}

			float priceMatch = 1.0f;
			if (searchParams.getPrice() != null) {
			    priceMatch = CourseClassUtils.focusMatchForPrice(courseClass, searchParams.getPrice().floatValue());
			}

			float nearMatch = 1.0f;
            List<Suburb> suburbs = searchParams.getSuburbs();
            Suburb suburb = suburbs.isEmpty() ? null:suburbs.get(0);
			if (suburb != null) {
				nearMatch = CourseClassUtils.focusMatchForNear(courseClass, suburb.getLatitude(), suburb.getLongitude(),suburb.getDistance());
			}

            float afterMatch = 1.0f;
            if (searchParams.getAfter() != null) {
                if (courseClass.getStartDate().after(searchParams.getAfter())) {
                    afterMatch = 1.0f;
                } else {
                    int days =  (int) Math.ceil( (searchParams.getAfter().getTime() - courseClass.getStartDate().getTime())/ (1000 * 60 * 60 * 24));
                    //#16568 .check for 0 required to avoid division by zero on courses render.
                    //#17083. but even if the date distance is less then 1 day we should show this class as partially matched
                    if (days <= 1) {
                    	return 0.5f;
                    }
                    afterMatch = 1/days;
                }
            }

            float beforeMatch = 1.0f;
            if (searchParams.getBefore() != null) {
                if (courseClass.getStartDate() == null) {
                    beforeMatch = 0f;
                } else if (courseClass.getStartDate().before(searchParams.getBefore()))
                    beforeMatch = 1.0f;
                else {
                    int days =  (int) Math.ceil( (courseClass.getStartDate().getTime() - searchParams.getBefore().getTime())/ (1000 * 60 * 60 * 24));
                    if (days <= 1) {
                    	return 0.5f;
                    }
                    beforeMatch = 1/days;
                }
            }



            return daysMatch * timeMatch * priceMatch * nearMatch * afterMatch * beforeMatch;
		}

		return bestFocusMatch;

	}
}
