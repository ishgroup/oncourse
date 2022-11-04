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
import CollegeForm from "./components/CollegeForm";
import { State } from "../../../../reducers/state";
import { Categories, CollegeSucureKey } from "../../../../model/preferences";
import FormContainer from "../FormContainer";
import { getTimezones } from "../../actions";

interface Props {
  college: any;
  timezones: string[];
  onInit: () => void;
}

export class CollegeBase extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const { college, timezones } = this.props;
    const sekKey = college && college[CollegeSucureKey.uniqueKey];

    const data = { ...college };

    delete data[CollegeSucureKey.uniqueKey];
    return (
      <FormContainer
        data={data}
        secKey={sekKey}
        timezones={timezones}
        category={Categories.college}
        form={formRoleName => <CollegeForm secKey={sekKey} formRoleName={formRoleName} />}
        formName="CollegeForm"
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  college: state.preferences.college,
  timezones: state.timezones
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => dispatch(getTimezones())
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(CollegeBase);
