/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Faculty } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import React from 'react';
import { FieldArray } from 'redux-form';
import DocumentsRenderer from '../../../../common/components/form/documents/DocumentsRenderer';
import { FormEditorField } from '../../../../common/components/form/formFields/FormEditor';
import { EditViewProps } from '../../../../model/common/ListView';

function FacultyMarketingTab(
  {
    twoColumn,
    dispatch,
    form,
    showConfirm
  }: EditViewProps<Faculty>) {
  return <Grid container columnSpacing={3} rowSpacing={2} className="pl-3 pr-3 pb-3 mb-3">
    <Grid item xs={12}>
      <div className="heading mt-2">{$t('marketing')}</div>
    </Grid>

    <Grid item xs={12}>
      <FormEditorField name="webDescription" label={$t('web_description')} />
    </Grid>
    <Grid item xs={12}>
      <FormEditorField name="shortWebDescription" label={$t('short_web_description')} />
    </Grid>

    <Grid item xs={12}>
      <FieldArray
        name="documents"
        label={$t('documents')}
        entity="Course"
        component={DocumentsRenderer}
        xsGrid={12}
        mdGrid={twoColumn ? 4 : 12}
        lgGrid={twoColumn ? 3 : 12}
        dispatch={dispatch}
        form={form}
        showConfirm={showConfirm}
        rerenderOnEveryChange
      />
    </Grid>
  </Grid>;
}

export default FacultyMarketingTab;