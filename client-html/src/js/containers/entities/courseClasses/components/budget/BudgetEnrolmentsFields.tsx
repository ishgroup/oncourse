/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { AppTheme, normalizeNumberToZero, preventDecimalEnter } from 'ish-ui';
import React, { useMemo } from 'react';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import { IAction } from '../../../../../common/actions/IshAction';
import FormField from '../../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../../common/components/form/formFields/Uneditable';
import { validateNonNegative } from '../../../../../common/utils/validation';
import { CourseClassExtended, CourseClassRoom } from '../../../../../model/entities/CourseClass';

const styles = (theme: AppTheme) => ({
  root: {
    background: theme.table.contrastRow.light,
    borderRadius: theme.shape.borderRadius,
    paddingTop: theme.spacing(1.5),
    paddingRight: theme.spacing(4)
  }
});

interface Props {
  values: CourseClassExtended;
  classRooms: CourseClassRoom[];
  classes?: any;
  enrolmentsCount: number;
  form: string;
  dispatch: Dispatch<IAction>
}

const BudgetEnrolmentsFields = React.memo<Props>(({
 values, enrolmentsCount, classes, classRooms
}) => {
  const seatedCapacityWarnings = useMemo(() => {
    const warnings = {
      max: null,
      min: null
    };

    if (!values.id) {
      return warnings;
    }

    classRooms.forEach(r => {
      if (Number(r.seatedCapacity) < values.minimumPlaces) {
        warnings.min = `Exceeds ${r.name} capacity`;
      }
      if (Number(r.seatedCapacity) < values.maximumPlaces) {
        warnings.max = `Exceeds ${r.name} capacity`;
      }
    });

    return warnings;
  }, [classRooms, values.maximumPlaces, values.minimumPlaces, values.id]);

  return (
    <Grid container direction="row" columnSpacing={3} className={classes.root}>
      <Grid item xs={3} className="centeredFlex pl-2">
        <Typography variant="body1">{$t('enrolments4')}</Typography>
      </Grid>
      <Grid item xs={2} className="text-end">
        <FormField
          type="number"
          name="minimumPlaces"
          label={$t('minimum')}
          className="flex-fill"
          onKeyPress={preventDecimalEnter}
          normalize={normalizeNumberToZero}
          format={normalizeNumberToZero}
          validate={validateNonNegative}
          warning={seatedCapacityWarnings.min}
          rightAligned
        />
      </Grid>
      <Grid item xs={2} className="text-end">
        <FormField
          name="maximumPlaces"
          type="number"
          label={$t('maximum')}
          className="flex-fill"
          onKeyPress={preventDecimalEnter}
          format={normalizeNumberToZero}
          warning={seatedCapacityWarnings.max}
          validate={validateNonNegative}
          rightAligned
        />
      </Grid>
      <Grid item xs={2} className="text-end">
        <FormField
          type="number"
          name="budgetedPlaces"
          label={$t('projected')}
          className="flex-fill"
          onKeyPress={preventDecimalEnter}
          format={normalizeNumberToZero}
          validate={validateNonNegative}
          rightAligned
        />
      </Grid>
      <Grid item xs={2} className="d-flex justify-content-end">
        <Uneditable value={enrolmentsCount || "0"} label={$t('actual')} rightAligned />
      </Grid>
      <Grid item xs={1} />
    </Grid>
  );
});

export default withStyles(BudgetEnrolmentsFields, styles);