/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FieldArray } from "redux-form";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import Delete from "@material-ui/icons/Delete";
import FormField from "../../../../../../common/components/form/form-fields/FormField";

const bindingsRenderer: any = ({ fields }) =>
  fields.map(f => (
    <>
      <Grid item xs={6}>
        <FormField name={`${f}.variable`} type="text" listSpacing={false} disableInputOffsets />
      </Grid>
      <Grid item xs={6}>
        <FormField name={`${f}.data`} type="text" listSpacing={false} disableInputOffsets />
      </Grid>
    </>
  ));

const EmailCardContent = ({ field, name, classes }) => (
  <Grid container className="mt-3">
    {Object.keys(field).map(v => {
      if (v === "type") {
        return null;
      }
      if (v === "bindings") {
        return (
          <Grid xs={5} key={v}>
            <div>
              <div className="heading">Bindings</div>
              <IconButton
                classes={{
                  root: "ml-1 inputAdornmentButton"
                }}
              >
                <Delete className="inputAdornmentIcon placeholderContent" />
              </IconButton>
            </div>

            <div className={classes.infoContainer}>
              <Grid container>
                <Grid item xs={6}>
                  <Typography variant="caption">Variable</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption">Data</Typography>
                </Grid>
                <FieldArray name={`${name}.bindings`} component={bindingsRenderer} />
              </Grid>
            </div>
          </Grid>
        );
      }
      return (
        <Grid xs={12} key={v}>
          <FormField
            type="multilineText"
            label={v.replace(v[0], v[0].toUpperCase())}
            name={`${name}.${v}`}
            labelAdornment={(
              <span>
                <IconButton
                  classes={{
                    root: "inputAdornmentButton"
                  }}
                >
                  <Delete className="inputAdornmentIcon placeholderContent" />
                </IconButton>
              </span>
            )}
            fullWidth
          />
        </Grid>
      );
    })}
  </Grid>
);

export default EmailCardContent;
