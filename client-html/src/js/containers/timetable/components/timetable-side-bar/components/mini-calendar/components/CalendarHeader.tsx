import React from "react";
import { IconButton, Typography } from "@mui/material";
import { createStyles, withStyles } from "@mui/styles";
import { KeyboardArrowLeft, KeyboardArrowRight } from "@mui/icons-material";

interface Props {
  month: string;
  year: number;
  previousMonth: () => void;
  nextMonth: () => void;
  classes?: any;
}

const styles = createStyles(theme => ({
  root: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: theme.spacing(0, 1)
  }
}));

const CalendarHeader = ({ classes, month, year, previousMonth, nextMonth }: Props) => (
  <div className={classes.root}>
    <IconButton onClick={previousMonth}>
      <KeyboardArrowLeft />
    </IconButton>
    <Typography align="center">
      {month} {year}
    </Typography>
    <IconButton onClick={nextMonth}>
      <KeyboardArrowRight />
    </IconButton>
  </div>
);

export default withStyles(styles)(CalendarHeader);
