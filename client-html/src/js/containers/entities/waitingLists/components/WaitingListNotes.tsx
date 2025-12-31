/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Grid } from '@mui/material';
import $t from '@t';
import * as React from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import { EditViewProps } from '../../../../model/common/ListView';

const WaitingListNotes: React.FunctionComponent<EditViewProps> = ({ twoColumn }) => (
  <Grid container columnSpacing={3} rowSpacing={2} className="pl-3 saveButtonTableOffset">
    <Grid item xs={12}>
      <div className="centeredFlex">
        <div className="heading pb-1">{$t('notes')}</div>
      </div>
    </Grid>
    <Grid item xs={twoColumn ? 6 : 12}>
      <FormField
        type="multilineText"
        name="studentNotes"
        disabled
        label={$t('student_notes')}
      />
    </Grid>
    <Grid item xs={twoColumn ? 6 : 12}>
      <FormField type="multilineText" name="privateNotes" label={$t('private_notes')} />
    </Grid>
  </Grid>
);

export default WaitingListNotes;
