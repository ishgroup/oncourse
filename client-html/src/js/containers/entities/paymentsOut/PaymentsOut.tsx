/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { PaymentOut } from "@api/model";
import { ExitToApp } from "@mui/icons-material";
import Link from "@mui/material/Link";
import Popover from "@mui/material/Popover";
import zIndex from "@mui/material/styles/zIndex";
import { createStyles, withStyles } from "@mui/styles";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
  clearListState,
  getFilters,
  setListCreatingNew,
  setListSelection,
} from "../../../common/components/list-view/actions";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { FilterGroup } from "../../../model/common/ListView";
import { getAccountTransactionLockedDate } from "../../preferences/actions";
import { getPlainAccounts } from "../accounts/actions";
import { getAmountOwing, setContraInvoices } from "../invoices/actions";
import { getAdministrationSites } from "../sites/actions";
import { getActivePaymentOutMethods, getAddPaymentOutContact } from "./actions";
import AddPaymentOutEditView from "./components/AddPaymentOutEditView";
import PaymentsOutEditView from "./components/PaymentOutEditView";
import { PaymentOutModel } from "./reducers/state";

const manualLink = getManualLink("refunding-a-credit-note-via-payment-out");

const nameCondition = (paymentOut: PaymentOutModel) => paymentOut.type;

const getWindowWidth = () => window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth || 1920;

const getWindowHeight = () => window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 1080;

const styles = theme => createStyles({
  dialog: {
    zIndex: zIndex.tooltip,
    padding: theme.spacing(1)
  },
  createLink: {
    display: "flex",
    alignItems: "center"
  },
  exitToApp: {
    marginLeft: theme.spacing(1),
    fontSize: "1.2rem"
  }
});

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Success",
        expression: "status is SUCCESS",
        active: false
      },
      {
        name: "Other",
        expression: "status not is SUCCESS",
        active: false
      }
    ]
  }
];

class PaymentsOut extends React.Component<any, any> {
  constructor(props) {
    super(props);
    this.state = { createNewDialogOpen: false };
  }

  componentDidMount() {
    this.props.getFilters();
    this.props.getActivePaymentOutMethods();
    this.props.getAccounts();
    this.props.getLockedDate();
    this.props.getAdministrationSites();
  }

  componentWillUnmount() {
    this.props.clearListState();
    this.props.clearContraInvoices();
  }

  onCreateNew() {
    const {
      location, onInit, setListCreatingNew, updateSelection
    } = this.props;

    this.closeCreateNewDialog();

    const urlParams = new URLSearchParams(location.search);

    setListCreatingNew(true);
    updateSelection(["new"]);
    onInit(urlParams.get("invoiceId"));
  }

  openCreateNewDialog() {
    const {
      match: { params }
    } = this.props;

    if (params.id === "new" && window.location.search?.includes("invoiceId")) {
      this.onCreateNew();
    } else if (!this.state.createNewDialogOpen) {
      this.setState({
        createNewDialogOpen: true
      });
    }
  }

  closeCreateNewDialog() {
    this.setState({
      createNewDialogOpen: false
    });
  }

  render() {
    const {
      classes
    } = this.props;

    return (
      <div>
        <ListView
          listProps={{
            primaryColumn: "payee.fullName",
            secondaryColumn: "paymentMethod.name"
          }}
          editViewProps={{
            manualLink,
            nameCondition
          }}
          EditViewContent={props => props.isNew ?  <AddPaymentOutEditView {...props}/> : <PaymentsOutEditView {...props}/>}
          rootEntity="PaymentOut"
          filterGroupsInitial={filterGroups}
          findRelated={[
            { title: "Contacts", list: "contact", expression: "paymentsOut.id" },
            { title: "Invoices", list: "invoice", expression: "paymentOutLines.paymentOut" },
            { title: "Transactions", list: "transaction", expression: "paymentOut.id" },
            { title: "Audits", list: "audit", expression: "entityIdentifier == PaymentOut and entityId" }
          ]}
          defaultDeleteDisabled
          customOnCreate={() => this.openCreateNewDialog()}
          noListTags
        />
        <Popover
          open={this.state.createNewDialogOpen}
          onClose={() => this.closeCreateNewDialog()}
          anchorReference="anchorPosition"
          anchorPosition={{ top: getWindowHeight() - 80, left: getWindowWidth() - 200 }}
          anchorOrigin={{
            vertical: "center",
            horizontal: "center"
          }}
          transformOrigin={{
            vertical: "center",
            horizontal: "center"
          }}
        >
          <div className={classes.dialog}>
            <Link
              href={`${window.location.origin}/invoice?filter=@Credit_notes`}
              target="_blank"
              color="textSecondary"
              underline="none"
              className={classes.createLink}
            >
              <span>Use the cogwheel to refund credit notes.</span>
              {" "}
              <ExitToApp className={classes.exitToApp} />
            </Link>
          </div>
        </Popover>
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: id => {
    dispatch(getAddPaymentOutContact(id));
    dispatch(getAmountOwing(id));
  },
  getLockedDate: () => {
    dispatch(getAccountTransactionLockedDate());
  },
  getFilters: () => {
    dispatch(getFilters("PaymentOut"));
  },
  getAdministrationSites: () => dispatch(getAdministrationSites()),
  getAccounts: () => getPlainAccounts(dispatch),
  clearListState: () => dispatch(clearListState()),
  getActivePaymentOutMethods: () => dispatch(getActivePaymentOutMethods()),
  setListCreatingNew: (creatingNew: boolean) => dispatch(setListCreatingNew(creatingNew)),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  clearContraInvoices: () => dispatch(setContraInvoices(null)),
});

export default connect<any, any, any>(null, mapDispatchToProps)(withStyles(styles, { withTheme: true })(PaymentsOut));