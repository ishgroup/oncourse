/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from '@mui/material/Grid';
import $t from '@t';
import React from 'react';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { EditViewProps } from '../../../../../model/common/ListView';
import { CourseClassExtended } from '../../../../../model/entities/CourseClass';

const CourseClassDetTab: React.FC<EditViewProps<CourseClassExtended>> = () => (
  <>
    <div className="pl-3 pr-3 centeredFlex">
      <div className="heading mt-2 mb-2">{$t('det_export')}</div>
    </div>
    <Grid container columnSpacing={3} className="pl-3 pr-3">
      <Grid item xs={12}>
        <FormField type="text" name="initialDetExport" label={$t('initial_det_export')} />
      </Grid>
      <Grid item xs={12}>
        <FormField type="text" name="midwayDetExport" label={$t('midway_det_export')} />
      </Grid>
      <Grid item xs={12}>
        <FormField type="text" name="finalDetExport" label={$t('final_det_export')} />
      </Grid>
    </Grid>
  </>
  );

export default CourseClassDetTab;
