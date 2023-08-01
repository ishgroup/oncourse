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

const CalendarTagsSwitcher = ({ className = null, TimetableContext }) => {
  const { tagsState, setTagsState } = useContext<TimetableContextState>(TimetableContext);

  const onChange = useCallback(e => setTagsState(e.target.value), [setTagsState]);

  return (
    <FormControl className={className} variant="standard">
      <Select value={tagsState} onChange={onChange}>
        <MenuItem value="Tag names">Tag names</MenuItem>
        <MenuItem value="Tag dots">Tag dots</MenuItem>
        <MenuItem value="Tag off">Tag off</MenuItem>
      </Select>
    </FormControl>
  );
};

export default CalendarTagsSwitcher;