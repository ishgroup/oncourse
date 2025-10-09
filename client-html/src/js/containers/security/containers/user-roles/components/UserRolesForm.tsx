/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { UserRole } from '@api/model';
import DeleteForever from '@mui/icons-material/DeleteForever';
import { Grid } from '@mui/material';
import $t from '@t';
import React, { ComponentClass } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Dispatch } from 'redux';
import { Form, getFormSyncErrors, getFormValues, initialize, reduxForm } from 'redux-form';
import AppBarActions from '../../../../../common/components/appBar/AppBarActions';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import Categories from '../../../../../model/user-roles/index';
import { State } from '../../../../../reducers/state';
import { removeUserRole, updateUserRole } from '../../../actions';
import UserRolePreference from './UserRolePreference';

const manualUrl = getManualLink("user-roles");

const initialRights: { [key: string]: string } = {};

Categories.forEach(c => c.permissions.forEach(p => {
  initialRights[p.name] = p.checkbox ? "false" : "Hide";
}));

class UserRolesFormBase extends React.PureComponent<any, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending;

  constructor(props) {
    super(props);

    if (props.dataItem) {
      props.dispatch(
        initialize("UserRolesForm", { name: props.dataItem.name, ...initialRights, ...props.dataItem.rights })
      );
    }
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps({
    dataItem, fetch, submitSucceeded, isNew
  }) {
    if (this.isPending && fetch && fetch.success === false) {
      this.isPending = false;
      this.rejectPromise();
    }
    if (this.isPending && fetch && fetch.success) {
      this.isPending = false;
      this.resolvePromise();
    }
    if (
      (dataItem && !this.props.dataItem)
      || (this.props.dataItem && this.props.dataItem.id !== dataItem.id)
      || (submitSucceeded && !isNew)
    ) {
      this.props.dispatch(initialize("UserRolesForm", { name: dataItem.name, ...initialRights, ...dataItem.rights }));
    }
  }

  componentDidUpdate() {
    const {
      dirty, nextLocation, history
    } = this.props;

    if (nextLocation && !dirty) {
      history.push(nextLocation);
    }
  }

  onSave = value => {
    this.isPending = true;

    const { dataItem, updateUserRole } = this.props;

    const parsedValue = JSON.parse(JSON.stringify({ ...dataItem, ...{ name: value.name }, ...{ rights: value } }));

    delete parsedValue.rights.name;
    delete parsedValue.created;
    delete parsedValue.modified;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      updateUserRole(parsedValue);
    });
  };

  onDelete = id => {
    this.isPending = true;

    const { redirectOnDelete, removeUserRole } = this.props;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      removeUserRole(id);
    }).then(() => {
      redirectOnDelete();
      this.forceUpdate();
    });
  };

  render() {
    const {
      className,
      handleSubmit,
      dataItem: {
        created = "", modified = "", id = "", rights = {}
      } = {},
      values,
      dirty,
      dispatch,
      isNew,
      validateUniqueNames,
      hasLicense,
      form,
      invalid,
      syncErrors
    } = this.props;

    return (
      <Form onSubmit={handleSubmit(this.onSave)} className={className}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl='audit?search=~"ACLRole"'
          disabled={!dirty}
          invalid={invalid}
          title={(isNew && (!values || !values.name || values.name.trim().length === 0))
            ? "New"
            : values && values.name.trim()}
          createdOn={() => (created ? new Date(created) : null)}
          modifiedOn={() => (modified ? new Date(modified) : null)}
          opened={isNew || Object.keys(syncErrors).includes("name")}
          containerClass="p-3"
          fields={(
            <Grid item xs={12}>
              <FormField
                type="text"
                name="name"
                label={$t('name')}
                validate={validateUniqueNames}
                required
              />
            </Grid>
          )}
          actions={hasLicense && !isNew && (
            <AppBarActions
              actions={[
                {
                  action: () => this.onDelete(id),
                  icon: <DeleteForever />,
                  tooltip: "Delete User Role",
                  confirmText: "User Role will be deleted permanently",
                  confirmButtonText: "DELETE"
                }
              ]}
            />
          )}
        >
          <Grid container>
            <Grid item md={12} lg={11} className="ml-2">
              {values
                && Categories.map((item, index) => (
                  <UserRolePreference key={index} item={item} values={values} dispatch={dispatch} initial={rights} />
                ))}
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("UserRolesForm")(state),
  syncErrors: getFormSyncErrors("UserRolesForm")(state),
  fetch: state.fetch,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  updateUserRole: (userRole: UserRole) => dispatch(updateUserRole(userRole)),
  removeUserRole: (id: number) => dispatch(removeUserRole(id))
});

const UserRolesForm = reduxForm({
  form: "UserRolesForm",
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps,
    mapDispatchToProps
  )(withRouter(UserRolesFormBase))
);

export default UserRolesForm as ComponentClass<any>;
