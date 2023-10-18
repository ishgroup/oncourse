import Delete from "@mui/icons-material/Delete";
import DragIndicator from "@mui/icons-material/DragIndicator";
import Edit from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import Collapse from "@mui/material/Collapse";
import IconButton from "@mui/material/IconButton";
import { alpha } from "@mui/material/styles";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import { makeAppStyles, stopEventPropagation, useHoverShowStyles } from "ish-ui";
import React, { useEffect, useMemo, useState } from "react";
import { getFormSyncErrors, getFormValues } from "redux-form";
import { useAppDispatch, useAppSelector } from "../../../../../common/utils/hooks";
import { CollectionFormSchema } from "../../../../../model/preferences/data-collection-forms/collectionFormSchema";
import CollectionFormField from "./CollectionFormField";
import CollectionFormHeading from "./CollectionFormHeading";
import { DATA_COLLECTION_FORM } from "./DataCollectionForm";

const useStyles = makeAppStyles(theme => ({
  dragIcon: {
    margin: theme.spacing(0, 2),
    color: theme.palette.action.focus,
    "&:hover": {
      color: theme.palette.action.active
    }
  },
  actionButton: {
    marginRight: "10px"
  },
  actionIcon: {
    fontSize: "20px"
  },
  actionIconActive: {
    color: theme.palette.action.active,
  },
  actionIconInactive: {
    color: theme.palette.action.focus,
  },
  cardRoot: {
    paddingTop: theme.spacing(1),
  },
  card: {
    zIndex: 1,
    borderRadius: `${theme.shape.borderRadius}px`,
    cursor: "pointer",
    backgroundColor: alpha(theme.palette.text.primary, 0.025),
    "&:hover $actionIcon": {
      color: theme.palette.action.active
    },
    "&:hover $actionIconInactive": {
      color: theme.palette.action.focus
    }
  },
  cardGrid: {
    gridTemplateColumns: "auto 1fr 1fr auto auto auto",
    display: "grid",
    alignItems: "center",
  },
  dragOver: {
    boxShadow: theme.shadows[2]
  },
}));

const CollectionFormFieldsRenderer = (
  {
    item,
    provided,
    snapshot,
    onDeleteClick
  }
) => {
  const values  = useAppSelector(state => getFormValues(DATA_COLLECTION_FORM)(state)) as CollectionFormSchema;
  const errors  = useAppSelector(state => getFormSyncErrors(DATA_COLLECTION_FORM)(state))  as CollectionFormSchema;
  const hasErrors = Boolean(errors?.items && errors?.items[item.id]);
  
  const [isEditing, setIsEditing] = useState(false);

  const field = values?.items[item.id];
  
  const isHeading = field.baseType === "heading";

  const isField = field.baseType === "field";

  const classes = useStyles();

  const onEditClick = () => setIsEditing(!isEditing);
  
  useEffect(() => {
    setIsEditing(false);
  }, [values.form.id]);

  useEffect(() => {
    if (field.baseType === "heading" && !field.name) {
      setIsEditing(true);
    }
  }, [field]);

  const hoverClasses = useHoverShowStyles();
  
  const relationTip = useMemo(() => {
    const releatedFieldIndex = isField && values.items.findIndex(i => i.baseType === "field" && i.type.uniqueKey === field.relatedFieldKey);
    
    if (releatedFieldIndex !== -1) {
      return `Only visible when ${(values.items[releatedFieldIndex] as any)?.label} value is ${(document.querySelector(`[name="items[${item.id}].relatedFieldValue"]`) as any)?.value || null}`;
    }

    return null;
  }, [field, item.id]);
  
  const handleDelete = e => {
    stopEventPropagation(e);
    onDeleteClick(item.id);
  };

  return (
    <div className={classes.cardRoot} ref={provided.innerRef} {...provided.draggableProps} data-draggable-id={item.id}>
      <div
        className={clsx(classes.card, {
          ["mt-4"]: isHeading && item.id !== 0,
          [clsx("paperBackgroundColor", classes.dragOver)]: snapshot.isDragging || Boolean(snapshot.combineTargetFor)
        })}
        onClick={onEditClick}
      >
        <div className={clsx(classes.cardGrid, hoverClasses.container)}>
          <div {...provided.dragHandleProps}>
            <DragIndicator
              className={clsx("d-flex", classes.dragIcon)}
            />
          </div>

          {isHeading && <div className={"heading"}>
            {field.name}
          </div>}

          {isField && <Typography  variant="body2">
            {field.label}
            {field.mandatory && "*"}
          </Typography>}

          <div>
            {isField && <Typography  variant="subtitle2" color="textSecondary" >
              {item.data?.type?.label}
            </Typography>}
          </div>

          <IconButton
            className={clsx("dndActionIconButton", hoverClasses.target)}
            onClick={onEditClick}
          >
            <Edit className={clsx(classes.actionIcon, classes.actionIconActive)} />
          </IconButton>

          {isField && <Tooltip title={relationTip}><IconButton
            className="dndActionIconButton"
            disabled={!relationTip}
          >
            <VisibilityIcon className={clsx(classes.actionIcon, relationTip ? classes.actionIconActive : classes.actionIconInactive)}  />
          </IconButton></Tooltip> }

          <IconButton
            className={clsx("dndActionIconButton", hoverClasses.target)}
            onClick={handleDelete}
          >
            <Delete className={clsx(classes.actionIcon, classes.actionIconActive)} />
          </IconButton>
        </div>

        <Collapse in={isEditing || hasErrors} mountOnEnter unmountOnExit>
          <div onClick={stopEventPropagation} className="p-3">
            {isHeading
              ? <CollectionFormHeading
                item={item}
                provided={provided}
                snapshot={snapshot}
              />
              : <CollectionFormField
                item={item}
                field={field}
                fields={values.items}
              />}
          </div>
        </Collapse>
      </div>
    </div>
  );
};

export default CollectionFormFieldsRenderer;