import React from "react";
import {withStyles} from "@material-ui/core/styles";
import clsx from "clsx";
import {Grid} from "@material-ui/core";

const styles = theme => ({
  contentWrapper: {
    padding: "15px 25px",
  },
  fullHeight: {
    minHeight: "100vh",
    height: "100vh",
  },
});

interface Props {
  classes: any;
  sidebar?: any;
  content?: any;
  fullHeight?: boolean;
}

const Layout = (props: Props) => {
  const {classes, sidebar, fullHeight, content} = props;

  return (
    <>
      <Grid>
        <Grid container className={clsx(fullHeight && classes.fullHeight)}>
          {sidebar &&
            sidebar
          }

          <Grid item xs={sidebar ? 10 : 12} className={classes.contentWrapper}>
            {content}
          </Grid>
        </Grid>
      </Grid>
    </>
  );
}

export default (withStyles(styles)(Layout));