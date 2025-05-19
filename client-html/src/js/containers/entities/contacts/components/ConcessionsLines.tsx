/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import { validateSingleMandatoryField } from '../../../../common/utils/validation';

const ConcessionsHeaderBase: React.FunctionComponent<any> = (props: any) => {
  const { row } = props;

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <Typography variant="subtitle2" noWrap>
        {row.type && row.type.name}
      </Typography>
    </div>
  );
};

export const ConcessionsHeader = ConcessionsHeaderBase;

export const ConcessionsContent = React.memo<any>(({ item, row, concessionTypes }) => (
  <Grid container columnSpacing={3} rowSpacing={2}>
    <Grid item xs={12}>
      <FormField
        type="select"
        name={`${item}.type`}
        selectValueMark="id"
        selectLabelMark="name"
        label={$t('concession_type')}
        returnType="object"
        items={concessionTypes || []}
        validate={validateSingleMandatoryField}
      />
    </Grid>
    <Grid item xs={12}>
      <FormField
        type="text"
        name={`${item}.number`}
        label={$t('number')}
        validate={row.type && row.type.requireNumber ? validateSingleMandatoryField : undefined}
      />
    </Grid>
    <Grid item xs={12}>
      <FormField
        type="date"
        name={`${item}.expiresOn`}
        label={$t('expiry')}
        validate={row.type && row.type.requireExpary ? validateSingleMandatoryField : undefined}
      />
    </Grid>
  </Grid>
));
