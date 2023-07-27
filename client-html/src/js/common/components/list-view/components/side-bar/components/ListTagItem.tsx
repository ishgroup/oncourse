/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import withStyles from "@mui/styles/withStyles";
import { createStyles } from "@mui/material";
import { CheckBoxOutlineBlank, CheckBox, IndeterminateCheckBox } from "@mui/icons-material";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";
import IconButton from "@mui/material/IconButton";
import KeyboardArrowUp from "@mui/icons-material/KeyboardArrowUp";
import TreeItem, { TreeItemProps } from "@mui/lab/TreeItem";
import clsx from "clsx";
import { BooleanArgFunction } from  "ish-ui";
import { FormMenuTag } from "../../../../../../model/tags";

const styles = theme => createStyles({
    checkbox: {
      height: "1em",
      width: "1em",
      marginLeft: ".3em",
      marginRight: theme.spacing(0.5),
    },
    checkboxFontSize: {
      fontSize: "18px"
    },
    checkboxLabelRoot: {
      marginRight: theme.spacing(0.5),
      "& $checkboxLabel": {
        fontSize: "12px",
      }
    },
    checkboxLabel: {
      fontSize: "12px",
    },
    collapseButton: {
      height: theme.spacing(3),
      width: theme.spacing(3),
      transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
      padding: 0,
      marginRight: theme.spacing(0.5),
    },
    collapseWrapper: {
      marginLeft: "12px"
    },
    root: {
      "& $content:hover, & $content$selected, & $content$focused": {
        backgroundColor: "inherit"
      }
    },
    label: {},
    rootExpanded: {
      "& > $label $collapseButton": {
        transform: "rotate(180deg)"
      }
    },
    parentOffset: {
      marginLeft: theme.spacing(-4)
    },
    parentWithChildrenOffset: {
      marginLeft: theme.spacing(-1.5)
    },
    iconContainer: {
      width: 0,
    },
    selected: {},
    focused: {},
    content: {
      padding: `0 ${theme.spacing(1)} 0 0`,
      "& $iconContainer": {
        width: 0,
      }
    },
    tagColorDotExtraSmall: {
      width: theme.spacing(1),
      minWidth: theme.spacing(1),
      height: theme.spacing(1),
      minHeight: theme.spacing(1),
      borderRadius: "100%",
      marginLeft: -theme.spacing(1.5),
    },
  });

interface Props extends TreeItemProps {
  item: FormMenuTag;
  handleExpand: any;
  classes: any;
  toggleActive: any;
  showColoredDots: boolean;
  toggleParentActive?: BooleanArgFunction;
  hasOffset?: boolean;
}

const ListTagItem: React.FC<Props> = ({
 classes, item, nodeId, handleExpand, hasOffset, toggleActive, showColoredDots
}) => (
  <TreeItem
    nodeId={nodeId}
    classes={{
      root: classes.root,
      expanded: classes.rootExpanded,
      selected: classes.selected,
      content: classes.content,
      label: classes.label,
      focused: classes.focused,
      iconContainer: classes.iconContainer,
      group: "ml-2"
    }}
    label={(
      <div className={clsx("centeredFlex", hasOffset ? classes.parentOffset : classes.parentWithChildrenOffset)}>
        <IconButton
          role={nodeId}
          onClick={handleExpand}
          className={clsx(classes.collapseButton, {
              "invisible": !item.children.length
            })}
        >
          <KeyboardArrowUp />
        </IconButton>
        <FormControlLabel
          className="overflow-hidden"
          classes={{
            root: classes.checkboxLabelRoot,
            label: clsx("text-nowrap text-truncate", classes.checkboxLabel),
          }}
          control={(
            <Checkbox
              value={nodeId}
              checked={item.active}
              className={classes.checkbox}
              color="secondary"
              indeterminate={item.indeterminate}
              onChange={toggleActive}
              icon={<CheckBoxOutlineBlank className={classes.checkboxFontSize} />}
              checkedIcon={<CheckBox className={classes.checkboxFontSize} />}
              indeterminateIcon={<IndeterminateCheckBox className={classes.checkboxFontSize} />}
            />
            )}
          label={item.tagBody.name}
        />
        {showColoredDots && (
          <div className={clsx(classes.tagColorDotExtraSmall, "mr-2")} style={{ background: "#" + item.tagBody.color }} />
        )}
      </div>
      )}
  >
    {item.children.map(c => {
        const key = c.prefix + c.tagBody.id.toString();
        return (
          <ListTagItem
            nodeId={key}
            item={c}
            classes={classes}
            key={key}
            handleExpand={handleExpand}
            toggleActive={toggleActive}
            showColoredDots={showColoredDots}
          />
        );
      })}
  </TreeItem>
  );

export default withStyles(styles)(ListTagItem);
