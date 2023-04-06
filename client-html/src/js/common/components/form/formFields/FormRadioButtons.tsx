/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { withStyles } from "@mui/styles";
import {
  FormControl, FormControlLabel, Radio, RadioGroup
} from "@mui/material";
import Typography from "@mui/material/Typography";

const styles = theme => ({
  root: {
    display: "flex"
  },
  group: {
    margin: theme.spacing(1, 0)
  }
});

const FormRadioButtons = props => {
  const {
    classes,
    items,
    disabled,
    input: { value, onChange, name },
    labelPath = "label",
    valuePath = "value",
    color = "primary",
    row
  } = props;

  return (
    <div className={classes.root}>
      <FormControl component="fieldset">
        <RadioGroup className={classes.group} value={value} onChange={(e: any) => onChange(e.target.value)} row={row}>
          {items
            && items.map((i, n) => (
              <FormControlLabel
                key={n}
                value={i[valuePath]}
                control={<Radio color={color} id={`input-${n}-${name}`} name={name} />}
                label={(
                  <Typography component="span" variant="body2">
                    {i[labelPath]}
                  </Typography>
                )}
                disabled={disabled}
                htmlFor={`input-${n}-${name}`}
              />
            ))}
        </RadioGroup>
      </FormControl>
    </div>
  );
};

export default withStyles(styles)(FormRadioButtons) as React.FunctionComponent<any>;
