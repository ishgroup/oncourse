/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { createContext, useReducer } from "react";
import { withStyles } from "@mui/styles";
import createStyles from "@mui/styles/createStyles";
import { isSameMonth, setDate, startOfMonth } from "date-fns";
import CircularProgress from "@mui/material/CircularProgress/CircularProgress";
import TimetableSideBar from "./components/timetable-side-bar/TimetableSideBar";
import Calendar from "./components/calendar/Calendar";
import { TimetableContextState } from "../../model/timetable";
import SearchBar from "./components/search-bar/SearchBar";
import { useAppSelector } from "../../common/utils/hooks";

const styles = () =>
  createStyles({
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
  calendarMode: "Compact",
  targetDay: todayInitial,
  selectedMonth: setDate(todayInitial, 1),
  selectedWeekDays: Array(7).fill(false),
  selectedDayPeriods: Array(3).fill(false)
};

const timetableReducer: React.Reducer<TimetableContextState, any> = (state, action) => {
  switch (action.type) {
    case "setTargetDay":
      return {
        ...state,
        selectedMonth: isSameMonth(action.payload, state.selectedMonth)
          ? state.selectedMonth
          : startOfMonth(action.payload),
        targetDay: action.payload
      };
    case "setSelectedMonth":
      return { ...state, selectedMonth: action.payload };
    case "setSelectedWeekDays":
      return { ...state, selectedWeekDays: action.payload };
    case "setSelectedDayPeriods":
      return { ...state, selectedDayPeriods: action.payload };
    case "setCalendarMode":
      return { ...state, calendarMode: action.payload };
    default: {
      throw new Error();
    }
  }
};

const getActionCreators = dispatch => ({
  setTargetDay: payload => dispatch({ type: "setTargetDay", payload }),
  setCalendarMode: payload => dispatch({ type: "setCalendarMode", payload }),
  setSelectedMonth: payload => dispatch({ type: "setSelectedMonth", payload }),
  setSelectedWeekDays: payload => dispatch({ type: "setSelectedWeekDays", payload }),
  setSelectedDayPeriods: payload => dispatch({ type: "setSelectedDayPeriods", payload })
});

export const TimetableContextProvider = props => {
  const [state, dispatch] = useReducer(timetableReducer, timetableContextStateInitial);
  return (
    <TimetableContext.Provider value={{ ...state, ...getActionCreators(dispatch) }}>
      {props.children}
    </TimetableContext.Provider>
  );
};

const Timetable = ({ classes }) => {
  const filtersLoading = useAppSelector(state => state.timetable.filtersLoading);
  
  return (
    <TimetableContextProvider>
      <div className={`d-grid relative ${classes.container}`}>
        <div className="paperBackgroundColor">
          <TimetableSideBar />
        </div>
        <div className={classes.calendar}>
          {filtersLoading ? <div className="w-100 h-100 centeredFlex justify-content-center"><CircularProgress size={40} thickness={5} /></div> : <Calendar />}
          <SearchBar />
        </div>
      </div>
    </TimetableContextProvider>
);
};

export default withStyles(styles)(Timetable);
