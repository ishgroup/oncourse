/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import { CheckoutItem, CheckoutSummary } from "../../../../../model/checkout";
import QuickEnrolItemViewFormWraper from "./CkecoutItemViewForm";
import MembershipEditView from "./MembershipEditView";
import ProductEditView from "./ProductEditView";
import VoucherEditView from "./VoucherEditView";
import CheckoutAppBar from "../../CheckoutAppBar";

interface Props {
  onClose?: any;
  openedItem?: CheckoutItem;
  summary?: CheckoutSummary;
}

const CheckoutItemView: React.FC<Props> = ({ onClose, openedItem, summary }) => {
  const formComponent = React.useMemo(() => {
    switch (openedItem.type) {
      case "membership":
        return MembershipEditView;
      case "product":
        return ProductEditView;
      case "voucher":
        return VoucherEditView;
      default:
        return null;
    }
  }, [openedItem]);

  const headerField = React.useMemo(() => (
    <>
      <CheckoutAppBar
        title={`${openedItem.type.charAt(0).toUpperCase()}${openedItem.type.slice(1)}: ${openedItem.name}`}
        type={openedItem.type}
        link={openedItem.id}
      />
    </>
    ), [openedItem]);

  const summaryVoucher = React.useMemo(() =>
    (openedItem.type === "voucher" ? summary.voucherItems.find(item => item.id === openedItem.id) : null),
    [openedItem]);

  return (
    <div className="root">
      <CustomAppBar>
        {headerField}
        <div>
          <Button
            classes={{
              root: "whiteAppBarButton",
              disabled: "whiteAppBarButtonDisabled"
            }}
            onClick={onClose}
          >
            Close
          </Button>
        </div>
      </CustomAppBar>
      <Grid container className="appBarContainer">
        {formComponent && <QuickEnrolItemViewFormWraper EditViewComponent={formComponent} summaryVoucher={summaryVoucher} />}
      </Grid>
    </div>
  );
};

export default CheckoutItemView;
