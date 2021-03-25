/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { withStyles, createStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import Typography from "@material-ui/core/Typography";
import Delete from "@material-ui/icons/Delete";
import DragIndicator from "@material-ui/icons/DragIndicator";
import IconButton from "@material-ui/core/IconButton";
import { Field } from "redux-form";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { PillCheckboxField } from "../../../../../common/components/form/PillCheckbox";

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
          hideLabel
          listSpacing={false}
          formatting="primary"
          required
          fullWidth
        />

        <FormField
          type="multilineText"
          name={`${field}.helpText`}
          label="Help Text"
          hideLabel
          listSpacing={false}
          formatting="secondary"
          truncateLines={4}
          fullWidth
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
        component={PillCheckboxField}
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
