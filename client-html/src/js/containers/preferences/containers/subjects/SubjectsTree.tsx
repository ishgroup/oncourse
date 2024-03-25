/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */


import { RenderItemParams, TreeData } from "@atlaskit/tree";
import { AnyArgFunction, NumberArgFunction } from "ish-ui";
import React, { useCallback } from "react";
import { FormTag, FormTagProps } from "../../../../model/tags";
import TagItem from "../../../tags/components/TagItem";
import Tree from "../../../tags/components/TagTreeBasis";
import { shouldNotUpdate, useHasError, useTagTreeHandlers } from "../../../tags/components/Trees";

interface SubjectsTreeProps extends Partial<FormTagProps> {
  rootTag: FormTag;
  onDrop: AnyArgFunction<void, TreeData>;
  setEditingId: NumberArgFunction;
  editingIds: number[];
  syncErrors: any;
}

const RenderedSubjectItem = ({
 item,
 provided,
 snapshot,
 syncErrors,
 classes,
 onDelete,
 changeVisibility,
 setEditingId,
 editingIds
}) => {
  const hasErrors = useHasError(syncErrors, item, editingIds, setEditingId);

  return (
    <div
      className={classes.cardRoot}
      ref={provided.innerRef}
      {...provided.draggableProps}
      data-draggable-id={item.data.id}>
        <TagItem
          item={item.data}
          classes={classes}
          key={item.data.id}
          onDelete={onDelete}
          changeVisibility={changeVisibility}
          provided={provided}
          snapshot={snapshot}
          setIsEditing={setEditingId}
          isEditing={hasErrors || editingIds.includes(item.data.id)}
          hideColor
        />
    </div>
  );
}

const SubjectsTree = React.memo<SubjectsTreeProps>(props => {
  const {
    rootTag,
    classes,
    onDelete,
    changeVisibility,
    setEditingId,
    editingIds,
    syncErrors,
    onDrop
  } = props;

  const { onDragEnd, treeState } = useTagTreeHandlers(rootTag, editingIds, syncErrors, onDrop);

  const renderItem = useCallback(({
    item,
    provided,
    snapshot
  }: RenderItemParams) => (
    <RenderedSubjectItem
      item={item as any}
      provided={provided}
      snapshot={snapshot}
      syncErrors={syncErrors}
      classes={classes}
      onDelete={onDelete}
      changeVisibility={changeVisibility}
      setEditingId={setEditingId}
      editingIds={editingIds}
    />
  ), [editingIds, syncErrors]);

  return treeState ? (
    <div>
      <TagItem
        item={rootTag}
        classes={classes}
        onDelete={onDelete}
        changeVisibility={changeVisibility}
        provided={{}}
        snapshot={{}}
        setIsEditing={setEditingId}
        isEditing={editingIds.includes(rootTag.id)}
        hideColor
      />
      <div className="ml-2">
        <Tree
          tree={treeState}
          renderItem={renderItem}
          onDragEnd={onDragEnd}
          isDragEnabled
          isNestingEnabled
        />
      </div>
    </div>
  ) : null;
}, shouldNotUpdate);

export default SubjectsTree;