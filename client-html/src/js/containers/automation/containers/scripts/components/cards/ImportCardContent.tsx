/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import { FieldArray } from "redux-form";
import FormField from "../../../../../../common/components/form/form-fields/FormField";

const ImportsRenderer = props => {
  const { fields, hasUpdateAccess, isInternal } = props;

  return fields.map(f => (
    <Grid item xs={12} key={f}>
      <FormField
        type="text"
        name={f}
        label="Library"
        disabled={!hasUpdateAccess || isInternal}
        fullWidth
      />
    </Grid>
  ));
};

const ImportCardContent = props => {
  const { classes, hasUpdateAccess, isInternal } = props;

  return (
    <Grid container className="pt-3">
      <FieldArray name="imports" component={ImportsRenderer} hasUpdateAccess={hasUpdateAccess} classes={classes} isInternal={isInternal} />
    </Grid>
  );
};

export default ImportCardContent;
