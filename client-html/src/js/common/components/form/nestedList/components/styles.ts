/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import createStyles from "@mui/styles/createStyles";

export const listStyles = theme =>
  createStyles({
    root: {
      listStyle: "none",
      margin: 0,
      paddingTop: theme.spacing(1),
      marginBottom: theme.spacing(1)
    },
    root__height: {
      maxHeight: theme.spacing(50)
    },
    root__item: {
      display: "grid",
      gridTemplateColumns: "1fr auto",
      alignItems: "center",
      padding: theme.spacing(0, 1),
      "&:hover": {
        borderRadius: `${theme.shape.borderRadius}px`,
        background: theme.palette.action.hover
      },
      "&:hover $deleteButton": {
        opacity: 1
      }
    },
    dInline: {
      display: "inline"
    },
    chipsWrapper: {
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center"
    },
    deleteButton: {
      opacity: 0,
      borderRadius: `${theme.shape.borderRadius}px`,
      margin: theme.spacing(0, 1)
    },
    deleteIcon: {
      width: "0.75em",
      height: "0.75em",
      color: theme.palette.action.active
    },
    chips: {
      display: "inline",
      backgroundColor: theme.palette.divider,
      borderRadius: "100px",
      padding: theme.spacing(0, 1),
      fontSize: "0.8em"
    },
    fade: {
      opacity: 0.5
    },
    textRow: {
      display: "grid",
      gridTemplateColumns: "2fr 2fr",
      alignItems: "center",
      paddingRight: theme.spacing(1),
      flex: 1
    },
    button: {
      padding: 0,
      margin: theme.spacing(0, 1),
      fontSize: "0.75em",
      height: "20px",
      minWidth: theme.spacing(5)
    },
    selectRelationIdTextField: {
      paddingBottom: 0,
      "& > div > div > span": {
        fontSize: 14
      }
    }
  });
