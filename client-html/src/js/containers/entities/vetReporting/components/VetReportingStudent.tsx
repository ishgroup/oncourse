/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { FormSection } from "redux-form";
import { EditViewProps } from "../../../../model/common/ListView";
import { VetReport } from "../../../../model/entities/VetReporting";
import ContactDetails from "../../contacts/components/ContactDetails";
import ContactsVET from "../../contacts/components/ContactsVET";
import ProfileHeading from "../../contacts/components/ProfileHeading";

const VetReportingEditView = (props: EditViewProps<VetReport> & { usiLocked: boolean }) => {
  const {
    twoColumn,
    isNew,
    form,
    dispatch,
    showConfirm,
    values,
    syncErrors,
    usiLocked,
    tabIndex
  } = props;

  return (
    <FormSection name="student">
      <div className={ twoColumn ? "pl-3 pb-1" : "pl-3 pt-3" }>
        <ProfileHeading
          isNew={isNew}
          form={form}
          dispatch={dispatch}
          showConfirm={showConfirm}
          values={values.student}
          twoColumn={twoColumn}
          isCompany={values.student.isCompany}
          usiLocked={usiLocked}
          syncErrors={syncErrors?.student || {}}
        />
      </div>

      <ContactDetails {...props} values={values.student} namePrefix="student" noCustomFields tabIndex={tabIndex} />
      <ContactsVET {...props} values={values.student} namePrefix="student" tabIndex="VET"/>
    </FormSection>
  );
};

export default props => props.values
  ? <VetReportingEditView {...props}/>
  : null;