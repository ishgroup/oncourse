import { Script } from '@api/model';
import DescriptionOutlinedIcon from '@mui/icons-material/DescriptionOutlined';
import ListItem from '@mui/material/ListItem';
import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import { AnyArgFunction } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import itemStyles from './itemStyles';

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

export default withStyles(FavoriteScriptItem, itemStyles);
