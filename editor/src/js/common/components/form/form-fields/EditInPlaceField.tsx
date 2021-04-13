
/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Wrapper component for Material Select and Text Field with edit in place functional
 * */

import React from "react";
import {change} from "redux-form";
import clsx from "clsx";
import ListItem from "@material-ui/core/ListItem";
import Edit from "@material-ui/icons/Edit";
import ExpandMore from "@material-ui/icons/ExpandMore";
import MenuItem from "@material-ui/core/MenuItem";
import ButtonBase from "@material-ui/core/ButtonBase";
import Typography from "@material-ui/core/Typography";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import ListItemText from "@material-ui/core/ListItemText";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";

const styles = theme => createStyles({
  textField: {
    paddingBottom: "13px",
    paddingLeft: "0",
    overflow: "hidden",
    display: "flex",
  },
  inlineTextField: {
    verticalAlign: "baseline",
    "& > div": {
      marginTop: 0,
    },
  },
  bottomMargin: {
    marginBottom: `${theme.spacing(1) + 1}px`,
  },
  topMargin: {
    marginTop: theme.spacing(1),
    paddingLeft: "0",
  },
  hiddenContainer: {
    display: "none",
  },
  invisibleContainer: {
    visibility: "hidden",
  },
  editButton: {
    padding: "4px",
    "&:hover": {
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main,
    },
  },
  editable: {
    width: "100%",
    overflow: "hidden",
    textOverflow: "ellipsis",
    justifyContent: "flex-start",
    "&:hover, &:hover $placeholderContent, &:hover $editButton": {
      color: theme.palette.primary.main,
      fill: theme.palette.primary.main,
    },
    "&$rightAligned": {
      display: "flex",
      alignItems: "center",
      justifyContent: "flex-end",
    },
  },
  rightAligned: {},
  readonly: {
    pointerEvents: "none",
  },
  textFieldLeftMargin: {
    marginLeft: theme.spacing(1),
  },
  rightPadding: {
    paddingRight: theme.spacing(2),
  },
  viewMode: {
    padding: 0,
    margin: 0,
  },
  label: {
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
    paddingBottom: "4px",
    right: "-46%",
    maxWidth: "100%",
    "&$labelShrink": {
      maxWidth: "calc(100% * 1.4)",
    },
  },
  labelTopZeroOffset: {
    "& + $textFieldBorderModified": {
      marginTop: 0,
    },
  },
  labelShrink: {},
  labelAdornment: {},
  placeholderContent: {
    color: theme.palette.divider,
  },
  chip: {
    margin: theme.spacing(0.25),
  },
  fitWidth: {
    maxWidth: "100%",
    overflow: "hidden",
    textOverflow: "ellipsis",
  },
  emptySelect: {
    color: "#fff",
    "& $placeholderContent": {
      color: "#fff",
    },
  },
  inlineContainer: {
    display: "inline-flex",
    marginLeft: "0.3em",
  },
  inlineInput: {
    padding: 0,
    minWidth: "2.2em",
  },
  inlineSelect: {
    "&$inlineSelect": {
      padding: 0,
    },
  },
  smallOffsetInput: {
    padding: "2px",
  },
  inlineMargin: {
    marginRight: "0.3em",
  },
  selectedItem: {
    backgroundColor: `${theme.palette.action.selected}`,
  },
  valueContainer: {
    width: "100%",
  },
  oneLineEditIcon: {
    position: "absolute",
    right: "-14px",
    bottom: "4px",
  },
  textFieldBorderModified: {
    "&:after": {
      borderBottomColor: theme.palette.primary.main,
    },

  },
  selectIcon: {
    fontSize: "24px",
    color: theme.palette.divider,
    verticalAlign: "middle",
  },
  hideArrows: {
    "&::-webkit-outer-spin-button": {
      "-webkit-appearance": "none",
      margin: 0,
    },
    "&::-webkit-inner-spin-button": {
      "-webkit-appearance": "none",
      margin: 0,
    },
    "-moz-appearance": "textfield",
  },
});

export class EditInPlaceFieldBase extends React.PureComponent<any, any> {
  private isAdornmentHovered: boolean = false;

  private preventEditOnFocus: boolean = false;

  state = {
    isEditing: false,
  };

  private containerNode: any;

  private inputNode: any;

  private timeout: any;

  componentWillUnmount() {
    const {
      clearOnUnmount,
      input: {name},
      meta: {dispatch, form},
    } = this.props;

    if (clearOnUnmount) {
      dispatch(change(form, name, null));
    }
  }

  componentDidUpdate() {
    if (this.state.isEditing && this.props.disabled) {
      this.setState({
        isEditing: false,
      });
    }
  }

  setInputNode = node => {
    if (node) {
      this.inputNode = node;
    }
  }

  setContainerNode = node => {
    this.containerNode = node;
  }

  edit = e => {
    if (e) {
      e.preventDefault();
      e.stopPropagation();
    }
    this.setState({
      isEditing: true,
    });
    if (!this.props.select) {
      this.timeout = setTimeout(() => {
        this.inputNode.focus();
      },                        50);
    }
  }

