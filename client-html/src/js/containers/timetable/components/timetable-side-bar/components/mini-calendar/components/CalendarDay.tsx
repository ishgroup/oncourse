import React, { useContext } from "react";
import { Button } from "@material-ui/core";
import { isSameDay } from "date-fns";
import { TimetableContext } from "../../../../../Timetable";
import withTheme from "@material-ui/styles/withTheme";
import { makeStyles } from "@material-ui/styles";

interface Props {
  day: number;
  isToday?: boolean;
  today?: any;
  date: Date;
  status: string;
  hasSession: boolean;
  disabled: boolean;
  theme?: any;
}

interface Styles extends Props {
  targetDay?: any;
}

const textColorChooser = (targetDay, date, status, theme) => {
  const today = isSameDay(date, new Date());
  const target = isSameDay(targetDay, date);
  if (status === "previous" || status === "next") {
    return theme.palette.action.disabled;
  } else if (today && target) {
    return theme.palette.common.white;
  } else if (today) {
    return theme.palette.primary.main;
  } else return;
};

const useStyles = makeStyles({
  root: ({ date, targetDay, status, theme, hasSession, disabled }: Styles) => ({
    padding: 0,
    borderRadius: 100,
    minWidth: "initial",
    boxShadow: "none",
    "&:active": {
      boxShadow: "none"
    },
    "&:focus": {
      boxShadow: "none"
    },
    "&::after": {
      display: hasSession ? "block" : "none",
      background: disabled
        ? theme.palette.action.disabled
        : isSameDay(targetDay, date) ? theme.palette.common.white : theme.palette.secondary.main,
      content: "''",
      position: "absolute",
      width: 4,
      height: 4,
      top: 4,
      borderRadius: "100%"
    },
    fontWeight: isSameDay(date, new Date()) ? "bold" : "normal",
    color: textColorChooser(targetDay, date, status, theme)
  })
});

const CalendarDay = React.memo((props: Props) => {
  const { day, date, disabled } = props;

  const { setTargetDay, targetDay } = useContext(TimetableContext);

  const classes = useStyles({ targetDay, ...props });

  const isSame = isSameDay(targetDay, date);

  return (
    <Button
      className={classes.root}
      onClick={() => setTargetDay(date)}
      variant={isSame ? "contained" : "text"}
      color={isSame ? "primary" : "default"}
      disabled={disabled}
    >
      {day}
    </Button>
  );
});

export default withTheme(CalendarDay);
