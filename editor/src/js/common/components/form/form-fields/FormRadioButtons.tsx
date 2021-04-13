/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import {withStyles} from "@material-ui/core/styles";
import {
 FormControlLabel, FormControl, Radio, RadioGroup,
} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";

const styles = theme => ({
  root: {
    display: "flex",
  },
  group: {
    margin: theme.spacing(1, 0),
  },
});

const FormRadioButtons = props => {
  const {
    classes,
    items,
    disabled,
    input: {value, onChange},
    labelPath = "label",
    valuePath = "value",
    color = "primary",
    row,
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
                control={<Radio color={color} />}
                label={(
                  <Typography component="span" variant="body2">
                    {i[labelPath]}
                  </Typography>
                )}
                disabled={disabled}
              />
            ))}
        </RadioGroup>
      </FormControl>
    </div>
  );
};

export default withStyles(styles)(FormRadioButtons) as React.FunctionComponent<any>;
