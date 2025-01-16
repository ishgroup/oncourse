import MenuItem from "@mui/material/MenuItem";
import React, { memo, useCallback, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change, isDirty, reset } from "redux-form";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../../common/components/list-view/constants";
import { CogwhelAdornmentProps } from "../../../../../model/common/ListView";
import { State } from "../../../../../reducers/state";
import BulkEditCogwheelOption from "../../../common/components/BulkEditCogwheelOption";
import { getSaleDetails, setSaleDelivered, setSaleDetails } from "../../actions";
import CancelSaleDialog from "./CancelSaleDialog";

interface Props extends CogwhelAdornmentProps {
  rollBackFormChanges: any;
  isFormDirty: boolean;
  setDialogId: any;
  saleStatus: any;
  saleType: any;
  getSaleDetails: any;
  setSaleDelivered: any;
  clearSaleDetails: any;
}

const SalesCogwheel = memo<Props>(props => {
  const {
    selection,
    menuItemClass,
    closeMenu,
    saleStatus,
    saleType,
    getSaleDetails,
    setSaleDelivered,
    clearSaleDetails,
    showConfirm,
    rollBackFormChanges,
    isFormDirty,
    setDialogId
  } = props;

  const [dialogOpened, setDialogOpened] = useState(false);

  const hasOneSelected = useMemo(() => selection.length === 1 && selection[0] !== "NEW", [selection.length]);

  const cancelAllowed = useMemo(() => hasOneSelected && saleStatus === "Active", [saleStatus, hasOneSelected]);

  const deliveredAllowed = useMemo(() => cancelAllowed && saleType === "Product", [cancelAllowed, saleType]);

  const cancelSaleCallback = useCallback(() => {
    rollBackFormChanges();
    setDialogId(Number(selection[0]));
    setDialogOpened(true);
  }, []);

  const setToDeliveredCallback = useCallback(() => {
    rollBackFormChanges();
    setSaleDelivered(selection[0]);
    closeMenu();
  }, []);

  useEffect(
    () => {
      if (hasOneSelected && selection[0] !== "NEW") {
        getSaleDetails(selection[0]);
      }

      return () => clearSaleDetails();
    },
    [selection, hasOneSelected]
  );

  const onClick = useCallback(
    e => {
      const status = e.target.getAttribute("role");

      switch (status) {
        case "cancelSale": {
          isFormDirty ? showConfirm({ onConfirm: cancelSaleCallback }) : cancelSaleCallback();
          break;
        }
        case "setToDelivered": {
          isFormDirty ? showConfirm({ onConfirm: setToDeliveredCallback }) : setToDeliveredCallback();
          break;
        }
      }
    },
    [isFormDirty]
  );

  return (
    <>
      <CancelSaleDialog opened={dialogOpened} setDialogOpened={setDialogOpened} />

      <MenuItem disabled={!cancelAllowed} className={menuItemClass} role="cancelSale" onClick={onClick}>
        Cancel Sale
      </MenuItem>
      <MenuItem disabled={!deliveredAllowed} className={menuItemClass} role="setToDelivered" onClick={onClick}>
        Set to delivered
      </MenuItem>
      <BulkEditCogwheelOption {...props} />
    </>
  );
});

const mapStateToProps = (state: State) => ({
  saleStatus: state.sales.selectedSaleStatus,
  saleType: state.sales.selectedSaleType,
  isFormDirty: isDirty(LIST_EDIT_VIEW_FORM_NAME)(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setDialogId: (id: number) => dispatch(change("CancelSaleForm", "id", id)),
  rollBackFormChanges: () => dispatch(reset(LIST_EDIT_VIEW_FORM_NAME)),
  getSaleDetails: (id: string) => dispatch(getSaleDetails(id)),
  clearSaleDetails: () => dispatch(setSaleDetails(null, null)),
  setSaleDelivered: (id: number) => dispatch(setSaleDelivered(id))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(SalesCogwheel);
