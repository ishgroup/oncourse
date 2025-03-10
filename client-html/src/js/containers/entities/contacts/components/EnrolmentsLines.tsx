/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import $t from '@t';
import React from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';

const EnrolmentsHeaderBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row } = props;

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <div>
        <Typography variant="subtitle2" noWrap>
          {row.courseName}
        </Typography>
      </div>
    </div>
  );
});

export const EnrolmentsHeaderLine = EnrolmentsHeaderBase;

export const EnrolmentsContentLine: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row, item } = props;

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={6}>
        <Uneditable value={row.invoiceNumber} label="Invoice#" />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="date"
          name={`${item}.createdOn`}
          label={$t('created')}
          disabled
        />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.uniqueCode} label={$t('unique_code')} />
      </Grid>
      <Grid item xs={6}>
        <Uneditable value={row.status} label={$t('status')} />
      </Grid>
    </Grid>
  );
});
