/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Grid, Typography } from '@mui/material';
import clsx from 'clsx';
import React, { Fragment, useEffect, useLayoutEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import {
  CalendarGrouping,
  CalendarGroupingState,
  CalendarMode,
  CalendarTagsState,
  TimetableDay
} from '../../../../../../model/timetable';
import { getTimetableSessionsByIds, getTimetableSessionsTags } from '../../../../actions';
import { NO_ROOM_LABEL, NO_TUTORS_LABEL } from '../../../../constants';
import { gapHoursDayPeriodsBase, getGapHours, getGroupings } from '../../../../utils';
import CalendarSession from '../session/CalendarSession';
import CalendarSessionHour from '../session/CalendarSessionHour';
import styles from '../styles';
import CalendarDayBase from './CalendarDayBase';

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
  tagsState: CalendarTagsState;
  calendarGrouping: CalendarGroupingState;
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
          <Grid container columnSpacing={3} key={i} className={i !== 2 ? "mb-2" : undefined}>
            <Grid item xs={1} className={classes.gapDayOffsetTop}>
              <p.periodIcon className={classes.stickyIcon} />
            </Grid>

            <Grid item xs={11} className={classes.gapDayOffsetTop}>
              <Grid container columnSpacing={3} spacing={2}>
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
    tagsState,
  }) => {
    const hasSelectedDayPeriods = selectedDayPeriods.includes(true);

    return hasSessions && gapHours.length ? (
      <>
        {gapHours.map((p, i) => {
          if (hasSelectedDayPeriods && !selectedDayPeriods[i]) {
            return null;
          }

          return (
            <Grid container columnSpacing={3} key={i} className={classes.gapPeriodOffsetTop}>
              <Grid item xs={1} className={classes.gapDayOffsetTop}>
                <p.periodIcon className={classes.stickyIcon} />
              </Grid>

              <Grid item xs={11}>
                <Grid container columnSpacing={3} spacing={2}>
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
                            tagsState={tagsState}
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

interface GroupingDayProps {
  classes: any;
  groupings: CalendarGrouping[];
  hasSessions: boolean;
  tagsState: CalendarTagsState;
  groupingState: CalendarGroupingState;
}

const GroupingDay = React.memo<GroupingDayProps>(
  ({
     classes,
     groupings,
     hasSessions,
     tagsState,
     groupingState
   }) => (hasSessions && groupings.length ? (
     <>
       {groupings.map((g, i) => (
         <Grid container columnSpacing={3} key={i}>
           <Grid item xs={2} className={classes.gapDayOffsetTop}>
             {g.sessions.some(s => s.name) ? (
               <>
                 <Typography
                   component="div"
                   variant="body2"
                   className={clsx({
                 "text-disabled": g.tutor === NO_TUTORS_LABEL || g.room === NO_ROOM_LABEL
               })}
                 >
                   {g.tutor || g.room}
                 </Typography>
                 {g.site && (
                 <Typography
                   component="div"
                   variant="body2"
                 >
                   {g.site}
                 </Typography>
               )}
               </>
              ) : (
                <Typography
                  component="div"
                  variant="body2"
                  className="text-disabled"
                >
                  Loading...
                </Typography>
              )}
           </Grid>

           <Grid item xs={10} className={classes.groupedDayWrapper}>
             {g.sessions.length ? (
                g.sessions.map(s => (
                  <CalendarSession
                    key={s.id}
                    tagsState={tagsState}
                    hideTutors={groupingState === "Group by tutor"}
                    hideRooms={groupingState === "Group by room"}
                    {...s}
                  />
                ))
              ) : (
                <Typography className="text-disabled dayOffset">available</Typography>
              )}
           </Grid>
         </Grid>
          ))}
     </>
    ) : (
      <Typography className="text-disabled dayOffset">available</Typography>
    ))
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
    tagsState,
    tagsUpdated,
    getSessionsTags,
    calendarGrouping
  } = props;

  const [gapHours, setGapHours] = useState([]);
  const [groupings, setGroupings] = useState<CalendarGrouping[]>([]);
  const [inView, setInView] = useState<boolean>(false);

  const sessionsIds = useMemo(() => sessions.map(s => s.id), [sessions]);

  useEffect(() => {
    if (!updated && !isScrolling && inView && sessions.length) {
      updateSessionsDetails(sessionsIds, monthIndex, dayIndex);
    }
    if (tagsState !== "Tag off" && updated && !tagsUpdated && !isScrolling && inView && sessionsIds.length) {
      getSessionsTags(sessionsIds, monthIndex, dayIndex);
    }
  }, [updated, tagsUpdated, isScrolling, inView, tagsState]);

  useEffect(() => {
    if (calendarMode === "Gap(Hours)" && sessions.length && (updated || tagsUpdated)) {
      setGapHours(getGapHours(sessions));
    }
  }, [calendarMode, sessions, updated, tagsUpdated]);

  useEffect(() => {
    if (calendarGrouping !== "No grouping" && sessions.length) {
      setGroupings(getGroupings(sessions, calendarGrouping));
    }
  }, [calendarGrouping, sessions, updated, tagsUpdated]);

  const dayNodeRef = useRef(null);

  useLayoutEffect(() => {
    if (dayNodesObserver && dayNodeRef.current) {
      dayNodeRef.current.setInView = setInView;
      dayNodeRef.current.isInView = inView;
      dayNodesObserver.observer.observe(dayNodeRef.current);
    }

    return () => {
      if (dayNodeRef.current && dayNodesObserver) dayNodesObserver.observer.unobserve(dayNodeRef.current);
    };
  }, [dayNodesObserver, dayNodeRef.current]);

  const renderedDays = useMemo(
    () => (sessions.length ? (
        sessions.map(s => (
          <CalendarSession
            key={s.id}
            tagsState={tagsState}
            {...s}
          />
        ))
      ) : (
        <Typography className="text-disabled dayOffset">available</Typography>
      )),
    [sessions, updated, selectedDayPeriods, tagsState, calendarGrouping]
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
          hasSessions={Boolean(sessions.length)}
          tagsUpdated={tagsUpdated}
          tagsState={tagsState}
        />
      ) : calendarGrouping === "No grouping" 
        ? renderedDays 
        : (
          <GroupingDay
            groupingState={calendarGrouping}
            hasSessions={Boolean(sessions.length)}
            classes={classes}
            groupings={groupings}
            tagsState={tagsState}
          />
        )}
    </CalendarDayBase>
  );
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  updateSessionsDetails: (ids: number[], monthIndex?: number, dayIndex?: number) =>
    dispatch(getTimetableSessionsByIds(ids, monthIndex, dayIndex)),
  getSessionsTags: (ids: number[], monthIndex?: number, dayIndex?: number) =>
    dispatch(getTimetableSessionsTags(ids, monthIndex, dayIndex))
});

export const CalendarDay = connect<any, any, any>(
  null,
  mapDispatchToProps
)(withStyles(CalendarDayWrapper, styles));
