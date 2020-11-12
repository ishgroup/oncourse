import React from "react";
import UsersForm from "./components/UsersForm";
import { State } from "../../../../reducers/state";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { clearUserPassword } from "../../actions";
import * as SecuritySettingsModel from "../../../../model/preferences/security/SecuritySettings";
import history from "../../../../constants/History";
import { getAdministrationSites } from "../../../entities/sites/actions";

class Users extends React.Component<any, any> {
  private unlisten;

  componentWillMount() {
    this.props.onInit();

    this.unlisten = this.props.history.listen(() => {
      this.onHistoryChange();
    });
  }

  shouldComponentUpdate({
    match: {
      params: { id }
    },
    users,
    submitSucceeded,
    formLogin
  }) {
    const currentName = this.props.match.params.id;

    if (currentName === "new" && submitSucceeded) {
      const newUser = users.find(item => item.login === formLogin);

      if (newUser && newUser.id) {
        setTimeout(() => {
          history.push("/security/users/" + newUser.id);
        });
      }
    }

    return (
      !this.props.users || !this.props.sites || currentName !== id || currentName === "new" || Boolean(submitSucceeded)
    );
  }

  onHistoryChange = () => {
    const { newPassword, clearPassword } = this.props;

    if (newPassword) {
      clearPassword();
    }
  };

  validateUniqueNames = (val, all, props) => {
    const { users } = this.props;

    const matches = users.filter(i => i.login === val.trim() && props.user.id !== i.id);

    return matches.length > 0 ? "User login must be unique" : undefined;
  };

  componentWillUnmount() {
    this.unlisten();
  }

  render() {
    const {
      match: {
        params: { id }
      },
      users,
      userRoles,
      sites,
      passwordComplexityFlag
    } = this.props;

    const isNew = id === "new";

    const currentUser = isNew
      ? {
          administrationCentre: sites && sites.length && sites[0].id,
          admin: true,
          active: true,
          accessEditor: false,
          passwordUpdateRequired: false
        }
      : users && users.find(item => item.id.toString() === id);

    return sites ? (
      <UsersForm
        user={currentUser}
        userRoles={userRoles}
        sites={sites}
        isNew={isNew}
        passwordComplexityFlag={passwordComplexityFlag}
        validateUniqueNames={this.validateUniqueNames}
      />
    ) : null;
  }
}

const mapStateToProps = (state: State) => ({
  users: state.security.users,
  userRoles: state.security.userRoles,
  sites: state.sites.adminSites,
  passwordComplexityFlag:
    state.preferences &&
    state.preferences.security &&
    state.preferences.security[SecuritySettingsModel.SecurityPasswordComplexity.uniqueKey],
  submitSucceeded: state.form.UsersForm && state.form.UsersForm.submitSucceeded,
  formLogin: state.form.UsersForm && state.form.UsersForm.values && state.form.UsersForm.values.login,
  newPassword: state.security.newPassword
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => {
      dispatch(getAdministrationSites());
    },
    clearPassword: () => {
      dispatch(clearUserPassword());
    }
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Users);
