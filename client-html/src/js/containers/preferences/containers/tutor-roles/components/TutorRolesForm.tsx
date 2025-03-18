/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefinedTutorRole } from '@api/model';
import DeleteForever from '@mui/icons-material/DeleteForever';
import Grid from '@mui/material/Grid';
import $t from '@t';
import { ShowConfirmCaller } from 'ish-ui';
import React from 'react';
import { Dispatch } from 'redux';
import { getFormSyncErrors, InjectedFormProps, reduxForm } from 'redux-form';
import { IAction } from '../../../../../common/actions/IshAction';
import AppBarActions from '../../../../../common/components/appBar/AppBarActions';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { useAppSelector } from '../../../../../common/utils/hooks';
import PayRates from './PayRates';

interface Props extends InjectedFormProps {
  isNew: boolean;
  value: DefinedTutorRole;
  dispatch: Dispatch<IAction>
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

const manualUrl = getManualLink("tutor-pay-rates");

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
                type="text"
                name="name"
                label={$t('name')}
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
                    label={$t('public_label')}
                    required
                  />
                </Grid>
                <Grid item xs={3}>
                  <FormField type="switch" name="active" label={$t('enabled')} color="primary" />
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
