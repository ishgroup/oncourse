/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import createStyles from "@mui/styles/createStyles";
import { AppTheme } from "../../../../../../../ish-ui/model/Theme";

const styles = (theme: AppTheme) => createStyles({
  content: {
    marginTop: theme.spacing(8)
  },
  payerCardMargin: {
    margin: "5px 5px 10px 5px"
  },
  payerLastCardMargin: {
    margin: "5px"
  },
  iframe: {
    border: "none"
  },
  iframeWrapper: {
    maxWidth: "400px",
    margin: "0 auto",
    height: "550px"
  },
  paymentDetails: {
    maxWidth: "390px"
  },
  cardLabelPadding: {
    padding: "2px 0px"
  },
  fieldCardRoot: {
    width: "100%",
    overflow: "visible",
    background: "#faf9f9",
    border: "1px solid #ebebeb",
    borderBottom: "2px solid #c0c0c0"
  },
  contentRoot: {
    fontFamily: "Arial, Helvetica, sans-serif",
    "-webkit-font-smoothing": "auto",
    color: "#333",
    fontSize: "14px",
    padding: "10px 15px 20px 15px",
    "& h1": {
      margin: "5px 0 5px 0",
      fontSize: "20px",
      fontWeight: 500
    },
  },
  legend: {
    width: "40%",
    whiteSpace: "nowrap"
  },
  legendLastMargin: {
    marginBottom: "40px",
  },
  payButton: {
    color: "#fff",
    width: "70%",
    margin: "15px auto 8px",
    borderRadius: "2px",
    border: "none",
    backgroundColor: "#000"
  }
});

export default styles;
