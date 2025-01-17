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

const CalendarGroupingsSwitcher = ({ className = null, TimetableContext }) => {
  const { calendarGrouping, setCalendarGrouping, calendarMode } = useContext<TimetableContextState>(TimetableContext);

  const onChange = useCallback(e => setCalendarGrouping(e.target.value), [setCalendarGrouping]);

  return (
    <FormControl
      className={className}
      variant="standard"
      disabled={calendarMode === "Gap(Hours)"}
    >
      <Select
        classes={{
          disabled: "text-disabled"
        }}
        value={calendarGrouping}
        onChange={onChange}
      >
        <MenuItem value="Group by tutor">Group by tutor</MenuItem>
        <MenuItem value="Group by room">Group by room</MenuItem>
        <MenuItem value="No grouping">No grouping</MenuItem>
      </Select>
    </FormControl>
  );
};

export default CalendarGroupingsSwitcher;