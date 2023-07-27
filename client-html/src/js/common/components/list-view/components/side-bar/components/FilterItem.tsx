import * as React from "react";
import { createStyles, withStyles } from "@mui/styles";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import Delete from "@mui/icons-material/Delete";
import clsx from "clsx";
import FormControlLabel from "@mui/material/FormControlLabel";
import Tooltip from "@mui/material/Tooltip";
import { CheckBox, CheckBoxOutlineBlank, IndeterminateCheckBox } from "@mui/icons-material";
import { AppTheme } from  "ish-ui";

const styles = (theme: AppTheme) =>
  createStyles({
    checkbox: {
      height: "1em",
      width: "1em",
      marginLeft: ".6em",
      marginRight: theme.spacing(0.5),
    },
    checkboxFontSize: {
      fontSize: "18px"
    },
    labelRoot: {
      "& $checkboxLabel": {
        fontSize: "12px",
      }
    },
    checkboxLabel: {},
    root: {
      display: "flex",
      alignItems: "center",
      "&:hover $deleteButton": {
        visibility: "visible"
      },
      height: theme.spacing(3),
      maxHeight: theme.spacing(3),
    },
    deleteButton: {
      visibility: "hidden",
      fontSize: "20px",
      height: "30px",
      width: "30px",
      padding: `${theme.spacing(0.5)}`
    }
  });

const FilterItem = props => {
  const {
    classes, label, checked, onChange, index, onDelete, id, isPrivate, deletable, rootEntity, expression, customLabel
  } = props;

  const renderedLabel = customLabel ? customLabel() : label;

  return (
    <div className={classes.root}>
      <FormControlLabel
        classes={{
          root: clsx("flex-fill overflow-hidden", classes.labelRoot),
          label: clsx("overflow-hidden text-nowrap", classes.checkboxLabel),
        }}
        control={(
          <Checkbox
            checked={checked}
            onChange={(e, v) => onChange(index, v)}
            className={classes.checkbox}
            color="secondary"
            icon={<CheckBoxOutlineBlank className={classes.checkboxFontSize} />}
            checkedIcon={<CheckBox className={classes.checkboxFontSize} />}
            indeterminateIcon={<IndeterminateCheckBox className={classes.checkboxFontSize} />}
          />
        )}
        label={
          expression ? (
            <Tooltip title={expression} placement="right">
              <div className="text-truncate">{renderedLabel}</div>
            </Tooltip>
          ) : (
            renderedLabel
          )
        }
      />

      {deletable && (
        <Tooltip title="Delete Filter" placement="right">
          <IconButton className={classes.deleteButton} onClick={() => onDelete(id, rootEntity, checked, isPrivate)}>
            <Delete fontSize="inherit" color="secondary" />
          </IconButton>
        </Tooltip>
      )}
    </div>
  );
};

export default withStyles(styles)(FilterItem);
