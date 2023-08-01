/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Session } from "@api/model";
import { format, getDaysInMonth, parse, setDate, setHours, startOfMonth } from "date-fns";
import { AfternoonIcon, appendTimezone, DD_MMM_YYYY_MINUSED, EveningIcon, HA, MorningIcon } from "ish-ui";
import { CalendarGrouping, CalendarGroupingState, TimetableMonth, TimetableSession } from "../../../model/timetable";
import { NO_ROOM_LABEL, NO_TUTORS_LABEL } from "../constants";

export const getFormattedMonthDays = (startDate: Date) => Array.from(Array(getDaysInMonth(startDate)), (v, i) => i + 1).map(d => ({
  day: setDate(startDate, d),
  sessions: [],
  updated: false,
  tagsUpdated: false
}));

export const getAllMonthsWithSessions = (sessions: TimetableSession[], startDate: Date): TimetableMonth[] => {
  const startDateYear = startDate.getFullYear();
  const startDateMonth = startDate.getMonth();

  const months = [
    {
      month: startOfMonth(startDate),
      days: getFormattedMonthDays(startDate),
      hasSessions: true
    }
  ];

  sessions.forEach((s, index) => {
    s.index = index;

    const sessionStartDate = s.siteTimezone ? appendTimezone(new Date(s.start), s.siteTimezone) : new Date(s.start);

    const sessionStartYear = sessionStartDate.getFullYear();

    const sessionStartMonth = sessionStartDate.getMonth();

    const sessionStartDayIndex = sessionStartDate.getDate() - 1;

    if (
      sessionStartYear === startDateYear
      && sessionStartMonth === startDateMonth
      && months[0].days[sessionStartDayIndex]
    ) {
      months[0].days[sessionStartDayIndex].sessions.push(s);
    } else {
      const sessionStartMonthIndex = months.findIndex(
        m => m.month.getMonth() === sessionStartMonth && m.month.getFullYear() === sessionStartYear
      );

      if (sessionStartMonthIndex !== -1) {
        months[sessionStartMonthIndex].days[sessionStartDayIndex].sessions.push(s);
      } else {
        months.push({
          month: startOfMonth(sessionStartDate),
          days: getFormattedMonthDays(sessionStartDate),
          hasSessions: true
        });

        months[months.length - 1].days[sessionStartDayIndex].sessions.push(s);
      }
    }
  });
  return months;
};

export const getMonthsWithinYear = (sessions: Session[], startDate: Date) => {
  const startDateMonth = startDate.getMonth();

  const months = [
    {
      month: startOfMonth(startDate),
      days: getFormattedMonthDays(startDate)
    }
  ];

  sessions.forEach(s => {
    const sessionStartDate = new Date(s.start);

    if (startDate.getFullYear() !== sessionStartDate.getFullYear()) {
      return;
    }

    const sessionStartMonth = sessionStartDate.getMonth();

    const sessionStartDayIndex = sessionStartDate.getDate() - 1;

    const sessionEndDate = new Date(s.end);

    const sessionEndMonth = sessionEndDate.getMonth();

    const sessionEndDayIndex = sessionStartDate.getDate() - 1;

    if (sessionStartMonth === startDateMonth && months[0].days[sessionStartDayIndex]) {
      months[0].days[sessionStartDayIndex].sessions.push(s);
    }

    if (
      sessionEndMonth === startDateMonth
      && sessionStartDayIndex !== sessionEndDayIndex
      && months[0].days[sessionEndDayIndex]
    ) {
      months[0].days[sessionEndDayIndex].sessions.push(s);
    }

    if (sessionStartMonth > startDateMonth) {
      const monthOffset = sessionStartMonth - startDateMonth;

      if (!months[monthOffset]) {
        months.push({
          month: startOfMonth(sessionStartDate),
          days: getFormattedMonthDays(sessionStartDate)
        });
      }

      if (months[monthOffset].days[sessionStartDayIndex]) {
        months[monthOffset].days[sessionStartDayIndex].sessions.push(s);
      }

      if (sessionStartDayIndex !== sessionEndDayIndex && months[monthOffset].days[sessionEndDayIndex]) {
        months[monthOffset].days[sessionEndDayIndex].sessions.push(s);
      }
    }
  });

  months.forEach(m => {
    m["hasSessions"] = m.days.some(d => d.sessions.length);
  });

  return months;
};

