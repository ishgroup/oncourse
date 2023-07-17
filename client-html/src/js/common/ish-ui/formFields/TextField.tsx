  /*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
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
