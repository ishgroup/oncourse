/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/**
 * Wrapper component for Material Select and Text Field with edit in plaxce functional
 * */

import React from "react";
import clsx from "clsx";
import { createStyles, withStyles } from "@mui/styles";
import { Edit } from "@mui/icons-material";
import {
  ButtonBase,
  FormControl,
  FormHelperText,
  Input,
  InputAdornment,
  InputLabel,
  Typography
} from "@mui/material";
import { AppTheme } from "../../../../model/common/Theme";
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";

const styles = (theme: AppTheme) => createStyles({
  inputEndAdornment: {
    display: "flex",
    fontSize: "18px",
    color: theme.palette.primary.main,
    opacity: 0.5,
    alignItems: "flex-end",
    alignSelf: "flex-end",
    marginBottom: "5px"
  },
  inputWrapper: {
    paddingBottom: 0,
    "& textarea": {
      paddingBottom: "5px"
    },
    "&:hover $hiddenContainer": {
      display: "flex",
    },
    "&:hover $invisibleContainer": {
      visibility: "visible",
    }
  },
  isEditing: {
    borderBottom: "none",
    "& $inputEndAdornment": {
      display: "flex",
      borderBottom: "none",
      opacity: 1,
    },
  },
  inlineTextField: {
    verticalAlign: "baseline",
    "& > div": {
      marginTop: 0
    }
  },
  topMargin: {
    marginTop: theme.spacing(1),
    paddingLeft: "0"
  },
  hiddenContainer: {
    display: "none"
  },
  invisibleContainer: {
    visibility: "hidden"
  },
  editButton: {
    padding: "4px",
    "&:hover": {
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main
    }
  },
  editable: {
    width: "100%",
    overflow: "hidden",
    textOverflow: "ellipsis",
    justifyContent: "flex-start",
    color: theme.palette.text.primaryEditable,
    fontWeight: 400,
    "&:hover, &:hover $placeholderContent, &:hover $editButton": {
      opacity: 0.35,
      color: theme.palette.primary.main
    },
    "&$rightAligned": {
      display: "flex",
      alignItems: "center",
      justifyContent: "flex-end"
    }
  },
  rightAligned: {},
  readonly: {
    fontWeight: 300,
    pointerEvents: "none"
  },
  viewMode: {
    padding: 0,
    margin: "0 0 3px",
  },
  spanLabel: {
    paddingLeft: "0.5px",
    marginTop: "-3px",
    display: "inline-block",
    height: "17px",
  },
  label: {
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
    maxWidth: "100%",
    marginRight: theme.spacing(0.5)
  },
  rightLabel: {
    left: "unset",
    right: theme.spacing(-2),
    "& $label": {
      marginRight: 0
    }
  },
  placeholderContent: {
    opacity: 0.25,
    fontWeight: 400,
  },
  chip: {
    margin: theme.spacing(0.25)
  },
  fitWidth: {
    maxWidth: "100%",
    overflow: "hidden",
    textOverflow: "ellipsis"
  },
  emptySelect: {
    "& $placeholderContent": {
      fontWeight: 400,
    }
  },
  inlineContainer: {
    display: "inline-block",
    marginLeft: "0.3em"
  },
  inlineInput: {
    padding: "0 0 1px 0",
    minWidth: "2.2em",
    fontSize: "inherit",
  },
  selectMainWrapper: {
    "&:hover $selectIconInput": {
      visibility: 'visible',
      color: theme.palette.primary.main,
    },
  },
  inlineSelect: {
    "&$inlineSelect": {
      padding: 0
    }
  },
  muiSelect: {
    "&:focus": {
      background: "none",
    }
  },
  smallOffsetInput: {
    padding: "2px"
  },
  inlineMargin: {
    marginRight: "0.3em"
  },
  selectedItem: {
    backgroundColor: `${theme.palette.action.selected}`
  },
  valueContainer: {
    width: "100%"
  },
  oneLineEditIcon: {
    position: "absolute",
    right: "-14px",
    bottom: "4px"
  },
  selectMenu: {
    zIndex: theme.zIndex.snackbar
  },
  selectIcon: {
    fontSize: "24px",
    color: theme.palette.divider,
    verticalAlign: "middle",
  },
  selectIconInput: {
    visibility: 'hidden',
  },
  hideArrows: {
    "&::-webkit-outer-spin-button": {
      "-webkit-appearance": "none",
      margin: 0
    },
    "&::-webkit-inner-spin-button": {
      "-webkit-appearance": "none",
      margin: 0
    },
    "-moz-appearance": "textfield"
  },
});

