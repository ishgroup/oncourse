import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import UsersForm from "./components/UsersForm";
import { State } from "../../../../reducers/state";
import * as SecuritySettingsModel from "../../../../model/preferences/security/SecuritySettings";
import history from "../../../../constants/History";
import { getAdministrationSites } from "../../../entities/sites/actions";

class Users extends React.Component<any, any> {
  private unlisten;

  componentDidMount() {
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
    formEmail,
  }) {
    const currentName = this.props.match.params.id;

    if (currentName === "new" && submitSucceeded) {
      const newUser = users.find(item => item.email === formEmail);

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

    const matches = users.filter(i => i.email === val.trim() && props.user.id !== i.id);

    return matches.length > 0 ? "User email must be unique" : undefined;
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
          administrationCentre: null,
          admin: false,
          active: true,
          accessEditor: false,
          passwordUpdateRequired: false
        }
      : users && users.find(item => item.id.toString() === id);

    const oldEmail = isNew ? null : currentUser ? currentUser.email : null;

    return sites ? (
      <UsersForm
        user={currentUser}
        userRoles={userRoles}
        sites={sites}
        isNew={isNew}
        passwordComplexityFlag={passwordComplexityFlag}
        oldEmail={oldEmail}
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
    state.preferences
    && state.preferences.security
    && state.preferences.security[SecuritySettingsModel.SecurityPasswordComplexity.uniqueKey],
  submitSucceeded: state.form.UsersForm && state.form.UsersForm.submitSucceeded,
  formEmail: state.form.UsersForm && state.form.UsersForm.values && state.form.UsersForm.values.email
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    onInit: () => {
      dispatch(getAdministrationSites());
    }
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Users);
