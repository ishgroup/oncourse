import React from 'react';
import ArrowBackIosIcon from '@material-ui/icons/ArrowBackIos';
import { withStyles } from "@material-ui/core/styles";

const styles = theme => ({
  iconBack: {
    fontSize: "1rem",
  },
})

interface Props {
  classes: any,
  text?: string;
}

const IconBack = (props: Props) => {
  const {classes, text} = props;

  return (
    <span className="centeredFlex">
      <ArrowBackIosIcon className={classes.iconBack}/>
      {text &&
        <span>{` ${text}`}</span>
      }
    </span>
  );
};

export default (withStyles(styles)(IconBack));