interface Props {
  ref?: any;
  input?: Partial<WrappedFieldInputProps>;
  meta?: Partial<WrappedFieldMetaProps>;
  type?: "password" | "percentage" | "number",
  returnType?: "object" | "string",
  classes?: any;
  helperText?: string,
  label?: string,
  listSpacing?: boolean,
  hideLabel?: boolean,
  maxLength?: number,
  disabled?: boolean,
  min?: number,
  max?: number,
  step?: string,
  className?: string,
  onKeyPress?: any,
  allowEmpty?: boolean,
  placeholder?: string,
  hidePlaceholderInEditMode?: boolean,
  InputProps?: any,
  InputLabelProps?: any,
  labelAdornment?: any,
  preformatDisplayValue?: any,
  truncateLines?: number,
  fieldClasses?: any,
  rightAligned?: boolean,
  disableInputOffsets?: boolean,
  onKeyDown?: any,
  onInnerValueChange?: any,
  defaultValue?: any
  disabledTab?: any
  autoWidth?: boolean,
  multiline?: boolean,
  inline?: boolean,
}

export class EditInPlaceFieldBase extends React.PureComponent<Props, any> {
  state = {
    isEditing: false
  };

  private inputNode: any;

  componentDidUpdate() {
    if (this.state.isEditing && this.props.disabled) {
      this.setState({
        isEditing: false
      });
    }
  }

  setInputNode = node => {
    if (node) {
      this.inputNode = node;
    }
  };

  edit = e => {
    if (e) {
      e.preventDefault();
      e.stopPropagation();
    }
    this.setState({
      isEditing: true
    });
  };

  onBlur = () => {
    const { input, meta: { invalid } } = this.props;

    this.setState({
      isEditing: invalid || false
    });
    input.onBlur(input.value);
  };

  onFocus = e => {
    const {
      input, type, multiline
    } = this.props;

    if (!this.state.isEditing) {
      this.setState({
        isEditing: true
      });
    }
    input.onFocus(e);

    if (this.inputNode) {
      if (!multiline) {
        this.inputNode.type = "text";
      }
      this.inputNode.setSelectionRange(this.inputNode.value.length, this.inputNode.value.length);
      if (type === "number") {
        this.inputNode.type = "number";
      }
    }
  };

  onFieldChange = v => {
    const {
      input: { onChange },
      disabled
    } = this.props;

    if (!disabled) {
      onChange(v);
    }
  };

  getInputLength = () => {
    const { input } = this.props;
    const length = String(input.value).length;
    
    return length + "ch";
  };

  getValue = () => {
    const {
      type,
      multiline,
      input: { value },
      placeholder,
      classes,
      preformatDisplayValue,
      defaultValue,
      fieldClasses = {}
    } = this.props;

    if (type === "password") {
      return value ? (
        value.replace(/./g, "•")
      ) : (
        <span className={clsx(classes.placeholderContent, classes.editable)}>••••••••••</span>
      );
    }

    if (type === "percentage") {
      if (typeof value === "number") {
        return value + "%";
      }
      if (defaultValue) {
        return defaultValue;
      }
    }

    const formattedValue = value;

    if (multiline && formattedValue) {
      return formattedValue.split(/\n/g).map((char, i) => (i > 0 ? (
        <React.Fragment key={i}>
          <br />
          {' '}
          {char}
        </React.Fragment>
      ) : (
        char
      )));
    }

    return formattedValue || formattedValue === 0 ? (
      preformatDisplayValue ? (
        preformatDisplayValue(formattedValue)
      ) : (
        formattedValue
      )
    ) : defaultValue || (
      <span className={clsx(classes.placeholderContent, classes.editable, fieldClasses.placeholder)}>
        {placeholder || "No value"}
      </span>
    );
  };