  onAdornmentOver = () => {
    this.isAdornmentHovered = true;
  }

  onAdornmentOut = () => {
    this.isAdornmentHovered = false;
  }

  onAdornmentClick = e => {
    if (this.isAdornmentHovered) {
      e.preventDefault();
    }

    this.timeout = setTimeout(() => {
      this.isAdornmentHovered = false;
      if (!this.state.isEditing) this.onBlur();
    },                        1000);
  }

  onBlur = () => {
    const {input, invalid} = this.props;

    if (this.isAdornmentHovered) {
      return;
    }
    this.setState({
      isEditing: invalid || false,
    });
    input.onBlur(input.value);
    clearTimeout(this.timeout);
  }

  onFocus = () => {
    const {input} = this.props;

    if (!this.state.isEditing) {
      this.setState({
        isEditing: true,
      });
    }
    input.onFocus();
  }

  onEditButtonFocus = (e, type) => {
    if (this.props.select && this.props.disabledTab && type === "focus") return;
    if (this.props.select && type === "focus" && this.preventEditOnFocus && !this.props.disabledTab) {
      this.preventEditOnFocus = false;
      return;
    }
    this.edit(e);
  }

  onSelectClose = () => {
    this.preventEditOnFocus = true;
    this.setState({
      isEditing: false,
    });
  }

  onSelectChange = e => {
    const {
      items, input, selectValueMark = "value", onInnerValueChange, returnType,
    } = this.props;

    const selected = items.find(i => i[selectValueMark] === e.target.value);

    if (typeof onInnerValueChange === "function") {
      onInnerValueChange(selected);
    }

    if (items.length) {
      input.onChange(returnType === "object" ? selected : e.target.value);
      return;
    }
    this.onSelectClose();
  }

  onFieldChange = v => {
    const {
      input: {onChange},
      disabled,
      stringValue,
    } = this.props;

    if (!disabled) {
      onChange(stringValue ? String(v) : v);
    }
  }

  getTruncatedValue = () => {
    const {
      truncateLines,
      input: {value},
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
  }

  getValue = () => {
    const {
      type,
      select,
      items,
      multiple,
      multiline,
      input: {value},
      placeholder,
      selectValueMark = "value",
      selectLabelMark = "label",
      selectLabelCondition,
      classes,
      returnType = "string",
      preformatDisplayValue,
      defaultValue,
      truncateLines,
      fieldClasses = {},
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
        item => (returnType === "object" ? item[selectValueMark] === value[selectValueMark] : item[selectValueMark] === value),
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
  }

  componentDidMount() {
    const {truncateLines} = this.props;

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
      meta: {error, invalid},
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
      autoWidth = true,
      placeholder,
      hidePlaceholderInEditMode,
      selectAdornment,
      returnType,
      InputProps: {...restInputProps} = {},
      labelAdornment,
      preformatDisplayValue,
      clearOnUnmount,
      truncateLines,
      fieldClasses = {},
      rightAligned,
      disableInputOffsets,
      onKeyDown,
      hideArrows,
      onInnerValueChange,
      defaultValue,
      disabledTab,
      fullWidth,
      sort,
      multiline,
      categoryKey,
      ...custom
    } = this.props;

    const {isEditing} = this.state;

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
          onMouseEnter={this.onAdornmentOver}
          onMouseLeave={this.onAdornmentOut}
          onMouseDown={this.onAdornmentClick}
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
      },                                       {});
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

    if (allowEmpty && (!multiple || !items.length)) {
      selectItems = [
        <MenuItem
          key="empty"
          value={null}
          classes={{
            root: clsx({
              [classes.selectedItem]: multiple,
              [classes.emptySelect]: multiple,
            }),
            selected: classes.emptySelect,
          }}
        >
          <span className={classes.placeholderContent}>{placeholder || "No value"}</span>
        </MenuItem>,
        ...selectItems,
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
        size: formatting === "inline" && input.value ? String(input.value.length + 1) : undefined,
        type: type !== "password" ? (type === "percentage" ? "number" : type) : undefined,
        className: clsx({
          [classes.inlineInput]: formatting === "inline",
          [classes.readonly]: disabled,
          [classes.smallOffsetInput]: disableInputOffsets,
          [classes.hideArrows]: hideArrows,
          "text-end": rightAligned,
        }),
        placeholder: hidePlaceholderInEditMode ? undefined : placeholder,
      },
      value: input.value,
      onFocus: this.onFocus,
      onChange: v => (type === "number" && max && Number(v) > Number(max) ? null : this.onFieldChange(v)),
    };

    const editIcon = select ? (
      <ExpandMore
        className={clsx("hoverIcon", classes.selectIcon, fieldClasses.placeholder, {
          [classes.hiddenContainer]: rightAligned && disabled,
          [classes.invisibleContainer]: !rightAligned && disabled,
        })}
      />
    ) : (
      <Edit
        className={clsx("hoverIcon editInPlaceIcon", fieldClasses.placeholder, {
          [classes.hiddenContainer]: rightAligned && disabled,
          [classes.invisibleContainer]: !rightAligned && disabled,
        })}
      />
    );

