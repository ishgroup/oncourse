/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useContext, useMemo, useState
} from "react";
import { format, getDay } from "date-fns";
import { TimetableMonth } from "../../../../../../model/timetable/index";
import { DD_MMM_YYYY_MINUSED } from "../../../../../../common/utils/dates/format";
import { CalendarDay } from "../day/CalendarDay";
import { TimetableContext } from "../../../../Timetable";
import { filterSessionsByPeriod } from "../../../../utils/index";
import CalendarMonthBase from "./CalendarMonthBase";

interface CompactModeMonthProps extends TimetableMonth {
  style: any;
  classes
  dayNodesObserver: any;
  index: number;
  isScrolling: boolean;
  tagsExpanded: any;
  setTagsExpanded: any;
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
    tagsExpanded,
    setTagsExpanded,
    parentRef
  } = props;

  const { selectedWeekDays, selectedDayPeriods, calendarMode } = useContext(TimetableContext);

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
          tagsExpanded={tagsExpanded}
          setTagsExpanded={setTagsExpanded}
        />
      ) : null;
    });
    setRenderedDays(rendered.filter(r => r));
  }, [days, isScrolling, month, index, hasSessions, selectedDayPeriods, selectedWeekDays, calendarMode, tagsExpanded]);

  return renderedDays.length ? (
    <CalendarMonthBase month={month} style={style} parentRef={parentRef}>
      {renderedDays}
    </CalendarMonthBase>
  ) : (
    <div ref={parentRef} style={{ ...style, height: "1px", fontSize: "0px" }}>---</div>
  );
};

export default CalendarMonth;
