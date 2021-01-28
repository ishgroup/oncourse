import * as React from "react";
import { createStyles, withStyles } from "@material-ui/core/styles";
import Checkbox from "@material-ui/core/Checkbox";
import IconButton from "@material-ui/core/IconButton";
import Delete from "@material-ui/icons/Delete";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Tooltip from "@material-ui/core/Tooltip";
import { CheckBox, CheckBoxOutlineBlank, IndeterminateCheckBox } from "@material-ui/icons";
import { AppTheme } from "../../../../../../model/common/Theme";

const styles = (theme: AppTheme) =>
  createStyles({
    checkbox: {
      height: "1em",
      width: "1em",
      marginLeft: ".3em"
    },
    checkboxFontSize: {
      fontSize: "18px"
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
          label: "overflow-hidden text-nowrap"
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
