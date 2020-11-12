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
package ish.util;

import org.apache.commons.lang3.time.DateUtils;

import java.util.*;

/**
 */
public class TimeZoneUtil {

	public static final String DEFAULT_SERVER_TIME_ZONE = "Australia/Sydney";

	/**
	 * list, This holds our time zone list. It is generated only once per application run.
	 */
	static private Map<String, String> choices = null;

	/**
	 * generateList This is called to generate the times zone list.
	 */
	private static Map<String, String> generateChoices() {
		// Get our starting list.
		List<String> startList = new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
		// Loop through the available time zones, checking for possible deletions.
		for (Iterator<String> iter = startList.iterator(); iter.hasNext();) {
			String current = iter.next();
			if (shouldDelete(current, startList)) {
				iter.remove();
			}
		}

		Collections.sort(startList);

		// Move anything starting with the letter "U" to the top of the list.
		List<String> uList = new ArrayList<>();
		for (Iterator<String> iter = startList.iterator(); iter.hasNext();) {
			String current = iter.next();
			if (current.toLowerCase().startsWith("australia")) {
				uList.add(current);
				iter.remove();
			}
		}
		for (int i = (uList.size() - 1); i >= 0; --i) {
			startList.add(0, uList.get(i));
		}

		Map<String, String> timezones = new LinkedHashMap<>();
		for (String zone : startList) {
			timezones.put(zone, zone);
		}

		return timezones;
	}

	/**
	 * get Call this to get the cleaned list of time zones.
	 */
	static public Map<String, String> getChoices() {
		if (choices == null) {
			choices = generateChoices();
		}
		return choices;
	}

	/**
	 * shouldDelete This is called to determine if a time zone should be deleted.
	 */
	private static boolean shouldDelete(String zoneString, List<String> remainingList) {
		List<String> copyOfRemainingList = new ArrayList<>(remainingList);

		// Define a list of duplicate zones that you want to remove.
		String[] deleteZones = new String[] {
				"Etc/Greenwich",
				"Etc/UCT",
				"Etc/UTC",
				"Etc/Universal",
				"Etc/Zulu",
				"Zulu",
				"Etc/GMT-0",
				"Etc/GMT0",
				"GMT0",
				"Greenwich",
				"Universal" };

		// Define a list of zone prefixes that you wish to keep.
		String[] protectedPrefixes = new String[] { "UTC", "Australia/" };
		// Get the time zone instance from the zone string.
		TimeZone zone = TimeZone.getTimeZone(zoneString);

		// Calculate how many copies remain in the list.
		int copies = 0;
		for (String remainingString : copyOfRemainingList) {
			TimeZone remainingZone = TimeZone.getTimeZone(remainingString);
			if (remainingZone.hasSameRules(zone)) {
				++copies;
			}
		}

		// Remove all zones listed in deleteZones that are duplicates.
		for (String delete : deleteZones) {
			if (zoneString.equalsIgnoreCase(delete) && copies > 1) {
				return true;
			}
		}

		// Keep all protected prefixes.
		for (String protect : protectedPrefixes) {
			if (zoneString.toLowerCase().startsWith(protect.toLowerCase())) {
				return false;
			}
		}

		// Remove all three letter zones that are duplicates.
		return zoneString.length() == 3 && copies > 1;

	}

	public static Date adjustDateForTimeZone(TimeZone from, TimeZone to, Date date) {
		int adjustment = to.getOffset(date.getTime()) - from.getOffset(date.getTime());

		return DateUtils.addMilliseconds(date, adjustment);
	}

	public static TimeZone getTimeZone(String timeZoneId) {
		return timeZoneId != null ? TimeZone.getTimeZone(timeZoneId) : TimeZone.getTimeZone(DEFAULT_SERVER_TIME_ZONE);
	}
}
