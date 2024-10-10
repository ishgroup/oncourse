/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { darken } from "@mui/material";
import { createStyles } from "@mui/styles";
import { AppTheme } from "ish-ui";

export const formCommonStyles = (theme: AppTheme) => createStyles({
  marginTop: {
    marginTop: "20px"
  },
  checkbox: {
    height: "50px",
    display: "flex"
  },
  deleteButton: {
    right: 0,
    top: 0,
    color: theme.palette.error.main,
    position: "absolute"
  },
  field: {
    paddingRight: "20px"
  },
});

export const cardsFormStyles = (theme: AppTheme) => createStyles({
  marginTop: {
    marginTop: "20px"
  },
  checkbox: {
    height: "50px",
    display: "flex"
  },
  deleteButton: {
    right: 0,
    color: theme.palette.error.main,
    position: "absolute"
  },
  field: {
    paddingRight: "20px"
  },
  adornmentLabelForwardArrow: {
    top: 4
  },
  shoppingCartActionBox: {
    backgroundColor: darken(theme.palette.background.paper, 0.05),
    borderRadius: theme.shape.borderRadius,
    marginLeft: 0,
    paddingRight: theme.spacing(2)
  },
  fromNameLabel: {
    "& > span": {
      paddingRight: 14
    }
  },
  fromNameLabelOnEdit: {
    paddingRight: 17
  },
});
