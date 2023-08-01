import { TagRequirement } from "@api/model";
import Delete from "@mui/icons-material/Delete";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import { AppTheme, ShowConfirmCaller, useHoverShowStyles } from "ish-ui";
import React, { useCallback, useMemo } from "react";
import { Dispatch } from "redux";
import { change, Field } from "redux-form";
import { ToogleCheckbox } from "../../../common/components/form/ToogleCheckbox";
import GetTagRequirementDisplayName from "../utils/GetTagRequirementDisplayName";

const styles = (theme: AppTheme) => createStyles({
  deleteIcon: {
    fontSize: "20px"
  },
  root: {
    display: "grid",
    gridTemplateColumns: "minmax(100px, 300px) 210px 230px 46px",
    alignItems: "center",
    marginBottom: theme.spacing(2)
  }
});

interface Props {
  classes: any;
  disabled: boolean;
  item: TagRequirement;
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

  const hoverClasses = useHoverShowStyles();

  return (
    <div className={clsx(classes.root, hoverClasses.container)}>
      <Typography variant="h5"  fontSize="1.3rem">
        {header}
      </Typography>

      <Field
        name={`${parent}.mandatory`}
        className="ml-1"
        margin="none"
        type="checkbox"
        chackedLabel="Mandatory"
        uncheckedLabel="Optional"
        component={ToogleCheckbox}
        disabled={disabled}
        onChange={onChange}
        debounced={false}
      />

      <Field
        name={`${parent}.limitToOneTag`}
        className="ml-1"
        margin="none"
        type="checkbox"
        chackedLabel="One tag only"
        uncheckedLabel="Unlimited"
        component={ToogleCheckbox}
        disabled={disabled}
        onChange={onChange}
        debounced={false}
      />

      <IconButton
        className={clsx("dndActionIconButton ml-1", hoverClasses.target,  {
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
