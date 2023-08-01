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
import MaintenanceForm from "./components/MaintenanceForm";

interface Props {
  MaintenanceTimes: EnumItem[];
  maintenance: any;
  getEnum: (name: EnumName) => void;
}

class Maintenance extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("MaintenanceTimes");
  }

  render() {
    const { maintenance, MaintenanceTimes = [] } = this.props;

    return (
      <div>
        <FormContainer
          enums={{ MaintenanceTimes }}
          data={maintenance}
          category={Categories.maintenance}
          form={formRoleName => <MaintenanceForm formRoleName={formRoleName} />}
          formName="MaintenanceForm"
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  MaintenanceTimes: state.enums.MaintenanceTimes,
  maintenance: state.preferences.maintenance
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getEnum: (name: EnumName) => dispatch(getEnum(name))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Maintenance);
