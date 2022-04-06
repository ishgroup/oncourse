/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
 useCallback, useContext, useEffect, useMemo, useRef, useState
} from "react";
import { createStyles, withStyles } from "@mui/styles";
import AutoSizer from "react-virtualized-auto-sizer";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
 addMonths, endOfMonth, format, isSameMonth, startOfMonth
} from "date-fns";
import clsx from "clsx";
import Typography from "@mui/material/Typography";
import CircularProgress from "@mui/material/CircularProgress/CircularProgress";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { State } from "../../../../reducers/state";
import {
  clearTimetableMonths,
  findTimetableSessions,
  getTimetableSessionsDays,
  setTimetableSearch
} from "../../actions";
import { TimetableContext } from "../../Timetable";
import { DD_MMM_YYYY_MINUSED } from "../../../../common/utils/dates/format";
import { animateListScroll, attachDayNodesObserver } from "../../utils";
import CalendarMonth from "./components/month/CalendarMonth";
import CalendarModesSwitcher from "../modesSwitcher/CalendarModesSwitcher";
import DynamicSizeList from "../../../../common/components/form/DynamicSizeList";
import { usePrevious } from "../../../../common/utils/hooks";

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

interface Props extends RouteComponentProps {
  classes?: any;
  months?: any;
  search?: string;
  sessionsLoading?: boolean;
  selectedMonthSessionDays?: number[];
  dispatch?: Dispatch;
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

const Calendar = React.memo<Props>(props => {
  const {
   targetDay, setTargetDay, selectedMonth, selectedWeekDays, selectedDayPeriods, calendarMode
  } = useContext(
    TimetableContext
  );

  const {
    search,
    classes,
    months,
    selectedMonthSessionDays,
    sessionsLoading,
    location,
    history,
    match: { url },
    dispatch,
  } = props;

  const [dayNodesObserver, setDayNodesObserver] = useState<any>();
  const [tagsExpanded, setTagsExpanded] = useState(true);

  const listEl = useRef(null);

  const prevSearch = usePrevious(search,"");
  const prevLocationSearch = usePrevious(location.search,"");

  const updateHistory = (pathname, search) => {
    const newUrl = window.location.origin + pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search
      });
    }
  };

  const targetDayHandler = (day: Date) => {
    if (listEl.current && listEl.current.state.isScrolling) {
      setTargetDay(day);
    }
  };

  const scrollToDayHandler = targetDayMonthIndex => {
    scrollToCalendarDay(targetDay, listEl.current, targetDayMonthIndex, dayNodesObserver);
  };

  // fetch next two months
  const loadNextMonths = baseDate => {
    const startMonth = startOfMonth(baseDate);

    const endMonth = endOfMonth(addMonths(startMonth, 1));

    dispatch(findTimetableSessions({ from: startMonth.toISOString(), to: endMonth.toISOString() }));
  };

  const onRowsRendered = useCallback(args => onRendered({
   ...args, months, loadNextMonths, sessionsLoading
  }), [months, loadNextMonths, sessionsLoading]);

  // Search effects
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const title = params.get("title");
    const searchString = params.get("search");

    if (title) {
      window.document.title = title;
    }

    if (searchString && !search) {
      dispatch(setTimetableSearch(decodeURIComponent(searchString)));
    }
  }, []);

  useEffect(() => {
    const params = new URLSearchParams(location.search);

    if (prevSearch !== search) {
      if (search) {
        params.set("search", encodeURIComponent(search));
      } else {
        params.delete("search");
      }
      const paramsString = decodeURIComponent(params.toString());
      updateHistory(url, paramsString ? "?" + paramsString : "");
    }

    if (prevLocationSearch !== location.search) {
      const prevUrlSearch = new URLSearchParams(prevLocationSearch);
      const filtersUrlString = params.get("filter");
      const searchString = params.get("search");

      // if (prevUrlSearch.get("filter") !== filtersUrlString) {
      //   const filtersString = getFiltersNameString(filterGroups);
      //   if (filtersString !== filtersUrlString) {
      //     const updated = [...filterGroups];
      //     this.setActiveFiltersBySearch(filtersUrlString, filterGroups);
      //     this.onChangeFilters(updated, "filters");
      //   }
      // }

      if (prevUrlSearch.get("search") !== searchString) {
        dispatch(setTimetableSearch(searchString ? decodeURIComponent(searchString) : ""));
      }
    }
  }, [search, url, prevSearch, prevLocationSearch, location.search]);

  useEffect(() => {
    if (search !== prevSearch) {
      const startMonth = startOfMonth(selectedMonth);
      const endMonth = endOfMonth(addMonths(startMonth, 1));

      dispatch(getTimetableSessionsDays(selectedMonth.getMonth(), selectedMonth.getFullYear()));
      dispatch(findTimetableSessions({ from: startMonth.toISOString(), to: endMonth.toISOString() }));
    }
  }, [search, prevSearch]);

  // Layout effects
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
      if (calendarMode === "Compact" && !(selectedMonthSessionDays[targetDay.getDate() - 1] !== 0)) {
        return;
      }
      listEl.current.scrollToItem(targetDayMonthIndex);
      scrollToDayHandler(targetDayMonthIndex);
    } else {
      dispatch(clearTimetableMonths());
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
  dispatch
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(withRouter(Calendar)));
