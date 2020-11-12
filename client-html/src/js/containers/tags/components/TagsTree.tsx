import React, { useCallback } from "react";
import TagsTreeItem from "./TagsTreeItem";

interface TagsTreeProps {
  tags?: any;
  classes?: any;
}

const expanded = [];

const setExpanded = tags => {
  tags.map(t => {
    if (t.children.length) {
      expanded.push(t.id.toString());
    }
    setExpanded(t.children);
  });
};

const TagsTree = React.memo<TagsTreeProps>(props => {
  const { tags, classes } = props;
  setExpanded(tags);
  return (
    <div className="container pl-1 pt-1">
      {tags.map(t => <TagsTreeItem key={t.id.toString()} tag={t} classes={classes} initialExpanded={expanded} />)}
    </div>
  );
});

export default TagsTree;
