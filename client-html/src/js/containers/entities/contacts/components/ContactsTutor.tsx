/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact, PayslipPayType, WorkingWithChildrenStatus } from '@api/model';
import { Grid } from '@mui/material';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { makeAppStyles, mapSelectItems, Switch } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import NumberFormat from 'react-number-format';
import { change } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import ExpandableContainer from '../../../../common/components/layout/expandable/ExpandableContainer';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import { formatTFN, parseTFN, validateTFN } from '../../../../common/utils/validation/tfnValidation';
import { EditViewProps } from '../../../../model/common/ListView';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';
import ContactCourseClass from './ContactCourseClass';

const useStyles = makeAppStyles()(() => ({
  switchWrapper: {
    display: "flex",
    flexDirection: "column",
  }
}));

const workingWithChildrenStatusItems = Object.keys(WorkingWithChildrenStatus).map(mapSelectItems);

const payslipPayTypes = Object.keys(PayslipPayType).map(mapSelectItems);

export const TFNInputMask = React.forwardRef<any, any>((props, ref) => (
  <NumberFormat
    {...props}
    getInputRef={ref}
    format="###-###-###"
    allowEmptyFormatting
  />
));

const ContactsTutor: React.FC<EditViewProps<Contact>> = props => {
  const {
    dispatch, twoColumn, values, isNew, form, tabIndex, expanded, setExpanded, syncErrors
  } = props;

  const [switchChanged, setSwitchChangedValue] = useState(false);
  const [switchValue, setSwitchValue] = useState(false);

  const { classes } = useStyles();

  useEffect(() => {
    if (isNew && !switchChanged) setSwitchValue(true);
  }, []);

  useEffect(() => {
    if (!switchChanged && values.tutor && values.tutor.defaultPayType) setSwitchValue(true);
  }, [values]);

  const changeEnableTutorPayGeneration = () => {
    const defaultPayTypeValue = !switchValue ? "Employee" : null;
    dispatch(change(LIST_EDIT_VIEW_FORM_NAME, 'tutor.defaultPayType', defaultPayTypeValue));

    if (!switchChanged) setSwitchChangedValue(true);
    setSwitchValue(!switchValue);
  };

  return values ? (
    <ExpandableContainer
      index={tabIndex}
      expanded={expanded}
      setExpanded={setExpanded}
      formErrors={syncErrors}
      className="pl-3 pr-3"
      header="Tutor"
    >
      <Grid container columnSpacing={3} rowSpacing={2}>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="tutor.givenNameLegal"
            label={$t('legal_first_name')}
            placeholder={values.firstName}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="tutor.familyNameLegal"
            label={$t('legal_last_name')}
            placeholder={values.lastName}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="tfn"
            label={$t('tax_file_number')}
            max="9"
            InputProps={{
              inputComponent: TFNInputMask
            }}
            validate={validateTFN}
            parse={parseTFN}
            format={formatTFN}
            debounced={false}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="text" name="tutor.payrollRef" label={$t('payroll_reference_number')} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.dateStarted" label={$t('date_started')} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.dateFinished" label={$t('date_finished')} />
        </Grid>
        {values.tutor && (
          <>
            <CustomFields
              entityName="Tutor"
              fieldName="tutor.customFields"
              entityValues={values}
              form={form}
              gridItemProps={{
                xs: twoColumn ? 6 : 12
              }}
            />
          </>
        )}

        <Grid item xs={twoColumn ? 6 : 12} className={classes.switchWrapper}>
          <Typography variant="caption" color="textSecondary">
            {$t('enable_tutor_pay_generation')}
          </Typography>
          <Switch
            onClick={() => changeEnableTutorPayGeneration()}
            checked={switchValue}
          />
        </Grid>

        {switchValue && (
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="select"
              name="tutor.defaultPayType"
              label={$t('tutor_pay_default_type')}
              items={payslipPayTypes}
              defaultValue="Employee"
            />
          </Grid>
        )}

        <Grid item xs={12} className="mt-2 pb-2">
          <div className="secondaryHeading">{$t('working_with_children_check_wwcc')}</div>
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="text" name="tutor.wwChildrenRef" label={$t('wwcc_number')} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="select" name="tutor.wwChildrenStatus" label={$t('wwcc_status')} items={workingWithChildrenStatusItems} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.wwChildrenExpiry" label={$t('wwcc_expiry_date')} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.wwChildrenCheckedOn" label={$t('wwcc_check_date')} />
        </Grid>
        {isNew && <div className="p-3" />}
        {!isNew && <ContactCourseClass {...props} />}
      </Grid>
    </ExpandableContainer>
  ) : null;
};

export default ContactsTutor;