/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { AppTheme } from 'ish-ui';

export const styles = (theme: AppTheme) => ({
  dragIcon: {
    fill: "#e0e0e0"
  },
  container: {
    width: "100%"
  },
  expansionPanelRoot: {
    margin: "0px !important",
    width: "100%",
  },
  expansionPanelDetails: {
    padding: 0,
    marginTop: `-${theme.spacing(9.75)}`,
  },
  expandIcon: {
    marginTop: -5,
    "& > button": {
      padding: 5,
    }
  },
  deleteButtonCustom: {
    padding: theme.spacing(1),
    marginTop: -5,
  }
});