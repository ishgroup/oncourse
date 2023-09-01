/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ButtonBase from "@mui/material/ButtonBase";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import React, { useCallback, useEffect } from "react";

const styles = theme =>
  createStyles({
    select: {
      border: "1px solid rgba(255,255,255,0.4)",
      padding: theme.spacing(0.25, 1),
      cursor: "pointer",
      "&:first-child": {
        borderRadius: theme.spacing(0.5, 0, 0, 0.5)
      },
      "&:last-child": {
        borderRadius: theme.spacing(0, 0.5, 0.5, 0)
      },
      "&:only-child": {
        borderRadius: theme.spacing(0.5)
      }
    },
    highlightSelect: {
      border: `1px solid ${theme.share.color.selectedItemText}`
    },
    selectTypography: {
      color: theme.share.color.itemText,
      fontSize: theme.spacing(1.5)
    },
    highlightedSelectTypography: {
      color: theme.share.color.selectedItemText
    }
  });

const SelectionSwitcher: React.FunctionComponent<any> = props => {
  const {
    classes, selectedRecords, allRecords, selectAll, setSelectAll, disabled
  } = props;

  const allRecordsText = allRecords !== null ? `${allRecords} found record${allRecords > 1 ? "s" : ""}` : "All records";

  const setAll = useCallback(() => {
    setSelectAll(true);
  }, [setSelectAll]);

  const setSelected = useCallback(() => {
    setSelectAll(false);
  }, [setSelectAll]);

  useEffect(() => {
    if (!selectAll && selectedRecords === 0) {
      setSelectAll(true);
    }
  }, [selectAll]);

  return (
    <div className={clsx("d-flex", disabled && "disabled")}>
      {selectedRecords > 0 && (
        <ButtonBase
          className={clsx(classes.select, selectAll ? "" : classes.highlightSelect)}
          onClick={setSelected}
          disabled={disabled}
        >
          <Typography className={clsx(classes.selectTypography, selectAll ? "" : classes.highlightedSelectTypography)}>
            {selectedRecords}
            {' '}
            selected record
            {selectedRecords > 1 ? "s" : ""}
          </Typography>
        </ButtonBase>
      )}

      <ButtonBase
        disabled={disabled}
        className={clsx(classes.select, selectAll ? classes.highlightSelect : "")}
        onClick={setAll}
      >
        <Typography className={clsx(classes.selectTypography, selectAll ? classes.highlightedSelectTypography : "")}>
          {allRecordsText}
        </Typography>
      </ButtonBase>
    </div>
  );
};

export default withStyles(styles)(SelectionSwitcher);
