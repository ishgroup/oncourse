/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { isBefore } from "date-fns";
import React from "react";
import clsx from "clsx";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import Typography from "@material-ui/core/Typography";
import ListItem from "@material-ui/core/ListItem";
import KeyboardArrowDown from "@material-ui/icons/KeyboardArrowDown";
import Button from "@material-ui/core/Button";
import { prefixer } from "../../../../../common/styles/mixins/prefixer";
import { filterPastClasses } from "../../../utils";
import { AppTheme } from "../../../../../model/common/Theme";
import { getAllMonthsWithSessions } from "../../../../timetable/utils";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";
import CalendarMonthBase from "../../../../timetable/components/calendar/components/month/CalendarMonthBase";
import CalendarDayBase from "../../../../timetable/components/calendar/components/day/CalendarDayBase";
import CalendarSession from "../../../../timetable/components/calendar/components/session/CalendarSession";
import { formatCurrency } from "../../../../../common/utils/numbers/numbersNormalizing";

const styles = (theme: AppTheme) => createStyles({
    root: {
      marginTop: 60
    },
    list: {
      padding: 0
    },
    showPastRoot: {
      paddingBottom: 5
    },
    showPastButtonPressed: {
      "& $showPastShevron": {
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
    selectedClass: {
      backgroundColor: "rgba(0, 0, 0, 0.08)"
    },
    disabledSessionButton: {
      color: `${theme.palette.text.disabled} !important`,
      "& $disabledWarningColor": {
        color: "#fdc5c1"
      }
    },
    disabledWarningColor: {}
  });

const hasSelectedPassedClasses = (course, selectedItems) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return selectedItems.some(i => i.id === course.id && course.class.endDateTime && isBefore(new Date(course.class.endDateTime), today));
};

const EnrolClassListView = React.memo<any>(props => {
  const {
   course, courseClasses, classes, onSelect, isClassesEmpty, selectedItems, currencySymbol
  } = props;

  const [showPastClasses, setShowPastClasses] = React.useState(false);
  const [months, setMonths] = React.useState<any[]>([]);

  React.useEffect(() => {
    setShowPastClasses(hasSelectedPassedClasses(course, selectedItems));
  }, [course.id, selectedItems.length]);

  const togglePastClasses = React.useCallback(() => {
    setShowPastClasses(prev => !prev);
  }, []);

  const visibleClasses = React.useMemo(() => {
    if (!showPastClasses) {
      return filterPastClasses(courseClasses);
    }
    return courseClasses;
  }, [showPastClasses, courseClasses]);

  const hidePassedClassesDisabled = React.useMemo(() => hasSelectedPassedClasses(course, selectedItems), [course, selectedItems]);

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
            ? new Date(visibleClasses[0].startDateTime)
            : appendTimezone(new Date(visibleClasses[0].startDateTime), visibleClasses[0].siteTimezone)
        )
      );
    }
  }, [visibleClasses]);

  return !isClassesEmpty ? (
    <div className={clsx("p-2 overflow-auto", classes.root)}>
      <List className={clsx("user-select-none", classes.list)}>
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
                    const isDisabled = selectedItems.some(i => i.id === s.id);
                    const isSelected = !isDisabled && course.class && course.class.id === s.id;
                    const isTransfered = course.transferedClassId === s.id;
                    const isTraineeship = course.isTraineeship === "true";

                    return (
                      <Button
                        key={s.id}
                        onClick={onSelect && (() => onSelect(s))}
                        classes={{
                          disabled: classes.disabledSessionButton
                        }}
                        className={clsx("text-left", classes.sessionButton, isSelected && classes.selectedClass)}
                        disabled={isDisabled || isTransfered}
                        fullWidth
                      >
                        <Grid container>
                          <Grid item xs={12} sm={8}>
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
                          <Grid item xs={12} sm={4}>
                            <Grid container>
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
                        </Grid>
                      </Button>
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
      <ListItem alignItems="flex-start" className="justify-content-space-between p-0-5" disabled>
        <Typography component="div" variant="body1">
          There are no classes available for this course.
        </Typography>
      </ListItem>
    </div>
  );
});

export default withStyles(styles)(EnrolClassListView);
