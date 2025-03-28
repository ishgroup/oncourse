/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PayrollRequest } from '@api/model';
import OpenInNew from '@mui/icons-material/OpenInNew';
import LoadingButton from '@mui/lab/LoadingButton';
import Button from '@mui/material/Button';
import Collapse from '@mui/material/Collapse';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import clsx from 'clsx';
import { format as formatDate } from 'date-fns';
import { openInternalLink, YYYY_MM_DD_MINUSED } from 'ish-ui';
import React, { useCallback, useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { change, getFormValues, InjectedFormProps, reduxForm } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import LoadingIndicator from '../../../../common/components/progress/LoadingIndicator';
import { State } from '../../../../reducers/state';

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
        <div className="heading p-3">{$t('generate_tutor_pay')}</div>

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
                {$t('wages_are_not_confirmed_and_will_be_not_processed')}
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
              {$t('confirm_now')}
            </Button>
          </Collapse>
        </DialogContent>

        <DialogActions className="p-1">
          <Button color="primary" onClick={onClose}>
            {$t('cancel')}
          </Button>

          <LoadingButton
            variant="contained"
            color="primary"
            className="payslipButton"
            type="submit"
            loading={loading}
            disabled={!item.totalWagesCount}
          >
            {$t('generate')}
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
