/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createStyles } from "@mui/material";
import { AppTheme } from "../../../../../ish-ui/model/Theme";

export default (theme: AppTheme) => createStyles({
  fileInput: {
    display: "none"
  },
  exportContainer: {
    height: "50vh",
    backgroundColor: theme.share.exportContainer.backgroundColor,
    backgroundImage: 'unset',
    padding: theme.spacing(2, 3, 3, 3),
    bottom: "64px",
    color: theme.share.color.selectedItemText,
    overflowY: "visible"
  },
  content: {
    height: "calc(100% - 64px)"
  },
  header: {
    height: "50px",
    marginBottom: theme.spacing(3)
  },
  headerText: {
    textTransform: "uppercase",
    fontSize: "14px",
    fontWeight: "bold" as "bold",
    color: theme.share.color.headerText
  },
  body: {
    height: "100%"
  },
  menuColumn: {
    paddingRight: theme.spacing(4),
    overflowY: "auto",
    overflowX: "hidden",
    height: "100%",
    position: "relative"
  },
  list: {
    padding: "0 8px 8px 0",
    minHeight: "100%",
    position: "relative",
    "&::after": {
      position: "absolute",
      right: 0,
      top: 0,
      bottom: 0,
      content: `" "`,
      borderRight: `1px solid ${theme.share.color.itemText}`,
      zIndex: 1201
    }
  },
  label: {
    color: theme.share.color.itemText
  },
  listItems: {
    color: theme.share.color.itemText,
    margin: theme.spacing(0, 0, 2, 0),
    padding: 0,
    cursor: "pointer",
    "&$listItemsSelected": {
      backgroundColor: "inherit"
    },
    "&:hover": {
      backgroundColor: "inherit"
    }
  },
  listItemsText: {
    fontWeight: "inherit",
    display: "block",
    marginRight: "24px"
  },
  listItemsSelected: {
    color: theme.share.color.selectedItemText,
    fontWeight: theme.typography.fontWeightMedium
  },
  formInputs: {
    minWidth: "120px",
    marginLeft: 0
  },
  closeButton: {
    background: "transparent",
    color: theme.share.closeButton.color,
    marginRight: "15px"
  },
  corner: {
    color: theme.share.exportContainer.backgroundColor,
    position: "absolute",
    transform: "rotate(90deg)",
    right: "77px",
    bottom: "-16px"
  },
  menuCorner: {
    width: theme.spacing(2),
    height: theme.spacing(2),
    borderLeft: `1px solid ${theme.share.color.itemText}`,
    borderBottom: `1px solid ${theme.share.color.itemText}`,
    transform: "rotate(45deg)",
    position: "absolute",
    right: "-15px",
    background: theme.share.exportContainer.backgroundColor,
    zIndex: 1202
  },
  hide1024andDown: {
    display: "none"
  },
  closeShareButtons: {
    display: "flex",
    justifyContent: "flex-end",
    alignItems: "center",
    flex: "0 auto"
  },
  form: {
    height: "100%",
    display: "flex",
    flexWrap: "nowrap",
    flexDirection: "column",
    justifyContent: "space-between"
  },
  formContent: {
    marginBottom: "16px",
    marginLeft: theme.spacing(-1),
    paddingLeft: theme.spacing(1)
  },
  shareButton: {
    color: theme.share.shareButton.color,
    backgroundColor: theme.share.shareButton.backgroundColor,
    marginLeft: theme.spacing(1),
    "&:hover": {
      backgroundColor: theme.share.shareButton.backgroundColorHover
    }
  },
  text: {
    color: theme.share.color.selectedItemText
  },
  customLabel: {
    color: theme.share.color.customLabel
  },
  previewWrapper: {
    paddingLeft: theme.spacing(2)
  },
  createPreviewCheckbox: {
    width: "32px",
    height: "32px",
    margin: "4px"
  },
  listbox: {
    maxHeight: "20vh"
  }
});
