/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Grid from "@mui/material/Grid";
import FormField from "../../../../common/components/form/formFields/FormField";
import { formatFundingSourceId } from "../../common/utils";
import {
  validateAssociatedCourseIdentifier,
  validateCharacter,
  validateCricosConfirmation,
  validateOutcomeIdTrainingOrg,
  validateVetFundingSourceState,
  validateVetPurchasingContractIdentifier,
} from "../../../../common/utils/validation";
import FormControlLabel from "@mui/material/FormControlLabel";
import Divider from "@mui/material/Divider";
import FormGroup from "@mui/material/FormGroup";
import React, { useCallback } from "react";
import { enrolmentExemptionTypeItems, enrolmentStudyReasonItems } from "../constants";
import { fundingSourceValues } from "../../courseClasses/constants";

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
            label="Funding contract"
            items={contracts}
            format={formatFundingSourceId}
          />
        </Grid>
      )}

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="studyReason"
          label="Study reason"
          items={enrolmentStudyReasonItems}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="vetFeeExemptionType"
          label="Fee exemption/concession type"
          items={enrolmentExemptionTypeItems}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="select"
          name="fundingSource"
          label="Default funding source national"
          items={fundingSourceValues}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetFundingSourceStateID"
          label="Default funding source - State"
          validate={validateVetFundingSourceState}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="vetIsFullTime" />}
          label="Full time flag (QLD)"
        />
      </Grid>

      <Grid item xs={12} className="pt-2 pb-3">
        <Divider />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="vetInSchools" />}
          label="VET in schools enrolment"
        />
      </Grid>
      <Grid item xs={twoColumn ? 8 : 12} className="mb-2">
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="suppressAvetmissExport" />}
          label="Do not report for AVETMISS"
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="associatedCourseIdentifier"
          label="Associated Course Identifier - (SA - SACE Student ID)"
          validate={validateAssociatedCourseIdentifier}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetPurchasingContractID"
          label="Default purchasing contract identifier (NSW Commitment ID)"
          validate={validateVetPurchasingContractIdentifier}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="outcomeIdTrainingOrg"
          label="Outcome identifier Training Organisation"
          validate={validateOutcomeIdTrainingOrg}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetClientID"
          label="Client identifier: apprenticeships"
          validate={validateVetClientID}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          type="text"
          name="vetTrainingContractID"
          label="Training contract: apprenticeships"
          validate={validateVetTrainingContractID}
        />
      </Grid>

      <Grid item xs={twoColumn ? 4 : 12} className="mb-2">
        <FormField
          type="text"
          name="cricosConfirmation"
          label="CRICOS: Confirmation of Enrolment (CoE)"
          validate={validateCricosConfirmation}
        />
      </Grid>

      <Grid item xs={12} className="mb-2">
        <FormGroup>
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="eligibilityExemptionIndicator" />}
            label="Eligibility exemption  indicator (Vic)"
          />
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="vetFeeIndicator" />}
            label="VET FEE HELP indicator (Vic)"
          />
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="trainingPlanDeveloped" />}
            label="Training plan developed (NSW)"
          />
        </FormGroup>
      </Grid>
    </>
  );
};

export default EnrolmentDetails;