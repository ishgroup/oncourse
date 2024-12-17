/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import CircularProgress from '@mui/material/CircularProgress';
import { isSameMonth, parse, setDate, startOfMonth } from 'date-fns';
import { DD_MMM_YYYY_MINUSED } from 'ish-ui';
import React, { createContext, useEffect, useReducer } from 'react';
import { withStyles } from 'tss-react/mui';
import { useAppDispatch, useAppSelector } from '../../common/utils/hooks';
import { LSGetItem, LSSetItem } from '../../common/utils/storage';
import { TimetableContextState } from '../../model/timetable';
import { getTimetableFilters } from './actions';
import Calendar from './components/calendar/Calendar';
import SearchBar from './components/search-bar/SearchBar';
import TimetableSideBar from './components/timetable-side-bar/TimetableSideBar';
import { LS_TIMETABLE_CALENDAR_MODE, LS_TIMETABLE_GROUPING_MODE, LS_TIMETABLE_TAGS_MODE } from './constants';

const styles = () =>
  ({
    container: {
      height: "100vh",
      gridTemplateColumns: "260px 1fr"
    },
    calendar: {
      display: "grid",
      gridTemplateRows: "1fr auto"
    }
  });

export const TimetableContext = createContext<TimetableContextState>(null);

const todayInitial = new Date();
todayInitial.setHours(0, 0, 0, 0);

export const timetableContextStateInitial: TimetableContextState = {
  calendarGrouping: "No grouping",
  calendarMode: "Compact",
  tagsState: "Tag names",
  targetDay: todayInitial,
  selectedMonth: setDate(todayInitial, 1),
  selectedWeekDays: Array(7).fill(false),
  selectedDayPeriods: Array(3).fill(false)
};

const initContext = (initial: TimetableContextState) => {
  const params = new URLSearchParams(window.location.search);

  let targetDay = todayInitial;
  const paramsDate = params.get("selectedDate");
  
  if (paramsDate) {
    targetDay = parse(paramsDate, DD_MMM_YYYY_MINUSED, new Date());
  }
  
  return {
    ...initial,
    calendarMode: params.get("calendarMode") || LSGetItem(LS_TIMETABLE_CALENDAR_MODE) || "Compact",
    calendarGrouping: LSGetItem(LS_TIMETABLE_GROUPING_MODE) || "No grouping",
    tagsState: LSGetItem(LS_TIMETABLE_TAGS_MODE) || "Tag names",
    targetDay,
    selectedMonth: setDate(targetDay, 1),
  };
};

const timetableReducer: React.Reducer<TimetableContextState, any> = (state, action) => {
  switch (action.type) {
    case "targetDay":
      return {
        ...state,
        selectedMonth: isSameMonth(action.payload, state.selectedMonth)
          ? state.selectedMonth
          : startOfMonth(action.payload),
        targetDay: action.payload
      };
    case "tagsState": {
      LSSetItem(LS_TIMETABLE_TAGS_MODE, action.payload);
      return { ...state, [action.type]: action.payload };
    }
    case "calendarMode": {
      LSSetItem(LS_TIMETABLE_CALENDAR_MODE, action.payload);
      return { ...state, [action.type]: action.payload };
    }
    case "calendarGrouping": {
      LSSetItem(LS_TIMETABLE_GROUPING_MODE, action.payload);
      return { ...state, [action.type]: action.payload };
    }
    default: {
      return { ...state, [action.type]: action.payload };
    }
  }
};

const getActionCreators = dispatch => ({
  setTargetDay: payload => dispatch({ type: "targetDay", payload }),
  setTagsState: payload => dispatch({ type: "tagsState", payload }),
  setCalendarMode: payload => dispatch({ type: "calendarMode", payload }),
  setSelectedMonth: payload => dispatch({ type: "selectedMonth", payload }),
  setSelectedWeekDays: payload => dispatch({ type: "selectedWeekDays", payload }),
  setSelectedDayPeriods: payload => dispatch({ type: "selectedDayPeriods", payload }),
  setCalendarGrouping: payload => dispatch({ type: "calendarGrouping", payload }),
});

export const TimetableContextProvider = props => {
  const [state, dispatch] = useReducer<any, TimetableContextState, any>(timetableReducer, timetableContextStateInitial, initContext);
  return (
    <TimetableContext.Provider value={{ ...state as any, ...getActionCreators(dispatch) }}>
      {props.children}
    </TimetableContext.Provider>
  );
};

const Timetable = ({ classes }: { classes? }) => {
  const filtersLoading = useAppSelector(state => state.timetable.filtersLoading);

  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getTimetableFilters());
  }, []);
  
  return (
    <TimetableContextProvider>
      <div className={`d-grid relative ${classes.container}`}>
        <div className="paperBackgroundColor">
          {!filtersLoading && <TimetableSideBar />}
        </div>
        <div className={classes.calendar}>
          {filtersLoading ? <div className="w-100 h-100 centeredFlex justify-content-center"><CircularProgress size={40} thickness={5} /></div> : <Calendar />}
          <SearchBar />
        </div>
      </div>
    </TimetableContextProvider>
);
};

export default withStyles(Timetable, styles);