    return (
      <div
        id={input.name}
        className={clsx(className, "outline-none", {
          [classes.inlineContainer]: formatting === "inline",
        })}
      >
        <div
          className={clsx({
            [classes.rightPadding]: formatting !== "inline",
            [classes.inlineMargin]: formatting === "inline",
            [classes.hiddenContainer]: !(isEditing || invalid),
            [classes.invisibleContainer]: isEditing && select && !invalid,
          })}
        >
          <FormControl
            error={invalid}
            margin="none"
            fullWidth={fullWidth}
            className={clsx({
              [classes.topMargin]: !listSpacing && !disableInputOffsets,
              [classes.bottomMargin]: listSpacing && formatting !== "inline",
              [classes.inlineTextField]: formatting === "inline",
            })}
            {...custom}
          >
            <InputLabel
              classes={{
                root: clsx(classes.label, !label && classes.labelTopZeroOffset),
                shrink: classes.labelShrink,
              }}
            >
              {labelContent}
            </InputLabel>
            {select
              ? (
                <Select
                  value={multiple
                    ? input.value || []
                    : returnType === "object"
                      ? selectLabelCondition
                        ? selectLabelCondition(input.value)
                        : input.value ? input.value[selectValueMark] : ""
                      : input.value}
                  inputRef={this.setInputNode}
                  classes={{
                    root: clsx(classes.textFieldBorderModified, fieldClasses.text),
                    select: clsx(formatting === "inline" && classes.inlineSelect),
                    // @ts-ignore
                    underline: fieldClasses.underline,
                  }}
                  multiple={multiple}
                  autoWidth={autoWidth}
                  open={isEditing}
                  onOpen={this.onFocus}
                  onClose={this.onSelectClose}
                  onChange={this.onSelectChange}
                >
                  {selectItems}
                </Select>
                )
              : (
                <Input
                  {...restInputProps}
                  {...textFieldProps}
                  multiline={multiline}
                  inputRef={this.setInputNode}
                  classes={{
                    root: clsx(classes.textFieldBorderModified, fieldClasses.text),
                    underline: fieldClasses.underline,
                  }}
                />
              )}
            <FormHelperText
              classes={{
                error: "shakingError",
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
            [classes.rightAligned]: rightAligned,
          })}
        >
          <div className={classes.fitWidth}>
            {!hideLabel && label && (
              <Typography variant="caption" color="textSecondary" className={fieldClasses.label} noWrap>
                {label}
                {' '}
                {labelAdornment && (
                  <span
                    onMouseEnter={this.onAdornmentOver}
                    onMouseLeave={this.onAdornmentOut}
                    onMouseDown={this.onAdornmentClick}
                  >
                    {labelAdornment}
                  </span>
                )}
              </Typography>
            )}

            {formatting === "primary" && (
            <ListItemText
              classes={{
                root: `${classes.viewMode} ${disabled ? classes.readonly : ""}`,
                primary: "d-flex",
              }}
              primary={(
                <ButtonBase
                  classes={{
                    root: classes.valueContainer,
                  }}
                  onFocus={e => this.onEditButtonFocus(e, "focus")}
                  onClick={e => this.onEditButtonFocus(e, "click")}
                  className={clsx("hoverIconContainer", fieldClasses.text)}
                  component="div"
                >
                  <span
                    ref={this.setContainerNode}
                    className={clsx(classes.editable, {
                      [classes.rightAligned]: rightAligned,
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
                  secondary: "d-flex",
                }}
                secondary={(
                  <ButtonBase
                    disabled={disabled}
                    classes={{
                      root: classes.valueContainer,
                    }}
                    onFocus={e => this.onEditButtonFocus(e, "focus")}
                    onClick={e => this.onEditButtonFocus(e, "click")}
                    component="span"
                    className={clsx("hoverIconContainer", fieldClasses.text)}
                  >
                    <span
                      ref={this.setContainerNode}
                      className={clsx(classes.editable, {
                        [classes.rightAligned]: rightAligned,
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
                  [classes.readonly]: disabled,
                })}
              >
                {editableComponent || this.getValue()}
                {editIcon}
              </ButtonBase>
            )}

            {formatting === "inline" && (
              <ButtonBase
                component="span"
                onFocus={e => this.onEditButtonFocus(e, "focus")}
                onClick={e => this.onEditButtonFocus(e, "click")}
                className={clsx(classes.editable, fieldClasses.text, "hoverIconContainer", "centeredFlex", {
                  [classes.rightAligned]: rightAligned,
                  [classes.readonly]: disabled,
                  [classes.inlineMargin]: disabled,
                })}
              >
                {editableComponent || this.getValue()}
                {editIcon}
              </ButtonBase>
            )}

            {helperText && <Typography variant="caption">{helperText}</Typography>}
          </div>
        </div>
      </div>
    );
  }
}

export default withStyles(styles)(EditInPlaceFieldBase);
