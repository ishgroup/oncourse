/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { PayslipStatus } from "@api/model";
import { executePayslip } from "../actions";
import PayslipGenerateCogwheelAction from "./PayslipGenerateCogwheelAction";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";

class PayslipCogwheelOptions extends React.PureComponent<any, any> {
  executePayslip = e => {
    const { executePayslip, selection, closeMenu } = this.props;

    const status = e.target.getAttribute("role");

    executePayslip(selection, status);
    closeMenu();
  };

  render() {
    const {
 selection, menuItemClass, closeMenu, showConfirm 
} = this.props;

    const hoSelectedOrNew = selection.length === 0 || selection[0] === "NEW";

    return (
      <>
        <BulkEditCogwheelOption {...this.props} />
        <MenuItem disabled={hoSelectedOrNew} className={menuItemClass} role="Completed" onClick={this.executePayslip}>
          Mark 
          {' '}
          {selection.length}
          {' '}
          tutor pay as completed
        </MenuItem>
        <MenuItem disabled={hoSelectedOrNew} className={menuItemClass} role="Approved" onClick={this.executePayslip}>
          Mark 
          {' '}
          {selection.length}
          {' '}
          tutor pay as approved
        </MenuItem>
        <MenuItem
          disabled={hoSelectedOrNew}
          className={menuItemClass}
          role="Paid/Exported"
          onClick={this.executePayslip}
        >
          Finalise 
          {' '}
          {selection.length}
          {' '}
          tutor pay
        </MenuItem>

        <PayslipGenerateCogwheelAction
          entity="Payslip"
          generateLabel="Generate tutor pay"
          closeMenu={closeMenu}
          showConfirm={showConfirm}
          menuItemClass={menuItemClass}
        />
      </>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  executePayslip: (ids: number[], status: PayslipStatus) => dispatch(executePayslip(ids, status))
});

export default connect<any, any, any>(null, mapDispatchToProps)(PayslipCogwheelOptions);