const easing = t => (t < 0.5 ? 16 * t * t * t * t * t : 1 + 16 * --t * t * t * t * t);

export const animateListScroll = (list, scrollTopFinal, scrollTopInitial, animationStartTime, dayNodesObserver) => {
  dayNodesObserver.preventUpdate = true;
  requestAnimationFrame(() => {
    const now = performance.now();
    const elapsed = now - animationStartTime;
    const scrollDelta = scrollTopFinal - scrollTopInitial;
    const easedTime = easing(Math.min(1, elapsed / 500));
    const scrollTop = scrollTopInitial + scrollDelta * easedTime;

    list.scrollTo(scrollTop);

    if (elapsed < 500) {
      animateListScroll(list, scrollTopFinal, scrollTopInitial, animationStartTime, dayNodesObserver);
    } else {
      dayNodesObserver.preventUpdate = false;
    }
  });
};

class DayNodesObserver {
  public observer: any;

  public preventUpdate: boolean;

  private currentStickyId: string = "";

  private readonly setTargetDay: any;

  private intersectionCallback = records => {
    for (const record of records) {
      const rootBoundsInfo = record.rootBounds;
      const rect = record.intersectionRect;

      if (rect.top === rootBoundsInfo.top && this.currentStickyId !== record.target.id) {
        this.currentStickyId = record.target.id;
        if (!this.preventUpdate) {
          this.setTargetDay(parse(this.currentStickyId, DD_MMM_YYYY_MINUSED, new Date()));
        }
      }

      if (!record.isInView && record.intersectionRatio > 0) {
        record.target.isInView = true;
        record.target.setInView(true);
      }

      if (record.target.isInView && record.intersectionRatio === 0) {
        record.target.isInView = false;
        record.target.setInView(false);
      }
    }
  };

  constructor(container, setTargetDay) {
    this.setTargetDay = setTargetDay;

    this.observer = new IntersectionObserver(this.intersectionCallback, {
      threshold: [0, 1],
      root: container,
      rootMargin: "-34px 0px 0px 0px"
    });
  }
}

export const attachDayNodesObserver = (container, setTargetDay) => new DayNodesObserver(container, setTargetDay);

export const gapHoursDayPeriodsBase = [
  {
    periodIcon: MorningIcon,
    hours: [
      {
        title: "9am",
        hour: 9,
        sessions: []
      },
      {
        title: "10am",
        hour: 10,
        sessions: []
      },
      {
        title: "11am",
        hour: 11,
        sessions: []
      }
    ]
  },
  {
    periodIcon: AfternoonIcon,
    hours: [
      {
        title: "12pm",
        hour: 12,
        sessions: []
      },
      {
        title: "1pm",
        hour: 13,
        sessions: []
      },
      {
        title: "2pm",
        hour: 14,
        sessions: []
      },
      {
        title: "3pm",
        hour: 15,
        sessions: []
      },
      {
        title: "4pm",
        hour: 16,
        sessions: []
      },
      {
        title: "5pm",
        hour: 17,
        sessions: []
      }
    ]
  },
  {
    periodIcon: EveningIcon,
    hours: [
      {
        title: "6pm",
        hour: 18,
        sessions: []
      }
    ]
  }
];

const getGapHoursBase = () => Array.from(Array(24), (_, i) => ({
  title: format(setHours(new Date(), i), HA).toLowerCase(),
  hour: i,
  sessions: []
}));

