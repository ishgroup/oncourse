/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { createStyles, withStyles } from "@mui/styles";
import { getDay } from "date-fns";
import { CalendarDay, getCalendarDays } from "ish-ui";
import React, { useContext } from "react";
import { TimetableContext } from "../../../../../Timetable";

const styles = theme =>
  createStyles({
    calendar: {
      display: "grid",
      gridTemplateColumns: `repeat(7, ${theme.spacing(3.75)})`,
      gridGap: 4,
      gridAutoRows: theme.spacing(3.75),
      padding: theme.spacing(0, 1),
      justifyContent: "center"
    }
  });

const isDisabled = (el, selectedWeekDays): boolean =>
  (selectedWeekDays.every(el => el === false) ? el.status !== "current" : !selectedWeekDays[getDay(el.date)]);

const CalendarBody = ({
 classes, month, selectedWeekDays, selectedMonthSessionDays 
}) => {
  const { setTargetDay, targetDay } = useContext(TimetableContext);

  return (
    <div className={classes.calendar}>
      {getCalendarDays(month).map((el, id) => (
        <CalendarDay
          key={id}
          {...el}
          disabled={isDisabled(el, selectedWeekDays)}
          selectedMonthSessionDays={selectedMonthSessionDays}
          setTargetDay={setTargetDay}
          targetDay={targetDay}
        />
      ))}
    </div>
  );
};

export default withStyles(styles)(CalendarBody);
