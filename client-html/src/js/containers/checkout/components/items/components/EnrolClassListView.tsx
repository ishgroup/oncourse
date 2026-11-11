/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { Button, Grid, List, ListItemButton, Radio, TableSortLabel, Typography } from '@mui/material';
import clsx from 'clsx';
import { isBefore } from 'date-fns';
import { appendTimezone, AppTheme, formatCurrency } from 'ish-ui';
import React from 'react';
import { useInView } from 'react-intersection-observer';
import { withStyles } from 'tss-react/mui';
import { useAppDispatch, useAppSelector } from '../../../../../common/utils/hooks';
import CalendarDayBase from '../../../../timetable/components/calendar/components/day/CalendarDayBase';
import CalendarMonthBase from '../../../../timetable/components/calendar/components/month/CalendarMonthBase';
import CalendarSession from '../../../../timetable/components/calendar/components/session/CalendarSession';
import { getAllMonthsWithSessions } from '../../../../timetable/utils';
import { checkoutClearCourseClassList, checkoutGetCourseClassList } from '../../../actions/chekoutItem';
import { filterPastClasses, getCourseClassSearch } from '../../../utils';

const styles = (theme: AppTheme, p, classes) => ({
  list: {
    padding: 0
  },
  showPastRoot: {
    paddingBottom: 5
  },
  showPastButtonPressed: {
    [`& .${classes.showPastShevron}`]: {
      transform: "rotate(180deg)"
    }
  },
  sortButton: {
    marginLeft: theme.spacing(1),
    '&&& .MuiTableSortLabel-icon': {
      color: theme.palette.primary.main
    }
  },
  showPastShevron: {
    transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
  },
  sessionButton: {
    textTransform: "initial",
    justifyContent: "flex-start"
  },
  sessionRow: {
    width: "100%",
    alignItems: "center"
  },
  disabledSessionButton: {
    color: `${theme.palette.text.disabled}`,
    [`& .${classes.disabledWarningColor}`]: {
      color: "#fdc5c1"
    }
  },
  disabledWarningColor: {}
});

const isSelectedPassedClass = course => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return course.class && course.class.endDateTime && isBefore(new Date(course.class.endDateTime), today);
};

