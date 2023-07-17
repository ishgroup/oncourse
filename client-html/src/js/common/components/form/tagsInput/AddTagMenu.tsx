import React from "react";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import KeyboardArrowLeft from "@mui/icons-material/KeyboardArrowLeft";
import MenuItem from "@mui/material/MenuItem";
import List from "@mui/material/List";
import AddTagMenuItem from "./AddTagMenuItem";
import { MenuTag, TagLike } from "../../../ish-ui/model/Fields";

const styles = theme => ({
  listItem: {
    padding: theme.spacing(0, 0.5, 0, 2)
  },
  proceedIcon: {
    marginTop: "2px"
  },
  backContainer: {
    marginLeft: "-6px"
  },
  tagColorDotSmall: {
    width: theme.spacing(2),
    height: theme.spacing(2),
    background: "red",
    borderRadius: "100%"
  }
});

interface Props<T> {
  classes?: any;
  tags: MenuTag<T>[];
  handleAdd: (tag: MenuTag<T>) => void;
  activeTag: MenuTag<T>;
  setActiveTag: (arg: (MenuTag<T> | ((prevState: MenuTag<T>) => MenuTag<T>))) => void;
}

function AddTagMenu<T extends TagLike>(
  {
    classes,
    tags,
    handleAdd,
    activeTag,
    setActiveTag
  }: Props<T>
) {
  const handleBack = () => {
    setActiveTag(prev => prev.parent);
  };

  return (
    <List
      className={classes.rootMenu}
    >
      {activeTag && (
        <MenuItem onClick={handleBack} className={classes.listItem}>
          <span className={clsx("d-flex textSecondaryColor", classes.backContainer)}>
            <KeyboardArrowLeft color="inherit" />
            Back
          </span>
        </MenuItem>
      )}
      {(activeTag ? activeTag.children : tags).map(t => (
        <AddTagMenuItem
          key={t.tagBody.id}
          tag={t}
          classes={classes}
          setActiveTag={setActiveTag}
          handleAdd={handleAdd}
        />
      ))}
    </List>
  );
}

export default withStyles(styles)(AddTagMenu);
