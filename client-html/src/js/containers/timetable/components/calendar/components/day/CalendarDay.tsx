/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useEffect, useMemo, useRef, Fragment, useState
} from "react";
import clsx from "clsx";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import withStyles from "@material-ui/styles/withStyles";
import { getTimetableSessionsByIds, getTimetableSessionsTags } from "../../../../actions/index";
import styles from "../styles";
import { gapHoursDayPeriodsBase, getGapHours } from "../../../../utils/index";
import { CalendarMode, TimetableDay } from "../../../../../../model/timetable/index";
import CalendarSessionHour from "../session/CalendarSessionHour";
import CalendarSession from "../session/CalendarSession";
import CalendarDayBase from "./CalendarDayBase";

interface CompactModeDayProps extends TimetableDay {
  monthIndex: number;
  dayIndex: number;
  dayId: string;
  updateSessionsDetails: (ids: number[], monthIndex?: number, dayIndex?: number) => void;
  getSessionsTags: (ids: number[], monthIndex?: number, dayIndex?: number) => void;
  dayNodesObserver: any;
  isScrolling: boolean;
  classes: any;
  calendarMode: CalendarMode;
  selectedDayPeriods: boolean[];
  tagsExpanded: any;
  setTagsExpanded: any;
}

const EmptyGapDay: React.FunctionComponent<any> = React.memo(
  ({
 classes, selectedDayPeriods, hasSelectedDayPeriods, updated, hasSessions
}) => (
  <>
    {gapHoursDayPeriodsBase.map((p, i) => {
        if (hasSelectedDayPeriods && !selectedDayPeriods[i]) {
          return null;
        }

        return (
          <Grid container key={i} className={i !== 2 ? "mb-2" : undefined}>
            <Grid item xs={1} className={classes.gapDayOffsetTop}>
              <p.periodIcon className={classes.stickyIcon} />
            </Grid>

            <Grid item xs={11} className={classes.gapDayOffsetTop}>
              <Grid container spacing={2}>
                {p.hours.map(h => (
                  <Fragment key={h.title}>
                    <Grid item xs={1}>
                      <Typography variant="body2" className="text-disabled">
                        {h.title}
                      </Typography>
                    </Grid>
                    <Grid item xs={11}>
                      <Typography className="text-disabled">
                        {hasSessions && !updated ? "Loading..." : "available"}
                      </Typography>
                    </Grid>
                  </Fragment>
                ))}
              </Grid>
            </Grid>
          </Grid>
        );
      })}
  </>
  )
);

const GapDay: React.FunctionComponent<any> = React.memo(
  ({
    classes,
    selectedDayPeriods,
    gapHours,
    updated,
    hasSessions,
    tagsUpdated,
    tagsExpanded,
    setTagsExpanded
  }) => {
    const hasSelectedDayPeriods = selectedDayPeriods.includes(true);

    return hasSessions && gapHours.length ? (
      <>
        {gapHours.map((p, i) => {
          if (hasSelectedDayPeriods && !selectedDayPeriods[i]) {
            return null;
          }

          return (
            <Grid container key={i} className={classes.gapPeriodOffsetTop}>
              <Grid item xs={1} className={classes.gapDayOffsetTop}>
                <p.periodIcon className={classes.stickyIcon} />
              </Grid>

              <Grid item xs={11}>
                <Grid container spacing={2}>
                  {p.hours.map(h => (
                    <Fragment key={h.title}>
                      <Grid item xs={1}>
                        <Typography
                          variant="body2"
                          className={clsx(classes.gapDayOffsetTop, {
                            "text-disabled": !h.sessions.length
                          })}
                        >
                          {h.title}
                        </Typography>
                      </Grid>
                      <Grid item xs={11}>
                        {h.sessions.length ? (
                          <CalendarSessionHour
                            key={h.title + h.sessions.length}
                            classes={classes}
                            sessions={h.sessions}
                            tagsExpanded={tagsExpanded}
                            setTagsExpanded={setTagsExpanded}
                          />
                        ) : (
                          <Typography className="text-disabled">available</Typography>
                        )}
                      </Grid>
                    </Fragment>
                  ))}
                </Grid>
              </Grid>
            </Grid>
          );
        })}
      </>
    ) : (
      <EmptyGapDay
        classes={classes}
        hasSelectedDayPeriods={hasSelectedDayPeriods}
        selectedDayPeriods={selectedDayPeriods}
        updated={updated && tagsUpdated}
        hasSessions={hasSessions}
      />
    );
  }
);

