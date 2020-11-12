import React from "react";
import { createStyles, withStyles } from "@material-ui/styles";
import CalendarDay from "./CalendarDay";
import { getDay } from "date-fns";
import { getCalendarDays } from "../utils";

const styles = theme =>
  createStyles({
    calendar: {
      display: "grid",
      gridTemplateColumns: `repeat(7, ${theme.spacing(4)}px)`,
      gridGap: 1,
      gridAutoRows: theme.spacing(4),
      padding: theme.spacing(0, 1),
      justifyContent: "center"
    }
  });

const isDisabled = (el, selectedWeekDays): boolean =>
  selectedWeekDays.every(el => el === false) ? el.status !== "current" : !selectedWeekDays[getDay(el.date)];

const CalendarBody = ({ classes, month, selectedWeekDays, selectedMonthSessionDays }: any) => {
  return (
    <div className={classes.calendar}>
      {getCalendarDays(month).map((el, id) => (
        <CalendarDay
          key={id}
          {...el}
          hasSession={el.status === "current" && selectedMonthSessionDays.includes(el.day)}
          disabled={isDisabled(el, selectedWeekDays)}
        />
      ))}
    </div>
  );
};

export default withStyles(styles)(CalendarBody);
