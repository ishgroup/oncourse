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
import { addMonths, endOfMonth, format, isAfter, parse, startOfMonth } from 'date-fns';
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
import { TimetableMonth } from '../../../../model/timetable';
import { State } from '../../../../reducers/state';
import {
  clearTimetableScrollDay,
  findTimetableSessions,
  getTimetableSessionsDays,
  setTimetableMonths,
  setTimetableSearch
} from '../../actions';
import { TimetableContext } from '../../Timetable';
import { attachDayNodesObserver, getFormattedMonthDays } from '../../utils';
import CalendarMonth from './components/month/CalendarMonth';
import CalendarGroupingsSwitcher from './components/switchers/CalendarGroupingsSwitcher';
import CalendarModesSwitcher from './components/switchers/CalendarModesSwitcher';
import CalendarTagsSwitcher from './components/switchers/CalendarTagsSwitcher';

const SCROLL_DAY_OFFSET = 32;
const SCROLL_DAY_SETTLE_DELAY = 150;
const SCROLL_DAY_SMOOTH_SETTLE_DELAY = 500;
const SCROLL_DAY_FINAL_THRESHOLD = 1;

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
  months?: TimetableMonth[];
  search?: string;
  searchError?: boolean;
  filters?: CoreFilter[];
  sessionsLoading?: boolean;
  selectedMonthSessionDays?: number[];
  dispatch?: Dispatch<IAction>;
  scrollToDay?: string;
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

const getScrollDayAnchor = (dayId: string) => document.querySelector(`[data-day-anchor="${dayId}"]`) as HTMLElement;

const getScrollDayDelta = (dayNode: HTMLElement, scroller: HTMLElement) => (
  dayNode.getBoundingClientRect().top - scroller.getBoundingClientRect().top - SCROLL_DAY_OFFSET
);

const parseTimetableDay = (dayId: string) => parse(dayId, DD_MMM_YYYY_MINUSED, new Date());

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
    const firstDate = firstSession.rows[0].values[0];

    if (isAfter(new Date(), endDate) || isAfter(new Date(firstDate), new Date())) {
      setTargetDay(firstDate);
    }
  }
};

