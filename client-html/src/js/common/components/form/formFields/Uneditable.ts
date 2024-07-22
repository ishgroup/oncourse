/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { UneditableBase } from "ish-ui";
import { connect } from "react-redux";
import { COMMON_PLACEHOLDER } from "../../../../constants/Forms";
import { State } from "../../../../reducers/state";

const mapStateToProps = (state: State, ownProps) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  placeholder: ownProps.placeholder || COMMON_PLACEHOLDER
});

export default connect(mapStateToProps)(UneditableBase);