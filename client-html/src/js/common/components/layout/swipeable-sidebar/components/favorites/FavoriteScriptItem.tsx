import React from "react";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import ListItem from "@material-ui/core/ListItem";
import Favorite from "@material-ui/icons/Favorite";
import FavoriteBorder from "@material-ui/icons/FavoriteBorder";
import { Script } from "@api/model";
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import { AnyArgFunction } from "../../../../../../model/common/CommonFunctions";
import itemStyles from "./itemStyles";

interface Props {
  item: Script;
  toggleFavorite: AnyArgFunction;
  isEditing?: boolean;
  favorite?: boolean;
  classes?: any;
  setScriptIdSelected?: AnyArgFunction;
  setExecMenuOpened?: AnyArgFunction;
}

const FavoriteScriptItem = (props: Props) => {
  const {
    classes, item, isEditing, toggleFavorite, favorite, setScriptIdSelected, setExecMenuOpened
  } = props;

  const openExecScriptModal = scriptId => {
    setScriptIdSelected(scriptId);
    setExecMenuOpened(true);
  };

  const handleIconClick = () => toggleFavorite(item.id);

  return (
    <ListItem
      dense
      disableGutters
      className={clsx(classes.listItem, {
        [classes.listItemEditing]: isEditing
      })}
    >
      {isEditing ? (
        favorite ? (
          <Favorite onClick={handleIconClick} className={clsx(classes.listItemIcon, classes.listItemIconActive)} />
        ) : (
          <FavoriteBorder onClick={handleIconClick} className={clsx(classes.listItemIcon, "invisible")} />
        )
      ) : null}

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
