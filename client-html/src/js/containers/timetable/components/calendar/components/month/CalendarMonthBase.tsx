/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Grid } from "@mui/material";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import { format } from "date-fns";
import { AppTheme } from "ish-ui";
import React from "react";

const styles = (theme: AppTheme) => createStyles({
    manyDaysWrapper: {
      "& > .dayOffset": {
        marginBottom: theme.spacing(1)
      }
    },
    month: {
      position: "sticky",
      top: theme.spacing(3.375),
      display: "flex",
      justifyContent: "flex-end",
      marginTop: theme.spacing(3.375)
    }
  });

interface MonthBaseProps {
  month: Date;
  classes?: any;
  style?: any;
  fullWidth?: boolean;
  parentRef?: any;
  showYear?: boolean;
  children?: React.ReactNode;
}

const CalendarMonthBase: React.FC<MonthBaseProps> = props => {
  const {
 month, style, classes, children, fullWidth, parentRef, showYear
} = props;

  const notNullDate = month.getFullYear() !== 1970;

  return (
    <Grid container columnSpacing={3} style={style} ref={parentRef}>
      <Grid item xs={1}>
        <div className={classes.month}>
          {
            notNullDate && (
            <div>
              <Typography variant="h5" display="block">{format(month, "MMM")}</Typography>
              {showYear
              && (
                <Typography
                  variant="caption"
                  color="textSecondary"
                  display="block"
                  align="right"
                >
                  {format(month, "yyyy")}
                </Typography>
              ) }
            </div>
)
          }

        </div>
      </Grid>

      <Grid item xs={fullWidth ? 11 : 9} className={classes.manyDaysWrapper}>
        {children}
      </Grid>
    </Grid>
  );
};

export default withStyles(styles)(CalendarMonthBase) as React.FC<MonthBaseProps>;
