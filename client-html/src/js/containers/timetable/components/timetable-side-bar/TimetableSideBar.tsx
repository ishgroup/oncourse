import React from 'react';
import { withStyles } from 'tss-react/mui';
import CustomFilters from './components/custom-filters/CustomFilters';
import DayPeriodFilter from './components/day-period-filter/DayPeriodFilter';
import MiniCalendar from './components/mini-calendar/MiniCalendar';

const styles = theme =>
  ({
    sideBar: {
      display: "flex",
      flexDirection: "column",
      "& > *": {
        marginBottom: theme.spacing(1)
      }
    }
  });

const TimetableSideBar = ({ classes }: { classes?: any }) => (
  <div className={classes.sideBar}>
    <MiniCalendar />
    <DayPeriodFilter />
    <CustomFilters />
  </div>
);

export default withStyles(TimetableSideBar, styles);
