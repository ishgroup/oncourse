/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { EnumItem, EnumName } from "@api/model";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Categories } from "../../../../model/preferences";
import { State } from "../../../../reducers/state";
import { getEnum } from "../../../preferences/actions";
import FormContainer from "../../../preferences/containers/FormContainer";
import SettingsForm from "./SettingsForm";

interface Props {
  security: any;
  TwoFactorAuthStatus: EnumItem[];
  getEnum: (name: EnumName) => void;
}

class Settings extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("TwoFactorAuthStatus");
  }

  render() {
    const { security, TwoFactorAuthStatus = [] } = this.props;

    return (
      <div>
        <FormContainer
          data={security}
          category={Categories.security}
          enums={{ TwoFactorAuthStatus }}
          form={formRoleName => <SettingsForm formRoleName={formRoleName} />}
          formName="SecuritySettingsForm"
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  security: state.preferences.security,
  TwoFactorAuthStatus: state.enums.TwoFactorAuthStatus
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getEnum: (name: EnumName) => dispatch(getEnum(name))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Settings);
