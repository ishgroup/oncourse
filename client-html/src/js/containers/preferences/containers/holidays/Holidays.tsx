/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Holiday as HoldayItemModel } from "@api/model";
import HolidaysForm from "./components/HolidaysForm";
import { deleteHolidaysItem, getHolidays, saveHolidays } from "../../actions";
import { showConfirm } from "../../../../common/actions";

interface Props {
  onInit: () => void;
  onSave: (items: HoldayItemModel[]) => void;
  onDelete: (id: number) => void;
  openConfirm?: (onConfirm: any, confirmMessage?: string) => void;
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
    openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage))
  });

export default connect<any, any, any>(null, mapDispatchToProps)(Holidays);