const EnrolClassListView = React.memo<{
  course, classes?, onSelect, selectedItems
} >(props => {
  const {
    course, classes, onSelect, selectedItems
  } = props;

  const currencySymbol = useAppSelector(state => state.location.currency && state.location.currency.shortCurrencySymbol);
  const checkCourseClassLoaded = useAppSelector(state => state.checkout.checkCourseClassLoaded);
  const courseClasses = useAppSelector(state => state.checkout.courseClasses);
  const dispatch = useAppDispatch();
  const resetClasses = () => dispatch(checkoutClearCourseClassList());

  const [showPastClasses, setShowPastClasses] = React.useState(false);
  const [sortDescending, setSortDescending] = React.useState(false);
  const [months, setMonths] = React.useState([]);
  const [isMounted, setIsMounted] = React.useState(false);

  const onLoadMoreClasses = (offset: number, id: number, showPastClasses: boolean, descending: boolean) => !checkCourseClassLoaded &&
    dispatch(checkoutGetCourseClassList(
      getCourseClassSearch(id) + (showPastClasses ? ' and endDateTime <= today' : ' and (endDateTime >= today or type is DISTANT_LEARNING)'),
      offset,
      !(showPastClasses && descending)
    ));

  const { ref, inView } = useInView();

  React.useEffect(() => {
    setIsMounted(true);
  }, []);

  React.useEffect(() => {
    if (inView && course && courseClasses.length) {
      onLoadMoreClasses(courseClasses?.length, course?.courseId, showPastClasses, sortDescending);
    }
  }, [inView, courseClasses.length]);

  React.useEffect(() => {
    if (course?.id) {
      setShowPastClasses(isSelectedPassedClass(course));
    }
  }, [course?.id]);

  React.useEffect(() => {
    if (isMounted) {
      onLoadMoreClasses(courseClasses?.length, course?.courseId, showPastClasses, sortDescending);
    }
  }, [showPastClasses, sortDescending]);

  const togglePastClasses = React.useCallback(() => {
    resetClasses();
    setSortDescending(false);
    setShowPastClasses(prev => !prev);
  }, []);

  const toggleSortOrder = React.useCallback(() => {
    resetClasses();
    setSortDescending(prev => !prev);
  }, []);

  const visibleClasses = React.useMemo(() => {
    return filterPastClasses(courseClasses, !showPastClasses);
  }, [showPastClasses, courseClasses]);

  const hidePassedClassesDisabled = React.useMemo(() => isSelectedPassedClass(course), [course]);

  React.useEffect(() => {
    if (!visibleClasses.length) {
      setMonths([]);
      return;
    }

    if (visibleClasses.length > 0) {
      setMonths(
        getAllMonthsWithSessions(
          visibleClasses.map((c: any) => ({ ...c, start: c.startDateTime, end: c.endDateTime })),
          visibleClasses[0].siteTimezone && visibleClasses[0].siteTimezone.length > 0
            ? appendTimezone(new Date(visibleClasses[0].startDateTime), visibleClasses[0].siteTimezone)
            : new Date(visibleClasses[0].startDateTime)
        )
      );
    }
  }, [visibleClasses]);

  const orderedMonths = React.useMemo(() => (showPastClasses && sortDescending
    ? [...months]
      .sort((a, b) => b.month.getTime() - a.month.getTime())
      .map(m => ({ ...m, days: [...m.days].reverse() }))
    : months), [months, showPastClasses, sortDescending]);

  return (
    <div className="p-2">
      <List className={classes.list}>
        <Grid size={{ sm: 12 }} className={clsx("text-center", classes.showPastRoot)}>
          <Button
            color="primary"
            onClick={togglePastClasses}
            disabled={hidePassedClassesDisabled}
          >
            {showPastClasses ? "Hide" : "Show"} finished classes
          </Button>
          {showPastClasses && (
            <Button
              color="primary"
              onClick={toggleSortOrder}
              className={classes.sortButton}
            >
              <TableSortLabel active direction={sortDescending ? 'asc' : 'desc'}/>
              {sortDescending ? "Newest first" : "Oldest first"}
            </Button>
          )}
        </Grid>
        {checkCourseClassLoaded && courseClasses.length === 0 && <ListItemButton alignItems="center" className="justify-content-center p-0-5" disabled>
          <Typography component="div" variant="body1">
            There are no {showPastClasses ? 'finished' : 'current'} classes available for this course.
          </Typography>
        </ListItemButton>}
        {orderedMonths.map(m => (
          <CalendarMonthBase key={m.month.getTime()} fullWidth {...m} showYear>
            {m.days.map(d => {
              if (!d.sessions.length) {
                return null;
              }
              return (
                <CalendarDayBase day={d.day} timezone={d.timezone} key={d.day.toString()}>
                  {d.sessions.map(s => {
                    const isSelected = selectedItems.some(i => i.type === "course" && i.class?.id === s.id);
                    const isTransfered = course.transferedClassId === s.id;
                    const isTraineeship = course.isTraineeship === "true";

                    return (
                      <Grid size={12} key={s.id}>
                        <Button
                          color="inherit"
                          onClick={onSelect && (() => onSelect(s))}
                          classes={{
                            disabled: classes.disabledSessionButton
                          }}
                          className={clsx("text-left w-100", classes.sessionButton)}
                          disabled={isSelected || isTransfered}
                        >
                          <Grid container className={classes.sessionRow}>
                            <Grid size={1}>
                              <Radio color="primary" checked={isSelected} />
                            </Grid>
                            <Grid size={{ xs: 11, sm: 7 }}>
                              <CalendarSession
                                {...s}
                                startLabel={s.startDateTime ? null : s.isSelfPaced ? "Self \n paced" : "No start date"}
                                classes={{ warningColor: classes.disabledWarningColor }}
                                warningMessage={isTransfered ? "The student has transferred out of this class" : undefined}
                                disableLink
                                disableTags
                                inView
                              />
                            </Grid>
                            <Grid container size={{ xs: 12, sm: 4 }}>
                              <Grid size={6}>
                                <Typography component="div">
                                  {isTraineeship ? "1 place" : `${s.placesLeft} place${s.placesLeft > 1 ? "s" : ""}`}
                                </Typography>
                              </Grid>
                              <Grid size={4}>
                                <Typography component="div" className="text-end money">
                                  {formatCurrency(s.price, currencySymbol)}
                                </Typography>
                              </Grid>
                              <Grid size={2} />
                            </Grid>
                          </Grid>
                        </Button>
                      </Grid>

                    );
                  })}
                </CalendarDayBase>
              );
            })}
          </CalendarMonthBase>
        ))}
        <div ref={ref} />
      </List>
    </div>
  );
});

export default withStyles(EnrolClassListView, styles);
