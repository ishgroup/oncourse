import React from "react";
import { createStyles, withStyles } from "@material-ui/core";
import WeekDay from "./WeekDay";

interface Props {
  classes?: any;
  switchWeekDays: (dayId: number) => void;
  selectedWeekDays: boolean[];
}

const styles = theme =>
  createStyles({
    root: {
      display: "grid",
      gridTemplateColumns: `repeat(7, ${theme.spacing(4)}px)`,
      gridColumnGap: 1,
      padding: theme.spacing(0, 1),
      marginBottom: theme.spacing(1),
      justifyContent: "center"
    }
  });

const week = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];

const CalendarWeekPanel = ({ classes, switchWeekDays, selectedWeekDays }: Props) => {
  return (
    <div className={classes.root}>
      {week.map((el, id) => (
        <WeekDay key={id} id={id} name={el} selected={selectedWeekDays[id]} click={switchWeekDays} />
      ))}
    </div>
  );
};

export default withStyles(styles)(CalendarWeekPanel);
