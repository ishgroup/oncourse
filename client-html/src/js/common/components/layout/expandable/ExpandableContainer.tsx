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
import { AppTheme } from "../../../../model/common/Theme";
import AddIcon from "../../icons/AddIcon";
import Divider from "@mui/material/Divider";

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
  inlineHeading?: boolean;
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
  inlineHeading = false,
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
      <Divider className="mb-2"/>
      <div ref={headerRef}>
        <div className="centeredFlex mb-2">
          <div className={clsx("centeredFlex", { "flex-fill": !inlineHeading })}>
            <div className="heading">{header}</div>
            {onAdd && (
              <AddIcon onClick={onAdd} />
            )}
          </div>
          <IconButton onClick={toggleExpand} className={onAdd && classes.adornmentOffset}>
            <ExpandMore
              className={clsx(classes.expandButton, isExpanded && classes.expandButtonExpanded)}
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
