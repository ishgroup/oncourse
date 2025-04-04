/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import KeyboardArrowDown from '@mui/icons-material/KeyboardArrowDown';
import { Button, Grid, List, ListItemButton, Radio, Typography } from '@mui/material';
import $t from '@t';
import clsx from 'clsx';
import { isBefore } from 'date-fns';
import { appendTimezone, AppTheme, formatCurrency } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import CalendarDayBase from '../../../../timetable/components/calendar/components/day/CalendarDayBase';
import CalendarMonthBase from '../../../../timetable/components/calendar/components/month/CalendarMonthBase';
import CalendarSession from '../../../../timetable/components/calendar/components/session/CalendarSession';
import { getAllMonthsWithSessions } from '../../../../timetable/utils';
import { filterPastClasses } from '../../../utils';

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
    showPastShevron: {
      transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
    },
    sessionButton: {
      textTransform: "initial",
      "& > span": {
        display: "block"
      }
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
  course, courseClasses, classes?, onSelect, isClassesEmpty, currencySymbol, selectedItems
}>(props => {
  const {
   course, courseClasses, classes, onSelect, isClassesEmpty, currencySymbol, selectedItems
  } = props;

  const [showPastClasses, setShowPastClasses] = React.useState(false);
  const [months, setMonths] = React.useState<any[]>([]);

  React.useEffect(() => {
    setShowPastClasses(isSelectedPassedClass(course));
  }, [course.id]);

  const togglePastClasses = React.useCallback(() => {
    setShowPastClasses(prev => !prev);
  }, []);

  const visibleClasses = React.useMemo(() => {
    if (!showPastClasses) {
      return filterPastClasses(courseClasses);
    }
    return courseClasses;
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
          visibleClasses.map(c => ({ ...c, start: c.startDateTime, end: c.endDateTime })),
          visibleClasses[0].siteTimezone && visibleClasses[0].siteTimezone.length > 0
            ? appendTimezone(new Date(visibleClasses[0].startDateTime), visibleClasses[0].siteTimezone)
            : new Date(visibleClasses[0].startDateTime)
        )
      );
    }
  }, [visibleClasses]);

  return !isClassesEmpty ? (
    <div className="p-2">
      <List className={classes.list}>
        <Grid item sm={12} className={clsx("text-center", classes.showPastRoot)}>
          <Button
            color="primary"
            classes={{
            root: clsx(!showPastClasses && classes.showPastButtonPressed),
            endIcon: classes.showPastShevron
          }}
            onClick={togglePastClasses}
            endIcon={<KeyboardArrowDown />}
            disabled={hidePassedClassesDisabled}
          >
            {showPastClasses ? "Hide finished classes" : "Show finished classes"}
          </Button>
        </Grid>
        {months.map((m, i) => (
          <CalendarMonthBase key={i} fullWidth {...m} showYear>
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
                      <Grid item xs={12} key={s.id}>
                        <Button
                          color="inherit"
                          onClick={onSelect && (() => onSelect(s))}
                          classes={{
                            disabled: classes.disabledSessionButton
                          }}
                          className={clsx("text-left w-100", classes.sessionButton)}
                          disabled={isSelected || isTransfered}
                        >
                          <Grid container>
                            <Grid item xs={1}>
                              <Radio color="primary" checked={isSelected} />
                            </Grid>
                            <Grid item xs={11} sm={7}>
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
                            <Grid item container xs={12} sm={4}>
                              <Grid item xs={6}>
                                <Typography component="div">
                                  {isTraineeship ? "1 place" : `${s.placesLeft} place${s.placesLeft > 1 ? "s" : ""}`}
                                </Typography>
                              </Grid>
                              <Grid item xs={4}>
                                <Typography component="div" className="text-end money">
                                  {formatCurrency(s.price, currencySymbol)}
                                </Typography>
                              </Grid>
                              <Grid item xs={2} />
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
      </List>
    </div>
  ) : (
    <div className={clsx("p-2 overflow-auto", classes.root)}>
      <ListItemButton alignItems="flex-start" className="justify-content-space-between p-0-5" disabled>
        <Typography component="div" variant="body1">
          {$t('there_are_no_classes_available_for_this_course')}
        </Typography>
      </ListItemButton>
    </div>
  );
});

export default withStyles(EnrolClassListView, styles);
