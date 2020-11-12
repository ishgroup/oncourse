import React, { createContext, useMemo, useReducer } from "react";
import { withStyles } from "@material-ui/core/styles";
import createStyles from "@material-ui/core/styles/createStyles";
import { isSameMonth, setDate, startOfMonth } from "date-fns";
import TimetableSideBar from "./components/timetable-side-bar/TimetableSideBar";
import Calendar from "./components/calendar/Calendar";
import { TimetableContextState } from "../../model/timetable";
import SearchBar from "./components/search-bar/SearchBar";
import store from "../../constants/Store";
import { setTimetableSearch } from "./actions";

const styles = theme =>
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
  const hasSearch = useMemo(() => {
    if (window.location.search) {
      const search = new URLSearchParams(window.location.search);

      const hasQuery = search.has("query");

      if (hasQuery) {
        store.dispatch(setTimetableSearch(search.get("query")));
      }

      if (search.has("title")) {
        window.document.title = search.get("title");
      }

      return hasQuery;
    }
    return false;
  }, [window.location.search]);

  return (
    <TimetableContextProvider>
      <div className={`d-grid relative ${classes.container}`}>
        <div className="paperBackgroundColor">
          <TimetableSideBar hasSearch={hasSearch} />
        </div>
        <div className={classes.calendar}>
          <Calendar />
          {!hasSearch && <SearchBar />}
        </div>
      </div>
    </TimetableContextProvider>
  );
};

export default withStyles(styles)(Timetable);
