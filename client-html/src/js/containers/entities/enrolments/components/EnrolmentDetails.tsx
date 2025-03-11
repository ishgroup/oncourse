/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Divider from '@mui/material/Divider';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import Grid from '@mui/material/Grid';
import $t from '@t';
import React, { useCallback } from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import {
  validateAssociatedCourseIdentifier,
  validateCharacter,
  validateCricosConfirmation,
  validateOutcomeIdTrainingOrg,
  validatePurchasingContractScheduleIdentifier,
  validateVetFundingSourceState,
  validateVetPurchasingContractIdentifier,
} from '../../../../common/utils/validation';
import { formatFundingSourceId } from '../../common/utils';
import { fundingSourceValues } from '../../courseClasses/constants';
import { enrolmentExemptionTypeItems, enrolmentStudyReasonItems } from '../constants';

const EnrolmentDetails = (
  {
    twoColumn,
    contracts,
    values
  }) => {

  const validateVetClientID = useCallback(value => {
    if (!value && values.vetTrainingContractID) {
      return "If you enter data in either the Training Contract ID or the Training Contract Client ID, then you must fill out both";
    }
    return validateCharacter(value, 10, "Training contract client identifier");
  }, [values.vetTrainingContractID]);

  const validateVetTrainingContractID = useCallback(value => {
    if (!value && values.vetClientID) {
      return "If you enter data in either the Training Contract ID or the Training Contract Client ID, then you must fill out both";
    }
    return validateCharacter(value, 10, "Training contract identifier");
  }, [values.vetClientID]);

  return (
    <>
      {contracts && (
        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="select"
            selectValueMark="id"
            selectLabelMark="name"
            name="relatedFundingSourceId"
            label={$t('funding_contract')}
            items={contracts}
            format={formatFundingSourceId}
          />
        </Grid>
      )}

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="studyReason"
          label={$t('study_reason')}
          items={enrolmentStudyReasonItems}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="vetFeeExemptionType"
          label={$t('fee_exemptionconcession_type')}
          items={enrolmentExemptionTypeItems}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="fundingSource"
          label={$t('default_funding_source_national')}
          items={fundingSourceValues}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetFundingSourceStateID"
          label={$t('default_funding_source_state2')}
          validate={validateVetFundingSourceState}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="vetIsFullTime" />}
          label={$t('full_time_flag_qld')}
        />
      </Grid>

      <Grid item xs={12} className="pt-2 pb-3">
        <Divider />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="vetInSchools" />}
          label={$t('vet_in_schools_enrolment')}
        />
      </Grid>
      <Grid item xs={twoColumn ? 8 : 12} className="mb-2">
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="suppressAvetmissExport" />}
          label={$t('do_not_report_for_avetmiss')}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="associatedCourseIdentifier"
          label={$t('associated_course_identifier_sa_sace_student_id')}
          validate={validateAssociatedCourseIdentifier}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetPurchasingContractID"
          label={$t('default_purchasing_contract_identifier_nsw_commitm')}
          validate={validateVetPurchasingContractIdentifier}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetPurchasingContractScheduleID"
          label={$t('purchasing_contract_schedule_identifier2')}
          validate={validatePurchasingContractScheduleIdentifier}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="outcomeIdTrainingOrg"
          label={$t('outcome_identifier_training_organisation')}
          validate={validateOutcomeIdTrainingOrg}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetClientID"
          label={$t('client_identifier_apprenticeships')}
          validate={validateVetClientID}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetTrainingContractID"
          label={$t('training_contract_apprenticeships')}
          validate={validateVetTrainingContractID}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12} className="mb-2">
        <FormField
          type="text"
          name="cricosConfirmation"
          label={$t('cricos_confirmation_of_enrolment_coe')}
          validate={validateCricosConfirmation}
        />
      </Grid>

      <Grid item xs={12} className="mb-2">
        <FormGroup>
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="eligibilityExemptionIndicator" />}
            label={$t('eligibility_exemption_indicator_vic')}
          />
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="vetFeeIndicator" />}
            label={$t('vet_fee_help_indicator_vic')}
          />
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="trainingPlanDeveloped" />}
            label={$t('training_plan_developed_nsw')}
          />
        </FormGroup>
      </Grid>
    </>
  );
};

export default EnrolmentDetails;