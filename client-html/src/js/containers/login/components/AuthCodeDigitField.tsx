import * as React from "react";
import { withStyles } from "@material-ui/core/styles";
import MuiTextField from "@material-ui/core/TextField";

const styles = theme => ({
  inputWrapper: {
    "&:nth-child(3)": {
      marginRight: "32px"
    }
  },
  root: {
    padding: "3px"
  },
  input: {
    width: "10px",
    border: "1px solid #ced4da",
    padding: "21px 25px",
    color: "#868686",
    transition: theme.transitions.create(["border-color", "box-shadow"]),
    "&:focus": {
      borderColor: "#d9d8d9",
      boxShadow: "0 0 0 0.1rem #d9d8d9"
    }
  }
});

class AuthDigitInputBase extends React.Component<any, any> {
  render() {
    const { classes, margin = "dense", ...props } = this.props;

    return (
      <MuiTextField
        type="text"
        classes={{
          root: classes.inputWrapper
        }}
        name={props.id}
        InputProps={{
          disableUnderline: true,
          classes: {
            root: classes.root,
            input: classes.input
          },
          inputProps: {
            maxLength: 1
          }
        }}
        margin={margin}
        {...props}
      />
    );
  }
}

const AuthDigitInput = withStyles(styles)(AuthDigitInputBase);

// Redux Form field auth code digit input component creator
export const AuthDigitField = props => {
  const { input, meta: { error, invalid, touched }, helperText, inputRef, ...custom } = props;

  return (
    <AuthDigitInput
      error={touched && invalid}
      inputRef={inputRef}
      helperText={(touched && error) || helperText}
      {...input}
      {...custom}
    />
  );
};
