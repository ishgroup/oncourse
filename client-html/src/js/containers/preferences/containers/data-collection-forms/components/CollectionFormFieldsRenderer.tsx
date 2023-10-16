import Delete from "@mui/icons-material/Delete";
import DragIndicator from "@mui/icons-material/DragIndicator";
import Edit from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import Collapse from "@mui/material/Collapse";
import IconButton from "@mui/material/IconButton";
import { alpha } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import { makeAppStyles, stopEventPropagation, useHoverShowStyles } from "ish-ui";
import React, { useCallback, useEffect, useState } from "react";
import { change, getFormValues } from "redux-form";
import { useAppSelector } from "../../../../../common/utils/hooks";
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

const reorder = (list, startIndex, endIndex) => {
  const result = Array.from(list);
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};

const onDragEnd = (result, items, dispatch) => {
  // dropped outside the list
  if (!result.destination) {
    return;
  }

  // dropped on the same position
  if (result.source.index === result.destination.index) {
    return;
  }

  const reordered = reorder(items, result.source.index, result.destination.index);

  dispatch(change("DataCollectionForm", "items", reordered));
};

const CollectionFormFieldBase = (
  {
    item,
    provided,
    snapshot
  }
) => {
  
  const values: any = useAppSelector(state => getFormValues(DATA_COLLECTION_FORM)(state));

  const [isEditing, setIsEditing] = useState(false);

  const field = values?.items[item.id];
  
  const isHeading = field.baseType === "heading";

  const isField = field.baseType === "field";

  const classes = useStyles();

  const onEditClick = () => setIsEditing(!isEditing);

  const onDeleteClick = useCallback(e => {
    stopEventPropagation(e);
    // onDelete(item);
  }, [item]);
  
  useEffect(() => {
    setIsEditing(false);
  }, [values.form.id]);

  const hoverClasses = useHoverShowStyles();

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
              {item.data.type.label}
            </Typography>}
          </div>

          <IconButton
            className={clsx("dndActionIconButton", hoverClasses.target)}
            onClick={onEditClick}
          >
            <Edit className={clsx(classes.actionIcon, classes.actionIconActive)} />
          </IconButton>

          {isField && <IconButton
            className="dndActionIconButton"
            disabled
          >
            <VisibilityIcon className={clsx(classes.actionIcon, classes.actionIconInactive)}  />
          </IconButton>}

          <IconButton
            className={clsx("dndActionIconButton", hoverClasses.target)}
            onClick={onDeleteClick}
          >
            <Delete className={clsx(classes.actionIcon, classes.actionIconActive)} />
          </IconButton>
        </div>

        <Collapse in={isEditing} mountOnEnter unmountOnExit>
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

const renderCollectionFormFields = props => <CollectionFormFieldBase {...props} />;

export default renderCollectionFormFields;
