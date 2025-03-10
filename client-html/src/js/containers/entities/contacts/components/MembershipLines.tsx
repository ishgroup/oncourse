/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React from 'react';
import { ContactLinkAdornment } from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import { productUrl } from '../../sales/utils';

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
    <Grid container columnSpacing={3} rowSpacing={2}>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.productName}
          label={$t('membership_name')}
          url={productUrl(row)}
        />
      </Grid>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.purchasedByName}
          label={$t('purchased_by')}
          labelAdornment={
            <ContactLinkAdornment id={row.purchasedById} />
          }
        />
      </Grid>
      <Grid item xs={gridSpacing}>
        <FormField type="date" name={`${item}.purchasedOn`} label={$t('purchased_on')} disabled />
      </Grid>
      <Grid item xs={gridSpacing}>
        <FormField type="date" name={`${item}.validFrom`} label={$t('valid_from')} disabled />
      </Grid>
      <Grid item xs={gridSpacing}>
        <FormField type="date" name={`${item}.expiresOn`} label={$t('valid_to')} disabled />
      </Grid>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.purchasePrice}
          label={$t('purchase_price')}
          money
        />
      </Grid>
      <Grid item xs={gridSpacing}>
        <Uneditable
          value={row.status}
          label={$t('status')}
        />
      </Grid>
    </Grid>
);
};
