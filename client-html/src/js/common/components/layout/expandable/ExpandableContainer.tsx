/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useRef, useMemo
} from "react";
import ExpandMore from "@mui/icons-material/ExpandMore";
import IconButton from "@mui/material/IconButton";
import Collapse from "@mui/material/Collapse";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import Divider from "@mui/material/Divider";
import { AppTheme } from "../../../../model/common/Theme";
import AddButton from "../../icons/AddButton";

const styles = (theme: AppTheme) =>
  createStyles({
    expandButton: {
      position: "absolute",
      right: 0,
      transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`
    },
    expandButtonExpanded: {
      transform: "rotate(180deg)"
    },
    controls: {
      position: "relative",
      paddingRight: theme.spacing(5)
    }
  });

interface Props {
  children: React.ReactNode;
  header: React.ReactNode;
  expanded: number[];
  index: number;
  setExpanded: (arg: number[]) => void;
  headerAdornment?: React.ReactNode;
  onChange?: (e: Event, expanded: number[]) => void;
  onAdd?: any;
  classes?: any;
  mountAll?: boolean;
}

const ExpandableContainer: React.FC<Props> = ({
  children,
  header,
  headerAdornment,
  onAdd,
  onChange,
  classes,
  expanded,
  setExpanded,
  index,
  mountAll,
}) => {
  const headerRef = useRef<any>();

  const isExpanded = useMemo(() => expanded.includes(index), [expanded, index]);

  const toggleExpand = useCallback(
    e => {
      const updated = [...expanded];

      if (isExpanded) {
        updated.splice(
          updated.findIndex(i => i === index),
          1
        );
      } else {
        updated.push(index);
      }

      if (onChange) {
        onChange(e, updated);
      }
      if (!e.isDefaultPrevented()) {
        setExpanded(updated);
      }
    },
    [isExpanded, expanded, index]
  );

  return (
    <>
      <Divider className={onAdd ? "mb-2" : "mb-3"} />
      <div ref={headerRef}>
        <div className={clsx("centeredFlex", onAdd ? "mb-2" : "mb-3", classes.controls)}>
          <div className="centeredFlex">
            <div className="heading">{header}</div>
            {onAdd && (
              <AddButton onClick={onAdd} />
            )}
          </div>
          {headerAdornment}
          <IconButton
            onClick={toggleExpand}
            className={clsx(classes.expandButton, isExpanded && classes.expandButtonExpanded)}
            data-testid={`expand-button-${index}`}
          >
            <ExpandMore />
          </IconButton>
        </div>
      </div>

      <div>
        <Collapse in={isExpanded} unmountOnExit={!mountAll} mountOnEnter={!mountAll}>
          {children}
        </Collapse>
      </div>
    </>
  );
};

export default withStyles(styles)(ExpandableContainer);