const Calendar = React.memo<Props>(props => {
  const {
    setTargetDay,
    selectedMonth,
    calendarMode
  } = useContext(
    TimetableContext
  );

  const {
    search,
    months,
    sessionsLoading,
    location,
    filters,
    searchError,
    scrollToDay,
    match: { url },
    dispatch,
  } = props;

  const { classes } = useStyles();

  const [dayNodesObserver, setDayNodesObserver] = useState<any>();
  const [isScrolling, setIsScrolling] = useState(false);

  const listEl = useRef<VirtuosoHandle>(null);
  const scrollerEl = useRef<HTMLElement>(null);
  const isScrollingRef = useRef(false);
  const renderedArgs = useRef<any>(null);
  const pendingScrollDayRef = useRef<string>(null);
  const scrollSettleTimeoutRef = useRef<number>(null);
  const scrollAnimationFrameRef = useRef<number>(null);
  const requestedScrollMonthRef = useRef<string>(null);

  const prevSearch = usePrevious(search, "");

  const params = new URLSearchParams(location.search);

  const clearPendingScrollDay = useCallback(() => {
    pendingScrollDayRef.current = null;
    requestedScrollMonthRef.current = null;
    if (dayNodesObserver) {
      dayNodesObserver.preventUpdate = false;
    }
    if (scrollSettleTimeoutRef.current) {
      window.clearTimeout(scrollSettleTimeoutRef.current);
      scrollSettleTimeoutRef.current = null;
    }
    if (scrollAnimationFrameRef.current) {
      window.cancelAnimationFrame(scrollAnimationFrameRef.current);
      scrollAnimationFrameRef.current = null;
    }
  }, [dayNodesObserver]);

  const alignToScrollDay = useCallback((behavior: ScrollBehavior = "auto") => {
    const scrollDay = pendingScrollDayRef.current;
    const list = listEl.current;
    const scroller = scrollerEl.current;

    if (!scrollDay || !list || !scroller) {
      return false;
    }

    const dayNode = getScrollDayAnchor(scrollDay);

    if (!dayNode) {
      return false;
    }

    const delta = getScrollDayDelta(dayNode, scroller);

    if (Math.abs(delta) > SCROLL_DAY_FINAL_THRESHOLD) {
      list.scrollBy({ top: delta, behavior });
    }

    return true;
  }, []);

  const scheduleScrollDayAlignment = useCallback((
    behavior: ScrollBehavior = "auto",
    clearWhenSettled = false,
    delay = 0
  ) => {
    if (!pendingScrollDayRef.current) {
      return;
    }

    if (scrollSettleTimeoutRef.current) {
      window.clearTimeout(scrollSettleTimeoutRef.current);
    }

    scrollSettleTimeoutRef.current = window.setTimeout(() => {
      scrollSettleTimeoutRef.current = null;
      if (scrollAnimationFrameRef.current) {
        window.cancelAnimationFrame(scrollAnimationFrameRef.current);
      }

      scrollAnimationFrameRef.current = window.requestAnimationFrame(() => {
        scrollAnimationFrameRef.current = null;
        const hadAnchor = alignToScrollDay(behavior);

        if (clearWhenSettled && hadAnchor) {
          if (behavior === "smooth") {
            scheduleScrollDayAlignment("auto", true, SCROLL_DAY_SMOOTH_SETTLE_DELAY);
            return;
          }

          scrollAnimationFrameRef.current = window.requestAnimationFrame(() => {
            scrollAnimationFrameRef.current = null;
            const scrollDay = pendingScrollDayRef.current;
            const dayNode = getScrollDayAnchor(scrollDay);
            const delta = dayNode && scrollerEl.current ? getScrollDayDelta(dayNode, scrollerEl.current) : 0;

            if (Math.abs(delta) <= SCROLL_DAY_FINAL_THRESHOLD) {
              clearPendingScrollDay();
              dispatch(clearTimetableScrollDay());
            } else {
              scheduleScrollDayAlignment("auto", true, SCROLL_DAY_SETTLE_DELAY);
            }
          });
        }
      });
    }, delay);
  }, [alignToScrollDay, clearPendingScrollDay, dispatch]);

  // fetch next two months
  const loadNextMonths = useCallback(debounce((baseDate: Date, reset = false) => {
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
    ], !reset));
    
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
    const selectedDate = params.get("selectedDate");
    dispatch(setTimetableSearch(searchString ? decodeURIComponent(searchString) : ""));

    if (searchString && !selectedDate) {
      try {
        initDateAsync(searchString, setTargetDay);
      } catch (e) {
        instantFetchErrorHandler(dispatch, e);
      }
    }
    loadNextMonths(selectedDate ? parseTimetableDay(selectedDate) : new Date(), true);
  }, []);

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
      setDayNodesObserver(attachDayNodesObserver(scrollerEl.current, setTargetDay));
    }
    return () => {
      if (dayNodesObserver) {
        dayNodesObserver.observer.disconnect();
      }
    };
  }, [scrollerEl.current, dayNodesObserver]);

  useEffect(() => {
    if (dayNodesObserver && pendingScrollDayRef.current) {
      dayNodesObserver.preventUpdate = true;
    }
  }, [dayNodesObserver]);

  useEffect(() => {
    if (scrollToDay) {
      pendingScrollDayRef.current = scrollToDay;
      if (dayNodesObserver) {
        dayNodesObserver.preventUpdate = true;
      }
      const dayNode = getScrollDayAnchor(scrollToDay);

      if (dayNode) {
        scheduleScrollDayAlignment(sessionsLoading ? "auto" : "smooth", !sessionsLoading);
      } else {
        const scrollDay = parseTimetableDay(scrollToDay);
        const scrollMonth = format(scrollDay, "yyyy-MM");

        if (requestedScrollMonthRef.current !== scrollMonth) {
          requestedScrollMonthRef.current = scrollMonth;
          loadNextMonths(scrollDay, true);
        }
      }
    }
  }, [scrollToDay, months, sessionsLoading, dayNodesObserver, scheduleScrollDayAlignment]);

  useEffect(() => () => clearPendingScrollDay(), []);

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
        totalListHeightChanged={() => scheduleScrollDayAlignment("auto", !sessionsLoading, SCROLL_DAY_SETTLE_DELAY)}
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
  scrollToDay: state.timetable.scrollToDay,
  selectedMonthSessionDays: state.timetable.selectedMonthSessionDays
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)((withRouter(Calendar)));
