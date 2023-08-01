import * as React from "react";
import { connect } from "react-redux";
import { LICENSE_ACCESS_CONTROL_KEY } from "../../../../constants/Config";
import history from "../../../../constants/History";
import { State } from "../../../../reducers/state";
import UserRolesForm from "./components/UserRolesForm";

class UserRoles extends React.Component<any, any> {
  shouldComponentUpdate({
    match: {
      params: { id }
    },
    userRoles,
    submitSucceeded,
    form
  }) {
    const currentName = this.props.match.params.id;

    if (currentName === "new" && submitSucceeded) {
      const newId = userRoles.find(item => item.name === form).id;

      if (newId) {
        setTimeout(() => {
          history.push("/security/userRoles/" + newId);
        });
      }
    }

    return !this.props.userRoles || currentName !== id || Boolean(submitSucceeded);
  }

  redirectOnDelete = () => {
    const { userRoles } = this.props;

    if (userRoles.length) {
      history.push("/security/userRoles/" + userRoles[0].id);
      return;
    }
    history.push("/security/settings");
  };

  validateUniqueNames = (val, all, props) => {
    const { userRoles } = this.props;

    const matches = userRoles.filter(i => i.name === val.trim() && props.dataItem.id !== i.id);

    return matches.length > 0 ? "Role name must be unique" : undefined;
  };

  render() {
    const {
      match: {
        params: { id }
      },
      userRoles,
      hasLicense
    } = this.props;

    const isNew = id === "new";

    const dataItem = isNew ? { name: "", rights: {} } : userRoles && userRoles.find(item => item.id.toString() === id);

    return (
      <UserRolesForm
        className="overflow-hidden"
        {...{
          dataItem,
          isNew,
          hasLicense,
          redirectOnDelete: this.redirectOnDelete,
          validateUniqueNames: this.validateUniqueNames
        }}
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  hasLicense: state.userPreferences[LICENSE_ACCESS_CONTROL_KEY] === "true",
  userRoles: state.security.userRoles,
  submitSucceeded: state.form.UserRolesForm && state.form.UserRolesForm.submitSucceeded,
  form: state.form.UserRolesForm && state.form.UserRolesForm.values && state.form.UserRolesForm.values.name
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(UserRoles);
