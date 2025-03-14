/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { alpha } from '@mui/material/styles';
import { AppTheme } from 'ish-ui';
import { HEADER_ROWS_COUNT, LIST_TWO_COLUMN_ROW_HEIGHT } from '../../../../../constants/Config';

export default (theme: AppTheme, p, classes) => ({
  infiniteLoaderListRoot: {
    "& > .resize-triggers": {
      display: "none"
    }
  },
  table: {
    background: theme.palette.background.paper,
    display: "flex",
    flexDirection: "column",
    width: "100%",
    height: "100%",
    overflow: "auto"
  },
  nestedTable: {
    background: theme.palette.background.paper,
    display: "flex",
    flexDirection: "column",
    overflow: "auto hidden",
    position: "relative",
    border: `1px solid ${theme.palette.divider}`,
    borderRadius: theme.shape.borderRadius
  },
  tableBody: {
    flex: "1 1 auto"
  },
  header: {
    display: "flex",
    position: "sticky",
    zIndex: 1,
    top: 0,
    height: `${LIST_TWO_COLUMN_ROW_HEIGHT * HEADER_ROWS_COUNT}px`,
    background: theme.palette.background.paper,
    borderBottom: `1px solid ${theme.palette.divider}`,
  },
  headerRow: {
    display: "flex",
    background: theme.palette.background.paper,
  },
  noSort: {
    cursor: "unset",
    lineHeight: "1.1rem"
  },
  headerCell: {
    display: "flex",
    fontSize: '0.75rem',
    position: "relative",
    padding: theme.spacing(2, 1),
    top: "1px",
    [`&:hover .${classes.resizer}`]: {
      opacity: 1
    }
  },
  draggableCellItem: {
    position: "relative",
    fontSize: '0.75rem',
    padding: theme.spacing(2, 1),
    transition: theme.transitions.create("padding", {
      duration: theme.transitions.duration.standard,
      easing: theme.transitions.easing.easeInOut
    }),
    "&:hover": {
      paddingLeft: theme.spacing(3),
      [`& .${classes.dragIndicator}`]: {
        visibility: "visible",
        fill: theme.palette.action.active
      }
    },
    [`&:hover .${classes.resizer}`]: {
      opacity: 1
    },
    [`&:hover:not(.${classes.activeRight}) .${classes.canSort}`]: {
      transition: theme.transitions.create("padding", {
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      }),
      paddingRight: theme.spacing(3)
    },
    [`&.${classes.activeRight} .${classes.rightSort}`]: {
      position: "static"
    },
    [`& .${classes.visibleDragIndicator}`]: {
      visibility: "visible",
      fill: theme.palette.action.active
    }
  },
  activeRight: {},
  rightAlighed: {},
  canSort: {
    cursor: "pointer"
  },
  isDragging: {
    boxShadow: theme.shadows[2],
    background: theme.palette.background.paper,
    transition: "none",
    paddingLeft: theme.spacing(3),
    [`&.${classes.rightAlighed}:not(.${classes.activeRight})`]: {
      paddingRight: theme.spacing(4)
    },
    [`&.${classes.rightAlighed}:has( .${classes.noSort})`]: {
      paddingRight: theme.spacing(1)
    }
  },
  dragIndicator: {
    cursor: "grab",
    position: "absolute",
    top: "50%",
    left: theme.spacing(-3),
    width: theme.spacing(3),
    height: theme.spacing(3),
    transform: "translateY(-50%)",
    visibility: "hidden"
  },
  visibleDragIndicator: {},
  bodyCell: {
    padding: theme.spacing(0.5, 1),
    whiteSpace: "nowrap",
    overflow: "hidden",
    textOverflow: "ellipsis",
    fontSize: "13px",
    fontWeight: 400,
    borderBottom: "none",
    [`&:hover .${classes.selectionCheckbox}`]: {
      display: "inline-flex",
    },
    [`&:hover .${classes.listDots}`]: {
      display: "none",
    }
  },
  row: {
    cursor: "pointer",
    display: "flex",
    [`&.${classes.selected}`]: {
      backgroundColor: theme.palette.action.selected,
      opacity: 1
    },
    [`&.${classes.selected} .${classes.selectionCheckbox}`]: {
      display: "inline-flex",
    },
    [`&.${classes.selected} .${classes.listDots}`]: {
      display: "none",
    },
    [`&:hover .${classes.deleteCell}, &.${classes.selected} .${classes.deleteCell}`]: {
      display: "inline-flex",
    },
  },
  deleteCell: {
    display: "none",
    fontSize: theme.spacing(2),
    padding: 6
  },
  oddRow: {
    backgroundColor: alpha(theme.palette.action.hover, 0.03)
  },
  threeColumnRow: {
    borderBottom: `1px solid ${theme.palette.divider}`,
    padding: theme.spacing(1, 2.5)
  },
  selectionCheckbox: {
    height: theme.spacing(1),
    width: theme.spacing(2.5),
    margin: 0,
    display: "none",
    position: "absolute",
    top: "50%",
    transform: "translateY(-50%)",
    left: "14px"
  },
  listDots: {
    height: "19px",
    margin: 0,
  },
  selected: {},
  resizer: {
    cursor: "col-resize",
    top: "50%",
    right: "3px",
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
    position: "fixed",
    right: theme.spacing(2),
    top: "1px",
    zIndex: theme.zIndex.appBar,
    color: theme.palette.text.secondary,
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
  },
  rightSort: {
    position: "absolute",
    right: 0
  }
} as const);
