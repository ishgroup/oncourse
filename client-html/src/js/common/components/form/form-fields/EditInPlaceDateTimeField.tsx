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
 ComponentClass, useMemo, useRef, useState
} from "react";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import { withStyles, createStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import ButtonBase from "@material-ui/core/ButtonBase";
import DateRange from "@material-ui/icons/DateRange";
import QueryBuilder from "@material-ui/icons/QueryBuilder";
import { ListItemText } from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import { format, isValid } from "date-fns";
import InputAdornment from "@material-ui/core/InputAdornment";
import IconButton from "@material-ui/core/IconButton";
import { DateTimeField } from "./DateTimeField";
import { formatStringDate } from "../../../utils/dates/formatString";
import {
  HH_MM_COLONED, III_DD_MMM_YYYY, III_DD_MMM_YYYY_HH_MM, YYYY_MM_DD_MINUSED
} from "../../../utils/dates/format";
import { appendTimezone, appendTimezoneToUTC } from "../../../utils/dates/formatTimezone";

const styles = theme => createStyles({
  textField: {
    paddingLeft: "0",
    paddingBottom: `${theme.spacing(2) - 3}px`
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
    "&:hover, &:hover $editButton": {
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main
    }
  },
  readonly: {
    pointerEvents: "none"
  },
  textFieldLeftMargin: {
    marginLeft: theme.spacing(1)
  },
  viewMode: {
    padding: 0,
    margin: 0
  },
  label: {
    whiteSpace: "nowrap"
  },
  placeholderContent: {
    color: theme.palette.divider
  },
  input: {
    width: "100%"
  },
  pickerButton: {
    width: theme.spacing(4),
    height: theme.spacing(4),
    padding: theme.spacing(0.5)
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
      transform: "scale(1.33) translate(5px,0)"
    },
    "&$labelShrink": {
      maxWidth: "calc(100% * 1.4)"
    }
  },
  inlineContainer: {
    display: "inline-flex",
    verticalAlign: "top",
    "&$hiddenContainer": {
      display: "none"
    }
  },
  inlineIcon: {
    position: "absolute",
    right: theme.spacing(-3)
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

const EditInPlaceDateTimeField: React.FC<any> = ({
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
   fullWidth,
   className,
   onKeyPress,
   placeholder,
   ...custom
  }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [textValue, setTextValue] = useState("");
  const [pickerOpened, setPickerOpened] = useState(false);

  const isAdornmentHovered = useRef<any>(false);
  const isIconOvered = useRef<any>(false);
  const inputNode = useRef<any>(null);

  const dateValue = useMemo(() => {
    let dateObj = input.value ? new Date(input.value) : null;
    if (timezone && input.value) {
      dateObj = appendTimezone(dateObj, timezone);
    }
    return dateObj;
  }, [input.value, timezone]);

  const onAdornmentOver = () => {
    isAdornmentHovered.current = true;
  };

  const onAdornmentOut = () => {
    isAdornmentHovered.current = false;
  };

  const onAdornmentClick = e => {
    if (isAdornmentHovered.current) {
      e.preventDefault();
    }
    setTimeout(() => {
      isAdornmentHovered.current = false;
      // onBlur();
      setIsEditing(false);
    }, 600);
  };

  const onInputChange = e => {
    setTextValue(e.target.value);
  };

  const openPicker = () => {
    setPickerOpened(true);
  };

  const edit = () => {
    setIsEditing(true);
    setTimeout(() => {
      inputNode.current.focus();
    }, 50);
  };

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
        formatted = v.toISOString();
      }
      input.onChange(formatted);
      input.onBlur(formatted);
      return;
    }
    input.onChange(null);
    input.onBlur(null);
  };

  const onBlur = () => {
    if (isAdornmentHovered.current || isIconOvered.current) {
      return;
    }

    setIsEditing(false);

    const parsed = textValue
      ? formatStringDate(textValue, type, formatDate || formatDateTime || formatTime)
      : "";

    if (parsed) {
      setTextValue(formatDateInner(new Date(parsed)));
      onChange(timezone ? appendTimezoneToUTC(parsed, timezone) : parsed);
    } else {
      onChange(null);
    }
  };

  const onClose = () => {
    setIsEditing(false);
    setPickerOpened(false);
  };

  const onFocus = () => {
    setTextValue(formatDateInner(dateValue));
    if (!isEditing) {
      setIsEditing(true);
    }
    input.onFocus();
  };

  const onEditButtonFocus = () => {
    edit();
  };

  const getValue = () => {
    if (!input.value) {
      return (
        <span className={clsx(classes.placeholderContent, classes.editable, fieldClasses.placeholder)}>{placeholder || "No value"}</span>
      );
    }

    return formatDateInner(dateValue);
  };

  const onButtonOver = () => {
    isIconOvered.current = true;
  };

  const onButtonLeave = () => {
    isIconOvered.current = false;
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
    <span onMouseEnter={onAdornmentOver} onMouseLeave={onAdornmentOut} onMouseDown={onAdornmentClick}>
      {label}
      {" "}
      <span className={classes.labelAdornment}>{labelAdornment}</span>
    </span>
  ) : (
    label
  );

  const editIcon = !disabled && (
    <IconButton className={clsx(classes.editButton, formatting === "inline" && classes.inlineIcon)}>
      {type === "time"
        ? <QueryBuilder className={clsx("hoverIcon", classes.editIcon, fieldClasses.placeholder)} />
        : <DateRange className={clsx("hoverIcon", classes.editIcon, fieldClasses.placeholder)} />}
    </IconButton>
  );

  return (
    <div
      id={input.name}
      className={clsx(className, "outline-none", {
        [classes.inlineContainer]: formatting === "inline"
      })}
    >
      <div className={classes.hiddenContainer}>
        <DateTimeField
          type={type}
          open={pickerOpened}
          value={dateValue}
          onChange={onPickerChange}
          onClose={onClose}
          {...custom}
        />
      </div>

      <div
        className={clsx({
          [classes.hiddenContainer]: !(isEditing || invalid),
          [classes.readonly]: disabled,
          [classes.editing]: formatting !== "inline",
          fullWidth
        })}
      >

        <FormControl
          error={invalid}
          margin="none"
          fullWidth={fullWidth}
          className={clsx("pr-2", {
            [classes.topMargin]: !listSpacing,
            [classes.bottomMargin]: listSpacing && formatting !== "inline",
            [classes.inlineTextField]: formatting === "inline"
          })}
        >
          {Boolean(label) && (
            <InputLabel
              classes={{
                root: classes.inputLabel,
                shrink: classes.labelShrink
              }}
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
              size: formatting === "inline" && input.value ? String(input.value.length + 1) : undefined,
              className: clsx({
                [classes.inlineInput]: formatting === "inline",
                [classes.readonly]: disabled
              }),
              placeholder
            }}
            value={!isEditing && invalid ? formatDateInner(dateValue) : textValue}
            classes={{
              root: clsx(classes.input, fieldClasses.text),
              underline: fieldClasses.underline,
              input: clsx(classes.input, fieldClasses.text)
            }}
            endAdornment={(
              <InputAdornment position="end">
                <IconButton
                  tabIndex={-1}
                  onClick={openPicker}
                  onMouseOver={onButtonOver}
                  onMouseLeave={onButtonLeave}
                  classes={{
                    root: clsx(classes.pickerButton, fieldClasses.text)
                  }}
                >
                  {type === "time" ? <QueryBuilder color="inherit" /> : <DateRange color="inherit" />}
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
      </div>
      <div
        className={clsx({
          [classes.hiddenContainer]: isEditing || invalid,
          [classes.textField]: listSpacing && formatting !== "inline",
          [classes.inlineContainer]: !isEditing && formatting === "inline"
        })}
      >
        {!hideLabel && label && (
          <Typography variant="caption" color="textSecondary" className={fieldClasses.label} noWrap>
            {labelContent}
          </Typography>
        )}

        {formatting === "primary" && (
          <ListItemText
            classes={{
              root: classes.viewMode
            }}
            primary={(
              <ButtonBase
                disabled={disabled}
                classes={{
                  disabled: classes.readonly
                }}
                onFocus={onEditButtonFocus}
                onClick={onEditButtonFocus}
                className={clsx(classes.editable, "hoverIconContainer")}
                component="div"
              >
                {editableComponent || getValue()}
                {editIcon}
              </ButtonBase>
            )}
          />
        )}

        {formatting === "secondary" && (
          <ListItemText
            classes={{
              root: classes.viewMode
            }}
            secondary={(
              <ButtonBase
                disabled={disabled}
                classes={{
                  disabled: classes.readonly
                }}
                onFocus={onEditButtonFocus}
                onClick={onEditButtonFocus}
                className={clsx(classes.editable, "hoverIconContainer")}
                component="span"
              >
                {editableComponent || getValue()}
                {editIcon}
              </ButtonBase>
            )}
          />
        )}

        {formatting === "custom" && (
          <ButtonBase
            disabled={disabled}
            classes={{
              disabled: classes.readonly
            }}
            component="div"
            onFocus={onEditButtonFocus}
            onClick={onEditButtonFocus}
            className={clsx(classes.editable, "hoverIconContainer")}
          >
            {editableComponent || getValue()}
            {editIcon}
          </ButtonBase>
        )}

        {formatting === "inline" && (
          <ButtonBase
            disabled={disabled}
            classes={{
              disabled: classes.readonly
            }}
            component="span"
            onFocus={onEditButtonFocus}
            onClick={onEditButtonFocus}
            className={clsx(classes.editable, classes.inline, "hoverIconContainer")}
          >
            {editableComponent || getValue()}
            {editIcon}
          </ButtonBase>
        )}
      </div>
    </div>
  );
};

export default withStyles(styles)(EditInPlaceDateTimeField) as ComponentClass<any>;
