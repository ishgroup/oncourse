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
import uniqid from "../../../common/utils/uniqid";
import { AnyArgFunction, NumberArgFunction } from "../../../model/common/CommonFunctions";
import { getDeepValue } from "../../../common/utils/common";

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

const TagsTree = React.memo<TagsTreeProps>(props => {
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

  const renderItem = useCallback(({
    item,
    provided,
    snapshot
  }: RenderItemParams) => {
    const hasErrors = Object.keys(getDeepValue(syncErrors, item.data.parent) || {}).some(key => errorKeys.includes(key as any));

    return (
      <div className={classes.cardRoot} ref={provided.innerRef} {...provided.draggableProps} data-draggable-id={item.data.id}>
        <TagItem
          item={item.data}
          classes={classes}
          key={item.data.id || uniqid()}
          onDelete={onDelete}
          changeVisibility={changeVisibility}
          provided={provided}
          snapshot={snapshot}
          setIsEditing={setEditingId}
          isEditing={hasErrors || item.data.id === editingId}
        />
      </div>
    );
  }, [editingId, syncErrors]);

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

export default TagsTree;
