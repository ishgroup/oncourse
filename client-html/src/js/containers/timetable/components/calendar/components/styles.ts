/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Theme } from "@mui/material";
import createStyles from "@mui/styles/createStyles";

const styles = (theme: Theme) =>
  createStyles({
    groupedDayWrapper: {
      "& .dayOffset:last-child": {
        marginBottom: theme.spacing(1)
      }
    },
    gapDayWrapper: {
      "& > $gapPeriodOffsetTop:first-child": {
        marginTop: 0
      }
    },
    gapDayOffsetTop: {
      paddingTop: "3px"
    },
    gapPeriodOffsetTop: {
      marginTop: theme.spacing(2)
    },
    stickyIcon: {
      position: "sticky",
      top: 35,
      "& path": {
        fill: `${theme.palette.text.primary}`
      }
    },
    root: {
      display: "grid"
    },
    sessions: {
      display: "grid",
      marginLeft: "32px",
      marginTop: "8px",
      gridRowGap: "8px"
    },
    codeLine: {
      display: "grid",
      gridAutoFlow: "column",
      gridColumnGap: "16px",
      justifyContent: "start",
      alignItems: "center"
    },
    expandButton: {
      padding: 0
    },
    expandIcon: {
      transition: "transform 200ms ease-in-out"
    },
    rotateIcon: {
      transform: "rotate(180deg)"
    }
  });

export default styles;
