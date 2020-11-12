/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import clsx from "clsx";
import Typography from "@material-ui/core/Typography";
import * as React from "react";

const getValue = (tableRow, column, condition?) =>
  typeof condition === "function" ? condition(tableRow.row) : tableRow.row[column];

const ThreeColumnListRow = ({
  tableRow,
  selected,
  primary,
  primaryCondition,
  secondary,
  secondaryCondition,
  colspan,
  classes,
  onDoubleClick,
  onClick,
  customClasses
}) => (
  <tr className={clsx(selected && classes.selected, customClasses)} onClick={onClick} onDoubleClick={onDoubleClick}>
    <td colSpan={colspan} className={classes.cell}>
      <div className={classes.content}>
        <Typography variant="subtitle2" color="textSecondary" noWrap>
          {getValue(tableRow, secondary, secondaryCondition)}
        </Typography>
        <Typography variant="body1" noWrap>
          {getValue(tableRow, primary, primaryCondition)}
        </Typography>
      </div>
    </td>
  </tr>
);

export default ThreeColumnListRow;
