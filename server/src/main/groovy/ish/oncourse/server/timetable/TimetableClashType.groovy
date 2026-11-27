/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.timetable

/**
 * Kinds of clash found by {@link TimetableClashService}, in the order they are listed for the same session.
 */
enum TimetableClashType {

    /**
     * Two sessions of the same class overlap.
     */
    SESSION,

    /**
     * The room is booked by another class at the same time.
     */
    ROOM,

    /**
     * The tutor is rostered on another session at the same time. Two sessions of the same class which are
     * a SESSION clash are not listed again as a TUTOR clash.
     */
    TUTOR,

    /**
     * The session falls on a holiday the college has entered (an unavailability of the whole business).
     */
    HOLIDAY,

    /**
     * The tutor is unavailable at that time.
     */
    TUTOR_UNAVAILABLE,

    /**
     * The room is unavailable at that time.
     */
    ROOM_UNAVAILABLE,

    /**
     * The site is unavailable at that time.
     */
    SITE_UNAVAILABLE,

    /**
     * The course is unavailable at that time.
     */
    COURSE_UNAVAILABLE
}
