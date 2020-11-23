/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useState
} from "react";
import AutoSizer from "react-virtualized-auto-sizer";
import { Typography } from "@material-ui/core";
import withStyles from "@material-ui/core/styles/withStyles";
import { Delete } from "@material-ui/icons";
import clsx from "clsx";
import ButtonBase from "@material-ui/core/ButtonBase";
import Button from "@material-ui/core/Button/Button";
import Paper from "@material-ui/core/Paper/Paper";
import { EntityRelationType } from "@api/model";
import { NestedListItem } from "../NestedList";
import { openInternalLink } from "../../../../utils/links";
import DynamicSizeList from "../../DynamicSizeList";
import { listStyles } from "./styles";
import FormField from "../../form-fields/FormField";
import { uniqueEntityRelationTypes } from "../../../../../containers/entities/courses/utils";

interface Props {
  classes: any;
  type: "list" | "search";
  items: NestedListItem[];
  onDelete?: (item: NestedListItem, index: number) => void;
  onClick?: (item: NestedListItem) => void;
  fade?: boolean;
  dataRowClass?: string;
  disabled?: boolean;
  relationTypes?: EntityRelationType[];
  formField?: string;
}

const RowContent = React.memo<any>(({
  style, item, index, classes, dataRowClass, disabled, onDelete, onClick, type, forwardedRef, relationTypes, formField
}) => {
  const uniqueRelationTypes = relationTypes && relationTypes.length ? uniqueEntityRelationTypes(relationTypes, item.relationId) : [];
  const headerText = (item, relationTypes) => {
    const isReverseRelation = String(item.relationId).includes("r");
    const filteredRelations = Array.isArray(relationTypes) ? relationTypes.filter(r => r.isReverseRelation === isReverseRelation) : [];
    const relation = filteredRelations.find(r => String(r.id) === String(parseInt(item.relationId, 10)));

    return (
      <Typography variant="body2" className={clsx("text-truncate", classes.dInline)}>
        {relation && relation.name ? `${item.primaryText} ${relation.fromName} ${relation.name} ${relation.toName}` : item.primaryText}
      </Typography>
    );
  };

  return (
    <li
      ref={forwardedRef}
      style={style}
      className={clsx("pb-1", {
        [classes.fade]: !item.active
      })}
    >
      <div className={clsx("d-flex", classes.root__item)}>
        <div
          className={clsx(classes.textRow, dataRowClass, {
            "linkDecoration": item.link
          })}
          onClick={item.link ? () => openInternalLink(item.link) : undefined}
        >
          {headerText(item, relationTypes)}

          <div className={classes.chipsWrapper}>
            <Typography
              className={clsx(
                "ml-2 mr-2 text-truncate",
                classes.dInline
              )}
              variant="caption"
            >
              {item.secondaryText}
            </Typography>

            <Typography variant="body2" className={classes.chips}>
              {item.entityName}
            </Typography>
          </div>
        </div>
        {type === "list" && uniqueRelationTypes.length && formField ? (
          <div className="ml-2">
            <FormField
              type="select"
              name={`${formField}[${index}][relationId]`}
              label="Type of relations"
              items={uniqueRelationTypes}
              selectValueMark="value"
              selectLabelMark="label"
              defaultValue={item.relationId}
              classes={{ textField: classes.selectRelationIdTextField }}
              placeholder="Select relation"
              hideLabel
            />
          </div>
        ) : ""}
        <span className={clsx("centeredFLex", disabled && "invisible")}>
          {type === "list" && onDelete && (
          <ButtonBase className={classes.deleteButton} onClick={() => onDelete(item, index)}>
            <Delete className={classes.deleteIcon} />
          </ButtonBase>
          )}
          {type === "search" && (
            <>
              <span className="flex-fill" />
              <Button className={classes.button} onClick={() => onClick(item, index)}>
                Add
              </Button>
            </>
          )}
        </span>
      </div>
    </li>
  );
});

const RowRenderer = React.forwardRef<any, any>(({ data, index, style }, ref) => {
  const { items, ...rest } = data;
  return <RowContent item={items[index]} style={style} forwardedRef={ref} index={index} {...rest} />;
});

const ListRenderer = React.memo(
  ({
 classes, items, onDelete, onClick, fade, dataRowClass, disabled, type, relationTypes, formField
}: Props) => {
    const [changedIndex, setChangedIndex] = useState<number>();

    useEffect(() => {
      if (typeof changedIndex === "number") {
        setChangedIndex(null);
      }
    }, [changedIndex]);

    const isVirtual = useMemo(() => items.length > 15, [items.length]);

    const deleteHandler = useCallback(
      onDelete
        ? (item, index) => {
            onDelete(item, index);
            setChangedIndex(index);
          }
        : undefined,
      [onDelete]
    );

    const clickHandler = useCallback((item, index) => {
      onClick(item);
      setChangedIndex(index);
    }, []);

    const itemData = useMemo(() => ({
      items, dataRowClass, type, classes, disabled, onDelete: deleteHandler, onClick: clickHandler, relationTypes, formField
    }), [items, dataRowClass, type, classes, disabled, deleteHandler, clickHandler, relationTypes, formField]);

    return (
      <Paper className={clsx(classes.root, { [classes.fade]: fade, [classes.root__height]: isVirtual })}>
        {isVirtual ? (
          <AutoSizer disableHeight>
            {({ width }) => (
              <DynamicSizeList
                height={384}
                width={width}
                itemCount={items.length}
                itemData={itemData}
              >
                {RowRenderer}
              </DynamicSizeList>
            )}
          </AutoSizer>
        ) : (
          items.map((item, index) => (
            <RowContent
              key={item.id}
              item={item}
              style={{}}
              index={index}
              disabled={disabled}
              classes={classes}
              dataRowClass={dataRowClass}
              type={type}
              onDelete={onDelete}
              onClick={onClick}
              relationTypes={relationTypes}
              formField={formField}
            />
          ))
        )}
      </Paper>
    );
  }
);

export default withStyles(listStyles)(ListRenderer);
