/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import IconButton from "@mui/material/IconButton";
import Launch from "@mui/icons-material/Launch";
import Share from "@mui/icons-material/Share";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import CancelIcon from "@mui/icons-material/Cancel";
import DoneRounded from "@mui/icons-material/DoneRounded";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import { LinkAdornment } from "../../../../../../ish-ui/formFields/FieldAdornments";
import { openInternalLink } from "../../../../../common/utils/links";
import { CheckoutPayment, CheckoutSummary } from "../../../../../model/checkout";
import { AppTheme } from "../../../../../../ish-ui/model/Theme";
import CheckoutPaymentExpandableItemRenderer from "./CheckoutPaymentExpandableItemRenderer";
import { getContactFullName } from "../../../../entities/contacts/utils";

const styles = (theme: AppTheme) =>
  createStyles({
    root: {
      width: "100%",
      textAlign: "center",
      padding: 70,
      paddingTop: 30,
      marginTop: 70
    },
    iconsContainer: {
      marginTop: "25px",
      marginBottom: "25px"
    },
    icon: {
      width: "70px",
      height: "70px"
    },
    iconSuccess: {
      color: "green",
      border: "3px solid",
      borderRadius: "100%",
      padding: theme.spacing(1)
    },
    button: {
      margin: "3px"
    },
    successExpansionPanelRoot: {
      paddingTop: 10,
      paddingBottom: 10,
      marginBottom: 0,
      width: "100%"
    },
    tableTab: {
      padding: theme.spacing(0, 2),
      borderRadius: theme.shape.borderRadius,
      "&:nth-of-type(odd)": {
        background: theme.table.contrastRow.main
      },
      minHeight: theme.spacing(6)
    },
    itemTitle: {
      marginLeft: -11
    },
    loader: {
      top: 0,
      left: 0,
      opacity: 1,
      height: "100vh",
      background: "rgba(0, 0, 0, 0.5)"
    },
    paymentIdSection: {
      marginTop: 10,
      marginBottom: 10,
      textAlign: "left"
    },
    paymentIdPaper: {
      paddingTop: 12,
      paddingBottom: 12
    }
  });

interface PaymentMessageRendererProps {
  summary?: CheckoutSummary;
  payment?: CheckoutPayment;
  classes?: any;
  tryAgain?: () => void;
  validatePayment?: boolean;
}

const CancelPaymentMessage: React.FC<any> = props => {
  const { classes, tryAgain } = props;

  return (
    <div className={classes.root}>
      <div>
        <Typography variant="h5">Payment cancelled</Typography>
        <div className={classes.iconsContainer}>
          <CancelIcon className={clsx(classes.icon, "rotateOutCustom")} color="error" />
        </div>
      </div>
      <div className="flex-column align-items-center">
        <Button color="primary" className={clsx("submitButton", classes.button)} onClick={tryAgain}>
          Try again
        </Button>
      </div>
    </div>
  );
};

const FailedPaymentMessage: React.FC<any> = props => {
  const {
    classes, tryAgain, validatePayment, payment
  } = props;

  return (
    <div className={classes.root}>
      <div>
        <Typography variant="h5">Failed Payment</Typography>
        <div className={classes.iconsContainer}>
          <CancelIcon className={clsx("rotateOutCustom", classes.icon)} color="error" />
        </div>
      </div>
      <Typography variant="subtitle1">
        Reason:
        {" "}
        {payment.process.data && payment.process.data.responseText}
      </Typography>

      {(validatePayment || (!validatePayment && !(/(4|5)+/.test(payment.process.statusCode)))) && (
        <div className="flex-column align-items-center">
          <Button color="primary" className={clsx("submitButton", classes.button)} onClick={tryAgain}>
            Try again
          </Button>
        </div>
      )}
    </div>
  );
};

const SuccessPaymentMessage: React.FC<any> = props => {
  const { classes, summary, payment } = props;

  const onInvoiceLinkClick = React.useCallback(e => {
    e.stopPropagation();
    e.preventDefault();
    if (payment.invoice) openInternalLink(`/invoice/${payment.invoice.id}`);
  }, [payment.invoice]);

  const onInvoiceShareLinkClick = React.useCallback(e => {
    e.stopPropagation();
    e.preventDefault();
    if (payment.invoice) openInternalLink(`/invoice?openShare=true&search=id is ${payment.invoice.id}`);
  }, [payment.invoice]);

  const invoiceNumber = payment.invoice ? payment.invoice.invoiceNumber : null;

  return (
    <div className={clsx(classes.root, "mt-0")}>
      <div>
        <div className={classes.iconsContainer}>
          <DoneRounded className={clsx(classes.icon, classes.iconSuccess, "rotateOutCustom")} />
        </div>
        <Typography variant="h5">Transaction successful</Typography>
      </div>
      <div>
        <Grid container className="p-3 align-content-between">
          {payment.invoice && (
          <Grid item xs={12}>
            {summary.list.map((list, i) => (
              <CheckoutPaymentExpandableItemRenderer
                key={i}
                classes={classes}
                header={list.payer ? (
                  <>
                    {`Invoice ${invoiceNumber} to ${getContactFullName(list.contact)}`}
                    <span className="appHeaderFontSize ml-1">
                      <IconButton
                        disabled={payment.invoice && !payment.invoice.id}
                        onClick={onInvoiceLinkClick}
                        onFocus={e => e.stopPropagation()}
                        color="inherit"
                        classes={{
                                root: "inputAdornmentButton"
                              }}
                      >
                        <Launch className="inputAdornmentIcon" color="inherit" />
                      </IconButton>
                    </span>
                    <span className="appHeaderFontSize ml-1">
                      <IconButton
                        disabled={payment.invoice && !payment.invoice.id}
                        onClick={onInvoiceShareLinkClick}
                        onFocus={e => e.stopPropagation()}
                        color="inherit"
                        classes={{
                                root: "inputAdornmentButton"
                              }}
                      >
                        <Share className="inputAdornmentIcon" color="inherit" />
                      </IconButton>
                    </span>
                  </>
                      ) : getContactFullName(list.contact)}
                items={list.items}
                isPayer={list.payer}
                voucherItems={summary.voucherItems}
                invoiceLine={payment.invoice && payment.invoice.invoiceLines.filter(l => l.contactId === list.contact.id)}
              />
                  ))}
          </Grid>
              )}
          {payment.paymentId && (
          <Grid item xs={12} className={classes.paymentIdSection}>
            <Paper elevation={0} className={clsx("pl-3 pr-3", classes.paymentIdPaper)}>
              <Typography variant="caption" className="heading mt-1 mb-1 mr-2">
                {`Payment ID: ${payment.paymentId}`}
                <LinkAdornment
                  linkColor="inherit"
                  linkHandler={() => openInternalLink(`/paymentIn/${payment.paymentId}`)}
                  link={payment.paymentId}
                  className="appHeaderFontSize ml-1"
                />
              </Typography>
            </Paper>
          </Grid>
              )}
        </Grid>
      </div>
    </div>
  );
};

const PaymentMessageRenderer: React.FC<PaymentMessageRendererProps> = props => {
  const { payment } = props;

  const render = () => {
    switch (payment.process.status) {
      case "cancel":
        return <CancelPaymentMessage {...props} />;
      case "fail":
        return <FailedPaymentMessage {...props} />;
      case "success":
        return <SuccessPaymentMessage {...props} />;
      default:
        return null;
    }
  };

  return (
    <>
      {!payment.isProcessing && !payment.isFetchingDetails && render()}
    </>
  );
};

export default withStyles(styles)(PaymentMessageRenderer);