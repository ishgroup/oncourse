/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import {
  reduxForm, getFormValues, InjectedFormProps, change
} from "redux-form";
import { format as formatDate } from "date-fns";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import Collapse from "@mui/material/Collapse";
import DialogContentText from "@mui/material/DialogContentText";
import IconButton from "@mui/material/IconButton";
import OpenInNew from "@mui/icons-material/OpenInNew";
import { PayrollRequest } from "@api/model";
import LoadingButton from "@mui/lab/LoadingButton";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import LoadingIndicator from "../../../../common/components/progress/LoadingIndicator";
import { YYYY_MM_DD_MINUSED } from  "ish-ui";
import { openInternalLink } from "ish-ui";
import Button from "@mui/material/Button";

interface Props extends InjectedFormProps {
  opened: boolean;
  onClose: () => void;
  value?: any;
  onGenerate?: (payrollRequest: PayrollRequest, confirm: boolean) => void;
  preparePayroll?: (payrollRequest: PayrollRequest) => void;
  item?: any;
  selection?: any;
  closeMenu?: any;
  confirmNowIsAllowed?: any;
  dispatch?: any;
  entity?: string;
  payrollGenerated?: boolean;
}

export const FORM: string = "GENERATE_TUTOR_PAY_FORM";

const GenerateTutorPayModalForm: React.FC<Props> = props => {
  const {
    opened,
    onClose,
    handleSubmit,
    value,
    item,
    preparePayroll,
    selection,
    onGenerate,
    closeMenu,
    confirmNowIsAllowed,
    dispatch,
    entity,
    payrollGenerated
  } = props;

  const [loading, setLoading] = useState(false);

  const onCloseDialog = useCallback(() => {
    setLoading(false);
    onClose();
  }, []);

  const preparePayrollRequest = date => ({
      untilDate: formatDate(new Date(date), YYYY_MM_DD_MINUSED),
      entityName: entity,
      recordIds: selection
    });

  const onDateChange = useCallback((e, value, previousValue) => {
    if (value !== previousValue) {
      preparePayroll(preparePayrollRequest(value));
    }
  }, []);

  const onSubmit = useCallback(values => {
    setLoading(true);
    onGenerate(preparePayrollRequest(values.untilDate), values.confirm);
  }, []);

  const onConfirmChange = useCallback(() => {
    dispatch(change(FORM, "confirm", true));
  }, []);

  const onWagesLinkClick = useCallback(() => {
    openInternalLink(item.wagesLink);
  }, [item]);

  useEffect(() => {
    if (payrollGenerated) {
      setLoading(false);
      closeMenu();
      openInternalLink("/payslip");
    }
  }, [payrollGenerated]);

  return value && item ? (
    <Dialog open={opened} onClose={onCloseDialog}>
      <LoadingIndicator />
      <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
        <div className="heading p-3">Generate Tutor Pay</div>

        <DialogContent>
          <FormField
            type="date"
            name="untilDate"
            label={`For ${
              value.confirm ? item.totalWagesCount : item.unprocessedWagesCount
            } unprocessed wages until and including`}
            onChange={onDateChange}
            required
                      />

          <Collapse in={!value.confirm}>
            <DialogContentText
              className={clsx("centeredFlex", {
                "invisible": !item.unconfirmedWagesCount
              })}
            >
              <span>
                {item.unconfirmedWagesCount}
                {' '}
                wages are not confirmed and will be not processed
              </span>
              <IconButton className="smallIconButton" onClick={onWagesLinkClick}>
                <OpenInNew fontSize="small" color="secondary" />
              </IconButton>
            </DialogContentText>
            <Button
              disabled={!confirmNowIsAllowed}
              size="small"
              variant="outlined"
              onClick={onConfirmChange}
              className={clsx("mt-1", {
                "d-none": !item.unconfirmedWagesCount
              })}
            >
              Confirm Now
            </Button>
          </Collapse>
        </DialogContent>

        <DialogActions className="p-1">
          <Button color="primary" onClick={onClose}>
            Cancel
          </Button>

          <LoadingButton
            variant="contained"
            color="primary"
            className="payslipButton"
            type="submit"
            loading={loading}
            disabled={!item.totalWagesCount}
          >
            Generate
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  value: getFormValues(FORM)(state),
  payrollGenerated: state.payrolls.generated
});

const GenerateTutorPayModal = reduxForm({
  form: FORM
})(GenerateTutorPayModalForm);

export default connect<any, any, any>(mapStateToProps, null)(GenerateTutorPayModal as any);
