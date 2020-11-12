/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import { Grid } from "@material-ui/core";
import FormField from "../../../../../common/components/form/form-fields/FormField";

const ProductEditView: React.FC<any> = () => (
  <Grid container className="p-3">
    <Grid container>
      <Grid item sm={2}>
        <FormField type="text" name="code" label="SKU" disabled />
      </Grid>
      <Grid item sm={8}>
        <FormField type="money" name="totalFee" label="Sale price" disabled />
      </Grid>
    </Grid>
    <Grid container>
      <Grid item sm={6}>
        <FormField type="multilineText" name="description" label="Description" fullWidth disabled />
      </Grid>
    </Grid>
  </Grid>
  );

export default ProductEditView;
