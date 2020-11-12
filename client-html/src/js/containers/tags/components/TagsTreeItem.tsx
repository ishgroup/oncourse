import React, { useCallback, useMemo } from "react";
import clsx from "clsx";
import TreeView from "@material-ui/lab/TreeView/TreeView";
import IconButton from "@material-ui/core/IconButton";
import KeyboardArrowUp from "@material-ui/icons/KeyboardArrowUp";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import { CheckBox, CheckBoxOutlineBlank, IndeterminateCheckBox } from "@material-ui/icons";
import TreeItem from "@material-ui/lab/TreeItem/TreeItem";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";

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
    width: "100%",
    paddingTop: "2px",
    paddingBottom: "2px",
    "&:focus $content": {
      backgroundColor: "inherit"
    }
  },
  content: {
    "&:hover": {
      backgroundColor: "inherit"
    }
  },
  rootExpanded: {
    "& > $content $collapseButton": {
      transform: "rotate(180deg)"
    }
  },
  parentOffset: {
    marginLeft: theme.spacing(-3)
  }
});

interface TagsTreeItemProps {
  tag?: any;
  classes?: any;
  initialExpanded?: any;
}

const TagsTreeItem = React.memo<TagsTreeItemProps>(props => {
  const { tag, classes, initialExpanded } = props;

  const [expanded, setExpanded] = React.useState(initialExpanded);
  const hasOffset = useMemo(() => !tag.children.some(c => Boolean(c.children.length)), [tag.children]);

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

  const key = tag.id.toString();
  return (
    <TreeView expanded={expanded} classes={{ root: classes.root }}>
      <TreeItemRenderer
        kay={key}
        classes={classes}
        nodeId={key}
        item={tag}
        handleExpand={handleExpand}
        hasOffset={hasOffset}
      />
    </TreeView>
  );
});

const TreeItemRenderer: React.FC<any> = props => {
  const {
    classes, nodeId, item, handleExpand, hasOffset
  } = props;
  return (
    <TreeItem
      key={nodeId}
      nodeId={nodeId}
      classes={{
        root: classes.root,
        expanded: classes.rootExpanded,
        content: classes.content,
        group: "ml-2"
      }}
      label={(
        <div
          className={clsx("centeredFlex d-flex", hasOffset ? classes.parentOffset : "ml-0")}
        >
          {!item.parent ? (
            <FormControlLabel
              className="overflow-hidden"
              classes={{
                label: "text-nowrap text-truncate flex-fill"
              }}
              control={(
                <Checkbox
                  value={nodeId}
                  checked={item.active}
                  className={classes.checkbox}
                  color="secondary"
                  indeterminate={item.indeterminate}
                  icon={<CheckBoxOutlineBlank className={classes.checkboxFontSize} />}
                  checkedIcon={<CheckBox className={classes.checkboxFontSize} />}
                  indeterminateIcon={<IndeterminateCheckBox className={classes.checkboxFontSize} />}
                />
              )}
              label={item.name}
            />
          ) : (
            <Typography variant="body2" className="flex-fill">
              {item.name}
            </Typography>
          )}
          <IconButton
            role={nodeId}
            onClick={handleExpand}
            className={clsx(classes.collapseButton, {
              "invisible": !item.parent
            })}
          >
            <KeyboardArrowUp />
          </IconButton>
        </div>
      )}
    >
      {item.children.map(c => {
        const key = c.id.toString();
        return (
          <TreeItemRenderer
            kay={key}
            classes={classes}
            nodeId={key}
            item={c}
            handleExpand={handleExpand}
            hasOffset={hasOffset}
          />
        );
      })}
    </TreeItem>
  );
};

export default withStyles(styles)(TagsTreeItem) as any;
