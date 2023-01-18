/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import MenuItem from "@mui/material/MenuItem";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Button from "@mui/material/Button";
import {
  getFormValues, InjectedFormProps, reduxForm
} from "redux-form";
import FormField from "../../../../common/components/form/formFields/FormField";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { State } from "../../../../reducers/state";
import { getCertificatesRevokeStatus, revokeCertificate, setCertificatesRevokeStatus } from "../actions";

interface RevokeConfirmProps extends InjectedFormProps {
  open: boolean;
  onClose: AnyArgFunction;
  onConfirm: AnyArgFunction;
  values: any;
}

const stopPropagation = e => e.stopPropagation();

const RevokeConfirmBase: React.FunctionComponent<RevokeConfirmProps> = props => {
  const {
 open, onClose, onConfirm, values = {}, invalid
} = props;

  const onRevoke = useCallback(
    () => {
      onConfirm(values.reason);
    },
    [values.reason]
  );

  return (
    <Dialog open={open} onClose={onClose} fullWidth>
      <DialogTitle id="alert-dialog-title">Revoke Certificate</DialogTitle>
      <DialogContent>
        <FormField
          type="multilineText"
          name="reason"
          label="Reason for revoking certificate"
          onKeyDown={stopPropagation}
                    required
        />
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} color="primary">
          Cancel
        </Button>
        <Button onClick={onRevoke} color="primary" disabled={invalid} autoFocus>
          Revoke
        </Button>
      </DialogActions>
    </Dialog>
  );
};

const mapRevokeConfirmStateToProps = (state: State) => ({
  values: getFormValues("RevokeConfirmForm")(state)
});

const translateStringArrayToNumberArray = (stringArray: string[]): number[] => stringArray.map(item => parseInt(item, 10));

const RevokeConfirm = reduxForm({
  form: "RevokeConfirmForm"
})(connect<any, any, any>(mapRevokeConfirmStateToProps, null)(RevokeConfirmBase)) as any;

const RevokeCertificateCogwheel: React.FunctionComponent<any> = React.memo(props => {
  const {
    selection,
    revokeCertificate,
    menuItemClass,
    getCertificatesRevokeStatus,
    checkingRevokeStatus,
    clearCertificatesRevokeStatus,
    hasRevoked
  } = props;

  const [openDialog, setOpenDialog] = useState(false);

  const noSelectionOrNew = useMemo(() => selection.length === 0 || selection[0] === "NEW", [selection]);

  useEffect(
    () => {
      if (!noSelectionOrNew) {
        getCertificatesRevokeStatus(selection);
      }

      return () => clearCertificatesRevokeStatus();
    },
    [noSelectionOrNew, selection]
  );

  const onConfirm = useCallback(
    reason => {
      revokeCertificate(translateStringArrayToNumberArray(selection), reason);
      setOpenDialog(false);
    },
    [selection]
  );

  const onConfirmClose = useCallback(() => {
    setOpenDialog(false);
  }, []);

  return (
    <>
      <RevokeConfirm open={openDialog} onConfirm={onConfirm} onClose={onConfirmClose} />

      <MenuItem
        disabled={checkingRevokeStatus || hasRevoked || noSelectionOrNew}
        className={menuItemClass}
        onClick={() => setOpenDialog(true)}
      >
        Revoke
        {' '}
        {selection.length}
        {' '}
        certificate
        {selection.length > 1 && "s"}
      </MenuItem>
    </>
  );
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  revokeCertificate: (ids: number[], reason: string) => dispatch(revokeCertificate(ids, reason)),
  getCertificatesRevokeStatus: (ids: number[]) => dispatch(getCertificatesRevokeStatus(ids)),
  clearCertificatesRevokeStatus: () => dispatch(setCertificatesRevokeStatus(false))
});

const mapStateToProps = (state: State) => ({
  checkingRevokeStatus: state.certificates.checkingRevokeStatus,
  hasRevoked: state.certificates.hasRevoked
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(RevokeCertificateCogwheel);
