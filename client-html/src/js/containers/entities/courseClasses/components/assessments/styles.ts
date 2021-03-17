/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { createStyles } from "@material-ui/core/styles";
import { AppTheme } from "../../../../../model/common/Theme";

export default (theme: AppTheme) =>
  createStyles({
    rowWrapper: {
      minHeight: "36px",
      padding: "0 8px",
    },
    items: {
      marginLeft: -8,
      marginRight: -8,
      "& > div:nth-child(even)": {
        backgroundColor: theme.table.contrastRow.light
      },
      "&:first-child": {
        marginTop: 0
      }
    },
    tableHeader: {
      marginLeft: -8,
      marginRight: -8,
    },
    center: {
      display: "flex",
      justifyContent: "center",
      "&:hover $hiddenIcon, &:hover $hiddenTitleIcon": {
        visibility: "visible",
      }
    },
    hiddenIcon: {
      visibility: "hidden",
      position: "absolute",
      transform: "translate(calc(100% + 2px),-2px)",
      padding: 0
    },
    hiddenTitleIcon: {
      visibility: "hidden",
      position: "absolute",
      bottom: "-3px"
    }
  });
