import React, { useCallback, useContext } from "react";
import FormControl from "@material-ui/core/FormControl";
import MenuItem from "@material-ui/core/MenuItem";
import Select from "@material-ui/core/Select";
import { TimetableContext } from "../../Timetable";

const CalendarModesSwitcher = ({ className }) => {
  const { calendarMode, setCalendarMode } = useContext(TimetableContext);

  const onChange = useCallback(e => setCalendarMode(e.target.value), [setCalendarMode]);

  return (
    <FormControl className={className}>
      <Select value={calendarMode} onChange={onChange}>
        <MenuItem value="Compact">Compact</MenuItem>
        <MenuItem value="Gap(Days)">Gap (Days)</MenuItem>
        <MenuItem value="Gap(Hours)">Gap (Hours)</MenuItem>
      </Select>
    </FormControl>
  );
};

export default CalendarModesSwitcher;
