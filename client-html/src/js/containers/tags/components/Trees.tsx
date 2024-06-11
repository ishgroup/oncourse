/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import {
  moveItemOnTree,
  RenderItemParams,
  TreeData,
  TreeDestinationPosition,
  TreeItem,
  TreeSourcePosition
} from '@atlaskit/tree';
import { ItemId } from "@atlaskit/tree/types";
import { AnyArgFunction, NumberArgFunction } from "ish-ui";
import React, { useCallback, useEffect, useState } from "react";
import { getInvalidValueOdjects } from "../../../common/utils/common";
import { FormTag, FormTagProps } from "../../../model/tags";
import ChecklistItem from "./ChecklistItem";
import TagItem from "./TagItem";
import Tree from "./TagTreeBasis";

const PADDING_PER_LEVEL = 16;

interface TagsTreeProps extends Partial<FormTagProps> {
  rootTag: FormTag;
  onDrop: AnyArgFunction<void, TreeData>;
  setEditingId: NumberArgFunction;
  setEditingIds: (ids: number[]) => void;
  editingIds: number[];
  syncErrors: any;
}

export const treeItemDataToTag = (id: number | string, tree: TreeData, allTags: Tag[]): Tag => {
  const tag = { ...allTags.find(t => t.id === id) };
  tag.childTags = tree.items[id].children.map(id => treeItemDataToTag(id, tree, allTags));
  return tag;
};

export const treeDataToTags = (tree: TreeData, allTags: Tag[]): Tag[] => tree.items[tree.rootId].children.map(id => treeItemDataToTag(id, tree, allTags));

export const shouldNotUpdate = (prevProps: TagsTreeProps, currentProps: TagsTreeProps) => {
  if (prevProps.rootTag.id !== currentProps.rootTag.id) {
    return false;
  }

  if (prevProps.rootTag.refreshFlag !== currentProps.rootTag.refreshFlag) {
    return false;
  }

  if (prevProps.syncErrors !== currentProps.syncErrors) {
    return false;
  }

  return prevProps.editingIds === currentProps.editingIds;
};

export const tagToTreeItem = (tag: FormTag): TreeItem => ({
  id: tag.id?.toString(),
  children: tag.childTags?.map(t => t.id),
  hasChildren: Boolean(tag.childTags.length),
  isExpanded: true,
  data: tag
});

export const tagToTreeData = (tag: FormTag, prevResult?: Record<ItemId, TreeItem>): Record<ItemId, TreeItem> => {
  const result = prevResult || {};

  result[tag.id] = tagToTreeItem(tag);

  tag.childTags.forEach(t => {
    tagToTreeData(t, result);
  });
  
  return result;
};

export const setItemParent = (id: string | number, index, items: Record<ItemId, TreeItem>, prev?: string) => {
  items[id].data.parent = `${prev ? prev + "." : ""}childTags[${index}]`;
  items[id].children.forEach((cId, cIndex) => setItemParent(cId, cIndex, items, items[id].data.parent));
};

export const setParents = (data: TreeData) => {
  data.items[data.rootId].data.refreshFlag = false;
  data.items[data.rootId].children.forEach((id, index) => setItemParent(id, index, data.items));
};

export const useTagTreeHandlers = (rootTag, editingIds, syncErrors, onDrop) => {
  const [treeState, setTreeState] = useState<TreeData>();

  useEffect(() => {
    if (rootTag) {
      const newState = {
        rootId: rootTag.id,
        items: tagToTreeData(rootTag)
      };
      setParents(newState);
      setTreeState(newState);
    }
  }, [rootTag.id, rootTag.refreshFlag, editingIds, syncErrors]);

  const onDragEnd = (
    source: TreeSourcePosition,
    destination?: TreeDestinationPosition,
  ) => {
    if (!destination
      || (source.parentId === destination.parentId && source.index === destination.index )
      || (typeof destination.index !== "number" && String(source.parentId) === String(destination.parentId))
    ) {
      return;
    }
    onDrop(moveItemOnTree(treeState, source, destination));
  };
  
  return { onDragEnd, treeState };
};

const RenderedTagItem = ({
  item,
  provided,
  snapshot,
  classes,
  onDelete,
  changeVisibility,
  setEditingId,
  editingIds
}) => {
  return (
    <div className={classes.cardRoot} ref={provided.innerRef} {...provided.draggableProps} data-draggable-id={item.data.id}>
      <TagItem
        item={item.data}
        classes={classes}
        key={item.data.id}
        onDelete={onDelete}
        changeVisibility={changeVisibility}
        provided={provided}
        snapshot={snapshot}
        setIsEditing={setEditingId}
        isEditing={editingIds.includes(item.data.id)}
      />
    </div>
);
};

export const TagTree = React.memo<TagsTreeProps>(props => {
  const {
    rootTag, 
    classes, 
    onDelete,
    changeVisibility,
    setEditingId,
    setEditingIds,
    editingIds,
    syncErrors,
    onDrop
} = props;

  useEffect(() => {
    if (Object.keys(syncErrors || {}).length) {
      getInvalidValueOdjects(syncErrors, rootTag).forEach(t => setEditingIds(Array.from(new Set([...editingIds, t.id]))));
    }
  }, [syncErrors]);

  const { onDragEnd, treeState } = useTagTreeHandlers(rootTag, editingIds, syncErrors, onDrop);

  const renderItem = useCallback(({
    item,
    provided,
    snapshot
  }: RenderItemParams) => (
    <RenderedTagItem
      item={item as any}
      provided={provided}
      snapshot={snapshot}
      classes={classes}
      onDelete={onDelete}
      changeVisibility={changeVisibility}
      setEditingId={setEditingId}
      editingIds={editingIds}
    />
), [editingIds]);

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
      />
      <div className="ml-2">
        <Tree
          tree={treeState}
          renderItem={renderItem}
          onDragEnd={onDragEnd}
          offsetPerLevel={PADDING_PER_LEVEL}
          isDragEnabled
          isNestingEnabled
        />
      </div>
    </div>
) : null;
}, shouldNotUpdate);

const RenderedChecklistItem = ({
   item,
   provided,
   snapshot,
   classes,
   onDelete,
 }) => (
   <div className={classes.cardRoot} ref={provided.innerRef} {...provided.draggableProps} data-draggable-id={item.data.id}>
     <ChecklistItem
       item={item.data}
       classes={classes}
       key={item.data.id}
       onDelete={onDelete}
       provided={provided}
       snapshot={snapshot}
     />
   </div>
  );

export const ChecklistTree = React.memo<TagsTreeProps>(props => {
  const {
    rootTag,
    classes,
    onDelete,
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
    <RenderedChecklistItem
      item={item as any}
      provided={provided}
      snapshot={snapshot}
      classes={classes}
      onDelete={onDelete}
    />
    ), [syncErrors]);

  return treeState ? (
    <Tree
      tree={treeState}
      renderItem={renderItem}
      onDragEnd={onDragEnd}
      isDragEnabled
    />
  ) : null;
}, shouldNotUpdate);