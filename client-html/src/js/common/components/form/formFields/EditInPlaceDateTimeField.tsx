/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

/**
 * Wrapper component for DateTimeField with edit in place functional
 * */

import React, {
  useEffect, useMemo, useRef, useState
} from "react";
import clsx from "clsx";
import DateRange from "@mui/icons-material/DateRange";
import QueryBuilder from "@mui/icons-material/QueryBuilder";
import { format, isValid } from "date-fns";
import IconButton from "@mui/material/IconButton";
import { DateTimeField } from "./DateTimeField";
import { formatStringDate } from "../../../utils/dates/formatString";
import {
  HH_MM_COLONED, III_DD_MMM_YYYY, III_DD_MMM_YYYY_HH_MM, YYYY_MM_DD_MINUSED
} from "../../../utils/dates/format";
import { endFieldProcessingAction, startFieldProcessingAction } from "../../../actions/FieldProcessing";
import uniqid from "../../../utils/uniqid";
import { EditInPlaceDateTimeFieldProps } from "../../../../model/common/Fields";
import { makeAppStyles } from "../../../styles/makeStyles";
import EditInPlaceFieldBase from "./EditInPlaceFieldBase";
import { formatInTimeZone } from "date-fns-tz";
import { appendTimezoneToUTC } from "../../../utils/dates/formatTimezone";

const useStyles = makeAppStyles(theme => ({
  inlinePickerButton: {
    padding: "0.2em",
    marginBottom: "0.2em",
    fontSize: "1.3em",
    "&:hover": {
      color: theme.palette.primary.main,
    }
  },
  pickerButton: {
    width: theme.spacing(4),
    height: theme.spacing(4),
    bottom: theme.spacing(-0.5)
  },
  inlineContainer: {
    display: "inline-block",
    marginLeft: theme.spacing(0.5)
  }
}));

interface InputTypes {
  type?: "date" | "time" | "datetime"
}

