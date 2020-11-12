/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { change } from "redux-form";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import IconButton from "@material-ui/core/IconButton";
import Input from "@material-ui/core/Input";
import InputAdornment from "@material-ui/core/InputAdornment";
import ButtonBase from "@material-ui/core/ButtonBase";
import Typography from "@material-ui/core/Typography";
import ListItemText from "@material-ui/core/ListItemText";
import InputLabel from "@material-ui/core/InputLabel";
import { Attachment } from "@material-ui/icons";

const styles = theme => createStyles({
  textField: {
    paddingBottom: "13px",
    paddingLeft: "0",
    overflow: "hidden",
    display: "flex"
  },
  inlineTextField: {
    verticalAlign: "baseline"
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
  editIcon: {
    fontSize: "14px",
    color: theme.palette.divider,
    verticalAlign: "middle"
  },
  editable: {
    width: "100%",
    overflow: "hidden",
    textOverflow: "ellipsis",
    justifyContent: "flex-start",
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
    margin: 0
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
  labelShrink: {},
  labelAdornment: {},
  placeholderContent: {
    color: theme.palette.divider
  },
  chip: {
    margin: theme.spacing(0.25)
  },
  fitWidth: {
    maxWidth: "100%",
    overflow: "hidden",
    textOverflow: "ellipsis"
  },
  inlineContainer: {
    display: "inline-flex",
    marginLeft: "0.3em"
  },
  inlineInput: {
    padding: 0,
    minWidth: "2.2em"
  },
  smallOffsetInput: {
    padding: "2px"
  },
  inlineMargin: {
    marginRight: "0.3em"
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
    }
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
  }
});

export class EditInPlaceFileFieldBase extends React.PureComponent<any, any> {
  private isAdornmentHovered: boolean = false;

  state = {
    isEditing: false
  };

  private containerNode: any;

  private inputNode: any;

  private timeout: any;

  private fileInputRef: any;

  getFileInputRef = node => {
    this.fileInputRef = node;
  };

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

    this.timeout = setTimeout(() => {
      this.inputNode.focus();
    }, 50);
  };

  onAdornmentOver = () => {
    this.isAdornmentHovered = true;
  };

  onAdornmentOut = () => {
    this.isAdornmentHovered = false;
  };

  onAdornmentClick = e => {
    if (this.isAdornmentHovered) {
      e.preventDefault();
    }

    this.timeout = setTimeout(() => {
      this.isAdornmentHovered = false;
      if (!this.state.isEditing) this.onBlur();
    }, 1000);
  };

  onBlur = () => {
    const { input, invalid } = this.props;

    if (this.isAdornmentHovered) {
      return;
    }
    this.setState({
      isEditing: invalid || false
    });
    input.onBlur(input.value);
    clearTimeout(this.timeout);
  };

  onFocus = inputOnFocus => {
    if (!this.state.isEditing) {
      this.setState({
        isEditing: true
      });
    }
    inputOnFocus();
  };

  onEditButtonFocus = e => {
    this.edit(e);
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
      clone.innerText = `${wordArray.join(" ")}...##########`;
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

  getValue = () => {
    const {
      multiline,
      input: { value },
      placeholder,
      classes,
      preformatDisplayValue,
      defaultValue,
      truncateLines,
      fieldClasses = {}
    } = this.props;

    let formattedValue = value && value.name;

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

  openFilePicker = e => {
    e.preventDefault();
    e.stopPropagation();
    this.fileInputRef.click();
  };

  handleFileUpload = () => {
    const { meta: { dispatch, form }, input: { name } } = this.props;
    const file = this.fileInputRef && this.fileInputRef.files[0];

    if (file) {
      dispatch(change(form, name, file));
    }
  };

  render() {
    const {
      classes,
      formatting = "primary",
      input,
      meta: { error, invalid },
      helperText,
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
      returnType,
      InputProps: { ...restInputProps } = {},
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
      ...custom
    } = this.props;

    const { isEditing } = this.state;

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

    const textFieldProps = {
      onKeyPress,
      onBlur: this.onBlur,
      inputProps: {
        maxLength,
        step,
        min,
        max,
        onKeyDown,
        type,
        className: clsx({
          [classes.inlineInput]: formatting === "inline",
          [classes.readonly]: disabled,
          [classes.smallOffsetInput]: disableInputOffsets,
          [classes.hideArrows]: hideArrows,
          "text-end": rightAligned
        }),
        placeholder: hidePlaceholderInEditMode ? undefined : placeholder
      },
      value: typeof input.value === "object" ? input.value.name : input.value,
      onFocus: () => this.onFocus(input.onFocus)
    };

    const editIcon = (
      <Attachment
        className={clsx("hoverIcon", classes.editIcon, fieldClasses.placeholder, {
          [classes.hiddenContainer]: rightAligned && disabled,
          [classes.invisibleContainer]: !rightAligned && disabled
        })}
      />
    );

    return (
      <div
        id={input.name}
        className={clsx(className, "outline-none", {
          [classes.inlineContainer]: formatting === "inline"
        })}
      >
        <input type="file" className="d-none" onChange={this.handleFileUpload} ref={this.getFileInputRef} />
        <div
          className={clsx({
            [classes.rightPadding]: formatting !== "inline",
            [classes.inlineMargin]: formatting === "inline",
            [classes.hiddenContainer]: !(isEditing || invalid)
          })}
        >
          <FormControl
            error={invalid}
            margin="none"
            fullWidth={fullWidth}
            onClick={this.openFilePicker}
            className={clsx({
              [classes.topMargin]: !listSpacing && !disableInputOffsets,
              [classes.bottomMargin]: listSpacing && formatting !== "inline",
              [classes.inlineTextField]: formatting === "inline"
            })}
            {...custom}
          >
            <InputLabel
              classes={{
                root: clsx(classes.label, !label && classes.labelTopZeroOffset),
                shrink: classes.labelShrink
              }}
            >
              {labelContent}
            </InputLabel>
            <Input
              {...restInputProps}
              {...textFieldProps}
              endAdornment={(
                <InputAdornment position="end">
                  <IconButton
                    tabIndex={-1}
                    onClick={this.openFilePicker}
                    classes={{
                        root: clsx(classes.pickerButton, fieldClasses.text)
                      }}
                  >
                    <Attachment color="inherit" />
                  </IconButton>
                </InputAdornment>
)}
              inputRef={this.setInputNode}
              classes={{
                    root: clsx(classes.textFieldBorderModified, fieldClasses.text),
                    underline: fieldClasses.underline
                  }}
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
            [classes.rightAligned]: rightAligned
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
              <>
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
                      onFocus={e => this.onEditButtonFocus(e)}
                      onClick={e => this.onEditButtonFocus(e)}
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
              </>
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
                    onFocus={e => this.onEditButtonFocus(e)}
                    onClick={e => this.onEditButtonFocus(e)}
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
                onFocus={e => this.onEditButtonFocus(e)}
                onClick={e => this.onEditButtonFocus(e)}
                className={clsx(classes.editable, "hoverIconContainer", classes.fitWidth, fieldClasses.text, {
                  [classes.rightAligned]: rightAligned,
                  [classes.readonly]: disabled
                })}
              >
                {editableComponent || this.getValue()}
                {editIcon}
              </ButtonBase>
            )}

            {formatting === "inline" && (
              <ButtonBase
                component="span"
                onFocus={e => this.onEditButtonFocus(e)}
                onClick={e => this.onEditButtonFocus(e)}
                className={clsx(classes.editable, fieldClasses.text, "hoverIconContainer", "centeredFlex", {
                  [classes.rightAligned]: rightAligned,
                  [classes.readonly]: disabled,
                  [classes.inlineMargin]: disabled
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

export default withStyles(styles, { withTheme: true })(EditInPlaceFileFieldBase) as any;
