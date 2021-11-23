/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import TextField from "@mui/material/TextField";
import { MobileDatePicker, MobileDateTimePicker, MobileTimePicker } from "@mui/lab";
import { DD_MM_YYYY_HH_MM_SPECIAL, DD_MMM_YYYY_MINUSED, HH_MM_COLONED } from "../../../utils/dates/format";
import { stubComponent } from "../../../utils/common";

export const DateTimeField = props => {
  const {
   onChange, type, ampm, value, setPickerRef, formatDate, maxDate, minDate, ...rest
  } = props;

  const picker = type === "date" ? (
    <MobileDatePicker
      ref={setPickerRef}
      onChange={onChange}
      ampm={ampm || "false"}
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
        ampm={ampm || "false"}
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
