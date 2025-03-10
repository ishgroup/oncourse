/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Faculty } from '@api/model';
import { FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import React from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { useAppSelector } from '../../../../common/utils/hooks';
import { EditViewProps } from '../../../../model/common/ListView';
import { EntityChecklists } from '../../../tags/components/EntityChecklists';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';

function FacultyGeneralTab(
  {
    twoColumn,
    values,
    isNew,
    syncErrors,
    form
  }: EditViewProps<Faculty>) {
  
  const tags = useAppSelector(state => state.tags.entityTags.Faculty);
  
  return <Grid container columnSpacing={3} rowSpacing={2} className="pt-3 pl-3 pr-3">
    <Grid item xs={12}>
      <FullScreenStickyHeader
        opened={isNew || Object.keys(syncErrors).some(k => ['code', 'name'].includes(k))}
        twoColumn={twoColumn}
        title={twoColumn ? (
          <div className="d-inline-flex-center">
            <span>
              {values && values.code}
            </span>
            <span className="ml-2">
              {values && values.name}
            </span>
          </div>
        ) : (
          <div>
            <div>
              {values && values.code}
            </div>
            <div className="mt-2">
              {values && values.name}
            </div>
          </div>
        )}
        fields={(
          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid item xs={twoColumn ? 2 : 12}>
              <FormField
                type="text"
                label={$t('code')}
                name="code"
                placeholder={twoColumn ? "Code" : undefined}
                required
              />
            </Grid>
            <Grid item xs={twoColumn ? 4 : 12}>
              <FormField
                type="text"
                label={$t('name')}
                name="name"
                placeholder={twoColumn ? "Name" : undefined}
                required
              />
            </Grid>
          </Grid>
        )}
      />
    </Grid>

    <Grid item xs={twoColumn ? 8 : 12}>
      <FormField
        type="tags"
        name="tags"
        tags={tags}
      />
    </Grid>

    <Grid item xs={twoColumn ? 4 : 12}>
      <EntityChecklists
        className={twoColumn ? "mr-4" : null}
        entity="Faculty"
        form={form}
        entityId={values.id}
        checked={values.tags}
      />
    </Grid>

    <Grid item xs={12} className="centeredFlex">
      <FormControlLabel
        className="switchWrapper"
        control={<FormField type="switch" name="isShownOnWeb" />}
        label={$t('visible_online')}
        labelPlacement="start"
      />
    </Grid>

    <CustomFields
      entityName="Faculty"
      fieldName="customFields"
      entityValues={values}
      form={form}
      gridItemProps={{
        xs: twoColumn ? 6 : 12,
        lg: twoColumn ? 4 : 12
      }}
    />
  </Grid>;
}

export default FacultyGeneralTab;