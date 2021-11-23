/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import { Grid } from "@mui/material";
import FormField from "../../../../../common/components/form/formFields/FormField";

const ProductEditView: React.FC<any> = () => (
  <Grid container columnSpacing={3} rowSpacing={2} className="ml-0">
    <Grid item xs={12} container columnSpacing={3}>
      <Grid item sm={2}>
        <FormField type="text" name="code" label="SKU" disabled />
      </Grid>
      <Grid item sm={8}>
        <FormField type="money" name="totalFee" label="Sale price" disabled />
      </Grid>
    </Grid>
    <Grid item xs={12} container columnSpacing={3}>
      <Grid item sm={6}>
        <FormField type="multilineText" name="description" label="Description" disabled />
      </Grid>
    </Grid>
  </Grid>
  );

export default ProductEditView;
