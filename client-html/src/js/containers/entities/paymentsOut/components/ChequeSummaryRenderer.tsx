/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import FormField from "../../../../common/components/form/form-fields/FormField";

const ChequeSummaryRenderer = () => (
  <Grid item xs={12} container>
    <Grid item xs={4}>
      <FormField type="text" name="chequeSummary.chequeBank" label="Cheque bank" fullWidth />
    </Grid>
    <Grid item xs={4}>
      <FormField type="text" name="chequeSummary.chequeBranch" label="Cheque branch" fullWidth />
    </Grid>
    <Grid item xs={4}>
      <FormField type="text" name="chequeSummary.chequeDrawer" label="Cheque drawer" fullWidth />
    </Grid>
  </Grid>
);

export default ChequeSummaryRenderer;
