/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid/Grid";
import FormField from "../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../common/components/form/Uneditable";
import { buildUrl, productUrl } from "../../sales/utils";

export const MembershipHeader: React.FunctionComponent<any> = ({ row }) => (
  <div className="w-100 d-grid gridTemplateColumns-1fr">
    <div>
      <Typography variant="subtitle2" noWrap>
        {row.productName}
      </Typography>
    </div>
  </div>
);

export const MembershipContent: React.FunctionComponent<any> = ({ row, item, twoColumn }) => {
  const gridSpacing = twoColumn ? 4 : 6;
  return (
    <Grid container>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.productName}
          label="Membership name"
          url={productUrl(row)}
        />
      </Grid>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.purchasedByName}
          label="Purchased by"
          url={buildUrl(row.purchasedById, "Contacts")}
        />
      </Grid>
      <Grid item xs={gridSpacing}>
        <FormField type="date" name={`${item}.purchasedOn`} label="Purchased on" disabled />
      </Grid>
      <Grid item xs={gridSpacing}>
        <FormField type="date" name={`${item}.validFrom`} label="Valid from" disabled />
      </Grid>
      <Grid item xs={gridSpacing}>
        <FormField type="date" name={`${item}.expiresOn`} label="Valid to" disabled />
      </Grid>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.purchasePrice}
          label="Purchase price"
          money
        />
      </Grid>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.status}
          label="Status"
        />
      </Grid>
    </Grid>
);
};
