/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useContext } from "react";
import { addMonths, format } from "date-fns";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import CalendarHeader from "./components/CalendarHeader";
import CalendarWeekPanel from "./components/CalendarWeekPanel";
import CalendarBody from "./components/CalendarBody";
import { TimetableContext } from "../../../../Timetable";
import { State } from "../../../../../../reducers/state";
import { getTimetableSessionsDays } from "../../../../actions";

interface Props {
  classes?: any;
  getSessionsDays?: (month: number, year: number) => void;
  setTimetableSearch?: (search: string) => void;
  selectedMonthSessionDays?: number[];
}

const MiniCalendar: React.FunctionComponent<Props> = ({ getSessionsDays, selectedMonthSessionDays }) => {
  const {
   setSelectedWeekDays, selectedMonth, setSelectedMonth, selectedWeekDays
  } = useContext(TimetableContext);

  const previousMonth = () => {
    const prev = addMonths(selectedMonth, -1);
    setSelectedMonth(prev);
    getSessionsDays(prev.getMonth(), prev.getFullYear());
  };

  const nextMonth = () => {
    const next = addMonths(selectedMonth, 1);
    setSelectedMonth(next);
    getSessionsDays(next.getMonth(), next.getFullYear());
  };

  const switchWeekDays = dayId => {
    selectedWeekDays.map((el, id) => (id === dayId ? !el : el)).every(el => el)
      ? setSelectedWeekDays(Array(7).fill(false))
      : setSelectedWeekDays(selectedWeekDays.map((el, id) => (id === dayId ? !el : el)));
  };

  return (
    <div className="pb-2">
      <CalendarHeader
        month={format(selectedMonth, "MMM")}
        year={selectedMonth.getFullYear()}
        previousMonth={previousMonth}
        nextMonth={nextMonth}
      />
      <CalendarWeekPanel switchWeekDays={switchWeekDays} selectedWeekDays={selectedWeekDays} />
      <CalendarBody
        month={selectedMonth}
        selectedWeekDays={selectedWeekDays}
        selectedMonthSessionDays={selectedMonthSessionDays}
      />
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  selectedMonthSessionDays: state.timetable.selectedMonthSessionDays || []
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getSessionsDays: (month: number, year: number) => dispatch(getTimetableSessionsDays(month, year))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MiniCalendar);
