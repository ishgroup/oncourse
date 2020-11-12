/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import withStyles from "@material-ui/core/styles/withStyles";
import { createStyles } from "@material-ui/core";
import { CheckBoxOutlineBlank, CheckBox, IndeterminateCheckBox } from "@material-ui/icons";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import IconButton from "@material-ui/core/IconButton";
import KeyboardArrowUp from "@material-ui/icons/KeyboardArrowUp";
import TreeItem, { TreeItemProps } from "@material-ui/lab/TreeItem";
import clsx from "clsx";
import { BooleanArgFunction } from "../../../../../../model/common/CommonFunctions";
import { MenuTag } from "../../../../../../model/tags";

const styles = theme => createStyles({
    checkbox: {
      height: "1em",
      width: "1em",
      marginLeft: ".3em"
    },
    checkboxFontSize: {
      fontSize: "18px"
    },
    collapseButton: {
      height: theme.spacing(3),
      width: theme.spacing(3),
      transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
      padding: 0
    },
    collapseWrapper: {
      marginLeft: "12px"
    },
    root: {
      ["&:hover > $content $label, "
      + "&:focus > $content $label, &:focus > $content $label:hover, "
      + "&$selected > $content $label, &$selected > $content $label:hover,"
      + "&$selected:focus > $content $label"]: {
        backgroundColor: "inherit"
      }
    },
    label: {},
    rootExpanded: {
      "& > $content $collapseButton": {
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
      width: 0
    },
    selected: {},
    content: {}
  });

interface Props extends TreeItemProps {
  item: MenuTag;
  handleExpand: any;
  classes: any;
  toggleActive: any;
  toggleParentActive?: BooleanArgFunction;
  hasOffset?: boolean;
}

const ListTagItem: React.FC<Props> = ({
 classes, item, nodeId, handleExpand, hasOffset, toggleActive
}) => (
  <TreeItem
    nodeId={nodeId}
    classes={{
        root: classes.root,
        expanded: classes.rootExpanded,
        selected: classes.selected,
        content: classes.content,
        label: classes.label,
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
              label: "text-nowrap text-truncate"
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
          />
        );
      })}
  </TreeItem>
  );

export default withStyles(styles)(ListTagItem);
