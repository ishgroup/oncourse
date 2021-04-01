/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Dispatch } from "redux";
import { InjectedFormProps } from "redux-form";
import Grid from "@material-ui/core/Grid";
import DeleteForever from "@material-ui/icons/DeleteForever";
import { DefinedTutorRole } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import PayRates from "./PayRates";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { getManualLink } from "../../../../../common/utils/getManualLink";

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
  showConfirm?: any;
  history?: any;
  tutorRoles?: any;
  fetch?: any;
}

const manualLink = getManualLink("advancedSetup_Tutor");

const TutorRolesForm = React.memo<Props>(
  ({
    dirty,
    form,
    handleSubmit,
    isNew,
    valid,
    value,
    dispatch,
    handleDelete,
    onSubmit,
    showConfirm,
    disableRouteConfirm,
  }) => (
    <form className="container" autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
      {!disableRouteConfirm && dirty && <RouteChangeConfirm form={form} when={dirty} />}
      <CustomAppBar>
        <FormField
          type="headerText"
          name="name"
          placeholder="Name"
          margin="none"
          className="pl-1"
          listSpacing={false}
          required
        />

        <div className="flex-fill" />

        {!isNew && (
          <AppBarActions
            actions={[
              {
                action: handleDelete,
                icon: <DeleteForever />,
                confirm: true,
                tooltip: "Delete tutor role",
                confirmText: "Role will be deleted permanently",
                confirmButtonText: "DELETE"
              }
            ]}
          />
        )}

        <AppBarHelpMenu
          auditsUrl={`audit?search=~"DefinedTutorRole" and entityId == ${value.id}`}
          manualUrl={manualLink}
        />

        <FormSubmitButton
          disabled={!dirty}
          invalid={!valid}
        />
      </CustomAppBar>

      <Grid container>
        <Grid item xs={9}>
          <Grid container>
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
    </form>
  )
);

export default props => (props.value ? <TutorRolesForm {...props} /> : null);
