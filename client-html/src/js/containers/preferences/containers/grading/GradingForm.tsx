/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import { State } from "../../../../reducers/state";
import GradingTypesForm from "./components/GradingTypesForm";

const GradingForm: React.FC<any> = ({ gradingTypes }) => (
  <GradingTypesForm
    gradingTypes={gradingTypes}
  />
);

const mapStateToProps = (state: State) => ({
  gradingTypes: state.preferences.gradingTypes
});

export default connect(mapStateToProps)(GradingForm);