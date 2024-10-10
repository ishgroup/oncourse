/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { alpha } from '@mui/material';
import { AppTheme } from 'ish-ui';

export const styles = (theme: AppTheme, p, classes) =>({
  dragIcon: {
    margin: theme.spacing(0, 2),
    color: theme.palette.action.focus,
    "&:hover": {
      color: theme.palette.action.active
    }
  },
  actionButton: {
    marginRight: "10px"
  },
  actionIcon: {
    color: theme.palette.action.active,
    fontSize: "20px"
  },
  actionIconInactive: {
    color: theme.palette.action.focus,
    fontSize: "20px"
  },
  cardRoot: {
    paddingTop: theme.spacing(1),
  },
  card: {
    zIndex: 1,
    borderRadius: `${theme.shape.borderRadius}px`,
    cursor: "pointer",
    backgroundColor: alpha(theme.palette.text.primary, 0.025),
    [`&:hover .${classes.actionIconInactive}`]: {
      color: theme.palette.action.focus
    }
  },
  cardGrid: {
    gridTemplateColumns: "auto auto 1fr 1fr auto auto auto",
    display: "grid",
    alignItems: "center",
  },
  checklistCardGrid: {
    gridTemplateColumns: "auto 1fr auto",
    display: "grid",
    alignItems: "center",
  },
  dragOver: {
    boxShadow: theme.shadows[2]
  },
  tagColorDot: {
    width: "1em",
    height: "1em",
    borderRadius: "100%"
  },
  legend: {
    gridTemplateColumns: "1fr 1fr 108px",
    display: "grid",
    alignItems: "center",
    paddingLeft: "94px",
    marginBottom: theme.spacing(1)
  },
  fieldEditable: {
    paddingRight: theme.spacing(2),
    position: "relative",
    top: 2
  },
  nameEditable: {
    fontSize: "14px",
    fontWeight: 500
  },
  urlEditable: {
    fontSize: "14px",
  }
} as const);