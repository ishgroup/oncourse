/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useRef, useState
} from "react";
import IconButton from "@mui/material/IconButton";
import Visibility from "@mui/icons-material/Visibility";
import ListItem from "@mui/material/ListItem";
import List from "@mui/material/List";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Popover from "@mui/material/Popover";
import { COLUMN_WITH_COLORS } from "../utils";

const ColumnChooserItem = ({
 classes, column
}) => (
  <ListItem
    button
    classes={{ root: classes.columnChooserListItem }}
  >
    <FormControlLabel
      {...column.getToggleHiddenProps()}
      className="w-100"
      classes={{
          root: classes.columnChooserLabel
      }}
      disabled={column.disableVisibility}
      label={column.Header}
      control={(
        <Checkbox
          classes={{
              root: classes.columnChooserCheckbox
          }}
        />
      )}
    />
  </ListItem>
  );

const ColumnChooserOverlay = props => {
  const {
    columns, target, visible, onHide, classes
  } = props;

  let sortedColumns = [];
  const tagsColumn = columns.filter(column => column.id === COLUMN_WITH_COLORS);
  if (tagsColumn.length) {
    if (columns[0].id === "seletion") {
      sortedColumns.push(columns[0]);
      sortedColumns.push(tagsColumn[0]);
      sortedColumns = sortedColumns.concat(columns.filter(column => column.id !== COLUMN_WITH_COLORS && column.id !== "seletion"));
    } else {
      sortedColumns.push(tagsColumn[0]);
      sortedColumns = sortedColumns.concat(columns.filter(column => column.id !== COLUMN_WITH_COLORS && column.id !== "seletion"));
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
        {columnsForRender.map(column => (column.id !== "selection" && column.id !== "chooser" ? (
          <ColumnChooserItem
            key={column.id}
            column={column}
            classes={classes}
          />
        ) : null))}
      </List>
    </Popover>
  );
};

const ColumnChooserButton = React.forwardRef<any, any>((props, ref) => {
  const { className, onToggle } = props;

  return (
    <div className={className}>
      <IconButton onClick={onToggle} ref={ref} size="large" color="inherit">
        <Visibility color="inherit" />
      </IconButton>
    </div>
  );
});

const ColumnChooser = ({ classes, columns }) => {
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
      />
      <ColumnChooserButton ref={buttonRef} className={classes.columnChooserButton} onToggle={() => setVisible(!visible)} />
    </>
  );
};

export default ColumnChooser;
