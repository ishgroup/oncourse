/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useContext, useMemo, useState } from "react";
import { format, getDay } from "date-fns";
import { TimetableMonth } from "../../../../../../model/timetable";
import { DD_MMM_YYYY_MINUSED } from  "ish-ui";
import { CalendarDay } from "../day/CalendarDay";
import { TimetableContext } from "../../../../Timetable";
import { filterSessionsByPeriod } from "../../../../utils";
import CalendarMonthBase from "./CalendarMonthBase";

interface CompactModeMonthProps extends TimetableMonth {
  style: any;
  classes
  dayNodesObserver: any;
  index: number;
  isScrolling: boolean;
  parentRef?: any;
}

const CalendarMonth: React.FunctionComponent<CompactModeMonthProps> = props => {
  const {
    month,
    days,
    hasSessions,
    index,
    isScrolling,
    style,
    dayNodesObserver,
    parentRef
  } = props;

  const {
   selectedWeekDays, selectedDayPeriods, calendarMode, tagsState, calendarGrouping
  } = useContext(TimetableContext);

  const [renderedDays, setRenderedDays] = useState([]);

  const getFilteredSessions = useCallback(sessions => filterSessionsByPeriod(sessions, selectedDayPeriods), [
    selectedDayPeriods
  ]);

  const checkDayStart = useCallback(day => selectedWeekDays[getDay(day)], [selectedWeekDays]);

  useMemo(() => {
    const hasSelectedWeekDays = selectedWeekDays.includes(true);

    const hasSelectedDayPeriods = selectedDayPeriods.includes(true);

    const rendered = days.map((d, dayIndex) => {
      const dayId = format(d.day, DD_MMM_YYYY_MINUSED);

      const daySessions = hasSelectedDayPeriods ? getFilteredSessions(d.sessions) : d.sessions;

      const condition = (calendarMode !== "Compact" ? true : daySessions.length) && (hasSelectedWeekDays ? checkDayStart(d.day) : true);

      return condition ? (
        <CalendarDay
          {...d}
          dayIndex={dayIndex}
          dayId={dayId}
          key={dayId}
          monthIndex={index}
          isScrolling={isScrolling}
          dayNodesObserver={dayNodesObserver}
          sessions={daySessions}
          calendarMode={calendarMode}
          selectedDayPeriods={selectedDayPeriods}
          tagsState={tagsState}
          calendarGrouping={calendarGrouping}
        />
      ) : null;
    });
    setRenderedDays(rendered.filter(r => r));
  }, [days, isScrolling, month, index, hasSessions, selectedDayPeriods, selectedWeekDays, calendarMode, calendarGrouping, tagsState]);

  return renderedDays.length ? (
    <CalendarMonthBase month={month} style={style} parentRef={parentRef}>
      {renderedDays}
    </CalendarMonthBase>
  ) : (
    <div ref={parentRef} style={{ ...style, height: "1px", fontSize: "0px" }}>---</div>
  );
};

export default CalendarMonth;
