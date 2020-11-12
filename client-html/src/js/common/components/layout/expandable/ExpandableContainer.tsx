/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useRef, useEffect, useMemo 
} from "react";
import ExpandMore from "@material-ui/icons/ExpandMore";
import IconButton from "@material-ui/core/IconButton";
import Collapse from "@material-ui/core/Collapse";
import AddCircle from "@material-ui/icons/AddCircle";
import { createStyles, withStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import { AppTheme } from "../../../../model/common/Theme";

const styles = (theme: AppTheme) =>
  createStyles({
    expandButton: {
      transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`
    },
    expandButtonExpanded: {
      transform: "rotate(180deg)"
    },
    adornmentOffset: {
      marginLeft: theme.spacing(-1.5)
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
  mountAll
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

  useEffect(() => {
    if (isExpanded) {
      setTimeout(() => {
        if (headerRef.current) {
          headerRef.current.scrollIntoView({ block: "start", inline: "nearest", behavior: "smooth" });
        }
      }, 400);
    }
  }, [isExpanded]);

  return (
    <>
      <div ref={headerRef}>
        <div className="centeredFlex">
          <div className="heading">{header}</div>
          {onAdd && (
            <IconButton onClick={onAdd}>
              <AddCircle className="addButtonColor" />
            </IconButton>
          )}
          <IconButton onClick={toggleExpand} className={onAdd && classes.adornmentOffset}>
            <ExpandMore
              className={clsx("addButtonColor", classes.expandButton, isExpanded && classes.expandButtonExpanded)}
            />
          </IconButton>
          {headerAdornment}
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
