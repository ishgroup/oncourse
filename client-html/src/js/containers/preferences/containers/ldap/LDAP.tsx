import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Categories } from "../../../../model/preferences";
import { State } from "../../../../reducers/state";
import LDAPForm from "./components/LDAPForm";
import FormContainer from "../FormContainer";
import * as ldapModel from "../../../../model/preferences/Ldap";
import { getLdapConnection } from "../../../../common/actions";
import { getPreferences } from "../../actions";
import { IAction } from "../../../../common/actions/IshAction";

class LDAP extends React.Component<any, any> {
  private getLicence(licences) {
    return false;
  }

  handleTestLdapConnection() {
    const host = this.props.ldap[ldapModel.LdapHost.uniqueKey];
    const port = this.props.ldap[ldapModel.LdapServerPort.uniqueKey];
    const isSsl = this.props.ldap[ldapModel.LdapSSL.uniqueKey];
    const baseDn = this.props.ldap[ldapModel.LdapBaseDN.uniqueKey];
    const user = this.props.ldap[ldapModel.LdapUsernameAttribute.uniqueKey];
    console.log(host, port, isSsl, baseDn, user);
    this.props.handleTestConnection(host, port, isSsl, baseDn, user);
  }

  render() {
    const { ldap, licences } = this.props;
    return (
      <FormContainer
        data={ldap}
        testLdapConnection={this.handleTestLdapConnection.bind(this)}
        licence={licences && this.getLicence(licences)}
        category={Categories.ldap}
        form={<LDAPForm />}
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
