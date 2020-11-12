import React, { useContext, useEffect } from "react";
import { format, addMonths } from "date-fns";
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

const MiniCalendar: React.FunctionComponent<Props> = ({ classes, getSessionsDays, selectedMonthSessionDays }) => {
  const {
 setSelectedWeekDays, selectedMonth, setSelectedMonth, selectedWeekDays 
} = useContext(TimetableContext);

  useEffect(
    () => {
      getSessionsDays(selectedMonth.getMonth(), selectedMonth.getFullYear());
    },
    [selectedMonth]
  );

  const previousMonth = () => {
    setSelectedMonth(addMonths(selectedMonth, -1));
  };

  const nextMonth = () => {
    setSelectedMonth(addMonths(selectedMonth, 1));
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
