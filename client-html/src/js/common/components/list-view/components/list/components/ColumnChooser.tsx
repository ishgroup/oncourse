/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Visibility from "@mui/icons-material/Visibility";
import { FormControlLabel, List, ListItemButton } from "@mui/material";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import Popover from "@mui/material/Popover";
import React, { useRef, useState } from "react";
import { CHOOSER_COLUMN, COLUMN_WITH_COLORS, SELECTION_COLUMN } from "../constants";

const ColumnChooserItem = ({
                             classes, column, onHiddenChange
                           }) => (<ListItemButton
  classes={{root: classes.columnChooserListItem}}
>
  <FormControlLabel
    className="w-100"
    classes={{
      root: classes.columnChooserLabel
    }}
    disabled={column.columnDef.disableVisibility}
    label={column.columnDef.title}
    control={(
      <Checkbox
        onChange={e => {
          column.getToggleVisibilityHandler()(e);
          onHiddenChange();
        }}
        checked={column.getIsVisible()}
        classes={{
          root: classes.columnChooserCheckbox
        }}
      />
    )}
  />
</ListItemButton>);

const ColumnChooserOverlay = ({
                                columns, target, visible, onHide, classes, onHiddenChange
                              }) => {
  let sortedColumns = [];
  const tagsColumn = columns.filter(column => column.id === COLUMN_WITH_COLORS);
  if (tagsColumn.length) {
    if (columns[0].id === "seletion") {
      sortedColumns.push(columns[0]);
      sortedColumns.push(tagsColumn[0]);
      sortedColumns = sortedColumns.concat(columns.filter(column => column.id !== COLUMN_WITH_COLORS && column.id !== SELECTION_COLUMN));
    } else {
      sortedColumns.push(tagsColumn[0]);
      sortedColumns = sortedColumns.concat(columns.filter(column => column.id !== COLUMN_WITH_COLORS && column.id !== SELECTION_COLUMN));
    }
  }

  const columnsForRender = sortedColumns.length ? sortedColumns : columns;

  return (
    <Popover
      open={visible}
      anchorEl={target}
      onClose={onHide}
      transformOrigin={{
        vertical: "bottom",
        horizontal: "right"
      }}
    >
      <List>
        {columnsForRender.map(column => (column.id !== SELECTION_COLUMN && column.id !== CHOOSER_COLUMN ? (
          <ColumnChooserItem
            key={column.id}
            column={column}
            classes={classes}
            onHiddenChange={onHiddenChange}
          />
        ) : null))}
      </List>
    </Popover>
  );
};

const ColumnChooserButton = React.forwardRef<any, any>(({className, onToggle}, ref) => (
  <div className={className}>
    <IconButton onClick={onToggle} ref={ref} size="large" color="inherit">
      <Visibility color="inherit"/>
    </IconButton>
  </div>
));

const ColumnChooser = ({classes, columns, onHiddenChange}) => {
  const [visible, setVisible] = useState(false);
  const buttonRef = useRef<any>();

  return (
    <>
      <ColumnChooserOverlay
        visible={visible}
        target={buttonRef.current}
        columns={columns}
        classes={classes}
        onHide={() => setVisible(false)}
        onHiddenChange={onHiddenChange}
      />
      <ColumnChooserButton ref={buttonRef} className={classes.columnChooserButton}
                           onToggle={() => setVisible(!visible)}/>
    </>
  );
};

export default ColumnChooser;