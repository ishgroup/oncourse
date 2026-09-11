/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ModuleCreditPointsStatus, ModuleType } from '@api/model';
import { FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { normalizeNumberToPositive, sortDefaultSelectItems } from 'ish-ui';
import * as React from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';

const moduleTypes = Object.keys(ModuleType).map(key => ({ value: ModuleType[key], label: key }));
const creditPointsStatuses = Object.keys(ModuleCreditPointsStatus).map(key => ({ value: ModuleCreditPointsStatus[key], label: key }));

moduleTypes.sort(sortDefaultSelectItems);

const ModulesEditView = (props: any) => {
  const {
    isNew, values, twoColumn, syncErrors
  } = props;

  const isCustom = values?.isCustom === true;

  const isDisabled = isNew ? false : !isCustom;

  const gridSpacing = { size: { xs: twoColumn ? 6 : 12 } };

  return (
    <Grid container rowSpacing={2} columnSpacing={3} className="pt-2 pl-3 pr-3">
      <Grid size={12}>
        <FullScreenStickyHeader
          opened={isNew || Object.keys(syncErrors).includes("title")}
          disableInteraction={!isNew}
          twoColumn={twoColumn}
          title={values?.title}
          fields={(
            <Grid size={12}>
              <FormField
                type="text"
                disabled={isDisabled}
                name="title"
                label={$t('title')}
                required={isNew || isCustom}
              />
            </Grid>
          )}
        />
      </Grid>

      <Grid size={12} className="d-flex">
        <div className="heading mt-1 mb-1">{$t('avetmiss_data')}</div>
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="text"
          disabled={!isNew}
          name="nationalCode"
          label={$t('national_code')}
          required={isNew || isCustom}
        />
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="select"
          disabled={isDisabled}
          name="type"
          label={$t('type')}
          items={moduleTypes}
          required
        />
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="text"
          disabled={isDisabled}
          name="fieldOfEducation"
          label={$t('field_of_education')}
        />
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="text"
          name="specialization"
          label={$t('specialization')}
          maxLength={128}
        />
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="number"
          normalize={normalizeNumberToPositive}
          max="99999999.99"
          name="creditPoints"
          label={$t('credit_points')}
          debounced={false}
        />
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="select"
          name="creditPointsStatus"
          label={$t('credit_points_status')}
          items={creditPointsStatuses}
        />
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="number"
          normalize={normalizeNumberToPositive}
          name="nominalHours"
          label={$t('nominal_hours')}
          debounced={false}
        />
      </Grid>

      <Grid {...gridSpacing}>
        <FormField
          type="number"
          normalize={normalizeNumberToPositive}
          onKeyPress={ev => {
            if (ev.key.match(/\./)) {
              ev.preventDefault();
            }
          }}
          name="expiryDays"
          label={$t('expiry_days')}
          debounced={false}
        />
      </Grid>

      <Grid {...gridSpacing}>
        <div className="heading mb-2 mt-2">{$t('internal_options')}</div>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isOffered" />}
          label={$t('is_offered')}
        />
      </Grid>
    </Grid>
  );
};

export default ModulesEditView;
