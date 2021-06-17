/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import clsx from "clsx";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { format } from "date-fns";
import { withStyles, createStyles } from "@material-ui/core/styles";
import { AttendanceType } from "@api/model";
import Launch from "@material-ui/icons/Launch";
import IconButton from "@material-ui/core/IconButton";
import AttendanceActionsMenu from "./AttendanceActionsMenu";
import { AttandanceStepItem } from "../../../../../model/entities/CourseClass";
import { AppTheme } from "../../../../../model/common/Theme";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";

const styles = (theme: AppTheme) =>
  createStyles({
    sessionDay: {
      transform: "rotate(-60deg)",
      marginTop: 40,
      left: -26
    },
    dayItem: {
      maxWidth: "12.3%",
      minHeight: theme.spacing(15),
      "&:hover .invisible": {
        visibility: "visible"
      }
    },
    dayMenu: {
      position: "absolute",
      left: "-22px",
      bottom: "-12px",
      transform: "rotate(60deg)"
    },
    assessmentLink: {
      fontSize: "0.8em"
    }
  });

interface DayBaseProps extends AttandanceStepItem {
  classes?: any;
  wrapperClass?: string;
  dayNodeRef?: any;
  changeSessionRow: (type: AttendanceType) => void;
  hasStudentAttendance: boolean;
  type?: string;
}

const AttendanceDayBase: React.FC<DayBaseProps> = ({
  id,
  start,
  index,
  assessmentCode,
  dueDate,
  classes,
  changeSessionRow,
  hasStudentAttendance,
  siteTimezone,
  type
}) => {
  const sessionStartOrAssessmentCode = useMemo(
    () => (
      <span>
        {start
          && format(siteTimezone ? appendTimezone(new Date(start), siteTimezone) : new Date(start), "p")
            .replace(/\s/, "")
            .toLowerCase()}
        {assessmentCode}
      </span>
    ),
    [start, assessmentCode, siteTimezone]
  );

  const scrollToAssessment = useCallback(() => {
    const item = document.getElementById(`assessments[${index}]`);
    if (item) {
      item.scrollIntoView({ block: "start", inline: "nearest", behavior: "smooth" });
      if (item.parentElement.parentElement.getAttribute("aria-expanded") === "false") {
        setTimeout(() => item.click(), 500);
      }
    }
  }, [index]);

  return (
    <Grid item xs={2} className={clsx("relative", classes.dayItem)}>
      <div className={clsx("mb-3 relative", classes.sessionDay)}>
        <Typography variant="body2" className={classes.day}>
          {format(
            new Date((siteTimezone ? appendTimezone(new Date(start), siteTimezone) : start) || dueDate),
            "iii d MMM"
          )}
        </Typography>
        <Typography variant="caption">{sessionStartOrAssessmentCode}</Typography>
        <div className={classes.dayMenu}>
          {type === "Training plan" ? (
            <AttendanceActionsMenu
              className="invisible"
              onChange={changeSessionRow}
              type="Training plan"
              label="Mark ALL modules for this sessions as..."
            />
          ) : id && start && hasStudentAttendance && (
            <AttendanceActionsMenu
              className="invisible"
              onChange={changeSessionRow}
              type="Student"
              label="Mark ALL attendances for this session as..."
            />
          )}
          {dueDate && (
            <IconButton className="p-0" onClick={scrollToAssessment}>
              <Launch color="secondary" className={classes.assessmentLink} />
            </IconButton>
          )}
        </div>
      </div>
    </Grid>
  );
};

export default withStyles(styles)(AttendanceDayBase);
