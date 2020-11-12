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
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";

const styles = theme =>
  createStyles({
    card: {
      margin: "6px 0",
      padding: "6px 0",
      borderRadius: "4px"
    },
    dragIcon: {
      margin: "0 10px 0 20px"
    },
    title: {
      fontFamily: theme.typography.fontFamily,
      fontSize: "14px",
      color: theme.palette.secondary.main,
      "&:hover": {
        color: theme.palette.primary.main,
        fill: theme.palette.primary.main
      }
    },
    titleControls: {
      marginLeft: "10px"
    },
    deleteButton: {
      marginLeft: "20px",
      marginRight: "10px"
    },
    deleteIcon: {
      fontSize: "20px"
    }
  });

const validateUniqueNames = (value, allValues) => {
  const match = allValues.items.filter(
    item => item.baseType === "heading" && (item.name && item.name.trim() === value.trim())
  );
  return match.length > 1 ? "Heading name must be unique" : undefined;
};

const CollectionFormHeading = props => {
  const {
    classes, item, field, onDelete, className
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

      <div className={clsx("align-items-center flex-fill mw-100 overflow-hidden", classes.titleControls)}>
        <FormField
          type="text"
          name={`${field}.name`}
          label="Heading"
          hideLabel
          listSpacing={false}
          formatting="custom"
          editableComponent={(
            <Typography className={clsx("text-uppercase text-bold mw-100 overflow-hidden", classes.title)}>{item.name}</Typography>
          )}
          validate={[validateSingleMandatoryField, validateUniqueNames]}
          fullWidth
        />

        <FormField
          type="multilineText"
          name={`${field}.description`}
          label="Description"
          hideLabel
          listSpacing={false}
          formatting="secondary"
          truncateLines={4}
          fullWidth
        />
      </div>

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

export default withStyles(styles)(CollectionFormHeading);