export const getGapHours = (sessions: Session[]) => {
  let gapHours = getGapHoursBase();

  sessions.forEach(s => {
    const startDate = new Date(s.start);
    const endDate = new Date(s.end);
    const endHour = endDate.getHours();

    let startHour = startDate.getHours();

    while (startHour <= endHour) {
      if (startHour === endHour && startDate.getMinutes() === 0 && endDate.getMinutes() === 0) {
        break;
      }

      if (
        (startHour >= 0 && startHour < 12)
        || (startHour >= 12 && startHour < 18)
        || (startHour >= 18 && startHour < 24)
      ) {
        gapHours[startHour].sessions.push(s);
      }

      startHour++;
    }
  });

  let cutFromStart = 0;

  let cutFromStartStop = false;

  let cutFromEnd = 0;

  let cutFromEndStop = false;

  for (let i = 0; i < gapHours.length; i++) {
    if (!cutFromStartStop) {
      if (!gapHours[i].sessions.length && gapHours[i].hour < 9) {
        cutFromStart++;
      } else {
        cutFromStartStop = true;
      }
    }

    if (!cutFromEndStop) {
      if (!gapHours[gapHours.length - 1 - i].sessions.length && gapHours[gapHours.length - 1 - i].hour > 18) {
        cutFromEnd++;
      } else {
        cutFromEndStop = true;
      }
    }

    if (cutFromStartStop && cutFromEndStop) {
      break;
    }
  }

  gapHours = gapHours.slice(cutFromStart, gapHours.length - cutFromEnd);

  return gapHoursDayPeriodsBase.map((p, i) => ({
    periodIcon: p.periodIcon,
    hours: gapHours.filter(h => {
      switch (i) {
        case 0: {
          return h.hour < 12;
        }
        case 1: {
          return h.hour >= 12 && h.hour < 18;
        }
        case 2: {
          return h.hour >= 18;
        }
        default: {
          console.error("Gap Days parse error");
          return false;
        }
      }
    })
  }));
};

export const getGroupings = (sessions: Session[], grouping: CalendarGroupingState): CalendarGrouping[] => {
  const groupings = [];

  if (grouping === "Group by tutor") {
    const indexes = {};
    let baseIndex = 0;
    sessions.forEach(s => {
      s.tutors.forEach(t => {
        if (!indexes[t]) {
          indexes[t] = baseIndex;
          baseIndex++;
          groupings.push({
            tutor: t,
            sessions: [s]
          });
        } else {
          groupings[indexes[t]].sessions.push(s);
        }
      });
      if (!s.tutors.length) {
        if (!indexes[NO_TUTORS_LABEL]) {
          indexes[NO_TUTORS_LABEL] = baseIndex;
          baseIndex++;
          groupings.push({
            tutor: NO_TUTORS_LABEL,
            sessions: [s]
          });
        } else {
          groupings[indexes[NO_TUTORS_LABEL]].sessions.push(s);
        }
      }
    });
  }

  if (grouping === "Group by room") {
    const indexes = {};
    let baseIndex = 0;
    sessions.forEach(s => {
      const baseRoom = s.room || NO_ROOM_LABEL;
      const baseSite = s.site;
      const siteRoomKey = `${s.site}-${s.room}`;

      if (!indexes[siteRoomKey]) {
        indexes[siteRoomKey] = baseIndex;
        baseIndex++;
        groupings.push({
          room: baseRoom,
          site: baseSite,
          sessions: [s]
        });
      } else {
        groupings[indexes[siteRoomKey]].sessions.push(s);
      }
    });
  }

  return groupings;
};

export const testPeriod = (session: Session, periods: boolean[]): boolean => {
  const startDate = new Date(session.start);
  const startHour = startDate.getHours();

  return (
    (periods[0] && startHour >= 0 && startHour < 12)
    || (periods[1] && startHour >= 12 && startHour < 18)
    || (periods[2] && startHour >= 18 && startHour < 24)
  );
};

export const filterSessionsByPeriod = (sessions: Session[], periods: boolean[]): Session[] => sessions.filter(s => testPeriod(s, periods));
