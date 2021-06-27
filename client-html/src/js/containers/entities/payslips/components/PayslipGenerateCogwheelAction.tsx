/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect } from "react";
import MenuItem from "@material-ui/core/MenuItem";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { PayrollRequest } from "@api/model";
import { initialize, isDirty } from "redux-form";
import { format as formatDate } from "date-fns";
import PayslipGenerateDialog from "./PayslipGenerateDialog";
import { State } from "../../../../reducers/state";
import { clearPayrollPreparedWages, executePayroll, preparePayroll } from "../../payrolls/actions";
import { interruptProcess } from "../../../../common/actions";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { usePrevious } from "../../../../common/utils/hooks";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { AnyArgFunction, NoArgFunction } from "../../../../model/common/CommonFunctions";

const getPayrollInitial = (): PayrollRequest => {
  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  yesterday.setHours(0, 0, 0, 0);

  return {
    untilDate: formatDate(yesterday, YYYY_MM_DD_MINUSED)
  };
};

interface Props {
  entity: string;
  generateLabel: string;
  closeMenu: any;
  showConfirm: ShowConfirmCaller;
  menuItemClass: string;
  selection?: number[];
  preparePayroll?: (payrollRequest: PayrollRequest) => void;
  generatePayslip?: (payrollRequest: PayrollRequest, confirm: boolean) => void;
  isDirty?: boolean;
  resetEditView?: AnyArgFunction;
  listEditRecord?: any;
  clearPreparedPayroll?: NoArgFunction;
  processRunning?: boolean;
  processId?: string;
  generateIsAllowed?: boolean;
  preparedWages?: any;
  values?: PayrollRequest & { confirm?: boolean };
  confirmNowIsAllowed?: boolean;
}

const PayslipGenerateCogwheelAction: React.FC<Props> = ({
  preparePayroll,
  generatePayslip,
  closeMenu,
  isDirty,
  showConfirm,
  resetEditView,
  listEditRecord,
  clearPreparedPayroll,
  processRunning,
  processId,
  menuItemClass,
  generateIsAllowed,
  preparedWages,
  confirmNowIsAllowed,
  generateLabel,
  selection,
  entity
}) => {
  const preparePayslip = useCallback(() => {
    preparePayroll({
      ...getPayrollInitial(),
      recordIds: selection,
      entityName: selection && selection.length ? entity : null
    });
  }, [selection, entity]);

  const checkDirtyBeforePreparePayslip = useCallback(() => {
    if (isDirty) {
      showConfirm({
        onConfirm: () => {
          resetEditView(listEditRecord);
          preparePayslip();
        }
      });
      return;
    }
    preparePayslip();
  }, [isDirty, listEditRecord, selection]);

  const closeGenerateDialog = useCallback(() => {
    closeMenu();
    clearPreparedPayroll();

    if (processRunning) {
      interruptProcess(processId);
    }
  }, []);

  const prevProcessRunning = usePrevious(processRunning);
  const prevPreparedWages = usePrevious(preparedWages);

  useEffect(() => {
    if (!processRunning && prevProcessRunning) {
      clearPreparedPayroll();
    }
  }, [processRunning]);

  useEffect(() => {
    if (!preparedWages && prevPreparedWages) {
      closeMenu();
    }
  }, [preparedWages]);

  return (
    <>
      {preparedWages && (
        <PayslipGenerateDialog
          onClose={closeGenerateDialog}
          preparedWages={preparedWages}
          confirmNowIsAllowed={confirmNowIsAllowed}
          preparePayroll={preparePayroll}
          generatePayslip={generatePayslip}
        />
      )}

      <MenuItem disabled={!generateIsAllowed} className={menuItemClass} onClick={checkDirtyBeforePreparePayslip}>
        {generateLabel}
      </MenuItem>
    </>
  );
};

const mapStateToProps = (state: State, ownProps: Props) => ({
  confirmNowIsAllowed:
    state.access.hasOwnProperty(`/a/v1/list/option/payroll?entity=${ownProps.entity}&bulkConfirmTutorWages=true`)
    && state.access[`/a/v1/list/option/payroll?entity=${ownProps.entity}&bulkConfirmTutorWages=true`]["POST"],
  preparedWages: state.payrolls.preparedWages,
  processRunning: state.process && state.process.status === "In progress",
  processId: state.process && state.process.processId,
  isDirty: isDirty(LIST_EDIT_VIEW_FORM_NAME)(state),
  listEditRecord: state.list.editRecord,
  generateIsAllowed:
    state.access.hasOwnProperty(`/a/v1/list/option/payroll?entity=${ownProps.entity}`)
    && state.access[`/a/v1/list/option/payroll?entity=${ownProps.entity}`]["PUT"]
});

const mapDispatchToProps = (dispatch: Dispatch<any>, ownProps: Props) => ({
  preparePayroll: (payrollRequest: PayrollRequest) => dispatch(preparePayroll(ownProps.entity, payrollRequest)),
  generatePayslip: (payrollRequest: PayrollRequest, confirm: boolean) =>
    dispatch(executePayroll(ownProps.entity, confirm, payrollRequest)),
  clearPreparedPayroll: () => dispatch(clearPayrollPreparedWages()),
  interruptProcess: (processId: string) => dispatch(interruptProcess(processId)),
  resetEditView: (record: any) => dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, record))
});

export default connect<any, any, Props>(mapStateToProps, mapDispatchToProps)(PayslipGenerateCogwheelAction);
