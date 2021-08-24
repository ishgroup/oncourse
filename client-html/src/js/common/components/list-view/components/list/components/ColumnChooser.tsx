/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useRef, useState
} from "react";
import IconButton from "@material-ui/core/IconButton";
import Visibility from "@material-ui/icons/Visibility";
import ListItem from "@material-ui/core/ListItem";
import List from "@material-ui/core/List";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import Popover from "@material-ui/core/Popover";
import { COLUMN_WITH_COLORS } from "../utils";

const ColumnChooserItem = ({ classes, column, columnId, setShowColoredDots }) => {
  const customToggleHiddenProps = { ...column.getToggleHiddenProps() };

  if (columnId === COLUMN_WITH_COLORS) {
    const customOnChange = e => {
      column.toggleHidden(!e.target.checked);
      setShowColoredDots(e.target.checked);
    };

    customToggleHiddenProps.onChange = customOnChange;
  }

  return (
    <ListItem
      button
      classes={{ root: classes.columnChooserListItem }}
    >
      <FormControlLabel
        {...customToggleHiddenProps}
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
};

const ColumnChooserOverlay = props => {
  const {
    columns, target, visible, onHide, classes, setShowColoredDots
  } = props;

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
        {columns.map(column => (column.id !== "selection" && column.id !== "chooser" ? (
          <ColumnChooserItem
            key={column.id}
            column={column}
            classes={classes}
            setShowColoredDots={setShowColoredDots}
            columnId={column.id}
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
      <IconButton onClick={onToggle} ref={ref}>
        <Visibility />
      </IconButton>
    </div>
  );
});

const ColumnChooser = ({ classes, columns, setShowColoredDots }) => {
  const [visible, setVisible] = useState(false);
  const buttonRef = useRef<any>();

  return (
    <>
      <ColumnChooserOverlay
        visible={visible}
        target={buttonRef.current}
        columns={columns}
        classes={classes}
        setShowColoredDots={setShowColoredDots}
        onHide={() => setVisible(false)}
      />
      <ColumnChooserButton ref={buttonRef} className={classes.columnChooserButton} onToggle={() => setVisible(!visible)} />
    </>
  );
};

export default ColumnChooser;
