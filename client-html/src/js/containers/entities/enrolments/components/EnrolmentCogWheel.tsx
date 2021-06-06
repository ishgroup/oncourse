/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import MenuItem from "@material-ui/core/MenuItem";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import CreateCertificateMenu
  from "../../../../common/components/list-view/components/bottom-app-bar/components/CreateCertificateMenu";
import EntityService from "../../../../common/services/EntityService";
import { State } from "../../../../reducers/state";
import AvetmissExportModal, { manualAvetmisConfirm } from "../../../avetmiss-export/components/modal/AvetmissExportModal";
import { getPlainAccounts } from "../../accounts/actions";
import BulkEditCogwheelOption from "../../common/components/BulkEditCogwheelOption";
import { getPlainTaxes } from "../../taxes/actions";
import { getEnrolmentInvoiceLines } from "../actions";
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
    showConfirm
  } = props;

  const [dialogOpened, setDialogOpened] = useState(null);
  const [enrolmentActionsEnabled, setEnrolmentActionsEnabled] = useState(false);

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

  const onClick = useCallback(e => {
    const status = e.target.getAttribute("role");

    if (status === "Avetmiss-Export") {
      return manualAvetmisConfirm(() => {
        setDialogOpened(status);
        },
        showConfirm);
    }

    setDialogOpened(status);
  }, []);

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
        selection={selection}
        opened={dialogOpened === "Avetmiss-Export"}
        setDialogOpened={setDialogOpened}
        closeMenu={closeMenu}
        enrolmentsCount={selection.length}
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
      <MenuItem disabled={!selectedAndNotNew} className={menuItemClass} role="Avetmiss-Export" onClick={onClick}>
        AVETMISS 8 export
      </MenuItem>
      <BulkEditCogwheelOption {...props} />
    </>
  );
});

const mapStateToProps = (state: State) => ({
  search: state.list.searchQuery,
  invoices: state.enrolments.invoiceLines,
  hasQePermissions: state.access["ENROLMENT_CREATE"]
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
    dispatch,
    getEnrolmentInvoiceLines: (id: string) => dispatch(getEnrolmentInvoiceLines(id)),
    getAccounts: () => getPlainAccounts(dispatch),
    getTaxes: () => dispatch(getPlainTaxes())
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EnrolmentCogWheel);
