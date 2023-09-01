/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Delete from "@mui/icons-material/Delete";
import DragIndicator from "@mui/icons-material/DragIndicator";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import * as React from "react";
import { Field } from "redux-form";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { ToogleCheckbox } from "../../../../../common/components/form/ToogleCheckbox";

const styles = () => createStyles({
  card: {
    margin: "6px 0",
    padding: "6px 0",
    borderRadius: "4px"
  },
  dragIcon: {
    margin: "0 10px 0 20px",
    fill: "#e0e0e0"
  },
  chip: {
    margin: "0 30px",
    minWidth: "8em",
    height: "26px"
  },
  textFieldsContainer: {
    flex: "2",
    marginLeft: "10px",
    marginRight: "50px"
  },
  deleteButton: {
    marginRight: "10px"
  },
  deleteIcon: {
    fontSize: "20px"
  },
  helpText: {
    marginLeft: "1px"
  }
});

const CollectionFormField = props => {
  const {
    classes, item, onDelete, field, className
  } = props;

  return (
    <div
      className={clsx({
        [clsx("centeredFlex", classes.card)]: true,
        [className]: true
      })}
    >
      <DragIndicator
        className={clsx({
          "dndActionIcon": true,
          [clsx("d-flex", classes.dragIcon)]: true
        })}
      />

      <div className={clsx("mw-100 overflow-hidden", classes.textFieldsContainer)}>
        <FormField
          type="text"
          name={`${field}.label`}
          label="Label"
          required
         />

        <FormField
          type="multilineText"
          name={`${field}.helpText`}
          label="Help Text"
          className="mt-1"
          truncateLines={4}
        />
      </div>

      <Typography id={`${field}.type`} variant="subtitle2" color="textSecondary" className="flex-fill">
        {item.type.label}
      </Typography>

      <Field
        name={`${field}.mandatory`}
        label="Label"
        margin="none"
        type="checkbox"
        chackedLabel="Mandatory"
        uncheckedLabel="Optional"
        component={ToogleCheckbox}
        className={classes.chip}
      />

      <IconButton className={clsx(classes.deleteButton, "dndActionIconButton")} onClick={onDelete}>
        <Delete
          className={clsx({
            [classes.deleteIcon]: true,
            "dndActionIcon": true
          })}
        />
      </IconButton>
    </div>
  );
};

export default withStyles(styles)(CollectionFormField);