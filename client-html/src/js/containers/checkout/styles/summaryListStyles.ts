/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import createStyles from "@material-ui/core/styles/createStyles";
import { AppTheme } from "../../../model/common/Theme";

export const summaryListStyles = (theme: AppTheme) => createStyles({
  itemTotal: {
    paddingRight: "50px !important"
  },
  topRightlabel: {
    paddingRight: "38px"
  },
  root: {
    paddingTop: "10px",
    paddingBottom: "10px"
  },
  panel: {
    boxShadow: "none",
    width: "100%"
  },
  tableTab: {
    padding: theme.spacing(0, 1),
    borderRadius: theme.shape.borderRadius,
    "&:nth-of-type(odd)": {
      background: theme.table.contrastRow.main
    },
    "&:last-child": {
      background: "inherit"
    },
    minHeight: theme.spacing(6)
  },
  headerItem: {
    paddingLeft: "22px"
  },
  summaryItemPrice: {
    paddingRight: 30
  },
  originalPrice: {
    textDecoration: "line-through",
    marginRight: 3,
    color: "rgba(0, 0, 0, 0.56)"
  },
  summaryPanelRoot: {
    "&:before": {
      height: "0px !important"
    }
  },
  expansionSummaryRoot: {
    padding: 0
  },
  expansionSummaryContent: {
    margin: "0px !important"
  },
  expandIcon: {
    padding: theme.spacing(0.5),
    marginRight: -5,
    marginLeft: 2,
    "&:hover": {
      backgroundColor: theme.palette.action.hover
    }
  },
  panelExpanded: {
    margin: "0px !important",
    borderRadius: theme.shape.borderRadius,
    marginTop: "5px !important"
  },
  classQuantity: {
    paddingRight: 3
  },
  payerChip: {
    marginRight: theme.spacing(2),
    padding: theme.spacing(0, 1.5)
  },
  setPayerChip: {
    marginRight: theme.spacing(2),
    padding: theme.spacing(0, 1.5),
    display: "none",
    transition: "all 0.2s ease-in-out"
  },
  payerChipLabel: {
    fontWeight: "bolder"
  },
  summaryExpansionPanel: {
    "&:hover $setPayerChip": {
      display: "flex"
    }
  },
  itemTitle: {
    marginLeft: -11
  },
  itemPriceInput: {
    maxWidth: theme.spacing(6)
  },
  discountPlaceholder: {
    color: "inherit"
  }
});
