/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
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
import { formatStringDate } from "../../utils/dates/formatString";
import {
  HH_MM_COLONED, III_DD_MMM_YYYY, III_DD_MMM_YYYY_HH_MM, YYYY_MM_DD_MINUSED
} from "../../utils/dates/format";
import { endFieldProcessingAction, startFieldProcessingAction } from "../../actions/FieldProcessing";
import { makeAppStyles } from "../../styles/makeStyles";
import EditInPlaceFieldBase from "./EditInPlaceFieldBase";
import { prependTimezone, appendTimezone } from "../../utils/dates/formatTimezone";
import { useAppSelector } from "../../utils/hooks";
import { EditInPlaceDateTimeFieldProps, FieldInputProps, FieldMetaProps } from "../model/Fields";

const useStyles = makeAppStyles(theme => ({
  inlinePickerButton: {
    padding: "2px",
    marginBottom: "-4px",
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
  }
}));

interface InputTypes {
  type?: "date" | "time" | "datetime"
}

function EditInPlaceDateTimeField<IP extends FieldInputProps, MP extends FieldMetaProps>(
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
  }: EditInPlaceDateTimeFieldProps<IP, MP>  & InputTypes
) {
  const [textValue, setTextValue] = useState(defaultValue || "");
  const [pickerOpened, setPickerOpened] = useState(false);

  const processActionId = useAppSelector(state => state.fieldProcessing[input.name]);

  const inputNode = useRef<any>(null);

  const classes = useStyles();

  const formatDateInner = dateObj => {
    if (!dateObj) {
      return "";
    }

    if (dateObj.toString() === "Invalid Date") return "";

    switch (type) {
      case "date":
        return format(dateObj, formatDate || III_DD_MMM_YYYY);
      case "time":
        return format(dateObj, formatTime || HH_MM_COLONED);
      case "datetime":
        return format(dateObj, formatDateTime || III_DD_MMM_YYYY_HH_MM);
      default:
        return dateObj.toString();
    }
  };

  const dateValue = useMemo(() => {
    let base = input.value ? new Date(input.value) : null;
    
    if (base && timezone) {
      base = appendTimezone(base, timezone);
    }
    
    return base;
  }, [input.value, timezone]);

  useEffect(() => {
    setTextValue(formatDateInner(dateValue));
  }, [dateValue]);

  useEffect(() => {
    if (!active && processActionId) {
      dispatch(endFieldProcessingAction(input.name));
    }
  }, [input.value, input.name, active, processActionId]);

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

      if (timezone && formatted) {
        formatted = prependTimezone(formatted, timezone)?.toISOString();
      }

      input.onChange(formatted);
      input.onBlur(formatted);
      return;
    }
    setTextValue("");
    input.onChange(null);
    input.onBlur(null);
  };

  const onBlur = () => {
    dispatch(startFieldProcessingAction(input.name));

    if (persistValue && !textValue) {
      input.onChange(input.value);
      input.onBlur(input.value);
      setTextValue(formatDateInner(dateValue));
      return;
    }

    const parsed = textValue
      ? formatStringDate(textValue, type, dateValue || new Date(), formatDate || formatDateTime || formatTime)
      : null;

    if (parsed) {
      let formatted;
      if (formatValue) {
        formatted = format(parsed, formatValue);
      } else if (type === "date" && isValid(parsed)) {
        formatted = format(parsed, YYYY_MM_DD_MINUSED);
      } else {
        try {
          formatted = parsed.toISOString();
        } catch {
          formatted = null;
        }
      }
      setTextValue(formatDateInner(parsed));

      if (timezone && formatted) {
        formatted = prependTimezone(formatted, timezone).toISOString();
      }

      input.onChange(formatted);
      input.onBlur(formatted);
    } else {
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
              name={input.name}
              value={textValue}
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
}

export default EditInPlaceDateTimeField;