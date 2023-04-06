/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import TextField from "@mui/material/TextField";
import { MobileDatePicker, MobileDateTimePicker, MobileTimePicker } from "@mui/x-date-pickers";
import { DD_MM_YYYY_HH_MM_SPECIAL, DD_MMM_YYYY_MINUSED, HH_MM_COLONED } from "../../../utils/dates/format";

export const DateTimeField = props => {
  const {
   onChange, type, ampm, value, setPickerRef, formatDate, maxDate, minDate, ...rest
  } = props;

  const picker = type === "date" ? (
    <MobileDatePicker
      ref={setPickerRef}
      onChange={onChange}
      ampm={ampm || false}
      value={value}
      format={formatDate || DD_MMM_YYYY_MINUSED}
      maxDate={maxDate || undefined}
      minDate={minDate || undefined}
      renderInput={props => <TextField {...props} />}
      variant="standard"
      {...rest}
    />
    ) : type === "time" ? (
      <MobileTimePicker
        ref={setPickerRef}
        onChange={onChange}
        ampm={ampm || false}
        value={value}
        format={formatDate || HH_MM_COLONED}
        maxDate={maxDate || undefined}
        minDate={minDate || undefined}
        renderInput={props => <TextField {...props} />}
        variant="standard"
        {...rest}
      />
    ) : type === "datetime" ? (
      <MobileDateTimePicker
        ref={setPickerRef}
        onChange={onChange}
        ampm={ampm || false}
        value={value}
        format={formatDate || DD_MM_YYYY_HH_MM_SPECIAL}
        maxDate={maxDate || undefined}
        minDate={minDate || undefined}
        renderInput={props => <TextField {...props} />}
        variant="standard"
        {...rest}
      />
    ) : null;

  return picker || null;
};
