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
import { getEnum } from "../../actions";
import FormContainer from "../FormContainer";
import AvetmissForm from "./components/AvetmissForm";

interface Props {
  avetmiss: any;
  ExportJurisdiction: EnumItem[];
  TrainingOrg_Types: EnumItem[];
  AddressStates: EnumItem[];
  getEnum: (name: EnumName) => void;
}

class Avetmiss extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("ExportJurisdiction");
    this.props.getEnum("TrainingOrg_Types");
    this.props.getEnum("AddressStates");
  }

  render() {
    const {
      avetmiss, ExportJurisdiction = [], TrainingOrg_Types = [], AddressStates = []
    } = this.props;

    return (
      <FormContainer
        data={avetmiss}
        enums={{
          ExportJurisdiction,
          TrainingOrg_Types,
          AddressStates
        }}
        category={Categories.avetmiss}
        form={formRoleName => <AvetmissForm formRoleName={formRoleName} />}
        formName="AvetmissForm"
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  avetmiss: state.preferences.avetmiss,
  ExportJurisdiction: state.enums.ExportJurisdiction,
  TrainingOrg_Types: state.enums.TrainingOrg_Types,
  AddressStates: state.enums.AddressStates
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getEnum: (name: EnumName) => dispatch(getEnum(name))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Avetmiss);
