/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import clsx from "clsx";
import TreeView from "@material-ui/lab/TreeView";
import ListTagItem from "./ListTagItem";
import { MenuTag } from "../../../../../../model/tags";
import { updateIndeterminateState, getUpdated } from "../../../utils/listFiltersUtils";

interface Props {
  rootTag: MenuTag;
  classes: any;
  updateActive: (updated: MenuTag) => void;
}

const ListTagGroup: React.FC<Props> = ({ rootTag, classes, updateActive }) => {
  const [expanded, setExpanded] = React.useState([]);

  const hasOffset = useMemo(() => !rootTag.children.some(c => Boolean(c.children.length)), [rootTag.children]);

  const handleExpand = useCallback(event => {
    const nodeId = event.currentTarget.getAttribute("role");

    setExpanded(prev => {
      const prevIndex = prev.findIndex(p => p === nodeId);

      const updated = [...prev];

      if (prevIndex !== -1) {
        updated.splice(prevIndex, 1);
      } else {
        updated.push(nodeId);
      }

      return updated;
    });
  }, []);

  const toggleActive = useCallback(
    (e, checked) => {
      const id = e.currentTarget.value;

      const children = getUpdated(rootTag.children, id, checked);
      updateIndeterminateState(children, id);
      updateActive({ ...rootTag, children });
    },
    [rootTag.children]
  );

  return (
    <div>
      <div className={clsx("heading mt-2", classes.listHeaderOffset)}>
        {rootTag.prefix ? `${rootTag.prefix} (${rootTag.tagBody.name})` : rootTag.tagBody.name}
      </div>
      <TreeView expanded={expanded}>
        {rootTag.children.map(c => {
          const key = c.prefix + c.tagBody.id.toString();
          return (
            <ListTagItem
              hasOffset={hasOffset}
              handleExpand={handleExpand}
              nodeId={key}
              item={c}
              key={key}
              toggleActive={toggleActive}
            />
          );
        })}
      </TreeView>
    </div>
  );
};

export default ListTagGroup;
