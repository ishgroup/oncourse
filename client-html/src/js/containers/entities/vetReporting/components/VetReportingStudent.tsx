/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo, useState } from "react";
import { Contact } from "@api/model";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import ProfileHeading from "../../contacts/components/ProfileHeading";
import ContactDetails from "../../contacts/components/ContactDetails";
import ContactsVET from "../../contacts/components/ContactsVET";

const VetReportingEditView = (props: EditViewProps<Contact> & { usiLocked: boolean }) => {
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
    <div>
      <div className="pl-3">
        <ProfileHeading
          isNew={isNew}
          form={form}
          dispatch={dispatch}
          showConfirm={showConfirm}
          values={values}
          twoColumn={twoColumn}
          isCompany={values.isCompany}
          usiLocked={usiLocked}
          syncErrors={syncErrors}
        />
      </div>

      <ContactDetails {...props} tabIndex={tabIndex} />
      <ContactsVET {...props} tabIndex="VET"/>
    </div>
  );
};

export default props => props.values
  ? <VetReportingEditView {...props}/>
  : null;
