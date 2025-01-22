/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import { addMonths, endOfMonth, format, isAfter, isSameMonth, startOfMonth } from 'date-fns';
import { DD_MMM_YYYY_MINUSED, DynamicSizeList, makeAppStyles, usePrevious } from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useContext, useEffect, useMemo, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router-dom';
import AutoSizer from 'react-virtualized-auto-sizer';
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
    position: "relative"
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

const MonthRenderer = React.forwardRef<any, any>(({
 index, isScrolling, data, style
}, ref) => (
  <CalendarMonth
    parentRef={ref}
    index={index}
    isScrolling={isScrolling}
    style={style}
    dayNodesObserver={data.dayNodesObserver}
    {...data.months[index]}
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

const scrollToCalendarDay = debounce((dayNode, list, index, dayNodesObserver) => {
  if (dayNode && dayNodesObserver && list._instanceProps.itemMetadataMap[index]) {
    animateListScroll(
      list,
      dayNode.parentElement.offsetTop + list._instanceProps.itemMetadataMap[index].offset - 32,
      list._outerRef.scrollTop,
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

  const listEl = useRef(null);
  const renderedArgs = useRef<any>(null);

  const prevSearch = usePrevious(search, "");

  const params = new URLSearchParams(location.search);
  const prevParams = usePrevious(params, params);

  const targetDayHandler = (day: Date) => {
    if (listEl.current && listEl.current.state.isScrolling) {
      setTargetDay(day);
    }
  };

  const scrollToDayHandler = (targetDayMonthIndex, node?) => {
    const dayNode = node || document.getElementById(format(targetDay, DD_MMM_YYYY_MINUSED));
    scrollToCalendarDay(dayNode, listEl.current, targetDayMonthIndex, dayNodesObserver);
  };

  // fetch next two months
  const loadNextMonths = (baseDate, reset = false) => {
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
  };

  const onRowsRendered = args => {
    renderedArgs.current = args;
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
      updateHistory(params,url);
    }
  }, [calendarMode]);

  useEffect(() => {
    const targetDayUrlString = params.get("selectedDate");
    const targetDayString = format(targetDay, DD_MMM_YYYY_MINUSED);

    if (targetDayUrlString !== targetDayString) {
      params.set("selectedDate", targetDayString);
      updateHistory(params,url);
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
        updateHistory(params,url);
      }
    }
  }, [filters, params]);

  useEffect(() => {
    if (prevSearch !== search) {
      if (search) {
        params.set("search", encodeURIComponent(search));
      } else {
        params.delete("search");
      }
      updateHistory(params,url);
    }
  }, [search, prevSearch]);

  useEffect(() => {
    const currentSearch = decodeURIComponent(params.get("search"));
    const prevSearch = decodeURIComponent(prevParams.get("search"));

    const currentFilters = params.get("filter");
    const prevFilters = prevParams.get("filter");

    if (currentSearch !== prevSearch || currentFilters !== prevFilters) {
      loadNextMonths(selectedMonth, true);
      setScrollToTargetDayOnRender(targetDay);
    }
  }, [params, prevParams]);

  useEffect(() => {
    dispatch(getTimetableSessionsDays(selectedMonth.getMonth(), selectedMonth.getFullYear()));
  }, [selectedMonth, search, filters]);

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

  const itemData = useMemo(() => ({
   months, dayNodesObserver
  }), [months, dayNodesObserver]);

  return (
    <div className={classes.root}>
      {((!sessionsLoading && !hasSessions) || searchError) && (
        <div className={classes.centered}>
          <div className="noRecordsMessage">
            <Typography variant="h6" color="inherit" align="center">
              No sessions were found
            </Typography>
          </div>
        </div>
      )}

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
            {MonthRenderer as any}
          </DynamicSizeList>
        )}
      </AutoSizer>
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