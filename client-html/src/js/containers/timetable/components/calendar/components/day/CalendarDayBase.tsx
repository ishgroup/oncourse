/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Grid, Theme } from '@mui/material';
import Typography from '@mui/material/Typography';
import { format } from 'date-fns';
import { appendTimezone, D_III } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';

const styles = (theme: Theme) =>
  ({
    day: {
      position: "sticky",
      top: 32,
      display: "flex",
      justifyContent: "center"
    },
    commonDayOffsetTop: {
      marginTop: theme.spacing(4)
    }
  });

interface DayBaseProps {
  day: Date;
  timezone?: string;
  classes?: any;
  wrapperClass?: string;
  dayNodeRef?: any;
  id?: string;
  children?: React.ReactNode;
}

const CalendarDayBase: React.FC<DayBaseProps> = props => {
  const {
 day, timezone, id, dayNodeRef, classes, wrapperClass, children
} = props;

  const notNullDate = day.getFullYear() !== 1970;

  return (
    <Grid container className={classes.commonDayOffsetTop}>
      <Grid item xs={2}>
        <div className={classes.day} id={id} ref={dayNodeRef}>
          {notNullDate && <Typography align="right">{format(timezone ? appendTimezone(day, timezone) : day, D_III)}</Typography>}
        </div>
      </Grid>

      <Grid container item xs={10} className={wrapperClass}>
        {children}
      </Grid>
    </Grid>
  );
};

export default withStyles(CalendarDayBase, styles);
