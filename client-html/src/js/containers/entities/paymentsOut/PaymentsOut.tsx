/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Popover } from "@mui/material";
import { createStyles, withStyles } from "@mui/styles";
import Link from "@mui/material/Link";
import zIndex from "@mui/material/styles/zIndex";
import { ExitToApp } from "@mui/icons-material";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup } from "../../../model/common/ListView";
import { getActivePaymentOutMethods } from "./actions";
import { getPlainAccounts } from "../accounts/actions";
import { clearListState, getFilters, } from "../../../common/components/list-view/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getAccountTransactionLockedDate } from "../../preferences/actions";
import PaymentsOutEditView from "./components/PaymentOutEditView";
import { PaymentOutModel } from "./reducers/state";
import { getAdministrationSites } from "../sites/actions";

const manualLink = getManualLink("processingEnrolments_PaymentOut");

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
  }

  openCreateNewDialog() {
    this.setState({
      createNewDialogOpen: true
    });
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
          EditViewContent={PaymentsOutEditView}
          rootEntity="PaymentOut"
          onInit={() => this.openCreateNewDialog()}
          filterGroupsInitial={filterGroups}
          findRelated={[
            { title: "Contacts", list: "contact", expression: "paymentsOut.id" },
            { title: "Invoices", list: "invoice", expression: "paymentOutLines.paymentOut" },
            { title: "Transactions", list: "transaction", expression: "paymentOut.id" },
            { title: "Audits", list: "audit", expression: "entityIdentifier == PaymentOut and entityId" }
          ]}
          defaultDeleteDisabled
          customOnCreate
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
              href={`${window.location.origin}/invoice?search=amountOwing < 0`}
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
  getLockedDate: () => {
    dispatch(getAccountTransactionLockedDate());
  },
  getFilters: () => {
    dispatch(getFilters("PaymentOut"));
  },
  getAdministrationSites: () => dispatch(getAdministrationSites()),
  getAccounts: () => getPlainAccounts(dispatch),
  clearListState: () => dispatch(clearListState()),
  getActivePaymentOutMethods: () => dispatch(getActivePaymentOutMethods())
});

export default connect<any, any, any>(null, mapDispatchToProps)(withStyles(styles, { withTheme: true })(PaymentsOut));