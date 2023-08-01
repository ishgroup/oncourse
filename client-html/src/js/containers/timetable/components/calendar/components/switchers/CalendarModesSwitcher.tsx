/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import React, { useCallback, useContext } from "react";
import { TimetableContextState } from "../../../../../../model/timetable";

const CalendarModesSwitcher = ({ className = null, TimetableContext }) => {
  const {
 calendarMode, calendarGrouping, setCalendarMode, setCalendarGrouping 
} = useContext<TimetableContextState>(TimetableContext);

  const onChange = useCallback(e => {
    setCalendarMode(e.target.value);
    
    if (e.target.value === "Gap(Hours)" && calendarGrouping !== "No grouping") {
      setCalendarGrouping("No grouping");
    }
  }, [setCalendarMode, setCalendarGrouping, calendarGrouping]);

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
