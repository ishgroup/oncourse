/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { withStyles, createStyles } from "@mui/styles";
import Typography from "@mui/material/Typography";
import { format } from "date-fns";
import { AttandanceMonth } from "../../../../../model/entities/CourseClass";

const styles = () =>
  createStyles({
    manyDaysWrapper: {
      marginTop: 10,
      visibility: "hidden"
    },
    monthText: {
      fontSize: 12,
      color: "#e69330"
    },
    monthSessions: {
      fontSize: 0,
      width: 4,
      height: 16,
      background: "#7d7d7d",
      margin: "0 20px 0 0"
    }
  });

interface MonthBaseProps extends Partial<AttandanceMonth> {
  classes?: any;
  style?: any;
  fullWidth?: boolean;
}

const AttendanceMonthBase: React.FC<MonthBaseProps> = props => {
  const { month, days, classes } = props;

  return (
    <div className="flex-column align-items-start">
      <Typography variant="body1" className={classes.monthText}>
        {format(month, "MMM")}
      </Typography>
      <div className={`flex-row ${classes.manyDaysWrapper}`}>
        {days.map(d => {
          if (!d.items.length) {
            return null;
          }
          return (
            <div key={d.day.toString()} className="d-flex">
              {d.items.map(s => <div key={s.id} className={`flex-row ${classes.monthSessions}`} />)}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default withStyles(styles)(AttendanceMonthBase) as React.FC<MonthBaseProps>;
