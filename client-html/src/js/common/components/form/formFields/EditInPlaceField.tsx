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
import { change } from "redux-form";
import clsx from "clsx";
import { createStyles, withStyles } from "@mui/styles";
import { Edit, ExpandMore } from "@mui/icons-material";
import {
  ButtonBase,
  FormControl,
  FormHelperText,
  Input,
  InputAdornment,
  InputLabel,
  ListItem,
  MenuItem,
  Select,
  Typography
} from "@mui/material";
import { AppTheme } from "../../../../model/common/Theme";

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

export class EditInPlaceFieldBase extends React.PureComponent<any, any> {
  private preventEditOnFocus: boolean = false;

  state = {
    isEditing: false
  };

  private inputNode: any;

  componentWillUnmount() {
    const {
      clearOnUnmount,
      input: { name },
      meta: { dispatch, form }
    } = this.props;

    if (clearOnUnmount) {
      dispatch(change(form, name, null));
    }
  }

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
    if (!this.props.select) {
      setTimeout(() => {
        if (this.inputNode) this.inputNode.focus();
      }, 50);
    }
  };

  onBlur = () => {
    const { input, invalid } = this.props;

    this.setState({
      isEditing: invalid || false
    });
    input.onBlur(input.value);
  };

  onFocus = () => {
    const {
      input, type, select, multiline
    } = this.props;

    if (!this.state.isEditing) {
      this.setState({
        isEditing: true
      });
    }
    input.onFocus();

    if (!select && this.inputNode) {
      if (!multiline) {
        this.inputNode.type = "text";
      }
      this.inputNode.setSelectionRange(this.inputNode.value.length, this.inputNode.value.length);
      if (type === "number") {
        this.inputNode.type = "number";
      }
    }
  };

  onEditButtonFocus = (e, type) => {
    if (this.props.select && this.props.disabledTab && type === "focus") return;
    if (this.props.select && type === "focus" && this.preventEditOnFocus && !this.props.disabledTab) {
      this.preventEditOnFocus = false;
      return;
    }
    this.edit(e);
  };

  onSelectClose = () => {
    this.preventEditOnFocus = true;
    this.setState({
      isEditing: false
    });
  };

  onSelectChange = e => {
    const {
      items, input, selectValueMark = "value", onInnerValueChange, returnType
    } = this.props;

    const selected = items.find(i => i[selectValueMark] === e.target.value);

    if (typeof onInnerValueChange === "function") {
      onInnerValueChange(selected);
    }

    if (items.length) {
      input.onChange(returnType === "object" ? selected || null : e.target.value);
      return;
    }
    this.onSelectClose();
  };

  onFieldChange = v => {
    const {
      input: { onChange },
      disabled,
      stringValue
    } = this.props;

    if (!disabled) {
      onChange(stringValue ? String(v) : v);
    }
  };

  getInputLength = () => {
    const { input, type } = this.props;
    let commas = 0;
    const length = String(input.value).length;

    if (type === "money") {
      let amountLength = length;
      for (let i = 0; i < length; i++ ) {
        if (amountLength - 3 > 0) {
          commas++;
          amountLength -= 3;
        }
      }
    }
    return (length + commas) + "ch";
  }

  getValue = () => {
    const {
      type,
      select,
      items,
      multiple,
      multiline,
      input: { value },
      placeholder,
      selectValueMark = "value",
      selectLabelMark = "label",
      selectLabelCondition,
      classes,
      returnType = "string",
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

    if (select) {
      if (multiple && value.length) {
        return value.map((item, index) => (
          <span key={index} className={classes.editable}>
            {item}
            {value.length > 1 ? index + 1 !== value.length && ", " : null}
          </span>
        ));
      }

      const match = value !== null && value !== undefined && items.find(
        item => (returnType === "object" ? item[selectValueMark] === value[selectValueMark] : item[selectValueMark] === value)
      );

      if (match) return selectLabelCondition ? selectLabelCondition(match) : match[selectLabelMark];

      if (defaultValue) {
        return defaultValue;
      }
      return (
        <span className={clsx(classes.placeholderContent, classes.editable, fieldClasses.placeholder)}>
          {placeholder || "No value"}
        </span>
      );
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

  getSelectValue = () => {
    const {
      input, multiple, returnType, selectLabelCondition, selectValueMark
    } = this.props;

    return multiple
      ? input.value || []
      : returnType === "object"
        ? selectLabelCondition
          ? selectLabelCondition(input.value)
          : input.value ? input.value[selectValueMark] : ""
        : [undefined, null].includes(input.value)
          ? ""
          : input.value;
  }

  render() {
    const {
      classes,
      formatting = "primary",
      selectValueMark = "value",
      selectLabelMark = "label",
      selectLabelCondition,
      input,
      meta: { error, invalid },
      helperText,
      select,
      items,
      label,
      listSpacing = true,
      hideLabel,
      editableComponent,
      maxLength,
      type,
      disabled,
      multiple,
      min,
      max,
      step,
      className,
      onKeyPress,
      allowEmpty,
      placeholder,
      hidePlaceholderInEditMode,
      selectAdornment,
      returnType,
      InputProps: { ...restInputProps } = {},
      InputLabelProps = {},
      labelAdornment,
      preformatDisplayValue,
      clearOnUnmount,
      truncateLines,
      fieldClasses = {},
      rightAligned,
      disableInputOffsets,
      onKeyDown,
      onInnerValueChange,
      defaultValue,
      disabledTab,
      sort,
      multiline,
      categoryKey,
      autoWidth = true,
      zeroPadding,
      allowNegative,
      ...custom
    } = this.props;

    const { isEditing } = this.state;

    const isInline = formatting === "inline";

    const sortedItems = items && (sort
      ? [...items].sort(typeof sort === "function"
        ? sort
        : (aOption, bOption) => {
          const aLabel = selectLabelCondition ? selectLabelCondition(aOption) : aOption[selectLabelMark];
          const bLabel = selectLabelCondition ? selectLabelCondition(bOption) : bOption[selectLabelMark];

          return (aLabel.toLowerCase()).localeCompare(bLabel.toLowerCase());
        })
      : [...items]);

    let selectItems;

    if (categoryKey) {
      const categoryGroup = sortedItems.reduce((acc, cur) => {
        if (Array.isArray(acc[cur[categoryKey]])) {
          acc[cur[categoryKey]].push(cur);
        } else {
          acc[cur[categoryKey]] = [cur];
        }
        return acc;
      }, {});
      selectItems = [];
      Object.keys(categoryGroup).forEach(k => {
        selectItems.push(<ListItem key={k} className={clsx("heading", selectItems.length && "mt-2")}>{k}</ListItem>);
        categoryGroup[k].sort((aOption, bOption) => {
          const aLabel = selectLabelCondition ? selectLabelCondition(aOption) : aOption[selectLabelMark];
          const bLabel = selectLabelCondition ? selectLabelCondition(bOption) : bOption[selectLabelMark];
          return (aLabel.toLowerCase()).localeCompare(bLabel.toLowerCase());
        });
        selectItems.push(...categoryGroup[k].map((option, index) => (
          <MenuItem key={k + index} value={option[selectValueMark]}>
            {selectLabelCondition ? selectLabelCondition(option) : option[selectLabelMark]}
          </MenuItem>
        )));
      });
    } else {
      selectItems = sortedItems
        && sortedItems.map((option, index) => (
          <MenuItem key={index} value={option[selectValueMark]}>
            {selectLabelCondition ? selectLabelCondition(option) : option[selectLabelMark]}
          </MenuItem>
        ));
    }

    if (selectAdornment) {
      selectItems = selectAdornment.position === "start"
        ? [selectAdornment.content, ...selectItems]
        : [...selectItems, selectAdornment.content];
    }

    if (((items && !items.some(i => [undefined, null].includes(i[selectValueMark]))) || !items) && (allowEmpty || !input.value) && (!multiple || !items.length)) {
      selectItems = [
        <MenuItem
          key="empty"
          value=""
          classes={{
            root: clsx({
              [classes.selectedItem]: multiple,
              [classes.emptySelect]: multiple
            }),
            selected: classes.emptySelect
          }}
        >
          <span className={clsx(classes.placeholderContent, !isEditing && fieldClasses.placeholder)}>{placeholder || "No value"}</span>
        </MenuItem>,
        ...selectItems || []
      ];
    }

    const textFieldProps = {
      onKeyPress,
      onBlur: this.onBlur,
      inputProps: {
        maxLength,
        step,
        min,
        max,
        onKeyDown,
        allownegative: allowNegative,
        type: type !== "password" ? (type === "percentage" ? "number" : type) : undefined,
        className: clsx(fieldClasses.text, {
          [classes.inlineInput]: isInline,
          [classes.readonly]: disabled,
          [classes.smallOffsetInput]: disableInputOffsets,
          [classes.hideArrows]: ["percentage", "number"].includes(type),
          "text-end": rightAligned
        }),
        placeholder: placeholder || (!isEditing ? "No value" : ""),
        style: {
          maxWidth: isInline && !invalid ? this.getInputLength() : undefined
        },
      },
      value: input.value ? input.value : !isEditing && defaultValue ? defaultValue : input.value,
      onFocus: this.onFocus,
      onChange: v => (type === "number" && max && Number(v) > Number(max) ? null : this.onFieldChange(v))
    };

    const iconProps = {
      className: clsx("hoverIcon editInPlaceIcon", classes.selectIcon, fieldClasses.placeholder, {
        [classes.hiddenContainer]: (rightAligned || isInline) && disabled,
        [classes.invisibleContainer]: !(rightAligned || isInline) && disabled
      })
    };

    const editIcon = select ? (
      <ExpandMore
        {...iconProps}
      />
    ) : (
      <Edit
        {...iconProps}
      />
    );

    return (
      <div
        id={input.name}
        className={clsx(className, "outline-none", {
          [classes.inlineContainer]: isInline
        })}
      >
        <div
          className={clsx({
            [classes.inlineMargin]: isInline,
            [classes.rightAligned]: rightAligned,
            [classes.hiddenContainer]: isInline && !(isEditing || invalid),
            [classes.invisibleContainer]: isEditing && select && !invalid,
            "d-inline": isInline && (isEditing || invalid)
          })}
        >
          <FormControl
            fullWidth
            error={invalid}
            variant="standard"
            margin="none"
            className={clsx({
              [classes.topMargin]: !listSpacing && !disableInputOffsets,
              [classes.inlineTextField]: isInline
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

            {select
              ? (
                <div className={clsx(isInline && "d-inline", label && 'mt-2', classes.selectMainWrapper)}>
                  <Select
                    id={`input-select-${input.name}`}
                    name={input.name}
                    value={this.getSelectValue()}
                    inputRef={this.setInputNode}
                    inputProps={{
                      classes: {
                        root: classes.textFieldBorderModified,
                        underline: fieldClasses.underline
                      },
                      id: `input-${input.name}`
                    }}
                    classes={{
                      select: clsx(classes.muiSelect, fieldClasses.text, isInline && classes.inlineSelect),
                    }}
                    multiple={multiple}
                    autoWidth={autoWidth}
                    open={isEditing}
                    disabled={disabled}
                    onOpen={this.onFocus}
                    onClose={this.onSelectClose}
                    onChange={this.onSelectChange}
                    IconComponent={() => (!disabled && <ExpandMore className={classes.selectIconInput} onClick={this.onFocus} />)}
                    MenuProps={{
                      anchorOrigin: { vertical: 'top', horizontal: 'center' },
                      classes: {
                        root: classes.selectMenu
                      }
                    }}
                    displayEmpty
                    fullWidth
                  >
                    {selectItems}
                  </Select>
                </div>
              )
              : (
                <Input
                  {...restInputProps}
                  {...textFieldProps}
                  maxRows={truncateLines}
                  multiline={multiline}
                  inputRef={this.setInputNode}
                  classes={{
                    root: clsx(isInline && classes.inlineInput, classes.textFieldBorderModified,
                      fieldClasses.text, classes.inputWrapper, isEditing && classes.isEditing),
                    underline: fieldClasses.underline
                  }}
                  disabled={disabled}
                  endAdornment={!isInline && !disabled && (
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
              )}
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
        {isInline && (
          <div
            className={clsx({
              [classes.hiddenContainer]: isEditing || invalid || !isInline,
              [classes.rightAligned]: rightAligned,
              "d-inline": isInline && !(isEditing || invalid)
            })}
          >
            <div className={clsx(isInline ? "d-inline" : classes.fitWidth)}>
              {isInline && !hideLabel && label}

              {isInline && (
                <ButtonBase
                  component="span"
                  onFocus={e => this.onEditButtonFocus(e, "focus")}
                  onClick={e => this.onEditButtonFocus(e, "click")}
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
                  {editableComponent || this.getValue()}
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