/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import MenuItem from "@mui/material/MenuItem";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { IAction } from '../../../../common/actions/IshAction';
import CreateCertificateMenu
  from "../../../../common/components/list-view/components/bottom-app-bar/components/CreateCertificateMenu";
import EntityService from "../../../../common/services/EntityService";
import { useAppSelector } from "../../../../common/utils/hooks";
import { State } from "../../../../reducers/state";
import AvetmissExportModal from "../../../avetmiss-export/components/modal/AvetmissExportModal";
import { getPlainAccounts } from "../../accounts/actions";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";
import { getPlainTaxes } from "../../taxes/actions";
import { getEnrolmentInvoiceLines, setEnrolmentsDialog } from "../actions";
import CancelEnrolmentModal from "./modal/CancelEnrolmentModal";
import TransferEnrolmentModal from "./modal/TransferEnrolmentModal";

const EnrolmentCogWheel = React.memo<any>(props => {
  const {
    menuItemClass,
    selection,
    closeMenu,
    getEnrolmentInvoiceLines,
    getAccounts,
    getTaxes,
    dispatch,
    hasQePermissions,
    dialogOpened,
    setDialogOpened
  } = props;

  const [enrolmentActionsEnabled, setEnrolmentActionsEnabled] = useState(false);
  const [enrolmentIds, setEnrolmentIds] = useState(selection);

  const searchQuery = useAppSelector(state => state.list.searchQuery);

  useEffect(() => {
    if (selection.length > 0 && (dialogOpened === "Cancel" || dialogOpened === "Transfer")) {
      getEnrolmentInvoiceLines(selection[0]);
    }
  }, [dialogOpened]);

  useEffect(() => {
    getAccounts();
    getTaxes();
  }, []);

  useEffect(() => {
    const selectedSingleAndNotNew = selection.length === 1 && selection[0] !== "NEW";

    if (selectedSingleAndNotNew) {
      EntityService.getPlainRecords("Enrolment", "status", `id in (${selection.toString()})`).then(res => {
        if (res.rows.length > 0 && res.rows[0].values.length > 0) {
          const status = res.rows[0].values[0].toString();
          if (status === "In transaction" || status === "Active") {
            setEnrolmentActionsEnabled(true);
          }
        }
      }, console.error);
    }

    if (enrolmentActionsEnabled) setEnrolmentActionsEnabled(false);
  }, [selection]);

  const selectedAndNotNew = useMemo(() => selection.length >= 1 && selection[0] !== "NEW", [selection]);

  const onClick = useCallback(async e => {
    const status = e && e.target.getAttribute("role");
    setDialogOpened(status);
    if (status === "Avetmiss-Export") {
      if (selection.length) {
        setEnrolmentIds(selection);
      } else {
        const plainEnrolments = await EntityService.getRecordsByListSearch("Enrolment", searchQuery);
        const ids = plainEnrolments.rows.map(r => r.id);
        setEnrolmentIds(ids);
      }
    }
  }, [selection]);

  return (
    <>
      <CancelEnrolmentModal
        selection={selection}
        opened={dialogOpened === "Cancel"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
      />

      <TransferEnrolmentModal
        selection={selection}
        opened={dialogOpened === "Transfer"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
      />

      <AvetmissExportModal
        entity="Enrolment"
        ids={enrolmentIds}
        opened={dialogOpened === "Avetmiss-Export"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
        enrolmentsCount={enrolmentIds.length}
      />

      <CreateCertificateMenu
        entity="Enrolment"
        disableMenu={!selectedAndNotNew}
        selection={selection}
        closeMenu={closeMenu}
        dispatch={dispatch}
      />

      <MenuItem disabled={!enrolmentActionsEnabled} className={menuItemClass} role="Cancel" onClick={onClick}>
        Cancel an enrolment
      </MenuItem>
      <MenuItem
        disabled={!hasQePermissions || !enrolmentActionsEnabled}
        className={menuItemClass}
        role="Transfer"
        onClick={onClick}
      >
        Transfer an enrolment
      </MenuItem>
      <MenuItem disabled={selection[0] === "NEW"} className={menuItemClass} role="Avetmiss-Export" onClick={onClick}>
        AVETMISS 8 export {selection.length ? "selected" : "all"}
      </MenuItem>
      <BulkEditCogwheelOption {...props} />
    </>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.list.searchQuery,
  invoices: state.enrolments.invoiceLines,
  dialogOpened: state.enrolments.dialogOpened,
  hasQePermissions: state.access["ENROLMENT_CREATE"]
});

const mapDispatchToProps = (dispatch: Dispatch<IAction>) => ({
  dispatch,
  getEnrolmentInvoiceLines: (id: string) => dispatch(getEnrolmentInvoiceLines(id)),
  getAccounts: () => getPlainAccounts(dispatch),
  getTaxes: () => dispatch(getPlainTaxes()),
  setDialogOpened: dialog => dispatch(setEnrolmentsDialog(dialog))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EnrolmentCogWheel);