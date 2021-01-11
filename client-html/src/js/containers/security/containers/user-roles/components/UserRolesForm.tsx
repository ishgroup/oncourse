/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { ComponentClass } from "react";
import { Grid } from "@material-ui/core/";
import DeleteForever from "@material-ui/icons/DeleteForever";
import {
  getFormValues, initialize, reduxForm
} from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { UserRole } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import Categories from "../../../../../model/user-roles/index";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import Button from "../../../../../common/components/buttons/Button";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import AppBarActions from "../../../../../common/components/form/AppBarActions";
import UserRolePreference from "./UserRolePreference";
import { State } from "../../../../../reducers/state";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { updateUserRole, removeUserRole } from "../../../actions";
import { getManualLink } from "../../../../../common/utils/getManualLink";

const manualUrl = getManualLink("users_roles");

const initialRights: { [key: string]: string } = {};

Categories.forEach(c => c.permissions.forEach(p => {
  initialRights[p.name] = p.checkbox ? "false" : "Hide";
}));

class UserRolesFormBase extends React.PureComponent<any, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending;

  private disableConfirm;

  constructor(props) {
    super(props);

    if (props.dataItem) {
      props.dispatch(
        initialize("UserRolesForm", { name: props.dataItem.name, ...initialRights, ...props.dataItem.rights })
      );
    }
  }

  componentWillReceiveProps({
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
      this.disableConfirm = true;

      removeUserRole(id);
    }).then(() => {
      redirectOnDelete();
      this.disableConfirm = false;
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
      submitSucceeded,
      hasLicense,
      form
    } = this.props;

    return (
      <form onSubmit={handleSubmit(this.onSave)} className={className}>
        {!this.disableConfirm && !submitSucceeded && dirty && <RouteChangeConfirm form={form} when={dirty && hasLicense} />}

        <Grid container spacing={2}>
          <CustomAppBar>
            <Grid container>
              <Grid item xs={12} className="centeredFlex">
                <FormField
                  type="headerText"
                  name="name"
                  placeholder="Name"
                  margin="none"
                  listSpacing={false}
                  validate={[validateSingleMandatoryField, validateUniqueNames]}
                />

                <div className="flex-fill" />

                {hasLicense && !isNew && (
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

                <AppBarHelpMenu
                  created={created ? new Date(created) : null}
                  modified={modified ? new Date(modified) : null}
                  auditsUrl={'audit?search=~"ACLRole"'}
                  manualUrl={manualUrl}
                />

                <Button
                  text="Save"
                  type="submit"
                  size="small"
                  variant="text"
                  disabled={!hasLicense || !dirty}
                  rootClasses="whiteAppBarButton"
                  disabledClasses="whiteAppBarButtonDisabled"
                />
              </Grid>
            </Grid>
          </CustomAppBar>

          <Grid item md={12} lg={11} className="ml-2">
            {values
              && Categories.map((item, index) => (
                <UserRolePreference key={index} item={item} values={values} dispatch={dispatch} initial={rights} />
              ))}
          </Grid>
        </Grid>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("UserRolesForm")(state),
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    updateUserRole: (userRole: UserRole) => dispatch(updateUserRole(userRole)),
    removeUserRole: (id: number) => dispatch(removeUserRole(id))
  });

const UserRolesForm = reduxForm({
  form: "UserRolesForm"
})(
  connect<any, any, any>(
    mapStateToProps,
    mapDispatchToProps
  )(UserRolesFormBase)
);

export default UserRolesForm as ComponentClass<any>;