const CalendarDayWrapper: React.FunctionComponent<CompactModeDayProps> = React.memo(props => {
  const {
    day,
    dayId,
    sessions,
    updated,
    updateSessionsDetails,
    monthIndex,
    dayIndex,
    isScrolling,
    dayNodesObserver,
    classes,
    calendarMode,
    selectedDayPeriods,
    tagsExpanded,
    tagsUpdated,
    setTagsExpanded,
    getSessionsTags
  } = props;

  const [gapHours, setGapHours] = useState([]);
  const [inView, setInView] = useState<boolean>(false);

  const sessionsIds = useMemo(() => sessions.map(s => s.id), [sessions]);

  useEffect(() => {
    if (!updated && !isScrolling && inView && sessions.length) {
      updateSessionsDetails(sessionsIds, monthIndex, dayIndex);
    }
    if (updated && !tagsUpdated && !isScrolling && inView && sessionsIds.length) {
      getSessionsTags(sessionsIds, monthIndex, dayIndex);
    }
  }, [updated, tagsUpdated, isScrolling, inView]);

  useEffect(() => {
    if (!gapHours.length && calendarMode === "Gap(Hours)" && sessions.length && updated && tagsUpdated) {
      setGapHours(getGapHours(sessions));
    }
  }, [calendarMode, sessions, updated, tagsUpdated]);

  const dayNodeRef = useRef(null);

  useEffect(() => {
    if (dayNodesObserver && dayNodeRef.current) {
      dayNodeRef.current.setInView = setInView;
      dayNodeRef.current.isInView = inView;
      dayNodesObserver.observer.observe(dayNodeRef.current);
    }

    return () => dayNodesObserver.observer.unobserve(dayNodeRef.current);
  }, [dayNodesObserver, dayNodeRef.current]);

  const renderedDays = useMemo(
    () => (sessions.length ? (
        sessions.map(s => (
          <CalendarSession
            key={s.id}
            inView={inView}
            tagsExpanded={tagsExpanded}
            setTagsExpanded={setTagsExpanded}
            {...s}
          />
        ))
      ) : (
        <Typography className="text-disabled dayOffset">available</Typography>
      )),
    [sessions, updated, selectedDayPeriods, tagsExpanded, inView]
  );

  return (
    <CalendarDayBase
      day={day}
      id={dayId}
      dayNodeRef={dayNodeRef}
      wrapperClass={calendarMode === "Gap(Hours)" ? classes.gapDayWrapper : undefined}
    >
      {calendarMode === "Gap(Hours)" ? (
        <GapDay
          classes={classes}
          selectedDayPeriods={selectedDayPeriods}
          gapHours={gapHours}
          updated={updated}
          hasSessions={sessions.length}
          tagsUpdated={tagsUpdated}
          tagsExpanded={tagsExpanded}
          setTagsExpanded={setTagsExpanded}
        />
      ) : (
        renderedDays
      )}
    </CalendarDayBase>
  );
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    updateSessionsDetails: (ids: number[], monthIndex?: number, dayIndex?: number) => dispatch(getTimetableSessionsByIds(ids, monthIndex, dayIndex)),
    getSessionsTags: (ids: number[], monthIndex?: number, dayIndex?: number) => dispatch(getTimetableSessionsTags(ids, monthIndex, dayIndex))
  });

export const CalendarDay = connect<any, any, any>(
  null,
  mapDispatchToProps
)(withStyles(styles)(CalendarDayWrapper));
