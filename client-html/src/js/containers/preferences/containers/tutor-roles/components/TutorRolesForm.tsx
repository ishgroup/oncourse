/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { Dispatch } from "redux";
import { getFormSyncErrors, InjectedFormProps, reduxForm } from "redux-form";
import Grid from "@mui/material/Grid";
import DeleteForever from "@mui/icons-material/DeleteForever";
import { DefinedTutorRole } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import PayRates from "./PayRates";
import { useAppSelector } from "../../../../../common/utils/hooks";

interface Props extends InjectedFormProps {
  isNew: boolean;
  value: DefinedTutorRole;
  dispatch: Dispatch;
  onCreate: (role: DefinedTutorRole) => void;
  onUpdate: (role: DefinedTutorRole) => void;
  onDelete: any;
  handleDelete: any;
  onSubmit: (val: any) => void;
  disableRouteConfirm: boolean;
  showConfirm?: ShowConfirmCaller;
  history?: any;
  tutorRoles?: any;
  fetch?: any;
}

const manualUrl = getManualLink("advancedSetup_Tutor");

export const TUTOR_ROLES_FORM: string = "TutorRolesForm";

const TutorRolesForm = React.memo<Props>(
  ({
    dirty,
    form,
    handleSubmit,
    isNew,
    invalid,
    value,
    dispatch,
    handleDelete,
    onSubmit,
    showConfirm,
    disableRouteConfirm
  }) => {
    const syncErrors = useAppSelector(state => getFormSyncErrors(TUTOR_ROLES_FORM)(state));
    
    return (
      <form className="container" autoComplete="off" onSubmit={handleSubmit(onSubmit)} role={TUTOR_ROLES_FORM}>
        {!disableRouteConfirm && dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <AppBarContainer
          values={value}
          manualUrl={manualUrl}
          getAuditsUrl={id => `audit?search=~"DefinedTutorRole" and entityId == ${id}`}
          disabled={!dirty}
          invalid={invalid}
          title={(isNew && (!value || !value.name || value.name.trim().length === 0))
          ? "New"
          : value && value.name && value.name.trim()}
          opened={isNew || Object.keys(syncErrors).includes("name")}
          fields={(
            <Grid item xs={12}>
              <FormField
                name="name"
                label="Name"
                required
              />
            </Grid>
          )}
          actions={!isNew && (
            <AppBarActions
              actions={[
                {
                  action: handleDelete,
                  icon: <DeleteForever/>,
                  confirm: true,
                  tooltip: "Delete tutor role",
                  confirmText: "Role will be deleted permanently",
                  confirmButtonText: "DELETE"
                }
              ]}
            />
          )}
        >
          <Grid container>
            <Grid item xs={9}>
              <Grid container columnSpacing={3}>
                <Grid item xs={9}>
                  <FormField
                    type="text"
                    name="description"
                    label="Public label"
                    required
                  />
                </Grid>
                <Grid item xs={3}>
                  <FormField type="switch" name="active" label="Enabled" color="primary" fullWidth />
                </Grid>
              </Grid>
            </Grid>
          </Grid>

          <PayRates value={value} form={form} dispatch={dispatch} showConfirm={showConfirm} />
        </AppBarContainer>
      </form>
);
  }
);

export default reduxForm({
  form: TUTOR_ROLES_FORM,
  onSubmitFail,
})(props => (props.value ? <TutorRolesForm {...props} /> : null));
