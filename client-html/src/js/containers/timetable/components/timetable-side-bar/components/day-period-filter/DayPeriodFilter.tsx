import React, { useContext } from "react";
import { Button } from "@mui/material";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import { TimetableContext, timetableContextStateInitial } from "../../../../Timetable";
import { MorningIcon, AfternoonIcon, EveningIcon } from "./DayPeriodIcons";

const styles = theme =>
  createStyles({
    root: {
      display: "grid",
      gridAutoFlow: "column",
      gridColumnGap: theme.spacing(1),
      padding: theme.spacing(0, 1),
      marginBottom: theme.spacing(1)
    },
    dayPeriodButton: {
      textTransform: "lowercase",
      fontSize: "0.6em",
      padding: theme.spacing(0.5),
      "& svg": {
        width: theme.spacing(1.4),
        height: theme.spacing(1.4),
        marginRight: theme.spacing(0.5)
      },
      "& path": {
        fill: `${theme.palette.text.primary}`
      }
    },
    selectedButton: {
      backgroundColor: `${theme.palette.action.selected}`
    }
  });

interface Props {
  classes?: any;
}

interface State {
  activeFilter: boolean[];
}

const DayPeriodFilter: React.FunctionComponent<Props> = props => {
  const { classes } = props;

  const { selectedDayPeriods, setSelectedDayPeriods } = useContext(TimetableContext);

  const switchFilter = dayId => {
    const updated = selectedDayPeriods.map((p, i) => (i === dayId ? !p : p));

    setSelectedDayPeriods(updated.every(el => el === true) ? timetableContextStateInitial.selectedDayPeriods : updated);
  };

  return (
    <div className={classes.root}>
      <Button
        variant="outlined"
        className={clsx(classes.dayPeriodButton, {
          [classes.selectedButton]: selectedDayPeriods[0]
        })}
        onClick={() => switchFilter(0)}
      >
        <MorningIcon /> before 12pm
      </Button>
      <Button
        variant="outlined"
        className={clsx(classes.dayPeriodButton, {
          [classes.selectedButton]: selectedDayPeriods[1]
        })}
        onClick={() => switchFilter(1)}
      >
        <AfternoonIcon /> 12pm - 6pm
      </Button>
      <Button
        variant="outlined"
        className={clsx(classes.dayPeriodButton, {
          [classes.selectedButton]: selectedDayPeriods[2]
        })}
        onClick={() => switchFilter(2)}
      >
        <EveningIcon /> after 6pm
      </Button>
    </div>
  );
};

export default withStyles(styles)(DayPeriodFilter);
