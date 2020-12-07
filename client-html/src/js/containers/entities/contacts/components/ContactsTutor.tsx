/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Contact, PayslipPayType, WorkingWithChildrenStatus } from "@api/model";
import { Grid } from "@material-ui/core";
import NumberFormat from "react-number-format";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { openInternalLink } from "../../../../common/utils/links";
import { getContactFullName } from "../utils";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import ContactCourseClass from "./ContactCourseClass";
import { mapSelectItems } from "../../../../common/utils/common";
import { formatTFN, parseTFN, validateTFN } from "../../../../common/utils/validation/tfnValidation";

interface ContactsTutorProps {
  twoColumn?: boolean;
  isNew?: boolean;
  values?: Contact;
}

const workingWithChildrenStatusItems = Object.keys(WorkingWithChildrenStatus).map(mapSelectItems);

const payslipPayTypes = Object.keys(PayslipPayType).map(mapSelectItems);

export const TFNInputMask = props => {
  const { inputRef, ...other } = props;

  return (
    <NumberFormat
      {...other}
      getInputRef={inputRef}
      format="###-###-###"
      allowEmptyFormatting
    />
  );
};

const ContactsTutor: React.FC<ContactsTutorProps> = props => {
  const {
    twoColumn, values, isNew
  } = props;

  const onCalendarClick = () => {
    const tutorId = values.tutor && values.tutor.id;
    if (!tutorId) {
      openInternalLink(`/timetable`);
    } else {
      openInternalLink(
        `/timetable/search?query=tutor.id=${tutorId}&title=Tutor timetable for ${getContactFullName(values)}`
      );
    }
  };

  return values ? (
    <>
      <div className="heading p-3 pt-2 pb-0">TUTOR</div>
      <Grid container className="p-3">
        <Grid item xs={12} className="mb-2 pt-2 pb-2">
          <TimetableButton onClick={onCalendarClick} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="tutor.givenNameLegal"
            label="Legal first name"
            placeholder={values.firstName}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="tutor.familyNameLegal"
            label="Legal last name"
            placeholder={values.lastName}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="tfn"
            label="Tax file number"
            max={9}
            InputProps={{
              inputComponent: TFNInputMask
            }}
            validate={validateTFN}
            parse={parseTFN}
            format={formatTFN}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="text" name="tutor.payrollRef" label="Payroll reference number" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.dateStarted" label="Date started" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.dateFinished" label="Date finished" />
        </Grid>
        <Grid item xs={12}>
          <FormField type="select" name="tutor.defaultPayType" label="Tutor pay default type" items={payslipPayTypes} defaultValue={"Employee"} allowEmpty />
        </Grid>
        <Grid item xs={12} className="mt-2 pb-2">
          <div className="heading">WORKING WITH CHILDREN CHECK (WWCC)</div>
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="text" name="tutor.wwChildrenRef" label="WWCC number" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="select" name="tutor.wwChildrenStatus" label="WWCC status" items={workingWithChildrenStatusItems} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.wwChildrenExpiry" label="WWCC expiry date" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="tutor.wwChildrenCheckedOn" label="WWCC check date" />
        </Grid>
        {isNew && <div className="p-3" />}
        {!isNew && <ContactCourseClass {...props} />}
      </Grid>
    </>
  ) : null;
};

export default ContactsTutor;
