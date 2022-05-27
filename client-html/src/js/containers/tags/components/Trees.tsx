import React, { useCallback, useEffect, useState } from "react";
import {
  moveItemOnTree,
  RenderItemParams,
  TreeData,
  TreeDestinationPosition,
  TreeItem,
  TreeSourcePosition
} from '@atlaskit/tree';
import { Tag } from "@api/model";
import { ItemId } from "@atlaskit/tree/types";
import Tree from "./TagTreeBasis";
import { FormTag, FormTagProps } from "../../../model/tags";
import TagItem from "./TagItem";
import { AnyArgFunction, NumberArgFunction } from "../../../model/common/CommonFunctions";
import { getDeepValue } from "../../../common/utils/common";
import ChecklistItem from "./ChecklistItem";

const PADDING_PER_LEVEL = 16;

interface TagsTreeProps extends Partial<FormTagProps> {
  rootTag: FormTag;
  onDrop: AnyArgFunction<void, TreeData>;
  setEditingId: NumberArgFunction;
  editingId: number;
  syncErrors: any;
}

const shouldNotUpdate = (prevProps: TagsTreeProps, currentProps: TagsTreeProps) => {
  if (prevProps.rootTag.id !== currentProps.rootTag.id) {
    return false;
  }

  if (prevProps.rootTag.refreshFlag !== currentProps.rootTag.refreshFlag) {
    return false;
  }

  if (prevProps.syncErrors !== currentProps.syncErrors) {
    return false;
  }

  return prevProps.editingId === currentProps.editingId;
};

const tagToTreeItem = (tag: FormTag): TreeItem => ({
  id: tag.id,
  children: tag.childTags.map(t => t.id),
  hasChildren: Boolean(tag.childTags.length),
  isExpanded: true,
  data: tag
});

const tagToTreeData = (tag: FormTag, prevResult?: Record<ItemId, TreeItem>): Record<ItemId, TreeItem> => {
  const result = prevResult || {};

  result[tag.id] = tagToTreeItem(tag);

  tag.childTags.forEach(t => {
    tagToTreeData(t, result);
  });
  
  return result;
};

const setItemParent = (id: string | number, index, items: Record<ItemId, TreeItem>, prev?: string) => {
  items[id].data.parent = `${prev ? prev + "." : ""}childTags[${index}]`;
  items[id].children.forEach((cId, cIndex) => setItemParent(cId, cIndex, items, items[id].data.parent));
};

const setParents = (data: TreeData) => {
  data.items[data.rootId].data.refreshFlag = false;
  data.items[data.rootId].children.forEach((id, index) => setItemParent(id, index, data.items));
};

const errorKeys: (keyof Tag)[] = ["urlPath", "name", "content"];

const useHasError = (syncErrors, item, editingId, setEditingId) => {
  const hasErrors = Object.keys(getDeepValue(syncErrors, item.data.parent) || {}).some(key => errorKeys.includes(key as any));

  useEffect(() => {
    if (hasErrors && !String(item.data.id).startsWith("new") && editingId !== item.data.id) {
      setEditingId(item.data.id);
    }
  }, [hasErrors, editingId, item.data.id]);
  
  return hasErrors;
};

const useTagTreeHandlers = (rootTag, editingId, syncErrors, onDrop) => {
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
  }, [rootTag.id, rootTag.refreshFlag, editingId, syncErrors]);

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
  syncErrors,
  classes,
  onDelete,
  changeVisibility,
  setEditingId,
  editingId
}) => {
  const hasErrors = useHasError(syncErrors, item, editingId, setEditingId);
  
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
        isEditing={hasErrors || item.data.id === editingId}
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
    editingId,
    syncErrors,
    onDrop
} = props;

  const { onDragEnd, treeState } = useTagTreeHandlers(rootTag, editingId, syncErrors, onDrop);

  const renderItem = useCallback(({
    item,
    provided,
    snapshot
  }: RenderItemParams) => (
    <RenderedTagItem
      item={item as any}
      provided={provided}
      snapshot={snapshot}
      syncErrors={syncErrors}
      classes={classes}
      onDelete={onDelete}
      changeVisibility={changeVisibility}
      setEditingId={setEditingId}
      editingId={editingId}
    />
), [editingId, syncErrors]);

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
        isEditing={rootTag.id === editingId}
      />
      <div className="ml-2">
        <Tree
          // @ts-ignore
          classes={classes}
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
    editingId,
    syncErrors,
    onDrop
  } = props;

  const { onDragEnd, treeState } = useTagTreeHandlers(rootTag, editingId, syncErrors, onDrop);

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
    ), [editingId, syncErrors]);

  return treeState ? (
    <Tree
      // @ts-ignore
      classes={classes}
      tree={treeState}
      renderItem={renderItem}
      onDragEnd={onDragEnd}
      isDragEnabled
    />
  ) : null;
}, shouldNotUpdate);