/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from '@mui/material/Grid';
import $t from '@t';
import * as React from 'react';
import { FieldArray } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';

const ImportsRenderer = props => {
  const { fields, hasUpdateAccess, isInternal } = props;

  return fields.map(f => (
    <Grid item xs={12} key={f}>
      <FormField
        type="text"
        name={f}
        label={$t('library')}
        disabled={!hasUpdateAccess || isInternal}
              />
    </Grid>
  ));
};

const ImportCardContent = props => {
  const { classes, hasUpdateAccess, isInternal } = props;

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="pt-3">
      <FieldArray name="imports" component={ImportsRenderer} hasUpdateAccess={hasUpdateAccess} classes={classes} isInternal={isInternal} />
    </Grid>
  );
};

export default ImportCardContent;
