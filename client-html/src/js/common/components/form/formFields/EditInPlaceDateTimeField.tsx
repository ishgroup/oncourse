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
 ComponentClass, useEffect, useMemo, useRef, useState
} from "react";
import FormControl from "@mui/material/FormControl";
import FormHelperText from "@mui/material/FormHelperText";
import Input from "@mui/material/Input";
import InputLabel from "@mui/material/InputLabel";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import DateRange from "@mui/icons-material/DateRange";
import QueryBuilder from "@mui/icons-material/QueryBuilder";
import { format, isValid } from "date-fns";
import InputAdornment from "@mui/material/InputAdornment";
import IconButton from "@mui/material/IconButton";
import TextField from "@mui/material/TextField";
import { DateTimeField } from "./DateTimeField";
import { formatStringDate } from "../../../utils/dates/formatString";
import { HH_MM_COLONED, III_DD_MMM_YYYY, III_DD_MMM_YYYY_HH_MM, YYYY_MM_DD_MINUSED } from "../../../utils/dates/format";
import { appendTimezone, appendTimezoneToUTC } from "../../../utils/dates/formatTimezone";

const styles = theme => createStyles({
  textField: {
    paddingLeft: "0",
    paddingBottom: "9px",
    height: "61px",
  },
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
  },
  editing: {
    paddingBottom: theme.spacing(1.25)
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
  readonly: {
    fontWeight: 300,
    pointerEvents: "none"
  },
  textFieldLeftMargin: {
    marginLeft: theme.spacing(1)
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
  inlineMargin: {
    marginLeft: "0.3em"
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
});

// @ts-ignore
Date.prototype.stdTimezoneOffset = function () {
  const jan = new Date(this.getFullYear(), 0, 1);
  const jul = new Date(this.getFullYear(), 6, 1);
  return Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
};

// @ts-ignore
Date.prototype.dstOffset = function () {
  return this.getTimezoneOffset() - this.stdTimezoneOffset();
};

const EditInPlaceDateTimeField: React.FC<any> = (
  {
   type,
   formatDate,
   formatTime,
   formatDateTime,
   timezone,
   input,
   classes,
   fieldClasses = {},
   formatting = "primary",
   meta: { error, invalid },
   InputProps = {},
   labelAdornment,
   helperText,
   label,
   listSpacing = true,
   hideLabel,
   editableComponent,
   disabled,
   formatValue,
   className,
   onKeyPress,
   placeholder,
   inlineMargin,
    persistValue,
   ...custom
  }
) => {
  const [isEditing, setIsEditing] = useState(false);
  const [textValue, setTextValue] = useState("");
  const [pickerOpened, setPickerOpened] = useState(false);

  const inputNode = useRef<any>(null);

  const isInline = formatting === "inline";

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

  const onAdornmentClick = e => {
    setTimeout(() => {
      setIsEditing(false);
    }, 600);
  };

  useEffect(() => {
    setTextValue(formatDateInner(dateValue));
  }, [dateValue]);

  const onInputChange = e => {
    e && setTextValue(e.target.value);
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
    setIsEditing(false);

    if (persistValue && !textValue) {
      input.onBlur(input.value);
      input.onChange(input.value);
      setTextValue(formatDateInner(dateValue));
      return;
    }

    const parsed = textValue
      ? formatStringDate(textValue, type, dateValue || new Date(), formatDate || formatDateTime || formatTime)
      : null;

    if (parsed) {
      const appended = timezone ? appendTimezoneToUTC(parsed, timezone) : parsed;
      input.onBlur(appended.toISOString());
      input.onChange(appended.toISOString());
    } else {
      input.onBlur(null);
      input.onChange(null);
    }
  };

  const onClose = () => {
    setIsEditing(false);
    setPickerOpened(false);
  };

  const onFocus = () => {
    input.onFocus();
  };

  const onEnterPress = e => {
    if (e.keyCode === 13) {
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
        [classes.inlineContainer]: isInline,
        [classes.inlineMargin]: inlineMargin
      })}
    >
      <div
        id={input.name}
        className={clsx('w-100', {
          [classes.readonly]: disabled,
          [classes.editing]: formatting !== "inline",
        })}
      >

        <DateTimeField
          type={type}
          open={pickerOpened}
          value={dateValue}
          onChange={onPickerChange}
          onClose={onClose}
          renderInput={pickerProps => (
            <FormControl
              {...pickerProps}
              error={invalid}
              variant="standard"
              margin="none"
              fullWidth
              className={clsx("pr-2", {
              [classes.topMargin]: !listSpacing,
              [classes.bottomMargin]: listSpacing && formatting !== "inline",
              [classes.inlineTextField]: isInline
            })}
            >
              {Boolean(label) && (
              <InputLabel
                classes={{
                  root: clsx(classes.inputLabel, fieldClasses.label),
                  shrink: classes.labelShrink
                }}
                shrink={true}
              >
                {labelContent}
              </InputLabel>
            )}
              <Input
                type="text"
                onKeyPress={onKeyPress}
                onChange={onInputChange}
                onFocus={onFocus}
                onBlur={onBlur}
                onKeyDown={onEnterPress}
                inputRef={inputNode}
                inputProps={{
                size: isInline && renderedValue ? renderedValue.length + 1 : undefined,
                className: clsx({
                  [classes.inlineInput]: isInline,
                  [classes.readonly]: disabled,
                }),
                placeholder: placeholder || (!isEditing && "No value"),
              }}
                value={textValue}
                classes={{
                root: clsx(classes.input, fieldClasses.text, isInline && classes.inlineInput,
                  classes.inputWrapper),
                underline: fieldClasses.underline,
                input: clsx(classes.input, fieldClasses.text)
              }}
                endAdornment={(
                  <InputAdornment position="end" className={classes.inputEndAdornment}>
                    <IconButton
                      tabIndex={-1}
                      onClick={openPicker}
                      classes={{
                      root: clsx(fieldClasses.text, isInline ? classes.inlinePickerButton : classes.pickerButton)
                    }}
                    >
                      {type === "time" ? <QueryBuilder fontSize="inherit" color="inherit" /> : <DateRange color="inherit" fontSize="inherit" />}
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

export default withStyles(styles)(EditInPlaceDateTimeField) as ComponentClass<any>;
