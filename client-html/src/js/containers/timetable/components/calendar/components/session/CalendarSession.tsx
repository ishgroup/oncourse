/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Chip from "@material-ui/core/Chip";
import React, { useCallback, useMemo } from "react";
import Typography from "@material-ui/core/Typography";
import { ClashType, Session } from "@api/model";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import { createStyles, Theme } from "@material-ui/core";
import { differenceInMinutes, format } from "date-fns";
import WarningMessage from "../../../../../../common/components/form/fieldMessage/WarningMessage";
import CalendarSessionTag from "./CalendarSessionTag";
import { stopEventPropagation } from "../../../../../../common/utils/events";
import { openCourseClassLink } from "../../../../../entities/courseClasses/utils";
import { formatDurationMinutes } from "../../../../../../common/utils/dates/formatString";
import { appendTimezone } from "../../../../../../common/utils/dates/formatTimezone";

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
      cursor: "pointer",
      marginTop: "2px",
      "&:hover": {
        textDecoration: "underline",
        color: theme.palette.secondary.main
      },
      "&:hover .tagColorDot": {
        transform: "scale(1.2)"
      }
    }
  });

interface SessionBaseProps extends Session {
  inView: boolean;
  tags?: any;
  tagsExpanded?: boolean;
  classes?: any;
  setTagsExpanded?: any;
  disableLink?: boolean;
  disableTags?: boolean;
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
    inView,
    classId,
    setTagsExpanded,
    start,
    end,
    tags,
    tagsExpanded,
    id,
    siteTimezone,
    disableLink,
    disableTags,
    clashes,
    hours,
    startLabel,
    warningMessage
  } = props;

  const onNameClick = useCallback(() => openCourseClassLink(classId), [classId]);

  const onTagsClick = useCallback(e => {
    stopEventPropagation(e);
    setTagsExpanded(prev => !prev);
  }, []);

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
    if (disableTags) {
      return null;
    }

    const tagsKeys = tags && Object.keys(tags);

    return tagsKeys ? (
      tagsKeys.length ? (
        tagsKeys.map(t => (
          <CalendarSessionTag key={id + t} color={"#" + tags[t]} name={t} tagsExpanded={tagsExpanded} />
        ))
      ) : null
    ) : (
      <Typography variant="caption" component="span" className="text-disabled">
        Loading...
      </Typography>
    );
  }, [tags, tagsExpanded, disableTags]);

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
        {name && inView ? (
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
              <Typography variant="caption" noWrap color={clashes && clashes.includes("Tutor") ? "error" : undefined}>
                with
                {" "}
                {tutors.length ? (
                  <span>{tutors.map((el, id) => (id === 0 ? el : ` ${el}`)).toString()}</span>
                ) : (
                  <span className={classes.warningColor}>No tutor set</span>
                )}
              </Typography>

              <Typography variant="caption" noWrap>
                {room ? (
                  <span>
                    <span className={clashes && clashes.includes("Room") ? "errorColor" : undefined}>{room}</span>
                    <span>,</span>
                    <span className={clashes && clashes.includes("Site") ? "errorColor" : undefined}>{site}</span>
                  </span>
                ) : (
                  <span className={classes.warningColor}>No room set</span>
                )}
              </Typography>
            </div>

            {disableTags ? null : renderedTags ? (
              <Typography color="textSecondary" variant="caption" component="div">
                <div className={classes.tagsWrapper} onClick={onTagsClick}>
                  {renderedTags}
                </div>
              </Typography>
            ) : (
              <Typography variant="caption" component="span" className="placeholderContent">
                No Tags
              </Typography>
            )}

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
