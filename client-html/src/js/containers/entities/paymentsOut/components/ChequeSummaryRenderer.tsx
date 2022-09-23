/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import Grid from "@mui/material/Grid";
import FormField from "../../../../common/components/form/formFields/FormField";

const ChequeSummaryRenderer = () => (
  <Grid item xs={12} container columnSpacing={3} rowSpacing={2}>
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
