import React, { useCallback, useMemo } from "react";
import clsx from "clsx";
import { change, Field } from "redux-form";
import { createStyles, withStyles } from "@mui/styles";
import Typography from "@mui/material/Typography";
import Delete from "@mui/icons-material/Delete";
import IconButton from "@mui/material/IconButton";
import { Dispatch } from "redux";
import { ToogleCheckbox } from "../../../common/components/form/ToogleCheckbox";
import GetTagRequirementDisplayName from "../utils/GetTagRequirementDisplayName";
import { ShowConfirmCaller } from "../../../model/common/Confirm";
import { AppTheme } from "../../../model/common/Theme";

const styles = (theme: AppTheme) => createStyles({
  deleteIcon: {
    fontSize: "20px"
  },
  root: {
    display: "grid",
    gridTemplateColumns: `0.5fr 1fr 1fr ${theme.spacing(4.5)}`,
    alignItems: "center",
    marginBottom: theme.spacing(2)
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
  
  const header = useMemo(() => GetTagRequirementDisplayName(item.type), [item.type]);

  return (
    <div className={classes.root}>
      <Typography variant="subtitle1" className="flex-fill">
        {header}
      </Typography>

      <Field
        name={`${parent}.mandatory`}
        margin="none"
        type="checkbox"
        chackedLabel="Mandatory"
        uncheckedLabel="Optional"
        component={ToogleCheckbox}
        disabled={disabled}
        onChange={onChange}
      />

      <Field
        name={`${parent}.limitToOneTag`}
        margin="none"
        type="checkbox"
        chackedLabel="One tag only"
        uncheckedLabel="Unlimited"
        component={ToogleCheckbox}
        disabled={disabled}
        onChange={onChange}
      />

      <IconButton
        className={clsx("dndActionIconButton", {
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
