/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Qualification, QualificationType } from '@api/model';
import { FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import { normalizeNumber, sortDefaultSelectItems } from 'ish-ui';
import * as React from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import { EditViewProps } from '../../../../model/common/ListView';

const qualificationTypes = Object.keys(QualificationType)
  .filter(i => Number.isNaN(Number(i)))
  .map(j => ({
    label: j,
    value: j
  }));

qualificationTypes.sort(sortDefaultSelectItems);

const QualificationsEditView = (props: EditViewProps<Qualification>) => {
  const {
    isNew, values, updateDeleteCondition, twoColumn
  } = props;

  if (!values) {
    return null;
  }

  React.useEffect(() => {
    if (updateDeleteCondition) {
      updateDeleteCondition(values.isCustom);
    }
  }, [values.isCustom, updateDeleteCondition]);

  const isDisabled = isNew ? false : !values.isCustom;

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="pt-2 pl-3 pr-3">
      <Grid item xs={12} className="d-flex">
        <div className="heading mt-2 mb-1">{$t('avetmiss_data')}</div>
      </Grid>

      <Grid item container columnSpacing={3} rowSpacing={2} xs={twoColumn ? 6 : 12}>
        <Grid item xs={12}>
          <FormField
            type="select"
            disabled={isDisabled}
            name="type"
            label={$t('type')}
            items={qualificationTypes}
            required={isNew || values.isCustom}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            disabled={isDisabled}
            name="qualLevel"
            label={$t('level')}
            required={!['Skill set', 'Local skill set'].includes(values.type) && (isNew || values.isCustom)}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="multilineText"
            disabled={isDisabled}
            name="title"
            label={$t('title')}
            required={isNew || values.isCustom}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            disabled={!isNew}
            name="nationalCode"
            label={$t('national_code')}
            required={isNew || values.isCustom}
          />
        </Grid>
      </Grid>

      <Grid item container columnSpacing={3} rowSpacing={2} xs={twoColumn ? 6 : 12}>
        <Grid item xs={12}>
          <FormField type="text" disabled={isDisabled} name="anzsco" label={$t('anzsco')} />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            disabled={isDisabled}
            name="fieldOfEducation"
            label={$t('field_of_education')}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            name="specialization"
            label={$t('specialization')}
            maxLength={128}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="number"
            normalize={normalizeNumber}
            debounced={false}
            name="nominalHours"
            label={$t('nominal_hours')}
          />
        </Grid>
      </Grid>

      <Grid item xs={12}>
        <div className="heading mb-2 mt-2">{$t('internal_options')}</div>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isOffered" />}
          label={$t('is_offered2')}
        />
      </Grid>
    </Grid>
  );
};

export default QualificationsEditView;
