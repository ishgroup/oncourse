/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";

import { DatePicker, TimePicker, DateTimePicker } from "@material-ui/pickers";
import { DD_MM_YYYY_HH_MM_SPECIAL, DD_MMM_YYYY_MINUSED, HH_MM_COLONED } from "../../../utils/dates/format";

export const DateTimeField = props => {
  const {
 onChange, type, ampm, value, setPickerRef, formatDate, maxDate, minDate, ...rest 
} = props;

  const picker = type === "date" ? (
    <DatePicker
      ref={setPickerRef}
      onChange={onChange}
      ampm={ampm || "false"}
      value={value}
      format={formatDate || DD_MMM_YYYY_MINUSED}
      maxDate={maxDate || undefined}
      minDate={minDate || undefined}
      {...rest}
    />
    ) : type === "time" ? (
      <TimePicker
        ref={setPickerRef}
        onChange={onChange}
        ampm={ampm || "false"}
        value={value}
        format={formatDate || HH_MM_COLONED}
        maxDate={maxDate || undefined}
        minDate={minDate || undefined}
        {...rest}
      />
    ) : type === "datetime" ? (
      <DateTimePicker
        ref={setPickerRef}
        onChange={onChange}
        ampm={ampm || false}
        value={value}
        format={formatDate || DD_MM_YYYY_HH_MM_SPECIAL}
        maxDate={maxDate || undefined}
        minDate={minDate || undefined}
        {...rest}
      />
    ) : null;

  return picker || null;
};
