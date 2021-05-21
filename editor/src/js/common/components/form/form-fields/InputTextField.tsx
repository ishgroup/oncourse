import React from "react";
import TextField from "@material-ui/core/TextField";
import withStyles from "@material-ui/core/styles/withStyles";

const styles = () => ({
  textField: {
    "& input": {
      borderRadius: 0,
      background: "none",
      padding: "6px 0 7px",
      font: "inherit",
    },
  },
  formControl: {
    whiteSpace: "noWrap",
    width: "100%",
    overflow: "hidden",
    textOverflow: "ellipsis",
  },
});

const TextInput: React.FC<any> = props => {
  const {classes, ...rest} = props;
  return (
    <TextField
      classes={{root: classes.textField}}
      InputLabelProps={{classes: {formControl: classes.formControl}}}
      {...rest}
    />
  );
};

const InputField = withStyles(styles as any)(TextInput);

export const InputTextField: React.FC<any> = props => {
  const {
    id, input, meta: {error, invalid, touched}, helperText, inputRef, disabled, stringValue, ...custom
  } = props;
  const {name, onChange} = input;

  const onFieldChange = React.useCallback(v => {
    if (!disabled) {
      onChange(stringValue ? String(v) : v);
    }
  },                                      []);

  return (
    <InputField
      id={id ? id : `text-${name}`}
      error={touched && invalid}
      inputRef={inputRef}
      helperText={(touched && error) || helperText}
      onChange={v => onFieldChange(v)}
      {...input}
      {...custom}
    />
  );
};