  render() {
    const {
      classes,
      inline,
      input,
      meta: { error, invalid },
      helperText,
      label,
      listSpacing = true,
      hideLabel,
      maxLength,
      type,
      disabled,
      ref,
      min,
      max,
      step,
      className,
      onKeyPress,
      allowEmpty,
      placeholder,
      hidePlaceholderInEditMode,
      returnType,
      InputProps: { ...restInputProps } = {},
      InputLabelProps = {},
      labelAdornment,
      preformatDisplayValue,
      truncateLines,
      fieldClasses = {},
      rightAligned,
      disableInputOffsets,
      onKeyDown,
      onInnerValueChange,
      defaultValue,
      disabledTab,
      multiline,
      ...custom
    } = this.props;

    const { isEditing } = this.state;

    const textFieldProps = {
      onKeyPress,
      onBlur: this.onBlur,
      inputProps: {
        ref,
        maxLength,
        step,
        min,
        max,
        onKeyDown,
        type: type !== "password" ? (type === "percentage" ? "number" : type) : undefined,
        className: clsx(fieldClasses.text, {
          [classes.inlineInput]: inline,
          [classes.readonly]: disabled,
          [classes.smallOffsetInput]: disableInputOffsets,
          [classes.hideArrows]: ["percentage", "number"].includes(type),
          "text-end": rightAligned
        }),
        placeholder: placeholder || (!isEditing ? "No value" : ""),
        style: {
          maxWidth: inline && !invalid ? this.getInputLength() : undefined
        },
      },
      value: input.value ? input.value : !isEditing && defaultValue ? defaultValue : input.value,
      onFocus: this.onFocus,
      onChange: v => (type === "number" && max && Number(v) > Number(max) ? null : this.onFieldChange(v))
    };

    const iconProps = {
      className: clsx("hoverIcon editInPlaceIcon", classes.selectIcon, fieldClasses.placeholder, {
        [classes.hiddenContainer]: (rightAligned || inline) && disabled,
        [classes.invisibleContainer]: !(rightAligned || inline) && disabled
      })
    };

    const editIcon =  <Edit
      {...iconProps}
    />;

    return (
      <div
        id={input.name}
        className={clsx(className, "outline-none", {
          [classes.inlineContainer]: inline
        })}
      >
        <div
          className={clsx({
            [classes.inlineMargin]: inline,
            [classes.rightAligned]: rightAligned,
            [classes.hiddenContainer]: inline && !(isEditing || invalid),
            "d-inline": inline && (isEditing || invalid)
          })}
        >
          <FormControl
            fullWidth
            error={invalid}
            variant="standard"
            margin="none"
            className={clsx({
              [classes.topMargin]: !listSpacing && !disableInputOffsets,
              [classes.inlineTextField]: inline
            })}
            {...custom}
          >
            {
              label && (
                <InputLabel
                  classes={{
                    root: clsx(
                      fieldClasses.label,
                      "d-flex",
                      "overflow-visible",
                      !label && classes.labelTopZeroOffset,
                      rightAligned && classes.rightLabel
                    ),
                  }}
                  {...InputLabelProps}
                  shrink={Boolean(label || input.value)}
                  htmlFor={`input-${input.name}`}
                >
                  <span className={classes.label}>
                    {label}
                  </span>
                  {labelAdornment}
                </InputLabel>
              )
            }

            <Input
              {...restInputProps}
              {...textFieldProps}
              maxRows={truncateLines}
              multiline={multiline}
              inputRef={this.setInputNode}
              classes={{
                root: clsx(inline && classes.inlineInput, classes.textFieldBorderModified,
                  fieldClasses.text, classes.inputWrapper, isEditing && classes.isEditing),
                underline: fieldClasses.underline
              }}
              disabled={disabled}
              endAdornment={!inline && !disabled && (
                <InputAdornment
                  position="end"
                  className={clsx(classes.inputEndAdornment, {
                    [classes.hiddenContainer]: rightAligned,
                    [classes.invisibleContainer]: !rightAligned
                  })}
                  onClick={() => this.inputNode && this.inputNode.focus()}
                >
                  <Edit />
                </InputAdornment>
              )}
              id={`input-${input.name}`}
              name={input.name}
            />

            <FormHelperText
              classes={{
                root: clsx(rightAligned && "text-end"),
                error: "shakingError"
              }}
            >
              {error || helperText}
            </FormHelperText>
          </FormControl>
        </div>
        {inline && (
          <div
            className={clsx({
              [classes.hiddenContainer]: isEditing || invalid || !inline,
              [classes.rightAligned]: rightAligned,
              "d-inline": inline && !(isEditing || invalid)
            })}
          >
            <div className={clsx(inline ? "d-inline" : classes.fitWidth)}>
              {inline && !hideLabel && label}

              {inline && (
                <ButtonBase
                  component="span"
                  onFocus={this.edit}
                  onClick={this.edit}
                  className={clsx(
                    "d-inline vert-align-bl hoverIconContainer",
                    classes.editable,
                    fieldClasses.text, {
                      [classes.rightAligned]: rightAligned,
                      [classes.readonly]: disabled,
                      [classes.inlineMargin]: disabled
                    }
                  )}
                >
                  {this.getValue()}
                  {editIcon}
                </ButtonBase>
              )}

              {helperText && <Typography variant="caption">{helperText}</Typography>}
            </div>
          </div>
        )}
      </div>
    );
  }
}

export default withStyles(styles)(EditInPlaceFieldBase);