/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import clsx from "clsx";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import Button from "@mui/material/Button";
import OpenInNew from "@mui/icons-material/OpenInNew";
import {
  reduxForm, change, getFormValues
} from "redux-form";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import Collapse from "@mui/material/Collapse";
import { format as formatDate } from "date-fns";
import IconButton from "@mui/material/IconButton";
import { Dispatch } from "redux";
import { PayrollRequest, WagesToProcess } from "@api/model";
import { connect } from "react-redux";
import LoadingButton from "@mui/lab/LoadingButton";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { openInternalLink } from "../../../../common/utils/links";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import LoadingIndicator from "../../../../common/components/layout/LoadingIndicator";

export const PAYSLIP_GENERATE_FORM = "PayslipGenerateForm";

interface Props {
  values?: PayrollRequest & { confirm?: boolean };
  preparedWages: WagesToProcess;
  onClose: any;
  confirmNowIsAllowed: boolean;
  preparePayroll: (request: PayrollRequest) => void;
  generatePayslip: (payrollRequest: PayrollRequest, confirm: boolean) => void;
  dispatch?: Dispatch;
  handleSubmit?: any;
  invalid?: boolean;
}

const PayslipGenerateDialog: React.FC<Props> = ({
  dispatch,
  handleSubmit,
  values,
  preparedWages,
  invalid,
  confirmNowIsAllowed,
  preparePayroll,
  generatePayslip,
  onClose
}) => {
  const [loading, setLoading] = useState(false);

  const onDateChange = useCallback((e, value, previousValue) => {
    if (value && value !== previousValue) {
      preparePayroll({ ...values, untilDate: formatDate(new Date(value), YYYY_MM_DD_MINUSED) });
    }
  }, []);

  const onConfirmChange = useCallback(() => {
    dispatch(change(PAYSLIP_GENERATE_FORM, "confirm", true));
  }, []);

  const onGenerate = useCallback((values: PayrollRequest & { confirm: boolean }) => {
    setLoading(true);

    const payrollRequest: PayrollRequest = {
      untilDate: formatDate(new Date(values.untilDate), YYYY_MM_DD_MINUSED),
      recordIds: values.recordIds,
      entityName: values.entityName
    };

    generatePayslip(payrollRequest, values.confirm);
  }, []);

  const onCloseHandler = useCallback(() => {
    setLoading(false);
    onClose();
  }, []);

  const onWagesLinkClick = useCallback(() => {
    openInternalLink(`/class/?search=id in (${preparedWages.unconfirmedClassesIds})`);
  }, [preparedWages.unconfirmedClassesIds]);

  return (
    <Dialog open={Boolean(preparedWages)} onClose={onCloseHandler}>
      <LoadingIndicator />
      <form autoComplete="off" onSubmit={handleSubmit(onGenerate)}>
        <DialogTitle>Generate Tutor Pay</DialogTitle>

        <DialogContent>
          <FormField
            type="date"
            name="untilDate"
            label={`For ${
              values.confirm ? preparedWages.totalWagesCount : preparedWages.unprocessedWagesCount
            } unprocessed wages until and including`}
            onChange={onDateChange}
            required
            fullWidth
          />

          <Collapse in={!values.confirm}>
            <DialogContentText
              className={clsx("centeredFlex m-0", {
                "invisible": !preparedWages.unconfirmedWagesCount
              })}
            >
              <span>
                {preparedWages.unconfirmedWagesCount}
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
                "d-none": !preparedWages.unconfirmedWagesCount
              })}
            >
              Confirm Now
            </Button>
          </Collapse>
        </DialogContent>

        <DialogActions className="pr-2 pb-2">
          <Button color="primary" onClick={onClose}>
            Cancel
          </Button>

          <LoadingButton
            variant="contained"
            color="primary"
            className="payslipButton"
            type="submit"
            loading={loading}
            disabled={invalid || !preparedWages.totalWagesCount}
          >
            Generate
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues(PAYSLIP_GENERATE_FORM)(state)
});

export default reduxForm<any, Props>({
  form: PAYSLIP_GENERATE_FORM
})(connect(mapStateToProps)((props: any) => (props.values ? <PayslipGenerateDialog {...props} /> : null)));
