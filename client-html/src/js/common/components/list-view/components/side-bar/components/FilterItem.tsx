import * as React from "react";
import { createStyles, withStyles } from "@mui/styles";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import Delete from "@mui/icons-material/Delete";
import clsx from "clsx";
import FormControlLabel from "@mui/material/FormControlLabel";
import Tooltip from "@mui/material/Tooltip";
import { CheckBox, CheckBoxOutlineBlank, IndeterminateCheckBox } from "@mui/icons-material";
import { AppTheme } from "../../../../../../model/common/Theme";

const styles = (theme: AppTheme) =>
  createStyles({
    checkbox: {
      height: "1em",
      width: "1em",
      marginLeft: ".3em",
    },
    checkboxFontSize: {
      fontSize: "18px"
    },
    checkboxLabel: {
      fontSize: "12px",
    },
    root: {
      display: "flex",
      alignItems: "center",
      "&:hover $deleteButton": {
        visibility: "visible"
      },
      maxHeight: theme.spacing(3)
    },
    deleteButton: {
      visibility: "hidden",
      fontSize: "20px",
      height: "30px",
      width: "30px",
      padding: `${theme.spacing(0.5)}px`
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
          label: clsx("overflow-hidden text-nowrap", classes.checkboxLabel),
        }}
        className="flex-fill overflow-hidden"
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
            <Tooltip title={expression}>
              <div className="text-truncate">{renderedLabel}</div>
            </Tooltip>
          ) : (
            renderedLabel
          )
        }
      />

      {deletable && (
        <Tooltip title="Delete Filter">
          <IconButton className={classes.deleteButton} onClick={() => onDelete(id, rootEntity, checked, isPrivate)}>
            <Delete fontSize="inherit" color="secondary" />
          </IconButton>
        </Tooltip>
      )}
    </div>
  );
};

export default withStyles(styles)(FilterItem);
