/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createStyles } from "@material-ui/core/styles";

export const formCommonStyles = theme => createStyles({
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
  }
});
