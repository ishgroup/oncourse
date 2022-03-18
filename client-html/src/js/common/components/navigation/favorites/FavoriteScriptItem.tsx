import React from "react";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import Typography from "@mui/material/Typography";
import ListItem from "@mui/material/ListItem";
import { Script } from "@api/model";
import DescriptionOutlinedIcon from '@mui/icons-material/DescriptionOutlined';
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import itemStyles from "./itemStyles";

interface Props {
  item: Script;
  isEditing?: boolean;
  classes?: any;
  setScriptIdSelected?: AnyArgFunction;
  setExecMenuOpened?: AnyArgFunction;
}

const FavoriteScriptItem = (props: Props) => {
  const {
    classes, item, isEditing, setScriptIdSelected, setExecMenuOpened
  } = props;

  const openExecScriptModal = scriptId => {
    setScriptIdSelected(scriptId);
    setExecMenuOpened(true);
  };

  return (
    <ListItem
      dense
      disableGutters
      className={clsx(classes.listItem, {
        [classes.listItemEditing]: isEditing
      })}
    >
      <Typography
        variant="body2"
        onClick={() => openExecScriptModal(item.id)}
        className={clsx(classes.listItemContent, "linkDecoration")}
      >
        {item.name}
        {' '}
        <DescriptionOutlinedIcon className={classes.scriptIcon} />
      </Typography>
    </ListItem>
  );
};

export default withStyles(itemStyles)(FavoriteScriptItem);
