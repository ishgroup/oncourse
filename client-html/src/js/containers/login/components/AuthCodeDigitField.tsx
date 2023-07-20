import * as React from "react";
import { withStyles } from "@mui/styles";
import Input from "@mui/material/Input";
import { AppTheme } from "../../../../ish-ui/model/Theme";

const styles = (theme: AppTheme) => ({
  inputWrapper: {
    "&:nth-child(3)": {
      marginRight: "32px"
    },
    padding: "3px"
  },
  input: {
    width: "10px",
    padding: "21px 25px",
    border: "1px solid #ced4da",
    borderRadius: theme.shape.borderRadius,
    color: "#868686",
    transition: theme.transitions.create(["border-color", "box-shadow"]),
    "&:focus": {
      borderColor: theme.palette.primary.main,
      boxShadow: `0 0 0 0.1rem ${theme.palette.primary.main}`
    }
  }
});

class AuthDigitInputBase extends React.Component<any, any> {
  render() {
    const { classes, margin = "dense", ...props } = this.props;

    return (
      <Input
        type="text"
        classes={{
          root: classes.inputWrapper,
          input: classes.input
        }}
        name={props.id}
        inputProps={{
          maxLength: 1
        }}
        disableUnderline
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
