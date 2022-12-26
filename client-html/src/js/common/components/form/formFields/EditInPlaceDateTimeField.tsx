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
import FormControl from "@mui/material/FormControl";
import FormHelperText from "@mui/material/FormHelperText";
import Input from "@mui/material/Input";
import InputLabel from "@mui/material/InputLabel";
import clsx from "clsx";
import DateRange from "@mui/icons-material/DateRange";
import QueryBuilder from "@mui/icons-material/QueryBuilder";
import { format, isValid } from "date-fns";
import InputAdornment from "@mui/material/InputAdornment";
import IconButton from "@mui/material/IconButton";
import { DateTimeField } from "./DateTimeField";
import { formatStringDate } from "../../../utils/dates/formatString";
import {
  HH_MM_COLONED, III_DD_MMM_YYYY, III_DD_MMM_YYYY_HH_MM, YYYY_MM_DD_MINUSED
} from "../../../utils/dates/format";
import { appendTimezone, appendTimezoneToUTC } from "../../../utils/dates/formatTimezone";
import { endFieldProcessingAction, startFieldProcessingAction } from "../../../actions/FieldProcessing";
import uniqid from "../../../utils/uniqid";
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import { FieldClasses } from "../../../../model/common/Fields";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { makeAppStyles } from "../../../styles/makeStyles";

const useStyles = makeAppStyles(theme => ({
  spanLabel: {
    paddingLeft: "0.5px",
    marginTop: "-3px",
    display: "inline-block",
    height: "17px",
  },
  inputEndAdornment: {
    color: theme.palette.primary.main,
    visibility: "hidden",
  },
  inputWrapper: {
    "&:hover $inputEndAdornment": {
      visibility: "visible"
    },
    "&:hover $hiddenContainer": {
      display: "inline-flex"
    },
  },
  topMargin: {
    marginTop: theme.spacing(1),
    paddingLeft: "0"
  },
  hiddenContainer: {
    display: "none"
  },
  editButton: {
    padding: "4px",
    "&:hover": {
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main
    }
  },
  editIcon: {
    fontSize: "18px",
    color: theme.palette.divider,
    display: "inline-flex"
  },
  editable: {
    color: theme.palette.text.primaryEditable,
    fontWeight: 400,
    "&:hover, &:hover $editButton": {
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main,
    }
  },
  viewMode: {
    padding: 0,
    margin: "-2px 0 0",
  },
  label: {
    whiteSpace: "nowrap"
  },
  placeholderContent: {
    color: theme.palette.text.disabled,
    opacity: 0.4,
    fontWeight: 400,
  },
  input: {
    width: "100%"
  },
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
    padding: theme.spacing(0.5),
    "&:hover": {
      color: theme.palette.primary.main,
    }
  },
  inputLabel: {
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
    paddingBottom: "4px",
    right: "-46%",
    maxWidth: "100%",
    "& $labelAdornment": {
      position: "absolute",
      transform: "scale(1.3) translate(5px,0)"
    },
    "&$labelShrink": {
      maxWidth: "calc(100% * 1.4)"
    }
  },
  inlineContainer: {
    display: "inline-flex",
    "&$hiddenContainer": {
      display: "none"
    }
  },
  inlineInput: {
    padding: "0 0 1px 0",
    minWidth: "2.2em",
    fontSize: "inherit"
  },
  inline: {},
  labelShrink: {},
  labelAdornment: {}
}));

interface Props {
  ref?: any;
  input?: Partial<WrappedFieldInputProps>;
  meta?: Partial<WrappedFieldMetaProps>;
  type?: "date" | "time" | "datetime"
  fieldClasses?: FieldClasses,
  onKeyPress?: AnyArgFunction;
  labelAdornment?: React.ReactNode;
  helperText?: React.ReactNode;
  formatDate?: string;
  formatTime?: string;
  formatDateTime?: string;
  timezone?: string;
  label?: string;
  formatValue?: string;
  className?: string;
  placeholder?: string;
  inline?: boolean;
  disabled?: boolean;
  persistValue?: boolean;
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
   helperText,
   label,
   disabled,
   formatValue,
   className,
   onKeyPress,
   placeholder,
   persistValue
  }: Props
) => {
  const [isEditing, setIsEditing] = useState(false);
  const [textValue, setTextValue] = useState("");
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
    let dateObj = input.value ? new Date(input.value) : null;
    if (timezone && input.value) {
      dateObj = appendTimezone(dateObj, timezone);
    }
    return dateObj;
  }, [input.value, timezone]);

  const onAdornmentClick = () => {
    setTimeout(() => {
      setIsEditing(false);
    }, 600);
  };

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

  const openPicker = () => {
    setPickerOpened(true);
  };

  const renderedValue = useMemo(() => {
    if (!input.value) {
      return (
        <span className={clsx(classes.placeholderContent, classes.editable, fieldClasses.placeholder)}>{placeholder || "No value"}</span>
      );
    }

    return formatDateInner(dateValue);
  }, [dateValue, input.value, placeholder, classes, fieldClasses]);

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

    setIsEditing(false);

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
    setIsEditing(false);
    setPickerOpened(false);
  };

  const onEnterPress = e => {
    if (e.keyCode === 13 && inputNode.current) {
      inputNode.current.blur();
    }
  };

  const onPickerChange = v => {
    onChange(timezone ? appendTimezoneToUTC(v, timezone) : v);
  };

  const labelContent = labelAdornment ? (
    <span onMouseDown={onAdornmentClick}>
      {label}
      {" "}
      <span className={classes.labelAdornment}>{labelAdornment}</span>
    </span>
  ) : (
    label
  );

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
          onChange={onPickerChange}
          onClose={onClose}
          renderInput={() => (
            <FormControl
              error={invalid}
              variant="standard"
              margin="none"
              fullWidth
              className={clsx({
                "pr-2": inline
              })}
            >
              {Boolean(label) && (
              <InputLabel
                classes={{
                  root: clsx(classes.inputLabel, fieldClasses.label),
                  shrink: classes.labelShrink
                }}
                shrink={true}
                htmlFor={`input-${input.name}`}
              >
                {labelContent}
              </InputLabel>
            )}
              <Input
                id={`input-${input.name}`}
                name={input.name}
                type="text"
                onKeyPress={onKeyPress}
                onChange={onInputChange}
                onFocus={input.onFocus}
                onBlur={onBlur}
                onKeyDown={onEnterPress}
                disabled={disabled}
                inputRef={inputNode}
                inputProps={{
                  size: inline && renderedValue ? renderedValue.length + 1 : undefined,
                  className: clsx({
                    [classes.inlineInput]: inline
                  }),
                  placeholder: placeholder || (!isEditing ? "No value" : ""),
                }}
                value={textValue}
                classes={{
                  root: clsx(classes.input, fieldClasses.text, inline && classes.inlineInput,
                    classes.inputWrapper),
                  underline: fieldClasses.underline,
                  input: clsx(classes.input, fieldClasses.text),
                }}
                endAdornment={(
                  <InputAdornment
                    position="end"
                    className={clsx(classes.inputEndAdornment, inline && classes.hiddenContainer)}
                  >
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
                    </IconButton>
                  </InputAdornment>
                )}
              />
              <FormHelperText
                classes={{
                error: "shakingError"
              }}
              >
                {error || helperText}
              </FormHelperText>
            </FormControl>
          )}
        />
      </div>
    </div>
  );
};

export default EditInPlaceDateTimeField;