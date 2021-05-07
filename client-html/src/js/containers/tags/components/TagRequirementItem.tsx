import React, { useCallback } from "react";
import clsx from "clsx";
import { change, Field } from "redux-form";
import { withStyles, createStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import Delete from "@material-ui/icons/Delete";
import IconButton from "@material-ui/core/IconButton";
import { Dispatch } from "redux";
import { PillCheckboxField } from "../../../common/components/form/PillCheckbox";
import GetTagRequirementDisplayName from "../utils/GetTagRequirementDisplayName";
import { ShowConfirmCaller } from "../../../model/common/Confirm";

const styles = () => createStyles({
  deleteButton: {
    marginRight: "10px"
  },
  deleteIcon: {
    fontSize: "20px"
  },
  chip: {
    margin: "0 20px",
    height: "2em",
    minWidth: "100px"
  },
  chip1: {
    minWidth: "8em"
  },
  chip2: {
    minWidth: "10em"
  },
  root: {
    maxWidth: "33em"
  }
});

interface Props {
  classes: any;
  disabled: boolean;
  item: any;
  parent: any;
  onDelete: any;
  index: number;
  openConfirm: ShowConfirmCaller;
  dispatch: Dispatch;
}

const TagRequirementItem: React.FC<Props> = props => {
  const {
    classes, disabled, item, parent, onDelete, index, openConfirm, dispatch
  } = props;

  const onChange = useCallback((e, newValue, previousValue, name) => {
    if (newValue) {
      e.preventDefault();

      let confirmMessage = "";

      if (name.endsWith("mandatory")) {
        confirmMessage = "You are setting this tag group as mandatory. If some records aren't already tagged from this group, you\n"
          + "              will be prompted to add a tag next time you edit those records.";
      }
      if (name.endsWith("limitToOneTag")) {
        confirmMessage = "You are setting this tag group to limit to one. If some records already have more than one tag from this group, you'll be prompted to remove some tags next time you edit those records.";
      }
      openConfirm({
        onConfirm: () => dispatch(change("TagsForm", name, newValue)),
        confirmMessage,
        confirmButtonText: "Ok"
      });
    }
  }, []);

  return (
    <div className={clsx("centeredFlex", classes.root)}>
      <Typography variant="subtitle2" color="textSecondary" className="flex-fill">
        {GetTagRequirementDisplayName(item.type)}
      </Typography>

      <div className="d-flex">
        <Field
          name={`${parent}.mandatory`}
          margin="none"
          type="checkbox"
          chackedLabel="Mandatory"
          uncheckedLabel="Optional"
          component={PillCheckboxField}
          className={clsx(classes.chip, classes.chip1)}
          disabled={disabled}
          onChange={onChange}
        />

        <Field
          name={`${parent}.limitToOneTag`}
          margin="none"
          type="checkbox"
          chackedLabel="Limit to one tag"
          uncheckedLabel="Unlimited"
          component={PillCheckboxField}
          className={clsx(classes.chip, classes.chip2)}
          disabled={disabled}
          onChange={onChange}
        />
      </div>

      <IconButton
        className={clsx(classes.deleteButton, "dndActionIconButton", {
          "invisible": disabled
        })}
        onClick={() => onDelete(index)}
      >
        <Delete className={clsx(classes.deleteIcon, "dndActionIcon")} />
      </IconButton>
    </div>
  );
};

export default withStyles(styles)(TagRequirementItem);
