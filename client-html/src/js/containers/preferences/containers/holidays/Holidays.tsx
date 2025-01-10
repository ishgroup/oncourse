/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Holiday as HoldayItemModel } from "@api/model";
import { ShowConfirmCaller } from "ish-ui";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { showConfirm } from "../../../../common/actions";
import { deleteHolidaysItem, getHolidays, saveHolidays } from "../../actions";
import HolidaysForm from "./components/HolidaysForm";

interface Props {
  onInit: () => void;
  onSave: (items: HoldayItemModel[]) => void;
  onDelete: (id: number) => void;
  openConfirm?: ShowConfirmCaller;
}

class Holidays extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }

  render() {
    return <HolidaysForm {...this.props} />;
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => dispatch(getHolidays()),
  onSave: (items: HoldayItemModel[]) => dispatch(saveHolidays(items)),
  onDelete: (id: number) => dispatch(deleteHolidaysItem(id)),
  openConfirm: props => dispatch(showConfirm(props))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Holidays);
