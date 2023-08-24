/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Contact } from "@api/model";
import { Grid } from "@mui/material";
import { ShowConfirmCaller } from "ish-ui";
import React, { useCallback } from "react";
import { Dispatch } from "redux";
import { Field } from "redux-form";
import FormField from "../../../../common/components/form/formFields/FormField";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { getContactFullName } from "../utils";
import AvatarRenderer from "./AvatarRenderer";

interface Props {
  form: string;
  dispatch: Dispatch;
  showConfirm: ShowConfirmCaller;
  values: Contact;
  twoColumn: boolean;
  isCompany: boolean;
  usiLocked: boolean;
  syncErrors: any;
  isNew: boolean;
  isFixed?: boolean;
  leftOffset?: number;
}

const ProfileHeading = (props: Props) => {
  const {
    form,
    dispatch,
    showConfirm,
    values,
    twoColumn,
    isCompany,
    usiLocked,
    syncErrors,
    isNew,
    isFixed,
    leftOffset
  } = props;

  const Avatar = useCallback(aProps => (
    <Field
      name="profilePicture"
      label="Profile picture"
      component={AvatarRenderer}
      showConfirm={showConfirm}
      email={values.email}
      twoColumn={twoColumn}
      props={{
        dispatch,
        form
      }}
      {...aProps}
    />
  ), [values.email]);

  return (
    <FullScreenStickyHeader
      leftOffset={leftOffset}
      isFixed={isFixed}
      opened={isNew || Object.keys(syncErrors).some(k => ['title', 'firstName', 'middleName', 'lastName'].includes(k))}
      twoColumn={twoColumn}
      Avatar={Avatar}
      title={(
        <>
          {values && !isCompany && values.title && values.title.trim().length > 0 ? `${values.title} ` : ""}
          {values ? (!isCompany ? getContactFullName(values) : values.lastName) : ""}
        </>
      )}
      fields={(
        <Grid container item xs={12} rowSpacing={2} columnSpacing={3}>
          {!isCompany && (
            <>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="title" label="Title" />
              </Grid>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="firstName" label="First name" disabled={usiLocked} required />
              </Grid>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="middleName" label="Middle name" />
              </Grid>
            </>
          )}
          <Grid item xs={isCompany ? 12 : twoColumn ? 2 : 6}>
            <FormField type="text" name="lastName" label={isCompany ? "Company name" : "Last name"} disabled={usiLocked} required />
          </Grid>
        </Grid>
      )}
    />
  );
};

export default ProfileHeading;