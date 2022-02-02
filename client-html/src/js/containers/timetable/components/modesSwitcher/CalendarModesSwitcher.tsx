import React, { useCallback, useContext } from "react";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import { TimetableContext } from "../../Timetable";

const CalendarModesSwitcher = ({ className }) => {
  const { calendarMode, setCalendarMode } = useContext(TimetableContext);

  const onChange = useCallback(e => setCalendarMode(e.target.value), [setCalendarMode]);

  return (
    <FormControl className={className} variant="standard">
      <Select value={calendarMode} onChange={onChange}>
        <MenuItem value="Compact">Compact</MenuItem>
        <MenuItem value="Gap(Days)">Gap (Days)</MenuItem>
        <MenuItem value="Gap(Hours)">Gap (Hours)</MenuItem>
      </Select>
    </FormControl>
  );
};

export default CalendarModesSwitcher;
