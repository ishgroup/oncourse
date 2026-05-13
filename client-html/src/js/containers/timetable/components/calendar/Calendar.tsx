/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { addMonths, endOfMonth, format, isAfter, isSameMonth, startOfMonth } from 'date-fns';
import { debounce } from 'es-toolkit/compat';
import { DD_MMM_YYYY_MINUSED, makeAppStyles, usePrevious } from 'ish-ui';
import React, { useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router-dom';
import { Virtuoso, VirtuosoHandle } from 'react-virtuoso';
import { Dispatch } from 'redux';
import { IAction } from '../../../../common/actions/IshAction';
import instantFetchErrorHandler from '../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import { getFiltersNameString, } from '../../../../common/components/list-view/utils/listFiltersUtils';
import EntityService from '../../../../common/services/EntityService';
import { updateHistory } from '../../../../common/utils/common';
import { CoreFilter } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';
import {
  clearTimetableMonths,
  findTimetableSessions,
  getTimetableSessionsDays,
  setTimetableMonths,
  setTimetableSearch
} from '../../actions';
import { TimetableContext } from '../../Timetable';
import { animateListScroll, attachDayNodesObserver, getFormattedMonthDays } from '../../utils';
import CalendarMonth from './components/month/CalendarMonth';
import CalendarGroupingsSwitcher from './components/switchers/CalendarGroupingsSwitcher';
import CalendarModesSwitcher from './components/switchers/CalendarModesSwitcher';
import CalendarTagsSwitcher from './components/switchers/CalendarTagsSwitcher';

const useStyles = makeAppStyles()(theme => ({
  root: {
    display: "flex",
    position: "relative",
    height: "100%",
    minHeight: 0
  },
  list: {
    flex: 1,
    "& > div": {
      overflow: "visible !important"
    }
  },
  disableOutline: {
    outline: "none"
  },
  modesSwitcher: {
    display: "flex",
    flexDirection: "column",
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
}));

interface Props extends RouteComponentProps {
  classes?: any;
  months?: any;
  search?: string;
  searchError?: boolean;
  filters?: CoreFilter[];
  sessionsLoading?: boolean;
  selectedMonthSessionDays?: number[];
  dispatch?: Dispatch<IAction>;
}

const MonthRenderer = React.memo<{ index, isScrolling, dayNodesObserver, month }>(({
 index, isScrolling, dayNodesObserver, month
}) => (
  <CalendarMonth
    index={index}
    isScrolling={isScrolling}
    dayNodesObserver={dayNodesObserver}
    {...month}
  />
));

const onRendered = ({
  visibleStartIndex,
  visibleStopIndex,
  months,
  loadNextMonths
}) => {
  if (visibleStopIndex >= visibleStartIndex && months.length < 12 && months[months.length - 1]) {
    const currentMonth = months[months.length - 1].month;

    const nextMonthsStart = addMonths(currentMonth, 1);

    if (nextMonthsStart.getFullYear() !== currentMonth.getFullYear()) {
      return;
    }

    loadNextMonths(nextMonthsStart);
  }
};

const scrollToCalendarDay = debounce((dayNode, list, scroller, dayNodesObserver) => {
  if (dayNode && list && scroller && dayNodesObserver) {
    const dayRect = dayNode.getBoundingClientRect();
    const scrollerRect = scroller.getBoundingClientRect();
    const scrollTopFinal = scroller.scrollTop + dayRect.top - scrollerRect.top - 32;

    animateListScroll(
      list,
      scrollTopFinal,
      scroller.scrollTop || 0,
      performance.now(),
      dayNodesObserver
    );
  }
}, 100);

const initDateAsync = async (searchString, setTargetDay) => {
  const firstSession = await EntityService.getPlainRecords(
    "Session",
    "startDatetime",
    `${searchString}`,
    1,
    0,
    "startDatetime",
    true
  );

  const lastSession = await EntityService.getPlainRecords(
    "Session",
    "startDatetime",
    `${searchString}`,
    1,
    0,
    "startDatetime",
    false
  );
  
  if (firstSession.rows.length && lastSession.rows.length) {
    const endDate = new Date(lastSession.rows[0].values[0]);
    const firstDate = new Date(firstSession.rows[0].values[0]);

    if (isAfter(new Date(), endDate) || isAfter(firstDate, new Date())) {
      setTargetDay(firstDate);
    } else {
      setTargetDay(new Date());
    }
  }
};

const Calendar = React.memo<Props>(props => {
  const {
    targetDay,
    setTargetDay,
    selectedMonth,
    selectedWeekDays,
    selectedDayPeriods,
    calendarMode
  } = useContext(
    TimetableContext
  );

  const {
    search,
    months,
    selectedMonthSessionDays,
    sessionsLoading,
    location,
    filters,
    searchError,
    match: { url },
    dispatch,
  } = props;

  const { classes } = useStyles();

  const [dayNodesObserver, setDayNodesObserver] = useState<any>();
  const [scrollToTargetDayOnRender, setScrollToTargetDayOnRender] = useState(targetDay);
  const [isScrolling, setIsScrolling] = useState(false);

  const listEl = useRef<VirtuosoHandle>(null);
  const scrollerEl = useRef<HTMLElement>(null);
  const isScrollingRef = useRef(false);
  const renderedArgs = useRef<any>(null);

  const prevSearch = usePrevious(search, "");

  const params = new URLSearchParams(location.search);

  const targetDayHandler = (day: Date) => {
    if (isScrollingRef.current) {
      setTargetDay(day);
    }
  };

  const scrollToDayHandler = (targetDayMonthIndex, node?) => {
    const dayNode = node || document.getElementById(format(targetDay, DD_MMM_YYYY_MINUSED));
    scrollToCalendarDay(dayNode, listEl.current, scrollerEl.current, dayNodesObserver);
  };

  // fetch next two months
  const loadNextMonths = useCallback(debounce((baseDate, reset = false) => {
    const startMonth = startOfMonth(baseDate);
    const endMonth = startOfMonth(addMonths(startMonth, 1));

    dispatch(setTimetableMonths([
      {
        month: startMonth,
        days: getFormattedMonthDays(startMonth),
        hasSessions: false
      },
      {
        month: endMonth,
        days: getFormattedMonthDays(endMonth),
        hasSessions: false
      }
    ], true));
    
    dispatch(findTimetableSessions({ from: startMonth.toISOString(), to: endOfMonth(endMonth).toISOString() }, reset));
  }, 100), []);

  const onRangeChanged = range => {
    renderedArgs.current = {
      visibleStartIndex: range.startIndex,
      visibleStopIndex: range.endIndex
    };
  };

  const onIsScrolling = scrolling => {
    isScrollingRef.current = scrolling;
    setIsScrolling(scrolling);
  };

  const onScrollerRef = ref => {
    if (ref instanceof HTMLElement) {
      scrollerEl.current = ref;
    }
  };
  
  useEffect(() => {
    if (!sessionsLoading && renderedArgs.current) {
      onRendered({ ...renderedArgs.current, months, loadNextMonths });
    }
  }, [sessionsLoading]);

  // Search effects
  useEffect(() => {
    const title = params.get("title");

    if (title) {
      window.document.title = title;
      params.delete("title");
    }

    const searchString = params.get("search");
    dispatch(setTimetableSearch(searchString ? decodeURIComponent(searchString) : ""));

    if (searchString && !params.get("selectedDate")) {
      try {
        initDateAsync(searchString, setTargetDay);
      } catch (e) {
        instantFetchErrorHandler(dispatch, e);
      }
    }

    loadNextMonths(targetDay, true);
  }, []);

  useEffect(() => {
    const calendarModeUrl = params.get("calendarMode");

    if (calendarModeUrl !== calendarMode) {
      params.set("calendarMode", calendarMode);
      updateHistory(params, url);
    }
  }, [calendarMode]);

  useEffect(() => {
    const targetDayUrlString = params.get("selectedDate");
    const targetDayString = format(targetDay, DD_MMM_YYYY_MINUSED);

    if (targetDayUrlString !== targetDayString) {
      params.set("selectedDate", targetDayString);
      updateHistory(params, url);
    }
  }, [targetDay]);

  useEffect(() => {
    if (filters.length) {
      const filtersString = getFiltersNameString([{ filters }]) || null;
      const filtersUrlString = params.get("filter");

      if (filtersString !== filtersUrlString) {
        if (filtersString) {
          params.set("filter", filtersString);
        } else {
          params.delete("filter");
        }
        updateHistory(params, url);
        loadNextMonths(selectedMonth, true);
      }
    }
  }, [filters]);

  useEffect(() => {
    if (prevSearch !== search) {
      if (search) {
        params.set("search", encodeURIComponent(search));
      } else {
        params.delete("search");
      }
      updateHistory(params, url);
      loadNextMonths(selectedMonth, true);
    }
  }, [search, prevSearch]);

  useEffect(() => {
    dispatch(getTimetableSessionsDays(selectedMonth.getMonth(), selectedMonth.getFullYear()));
  }, [selectedMonth, search, filters]);

  // Layout effects
  useEffect(() => {
    if (!dayNodesObserver && scrollerEl.current) {
      setDayNodesObserver(attachDayNodesObserver(scrollerEl.current, targetDayHandler));
    }
    return () => {
      if (dayNodesObserver) {
        dayNodesObserver.observer.disconnect();
      }
    };
  }, [scrollerEl.current, dayNodesObserver]);

  useEffect(() => {
    if (!listEl.current || isScrollingRef.current) {
      return;
    }

    const targetDayMonthIndex = months.findIndex(m => isSameMonth(m.month, targetDay));

    if (targetDayMonthIndex > -1 && targetDayMonthIndex < 2) {
      if (calendarMode === "Compact" && !(selectedMonthSessionDays[targetDay.getDate() - 1] !== 0)) {
        return;
      }
      scrollToDayHandler(targetDayMonthIndex);
    } else {
      dispatch(clearTimetableMonths());
      setScrollToTargetDayOnRender(targetDay);
      loadNextMonths(targetDay);
    }
  }, [targetDay, dayNodesObserver]);

  useEffect(() => {
    scrollToDayHandler(months.findIndex(m => isSameMonth(m.month, targetDay)));
  }, [selectedWeekDays, selectedDayPeriods, calendarMode]);

  useEffect(() => {
    const dayNode = document.getElementById(format(targetDay, DD_MMM_YYYY_MINUSED));
    if (dayNode && dayNodesObserver && scrollToTargetDayOnRender && months.length) {
      const targetDayMonthIndex = months.findIndex(m => isSameMonth(m.month, scrollToTargetDayOnRender));
      scrollToDayHandler(targetDayMonthIndex, dayNode);
      setScrollToTargetDayOnRender(null);
    }
  }, [months, targetDay, scrollToTargetDayOnRender, dayNodesObserver]);

  const hasSessions = useMemo(() => (calendarMode === "Compact" ? months.some(m => m.hasSessions) : true), [months, calendarMode]);

  const computeMonthKey = useCallback((index, month) => format(month.month, "yyyy-MM"), []);

  const renderMonth = useCallback((index, month) => (
    <MonthRenderer
      index={index}
      isScrolling={isScrolling}
      dayNodesObserver={dayNodesObserver}
      month={month}
    />
  ), [isScrolling, dayNodesObserver]);

  return (
    <div className={classes.root}>
      {((!sessionsLoading && !hasSessions) || searchError) && (
        <div className={classes.centered}>
          <div className="noRecordsMessage">
            <Typography variant="h6" color="inherit" align="center">
              {$t('no_sessions_were_found')}
            </Typography>
          </div>
        </div>
      )}

      <Virtuoso
        ref={listEl}
        scrollerRef={onScrollerRef}
        className={clsx(classes.list, classes.disableOutline)}
        style={{ height: "100%", width: "100%" }}
        data={months}
        computeItemKey={computeMonthKey}
        defaultItemHeight={1000}
        overscan={0}
        rangeChanged={onRangeChanged}
        isScrolling={onIsScrolling}
        itemContent={renderMonth}
      />
      <div className={classes.modesSwitcher}>
        <CalendarModesSwitcher TimetableContext={TimetableContext} />
        <CalendarTagsSwitcher className="mt-2" TimetableContext={TimetableContext} />
        <CalendarGroupingsSwitcher className="mt-2" TimetableContext={TimetableContext} />
      </div>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.timetable.search,
  months: state.timetable.months,
  filters: state.timetable.filters,
  searchError: state.timetable.searchError,
  sessionsLoading: state.timetable.sessionsLoading,
  selectedMonthSessionDays: state.timetable.selectedMonthSessionDays
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)((withRouter(Calendar)));
