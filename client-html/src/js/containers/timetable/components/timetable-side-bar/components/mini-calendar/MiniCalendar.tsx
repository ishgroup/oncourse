/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { addMonths, format } from "date-fns";
import { CalendarHeader, CalendarWeekPanel } from "ish-ui";
import React, { useContext } from "react";
import { connect } from "react-redux";
import { validateDate } from "../../../../../../common/utils/validation";
import { State } from "../../../../../../reducers/state";
import { TimetableContext } from "../../../../Timetable";
import CalendarBody from "./components/CalendarBody";

interface Props {
  classes?: any;
  setTimetableSearch?: (search: string) => void;
  selectedMonthSessionDays?: number[];
}

const MiniCalendar: React.FunctionComponent<Props> = ({ selectedMonthSessionDays }) => {
  const {
   setSelectedWeekDays, selectedMonth, setSelectedMonth, selectedWeekDays
  } = useContext(TimetableContext);

  const previousMonth = () => {
    const prev = addMonths(selectedMonth, -1);
    setSelectedMonth(prev);
  };

  const nextMonth = () => {
    const next = addMonths(selectedMonth, 1);
    setSelectedMonth(next);
  };

  const switchWeekDays = dayId => {
    selectedWeekDays.map((el, id) => (id === dayId ? !el : el)).every(el => el)
      ? setSelectedWeekDays(Array(7).fill(false))
      : setSelectedWeekDays(selectedWeekDays.map((el, id) => (id === dayId ? !el : el)));
  };

  return (
    <div className="pb-2">
      <CalendarHeader
        month={(selectedMonth && validateDate(selectedMonth)) ? format(selectedMonth, "MMM") : ""}
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

export default connect<any, any, any>(mapStateToProps)(MiniCalendar);
