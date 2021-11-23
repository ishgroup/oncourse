import React from "react";
import DayPeriodFilter from "./components/day-period-filter/DayPeriodFilter";
import MiniCalendar from "./components/mini-calendar/MiniCalendar";
import withStyles from "@mui/styles/withStyles";
import { createStyles } from "@mui/material";
import CustomFilters from "./components/custom-filters/CustomFilters";

const styles = theme =>
  createStyles({
    sideBar: {
      display: "flex",
      flexDirection: "column",
      "& > *": {
        marginBottom: theme.spacing(1)
      }
    }
  });

const TimetableSideBar = ({ classes, hasSearch }) => (
  <div className={classes.sideBar}>
    <MiniCalendar />
    <DayPeriodFilter />
    {!hasSearch && <CustomFilters />}
  </div>
);

export default withStyles(styles)(TimetableSideBar);
