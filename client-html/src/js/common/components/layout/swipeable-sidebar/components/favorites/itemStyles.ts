/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import createStyles from "@mui/styles/createStyles";

const itemStyles = createStyles(theme => ({
  quickEnrollItem: {
    lineHeight: "1.46429em"
  },
  scriptIcon: {
    fontSize: theme.spacing(2),
    verticalAlign: "text-bottom"
  },
  listItem: {
    lineHeight: "1.2em",
    padding: "4px 0",
    display: "block",
    "-moz-column-break-inside": "avoid",
    "-webkit-column-break-inside": "avoid",
    "column-break-inside": "avoid",
    "&:hover .invisible": {
      visibility: "visible"
    },
    "&$listItemEditing": {
      display: "inline-flex"
    },
    "&.d-none": {
      display: "none"
    }
  },
  listItemIcon: {
    color: theme.palette.text.primary,
    fontSize: theme.spacing(2),
    marginRight: theme.spacing(0.5),
    cursor: "pointer"
  },
  listItemIconActive: {
    color: theme.palette.primary.main
  },
  listItemEditing: {},
  listItemContent: {
    display: "inline-block"
  }
}));

export default itemStyles;
