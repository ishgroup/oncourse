/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createStyles, fade } from "@material-ui/core/styles";
import { AppTheme } from "../../../../../model/common/Theme";

export default (theme: AppTheme) => createStyles({
  infiniteLoaderListRoot: {
    "& > .resize-triggers": {
      display: "none"
    }
  },
  table: {
    background: theme.palette.background.paper,
    display: "flex",
    flexDirection: "column",
    overflow: "auto",
    height: "100%",
    position: "relative"
  },
  nestedTable: {
    background: theme.palette.background.paper,
    display: "flex",
    flexDirection: "column",
    overflow: "auto hidden",
    position: "relative",
    border: `1px solid ${theme.palette.divider}`,
  },
  tableBody: {
    flex: "1 1 auto"
  },
  header: {
    display: "flex",
    position: "sticky",
    zIndex: 1,
    top: 0,
    borderBottom: `1px solid ${theme.palette.divider}`,
    background: theme.palette.background.paper
  },
  headerCell: {
    position: "relative",
    padding: theme.spacing(2, 1),
    top: "1px",
    "&:hover $resizer": {
      opacity: 1
    },
    "& $draggingCell,&:focus $draggingCell": {
      color: theme.palette.divider
    }
  },
  draggingCell: {},
  listHeaderCell: {
    "&:hover": {
      "& $tableSortLabel": {
        opacity: 0.5
      }
    }
  },
  headerCellItem: {
    padding: theme.spacing(2, 1),
    zIndex: 1
  },
  headerRow: {
    display: "flex",
    background: theme.palette.background.paper
  },
  noSort: {
    lineHeight: "1.1rem"
  },
  draggableCellItem: {
    position: "relative",
    transition: theme.transitions.create("padding", {
      duration: theme.transitions.duration.standard,
      easing: theme.transitions.easing.easeInOut
    }),
    "&:hover": {
      paddingLeft: 24,
      "& $dragIndicator": {
        visibility: "visible",
        fill: theme.palette.action.active
      }
    },
    "& $visibleDragIndicator": {
      visibility: "visible",
      fill: theme.palette.action.active
    }
  },
  dragOver: {
    padding: theme.spacing(2, 0),
    paddingLeft: 24,
    boxShadow: theme.shadows[2],
    background: theme.palette.background.paper
  },
  dragIndicator: {
    position: "absolute",
    top: "50%",
    left: 0,
    width: 24,
    height: 24,
    transform: "translateY(-50%)",
    visibility: "hidden"
  },
  visibleDragIndicator: {},
  tableSortLabel: {},
  bodyCell: {
    padding: theme.spacing(0.5, 1),
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
    fontSize: "13px",
    borderBottom: "none",
    "&:hover $selectionCheckbox": {
      visibility: "visible"
    }
  },
  row: {
    "&$selected": {
      backgroundColor: theme.palette.action.selected,
      opacity: 1
    },
    "&$selected $selectionCheckbox": {
      visibility: "visible"
    },
    cursor: "pointer"
  },
  oddRow: {
    backgroundColor: fade(theme.palette.action.hover, 0.03)
  },
  threeColumnRow: {
    borderBottom: `1px solid ${theme.palette.divider}`,
    padding: theme.spacing(1, 2.5)
  },
  selectionCheckbox: {
    height: theme.spacing(1),
    width: theme.spacing(2.5),
    margin: 0,
    visibility: "hidden"
  },
  selected: {},
  resizer: {
    top: "50%",
    right: "-3px",
    width: "3px",
    height: "50%",
    transform: "translateY(-50%)",
    zIndex: 2,
    position: "absolute",
    userSelect: "none",
    opacity: 0,
    transition: theme.transitions.create("opacity", {
      duration: theme.transitions.duration.shorter,
      easing: theme.transitions.easing.easeInOut
    }),
    borderRight: `1px solid ${theme.palette.primary.main}`,
    borderLeft: `1px solid ${theme.palette.primary.main}`
  },
  columnChooserButton: {
    position: "absolute",
    right: theme.spacing(2),
    top: "1px",
    zIndex: theme.zIndex.appBar,
    backgroundColor: theme.palette.background.paper,
    borderRadius: "100%"
  },
  columnChooserCheckbox: {
    height: "20px",
    width: "20px",
    marginRight: theme.spacing(2)
  },
  columnChooserListItem: {
    padding: 0
  },
  columnChooserLabel: {
    margin: 0,
    padding: theme.spacing(0.25, 1.5)
  },
  cellButton: {
    height: "20px",
    width: "20px",
    pointerEvents: "all",
    margin: 0
  },
  cellLinkIcon: {
    fontSize: "14px",
    margin: "1px 0 0 1px"
  },
  hideOverflowY: {
    overflowY: "hidden"
  }
});
