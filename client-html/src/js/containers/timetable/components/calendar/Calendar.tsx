/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useContext, useEffect, useMemo, useRef, useState
} from "react";
import { createStyles, withStyles } from "@material-ui/core";
import AutoSizer from "react-virtualized-auto-sizer";
import { SearchRequest } from "@api/model";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
 addMonths, endOfMonth, format, isSameMonth, startOfMonth
} from "date-fns";
import clsx from "clsx";
import Typography from "@material-ui/core/Typography";
import CircularProgress from "@material-ui/core/CircularProgress/CircularProgress";
import { State } from "../../../../reducers/state";
import { clearTimetableMonths, findTimetableSessions } from "../../actions";
import { TimetableContext } from "../../Timetable";
import { DD_MMM_YYYY_MINUSED } from "../../../../common/utils/dates/format";
import { animateListScroll, attachDayNodesObserver } from "../../utils";
import CalendarMonth from "./components/month/CalendarMonth";
import CalendarModesSwitcher from "../modesSwitcher/CalendarModesSwitcher";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import DynamicSizeList from "../../../../common/components/form/DynamicSizeList";

const styles = theme => createStyles({
    root: {
      display: "flex"
    },
    list: {
      "& > div": {
        overflow: "visible !important"
      }
    },
    disableOutline: {
      outline: "none"
    },
    modesSwitcher: {
      position: "absolute",
      right: theme.spacing(5),
      top: theme.spacing(3),
      zIndex: 2
    },
    centered: {
      position: "absolute",
      width: "100%",
      height: "100%",
      display: "flex",
      zIndex: 1,
      background: theme.palette.background.default,
      justifyContent: "center",
      alignItems: "center"
    },
    hidden: {
      visibility: "hidden",
      zIndex: -1
    }
  });

interface Props {
  classes?: any;
  months?: any;
  search?: string;
  sessionsLoading?: boolean;
  selectedMonthSessionDays?: number[];
  getSessions?: (request: SearchRequest) => void;
  clearTimetableMonths?: AnyArgFunction;
}

const MonthRenderer = React.forwardRef<any, any>(({
 index, isScrolling, data, style
}, ref) => (
  <CalendarMonth
    parentRef={ref}
    index={index}
    isScrolling={isScrolling}
    style={style}
    dayNodesObserver={data.dayNodesObserver}
    tagsExpanded={data.tagsExpanded}
    setTagsExpanded={data.setTagsExpanded}
    {...data.months[index]}
  />
));

let scrollToTargetDayOnRender: Date = null;

const onRendered = ({
  visibleStopIndex, months, loadNextMonths, sessionsLoading
}) => {
  if (visibleStopIndex + 1 === months.length && !sessionsLoading) {
    const currentMonth = months[months.length - 1].month;

    const nextMonthsStart = addMonths(currentMonth, 1);

    if (nextMonthsStart.getFullYear() !== currentMonth.getFullYear()) {
      return;
    }

    loadNextMonths(nextMonthsStart);
  }
};

const scrollToCalendarDay = (day: Date, list, index, dayNodesObserver) => {
  const dayId = format(day, DD_MMM_YYYY_MINUSED);

  const dayNode = document.getElementById(dayId);

  if (dayNode) {
    animateListScroll(
      list,
      dayNode.parentElement.offsetTop + list._instanceProps.itemMetadataMap[index].offset - 32,
      list._outerRef.scrollTop,
      performance.now(),
      dayNodesObserver
    );
  }
};

