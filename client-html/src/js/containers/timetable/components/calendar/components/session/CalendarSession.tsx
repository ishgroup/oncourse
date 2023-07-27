/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Chip from "@mui/material/Chip";
import React, { useCallback, useMemo } from "react";
import Typography from "@mui/material/Typography";
import { ClashType, Session } from "@api/model";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import { createStyles, Theme } from "@mui/material";
import { differenceInMinutes, format } from "date-fns";
import WarningMessage from  "ish-ui";
import CalendarSessionTag from "./CalendarSessionTag";
import { openCourseClassLink } from "../../../../../entities/courseClasses/utils";
import { formatDurationMinutes } from "../../../../../../common/utils/dates/formatString";
import { appendTimezone } from "../../../../../../common/utils/dates/formatTimezone";
import { CalendarTagsState } from "../../../../../../model/timetable";

const styles = (theme: Theme) => createStyles({
    "@global": {
      ".dayOffset": {
        marginBottom: theme.spacing(1),
        "&:last-child": {
          marginBottom: 0
        }
      }
    },
    sessionRoot: {
      width: "100%",
      display: "grid",
      gridTemplateColumns: "80px minmax(0, 1fr)",
      minHeight: "67.5px"
    },
    clashSession: {
      gridTemplateColumns: "80px minmax(0, 1fr) 0.1fr",
    },
    timeColumn: {
      marginTop: "3px"
    },
    secondLine: {
      display: "grid",
      gridGap: theme.spacing(2),
      gridAutoFlow: "column",
      justifyContent: "start"
    },
    warningColor: {
      color: theme.palette.error.main
    },
    clash: {
      background: theme.palette.error.main,
      color: theme.palette.error.contrastText
    },
    loadingLine: {
      color: theme.palette.text.disabled,
      minHeight: "67.5px",
      paddingTop: "3px",
      display: "flex"
    },
    tagsWrapper: {
      marginTop: "2px"
    }
  });

interface SessionBaseProps extends Session {
  hideTutors?: boolean;
  hideRooms?: boolean;
  tags?: any;
  tagsState?: CalendarTagsState;
  classes?: any;
  disableLink?: boolean;
  clashes?: ClashType[];
  hours?: number;
  startLabel?: string;
  warningMessage?: string;
}

const CalendarSession: React.FC<SessionBaseProps> = props => {
  const {
    name,
    room,
    site,
    tutors,
    classes,
    classId,
    start,
    end,
    tags,
    tagsState,
    id,
    siteTimezone,
    disableLink,
    clashes,
    hours,
    startLabel,
    warningMessage,
    hideTutors,
    hideRooms
  } = props;

  const onNameClick = useCallback(() => openCourseClassLink(classId), [classId]);

  const sessionDuration = useMemo(() => {
    const startDate = siteTimezone ? appendTimezone(new Date(start), siteTimezone) : new Date(start);
    const endDate = siteTimezone ? appendTimezone(new Date(end), siteTimezone) : new Date(end);

    startDate.setSeconds(0, 0);
    endDate.setSeconds(0, 0);

    return formatDurationMinutes(differenceInMinutes(endDate, startDate));
  }, [end, start, siteTimezone]);

  const sessionStart = useMemo(
    () => {
      const startDate = new Date(start);
      const isNullDate = startDate.getFullYear() === 1970;

      return isNullDate ? null : (
        <span>
          {format(siteTimezone ? appendTimezone(startDate, siteTimezone) : startDate, "p")
          .replace(/\s/, "")
          .toLowerCase()}
        </span>
      );
    },
    [start, siteTimezone]
  );

  const renderedTags = useMemo(() => {
    if (!tagsState || tagsState === "Tag off") {
      return null;
    }

    const tagsKeys = tags && Object.keys(tags);
    
    return (
      <Typography color="textSecondary" variant="caption" component="div">
        <div className={classes.tagsWrapper}>
          {tagsKeys ? (
          tagsKeys.length ? (
            tagsKeys.map(t => (
              <CalendarSessionTag key={id + t} color={"#" + tags[t]} name={t} tagsState={tagsState} />
            ))
          ) : (
            <Typography variant="caption" component="span" className="placeholderContent">
              No Tags
            </Typography>
          )
        ) : (
          <Typography variant="caption" component="span" className="text-disabled">
            Loading...
          </Typography>
        )}
        </div>
      </Typography>
    );
  }, [tags, tagsState]);

  const hasClash = clashes && Boolean(clashes.length);

  return (
    <div className={clsx("dayOffset", classes.sessionRoot, hasClash && classes.clashSession)}>
      <div className={classes.timeColumn}>
        <Typography variant="body2" component="div" className="text-pre-line" color={hasClash ? "error" : undefined}>
          {sessionStart}
          {startLabel}
        </Typography>
        <Typography variant="caption" component="div" color={hasClash ? "error" : undefined}>
          {hours ? formatDurationMinutes(hours) : sessionDuration}
        </Typography>
      </div>

      <div>
        {name ? (
          <>
            <Typography
              component="div"
              className={disableLink ? undefined : "linkDecoration"}
              onClick={disableLink ? undefined : onNameClick}
              color={clashes && clashes.includes("Session") ? "error" : undefined}
              noWrap
            >
              {name}
            </Typography>

            <div className={classes.secondLine}>
              {!hideTutors && (
                <Typography variant="caption" noWrap color={clashes && clashes.includes("Tutor") ? "error" : undefined}>
                  {tutors.length ? (
                    <span>
                      with
                      {" "}
                      {tutors.map((el, id) => (id === 0 ? el : ` ${el}`)).toString()}
                    </span>
                  ) : (
                    <span className={classes.warningColor}>No tutor set</span>
                  )}
                </Typography>
              )}
              {!hideRooms && (
                <Typography variant="caption" noWrap>
                  {room ? (
                    <span>
                      <span className={clashes && clashes.includes("Room") ? "errorColor" : undefined}>{room}</span>
                      {site && (
                        <span className={clashes && clashes.includes("Site") ? "errorColor" : undefined}>
                          {", "}
                          {site}
                        </span>
                      )}
                    </span>
                  ) : (
                    <span className={classes.warningColor}>No room set</span>
                  )}
                </Typography>
              )}
            </div>

            {renderedTags}

            {warningMessage && <WarningMessage warning={warningMessage} className="m-0" />}
          </>
        ) : (
          <span className={classes.loadingLine}>Loading...</span>
        )}
      </div>

      {hasClash && (
        <div className="pt-1 pl-2">
          <Chip
            size="small"
            label="clash"
            className={classes.clash}
          />
        </div>
      )}
    </div>
  );
};

export default withStyles(styles)(CalendarSession) as React.FC<SessionBaseProps>;
