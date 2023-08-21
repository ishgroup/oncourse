/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getLdapConnection } from "../../../../common/actions";
import { IAction } from "../../../../common/actions/IshAction";
import { Categories } from "../../../../model/preferences";
import * as ldapModel from "../../../../model/preferences/Ldap";
import { State } from "../../../../reducers/state";
import { getPreferences } from "../../actions";
import FormContainer from "../FormContainer";
import LDAPForm from "./components/LDAPForm";

class LDAP extends React.Component<any, any> {

  handleTestLdapConnection() {
    const host = this.props.ldap[ldapModel.LdapHost.uniqueKey];
    const port = this.props.ldap[ldapModel.LdapServerPort.uniqueKey];
    const isSsl = this.props.ldap[ldapModel.LdapSSL.uniqueKey];
    const baseDn = this.props.ldap[ldapModel.LdapBaseDN.uniqueKey];
    const user = this.props.ldap[ldapModel.LdapUsernameAttribute.uniqueKey];
    this.props.handleTestConnection(host, port, isSsl, baseDn, user);
  }

  render() {
    const { ldap, licences } = this.props;
    return (
      <FormContainer
        data={ldap}
        testLdapConnection={this.handleTestLdapConnection.bind(this)}
        licence={licences}
        category={Categories.ldap}
        form={formRoleName => <LDAPForm formRoleName={formRoleName} />}
        formName="LDAPForm"
      />
    );
  }

  componentDidUpdate() {
    if (this.props.ldap && !this.props.licences) {
      this.props.getLicencesItems();
    }
  }
}

const mapStateToProps = (state: State) => ({
  ldap: state.preferences.ldap,
  licences: state.preferences.licences
});

const mapDispatchToProps = (dispatch: Dispatch<IAction<any>>) => {
  return {
    handleTestConnection: (host: string, port: string, isSsl: string, baseDn: string, user: string) =>
      dispatch(getLdapConnection(host, port, isSsl, baseDn, user)),
    getLicencesItems: () => dispatch(getPreferences(Categories.licences))
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(LDAP);
