/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { format, getDay, isSameDay, isToday } from 'date-fns';
import { CalendarDay, DD_MMM_YYYY_MINUSED, getCalendarDays } from 'ish-ui';
import React, { useContext } from 'react';
import { withStyles } from 'tss-react/mui';
import { useAppDispatch, useAppSelector } from '../../../../../../../common/utils/hooks';
import { setTimetableScrollDay } from '../../../../../actions';
import { TimetableContext } from '../../../../../Timetable';

const styles = theme =>
  ({
    calendar: {
      display: "grid",
      gridTemplateColumns: `repeat(7, ${theme.spacing(3.75)})`,
      gap: 4,
      gridAutoRows: theme.spacing(3.75),
      padding: theme.spacing(0, 1),
      justifyContent: "center"
    }
  });

const isDisabled = (el, selectedWeekDays): boolean =>
  (selectedWeekDays.every(el => el === false) ? el.status !== "current" : !selectedWeekDays[getDay(el.date)]);

const CalendarBody = ({
 classes, month, selectedWeekDays
}: { classes?, month, selectedWeekDays }) => {
  const { setTargetDay, calendarMode, targetDay } = useContext(TimetableContext);
  const selectedMonthSessionDays = useAppSelector(state => state.timetable.selectedMonthSessionDays);
  const dispatch = useAppDispatch();
  
  return (
    <div className={classes.calendar}>
      {getCalendarDays(month).map((el, id) => {
        const dayIndexValue = selectedMonthSessionDays[el.day - 1];
        const hasSession = selectedMonthSessionDays.length && el.status === 'current' &&  dayIndexValue !== 0;
        const setTargetDayHandler = day => {
          if (calendarMode !== 'Compact' || hasSession) {
            const targetDay = format(day, DD_MMM_YYYY_MINUSED);
            setTargetDay(targetDay);
            dispatch(setTimetableScrollDay(targetDay));
          }
        };

        return <CalendarDay
          {...el}
          key={id}
          dayIndexValue={dayIndexValue}
          hasSession={hasSession}
          isSameAsTarget={isSameDay(null, el.date)}
          isToday={isToday(el.date)}
          disabled={isDisabled(el, selectedWeekDays)}
          setTargetDay={setTargetDayHandler}
          targetDay={new Date(targetDay)}
        />;
      })}
    </div>
  );
};

export default withStyles(CalendarBody, styles);