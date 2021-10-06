
/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Wrapper component for Material Select and Text Field with edit in place functional
 * */

import React from "react";
import { change } from "redux-form";
import clsx from "clsx";
import ListItem from "@mui/material/ListItem";
import Edit from "@mui/icons-material/Edit";
import ExpandMore from "@mui/icons-material/ExpandMore";
import MenuItem from "@mui/material/MenuItem";
import ButtonBase from "@mui/material/ButtonBase";
import Typography from "@mui/material/Typography";
import FormControl from "@mui/material/FormControl";
import FormHelperText from "@mui/material/FormHelperText";
import CreateIcon from '@mui/icons-material/Create';
import ListItemText from "@mui/material/ListItemText";
import Input from "@mui/material/Input";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import { InputAdornment } from "@mui/material";

const styles = theme => createStyles({
  inputEndAdornment: {
    fontSize: "18px",
    color: theme.palette.primary.main,
    opacity: 0.5,
    display: "none",
  },
  inputWrapper: {
    "&:hover $inputEndAdornment": {
      display: "flex",
    },
    "&:before": {
      borderBottom: "1px solid transparent",
    },
  },
  isEditing: {
    borderBottom: "none!important",
    "& $inputEndAdornment": {
      display: "flex!important",
      borderBottom: "none!important",
      opacity: 1,
    },
  },
  textField: {
    paddingBottom: "9px",
    height: "60px",
    paddingLeft: "0",
    overflow: "hidden",
    display: "flex"
  },
  inlineTextField: {
    verticalAlign: "baseline",
    "& > div": {
      marginTop: 0
    }
  },
  bottomMargin: {
    marginBottom: `${theme.spacing(1) + 1}px`
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
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main
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
  textFieldLeftMargin: {
    marginLeft: theme.spacing(1)
  },
  rightPadding: {
    paddingRight: theme.spacing(2)
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
    paddingBottom: "4px",
    right: "-46%",
    maxWidth: "100%",
    "&$labelShrink": {
      maxWidth: "calc(100% * 1.4)"
    }
  },
  labelTopZeroOffset: {
    "& + $textFieldBorderModified": {
      marginTop: 0
    }
  },
  labelShrink: {
    "& $labelAdornment": {
      position: "absolute",
      transform: "scale(1.3) translate(5px,0)",
    },
  },
  labelAdornment: {},
  placeholderContent: {
    color: theme.palette.divider,
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
    color: "#fff",
    "& $placeholderContent": {
      color: "#fff",
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
  textFieldBorderModified: {
    "&:after": {
      borderBottomColor: theme.palette.primary.main
    },
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

  private containerNode: any;

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

  setContainerNode = node => {
    this.containerNode = node;
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
        this.inputNode.focus();
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

    if (!select) {
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

  getTruncatedValue = () => {
    const {
      truncateLines,
      input: { value }
    } = this.props;

    let result;

    this.containerNode.style.maxHeight = `calc(${getComputedStyle(this.containerNode).lineHeight} * ${truncateLines})`;

    const clone = this.containerNode.cloneNode();

    clone.innerText = value;
    this.containerNode.parentElement.appendChild(clone);
    this.containerNode.parentElement.removeChild(this.containerNode);

    const wordArray = clone.innerText.split(" ");
    while (clone.scrollHeight > clone.offsetHeight) {
      wordArray.pop();
      clone.innerText = wordArray.join(" ") + "..." + "##########";
    }

    if (value.length > clone.innerText.length) {
      result = clone.innerText.replace(/##########/, "");
    } else {
      result = value;
    }

    clone.parentElement.appendChild(this.containerNode);
    clone.parentElement.removeChild(clone);

    return result;
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
      truncateLines,
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

    let formattedValue = value;

    if (truncateLines && this.containerNode) {
      formattedValue = this.getTruncatedValue();
    }

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

  componentDidMount() {
    const { truncateLines } = this.props;

    if (truncateLines) this.forceUpdate();
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

    const labelContent = labelAdornment ? (
      <span>
        {label}
        {' '}
        <span
          className={classes.labelAdornment}
        >
          {labelAdornment}
        </span>
      </span>
    ) : (
      label
    );

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

    if (((items && !items.some(i => !i[selectValueMark])) || !items) && (allowEmpty || !input.value) && (!multiple || !items.length)) {
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
          <span className={classes.placeholderContent}>{placeholder || "No value"}</span>
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
        type: type !== "password" ? (type === "percentage" ? "number" : type) : undefined,
        className: clsx({
          [classes.inlineInput]: isInline,
          [classes.readonly]: disabled,
          [classes.smallOffsetInput]: disableInputOffsets,
          [classes.hideArrows]: ["percentage", "number"].includes(type),
          "text-end": rightAligned
        }),
        placeholder: placeholder || (!isEditing && "No value"),
        style: {
          maxWidth: isInline ? this.getInputLength() : undefined
        }
      },
      value: input.value ? input.value : !isEditing && defaultValue ? defaultValue : input.value,
      onFocus: this.onFocus,
      onChange: v => (type === "number" && max && Number(v) > Number(max) ? null : this.onFieldChange(v)),
      variant: restInputProps.variant || "standard",
    };

    const editIcon = select ? (
      <ExpandMore
        className={clsx("hoverIcon", classes.selectIcon, fieldClasses.placeholder, {
          [classes.hiddenContainer]: rightAligned && disabled,
          [classes.invisibleContainer]: !rightAligned && disabled
        })}
      />
    ) : (
      <Edit
        className={clsx("hoverIcon editInPlaceIcon", fieldClasses.placeholder, {
          [classes.hiddenContainer]: rightAligned && disabled,
          [classes.invisibleContainer]: !rightAligned && disabled
        })}
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
            [classes.rightPadding]: formatting !== "inline",
            [classes.inlineMargin]: isInline,
            [classes.hiddenContainer]: isInline && !(isEditing || invalid),
            [classes.invisibleContainer]: isEditing && select && !invalid,
            "d-inline": isInline && (isEditing || invalid)
          })}
        >
          <FormControl
            error={invalid}
            margin="none"
            fullWidth
            className={clsx({
              [classes.topMargin]: !listSpacing && !disableInputOffsets,
              [classes.bottomMargin]: listSpacing && formatting !== "inline",
              [classes.inlineTextField]: isInline
            })}
            {...custom}
          >
            {
              label && (
              <InputLabel
                classes={{
                  root: clsx(classes.label, !label && classes.labelTopZeroOffset),
                  shrink: classes.labelShrink
                }}
                {...InputLabelProps}
                shrink={Boolean(label || input.value)}
                variant={restInputProps.variant || "standart"}
              >
                {labelContent}
              </InputLabel>
              )
            }

            {select
              ? (
                <div className={clsx(isInline && "d-inline", label && 'mt-2', classes.selectMainWrapper)}>
                  <Select
                    value={multiple
                      ? input.value || []
                      : returnType === "object"
                        ? selectLabelCondition
                          ? selectLabelCondition(input.value)
                          : input.value ? input.value[selectValueMark] : ""
                        : input.value || ""}
                    inputRef={this.setInputNode}
                    classes={{
                      root: clsx(classes.textFieldBorderModified, fieldClasses.text),
                      select: clsx(isInline && classes.inlineSelect),
                      // @ts-ignore
                      underline: fieldClasses.underline
                    }}
                    multiple={multiple}
                    autoWidth={autoWidth}
                    open={isEditing}
                    disabled={disabled}
                    onOpen={this.onFocus}
                    onClose={this.onSelectClose}
                    onChange={this.onSelectChange}
                    IconComponent={() => (!disabled && <ExpandMore className={classes.selectIconInput} onClick={this.onFocus} />)}
                    variant="standard"
                    displayEmpty
                  >
                    {selectItems}
                  </Select>
                </div>
                )
              : (
                <Input
                  {...restInputProps}
                  {...textFieldProps}
                  multiline={multiline}
                  inputRef={this.setInputNode}
                  classes={{
                    root: clsx(isInline && classes.inlineInput, classes.textFieldBorderModified,
                      fieldClasses.text, classes.inputWrapper, isEditing && classes.isEditing),
                    underline: fieldClasses.underline
                  }}
                  disabled={disabled}
                  endAdornment={!isInline && !disabled && (
                    <InputAdornment position="end" className={classes.inputEndAdornment}>
                      <CreateIcon />
                    </InputAdornment>
                  )}
                />
              )}
            <FormHelperText
              classes={{
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
            [classes.textField]: listSpacing && formatting !== "inline",
            [classes.rightAligned]: rightAligned,
            "d-inline": isInline && !(isEditing || invalid)
          })}
        >
          <div className={clsx(isInline ? "d-inline" : classes.fitWidth)}>
            {isInline && !hideLabel && label && labelContent}

            {formatting === "primary" && (
            <ListItemText
              classes={{
                  root: `${classes.viewMode} ${disabled ? classes.readonly : ""}`,
                  primary: "d-flex"
                }}
              primary={(
                <ButtonBase
                  classes={{
                      root: classes.valueContainer
                    }}
                  onFocus={e => this.onEditButtonFocus(e, "focus")}
                  onClick={e => this.onEditButtonFocus(e, "click")}
                  className={clsx("hoverIconContainer", fieldClasses.text)}
                  component="div"
                >
                  <span
                    ref={this.setContainerNode}
                    className={clsx(classes.editable, {
                        [classes.rightAligned]: rightAligned
                      })}
                  >
                    {editableComponent || this.getValue()}
                    {editIcon}
                  </span>
                </ButtonBase>
                )}
            />
            )}

            {formatting === "secondary" && (
            <ListItemText
              classes={{
                  root: `${classes.viewMode}`,
                  secondary: "d-flex"
                }}
              secondary={(
                <ButtonBase
                  disabled={disabled}
                  classes={{
                      root: classes.valueContainer
                    }}
                  onFocus={e => this.onEditButtonFocus(e, "focus")}
                  onClick={e => this.onEditButtonFocus(e, "click")}
                  component="span"
                  className={clsx("hoverIconContainer", fieldClasses.text)}
                >
                  <span
                    ref={this.setContainerNode}
                    className={clsx(classes.editable, {
                        [classes.rightAligned]: rightAligned
                      })}
                  >
                    {editableComponent || this.getValue()}
                    {editIcon}
                  </span>
                </ButtonBase>
                )}
            />
            )}

            {formatting === "custom" && (
            <ButtonBase
              component="div"
              onFocus={e => this.onEditButtonFocus(e, "focus")}
              onClick={e => this.onEditButtonFocus(e, "click")}
              className={clsx(classes.editable, "hoverIconContainer", classes.fitWidth, fieldClasses.text, {
                  [classes.rightAligned]: rightAligned,
                  [classes.readonly]: disabled
                })}
            >
              {editableComponent || this.getValue()}
              {editIcon}
            </ButtonBase>
            )}

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