const EditInPlaceDateTimeField = (
  {
   type,
   formatDate,
   formatTime,
   formatDateTime,
   timezone,
   input,
   fieldClasses = {},
   inline,
   meta: { error, invalid, active, dispatch },
   labelAdornment,
   label,
   disabled,
   formatValue,
   className,
   onKeyPress,
   placeholder = "No value",
   persistValue,
   warning,
  defaultValue,
   rightAligned
  }: EditInPlaceDateTimeFieldProps & InputTypes
) => {
  const [textValue, setTextValue] = useState(defaultValue || "");
  const [pickerOpened, setPickerOpened] = useState(false);

  const inputNode = useRef<any>(null);
  const processActionId = useRef<string>(null);
  const processedValue = useRef<any>(null);

  const classes = useStyles();

  const formatDateInner = dateObj => {
    if (!dateObj) {
      return "";
    }

    if (dateObj.toString() === "Invalid Date") return "";

    switch (type) {
      case "date":
        return timezone ? formatInTimeZone(dateObj, timezone, formatDate || III_DD_MMM_YYYY) : format(dateObj, formatDate || III_DD_MMM_YYYY);
      case "time":
        return timezone ? formatInTimeZone(dateObj, timezone, formatTime || HH_MM_COLONED) : format(dateObj, formatTime || HH_MM_COLONED);
      case "datetime":
        return timezone ? formatInTimeZone(dateObj, timezone, formatDateTime || III_DD_MMM_YYYY_HH_MM) : format(dateObj, formatDateTime || III_DD_MMM_YYYY_HH_MM);
      default:
        return dateObj.toString();
    }
  };

  const dateValue = useMemo(() => input.value ? new Date(input.value) : null, [input.value, timezone]);

  useEffect(() => {
    setTextValue(formatDateInner(dateValue));
  }, [dateValue]);

  useEffect(() => {
    if (!active && processActionId.current && input.value === processedValue.current) {
      dispatch(endFieldProcessingAction(processActionId.current));
      processedValue.current = null;
      processActionId.current = null;
    }
  }, [input.value, active]);

  const onInputChange = e => {
    if (e) setTextValue(e.target.value);
  };

  const openPicker = e => {
    e.stopPropagation();
    setPickerOpened(true);
  };

  const onChange = (v: Date) => {
    if (v) {
      let formatted;
      if (formatValue) {
        formatted = format(v, formatValue);
      } else if (type === "date") {
        if (isValid(v)) {
          formatted = format(v, YYYY_MM_DD_MINUSED);
        } else {
          formatted = null;
        }
      } else {
        try {
          formatted = v.toISOString();
        } catch {
          formatted = null;
        }
      }
      setTextValue(formatDateInner(v));
      input.onChange(formatted);
      return;
    }
    setTextValue("");
    input.onChange(null);
  };

  const onBlur = () => {
    processActionId.current = uniqid();
    dispatch(startFieldProcessingAction({ id: processActionId.current }));

    if (persistValue && !textValue) {
      processedValue.current = input.value;
      input.onChange(input.value);
      input.onBlur(input.value);
      setTextValue(formatDateInner(dateValue));
      return;
    }

    const parsed = textValue
      ? formatStringDate(textValue, type, dateValue || new Date(), formatDate || formatDateTime || formatTime)
      : null;

    if (parsed) {
      const appended = timezone ? appendTimezoneToUTC(parsed, timezone) : parsed;
      let formatted;
      if (formatValue) {
        formatted = format(appended, formatValue);
      } else if (type === "date" && isValid(appended)) {
        formatted = format(appended, YYYY_MM_DD_MINUSED);
      } else {
        try {
          formatted = appended.toISOString();
        } catch {
          formatted = null;
        }
      }
      setTextValue(formatDateInner(appended));
      processedValue.current = formatted;
      input.onChange(formatted);
      input.onBlur(formatted);
    } else {
      processedValue.current = null;
      setTextValue(null);
      input.onChange(null);
      input.onBlur(null);
    }
  };

  const onClose = () => {
    setPickerOpened(false);
  };

  const onEnterPress = e => {
    if (e.keyCode === 13 && inputNode.current) {
      inputNode.current.blur();
    }
  };

  return (
    <div
      className={clsx(className, "outline-none", {
        [classes.inlineContainer]: inline,
        "pointer-events-none": disabled
      })}
    >
      <div
        id={input.name}
        className="w-100"
      >
        <DateTimeField
          type={type}
          toolbarTitle={label}
          open={pickerOpened}
          value={dateValue}
          onChange={onChange}
          onClose={onClose}
          renderInput={() => (
            <EditInPlaceFieldBase
              ref={inputNode}
              name={input.name}
              value={input.value}
              error={error}
              invalid={invalid}
              inline={inline}
              label={label}
              warning={warning}
              fieldClasses={fieldClasses}
              rightAligned={rightAligned}
              shrink={Boolean(label || input.value)}
              disabled={disabled}
              labelAdornment={labelAdornment}
              placeholder={placeholder}
              editIcon={
                <IconButton
                  tabIndex={-1}
                  onClick={openPicker}
                  classes={{
                    root: clsx(fieldClasses.text, inline ? classes.inlinePickerButton : classes.pickerButton)
                  }}
                >
                {type === "time"
                  ? <QueryBuilder fontSize="inherit" color="inherit" />
                  : <DateRange color="inherit" fontSize="inherit" />}
                  </IconButton>}
                InputProps={{
                  type: "text",
                  onFocus: input.onFocus,
                  inputRef: inputNode,
                  value: textValue,
                  onKeyPress,
                  disabled,
                  onBlur,
                  onKeyDown: onEnterPress,
                  onChange: onInputChange
                }}
              />
            )}
          />
      </div>
    </div>
  );
};

export default EditInPlaceDateTimeField;