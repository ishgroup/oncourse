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
import { EnumItem, EnumName } from "@api/model";
import ClassDefaultsForm from "./components/ClassDefaultsForm";
import { State } from "../../../../reducers/state";
import { Categories } from "../../../../model/preferences";
import FormContainer from "../FormContainer";
import { getEnum } from "../../actions";

interface Props {
  classDefaults: any;
  DeliveryMode: EnumItem[];
  ClassFundingSource: EnumItem[];
  getEnum: (name: EnumName) => void;
}

class ClassDefaults extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("DeliveryMode");
    this.props.getEnum("ClassFundingSource");
  }

  render() {
    const { classDefaults, DeliveryMode = [], ClassFundingSource = [] } = this.props;

    return (
      <FormContainer
        data={classDefaults}
        enums={{ DeliveryMode, ClassFundingSource }}
        category={Categories.classDefaults}
        form={formRoleName => <ClassDefaultsForm formRoleName={formRoleName} />}
        formName="ClassDefaultsForm"
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  classDefaults: state.preferences.classDefaults,
  DeliveryMode: state.enums.DeliveryMode,
  ClassFundingSource: state.enums.ClassFundingSource
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getEnum: (name: EnumName) => dispatch(getEnum(name))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ClassDefaults);