const Calendar: React.FunctionComponent<Props> = React.memo(props => {
  const {
   targetDay, setTargetDay, selectedWeekDays, selectedDayPeriods, calendarMode
  } = useContext(
      TimetableContext
    );

  const {
    search,
    classes,
    months,
    getSessions,
    selectedMonthSessionDays,
    clearTimetableMonths,
    sessionsLoading
  } = props;

  const [dayNodesObserver, setDayNodesObserver] = useState<any>();
  const [tagsExpanded, setTagsExpanded] = useState(true);

  const listEl = useRef(null);

  const targetDayHandler = (day: Date) => {
    if (listEl.current && listEl.current.state.isScrolling) {
      setTargetDay(day);
    }
  };

  const scrollToDayHandler = targetDayMonthIndex => {
    scrollToCalendarDay(targetDay, listEl.current, targetDayMonthIndex, dayNodesObserver);
  };

  // fetch next two months
  const loadNextMonths = useCallback( baseDate => {
    const startMonth = startOfMonth(baseDate);

    const endMonth = endOfMonth(addMonths(startMonth, 1));

    getSessions({ from: startMonth.toISOString(), to: endMonth.toISOString(), search });
  }, [search, getSessions]);

  const onRowsRendered = useCallback(args => onRendered({
   ...args, months, loadNextMonths, sessionsLoading
  }), [months, loadNextMonths, sessionsLoading]);

  useEffect(() => {
    if (!dayNodesObserver && listEl.current) {
      setDayNodesObserver(attachDayNodesObserver(listEl.current._outerRef, targetDayHandler));
    }
    return () => {
      if (dayNodesObserver) {
        dayNodesObserver.observer.disconnect();
      }
    };
  }, [listEl.current, dayNodesObserver]);

  useEffect(() => {
    if (!listEl.current || listEl.current.state.isScrolling) {
      return;
    }

    const targetDayMonthIndex = months.findIndex(m => isSameMonth(m.month, targetDay));

    if (targetDayMonthIndex !== -1) {
      if (calendarMode === "Compact" && !selectedMonthSessionDays.includes(targetDay.getDate())) {
        return;
      }
      listEl.current.scrollToItem(targetDayMonthIndex);
      scrollToDayHandler(targetDayMonthIndex);
    } else {
      clearTimetableMonths();
      scrollToTargetDayOnRender = targetDay;
      loadNextMonths(targetDay);
    }
  }, [targetDay, listEl.current]);

  useEffect(() => {
    scrollToDayHandler(months.findIndex(m => isSameMonth(m.month, targetDay)));
  }, [selectedWeekDays, selectedDayPeriods, calendarMode]);

  useEffect(() => {
    if (scrollToTargetDayOnRender && months.length) {
      const targetDayMonthIndex = months.findIndex(m => isSameMonth(m.month, scrollToTargetDayOnRender));
      scrollToDayHandler(targetDayMonthIndex);
      scrollToTargetDayOnRender = null;
    }
  }, [months]);

  const hasSessions = useMemo(() => months.some(m => m.hasSessions), [months]);

  const itemData = useMemo(() => ({
   months, dayNodesObserver, tagsExpanded, setTagsExpanded
  }), [months, dayNodesObserver, tagsExpanded, setTagsExpanded]);

  return (
    <div className={classes.root}>
      {!sessionsLoading && !hasSessions && (
        <div className={classes.centered}>
          <div className="noRecordsMessage">
            <Typography variant="h6" color="inherit" align="center">
              No sessions were found
            </Typography>
          </div>
        </div>
      )}

      <div className={`${classes.centered} ${months.length === 0 && sessionsLoading ? "" : classes.hidden}`}>
        <CircularProgress size={40} thickness={5} />
      </div>

      <AutoSizer>
        {({ width, height }) => (
          <DynamicSizeList
            listRef={listEl}
            className={clsx(classes.list, classes.disableOutline)}
            height={height}
            width={width}
            overscanCount={0}
            initialScrollOffset={0}
            estimatedItemSize={1000}
            itemData={itemData}
            itemCount={months.length}
            onItemsRendered={onRowsRendered}
            useIsScrolling
          >
            {MonthRenderer}
          </DynamicSizeList>
          )}
      </AutoSizer>

      <CalendarModesSwitcher className={classes.modesSwitcher} />
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.timetable.search,
  months: state.timetable.months,
  sessionsLoading: state.timetable.sessionsLoading,
  selectedMonthSessionDays: state.timetable.selectedMonthSessionDays
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getSessions: (request: SearchRequest) => dispatch(findTimetableSessions(request)),
  clearTimetableMonths: () => dispatch(clearTimetableMonths())
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(Calendar));
