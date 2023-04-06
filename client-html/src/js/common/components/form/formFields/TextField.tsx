  /*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

  /**
 * Wrapper component for Material Text Field
 * */

import * as React from "react";
import { withStyles } from "@mui/styles";
import MuiTextField from "@mui/material/TextField";

const styles = theme => ({
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1)
  }
});

class TextInput extends React.Component<any, any> {
  render() {
    const {
 classes, onChange, id, margin = "dense", ...props
} = this.props;

    return (
      <MuiTextField
        name={props.id}
        onChange={e => onChange(e.target.value, id)}
        margin={margin}
        {...props}
        variant="standard"
      />
    );
  }
}

export const TextField = withStyles(styles)(TextInput);

// Redux Form field text component creator
export const FormTextField = props => {
  const {
    input, meta: { error, invalid, touched }, helperText, inputRef, ...custom
  } = props;

  return (
    <TextField
      error={touched && invalid}
      inputRef={inputRef}
      helperText={(touched && error) || helperText}
      fullWidth
      {...input}
      {...custom}
    />
  );
};
