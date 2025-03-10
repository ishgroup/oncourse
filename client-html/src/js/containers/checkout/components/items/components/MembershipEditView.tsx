/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CheckoutMembershipProduct } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import { format as formatDate } from 'date-fns';
import { III_DD_MMM_YYYY } from 'ish-ui';
import React from 'react';
import { FormEditorField } from '../../../../../common/components/form/formFields/FormEditor';
import Uneditable from '../../../../../common/components/form/formFields/Uneditable';

const MembershipEditView = ({ values }: { values: CheckoutMembershipProduct }) => values ? (
  <Grid container columnSpacing={3} rowSpacing={2} className="ml-0">
    <Grid item xs={4}>
      <Uneditable label={$t('sku')} value={values.code} />
    </Grid>
    <Grid item xs={4}>
      <Uneditable label={$t('sale_price')} value={values.totalFee} money />
    </Grid>
    <Grid item xs={4}>
      <Uneditable label={$t('expires_on2')} {...values.expiresOn
        ? { value: values.expiresOn, format: v => formatDate(new Date(v), III_DD_MMM_YYYY) }
        : { value: "Never (Lifetime)" }
      } />
    </Grid>
    <Grid item xs={12}>
      <FormEditorField label={$t('description')} name="description" disabled />
    </Grid>
  </Grid>
) : null;

export default MembershipEditView;
