/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import { withStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import clsx from "clsx";
import ListItem from "@material-ui/core/ListItem";
import createStyles from "@material-ui/core/styles/createStyles";
import onCourseLogoChristmas from "../../../images/onCourseLogoChristmas.png";
import onCourseLogoDark from "../../../images/onCourseLogoDark.png";
import onCourseLogoLight from "../../../images/onCourseLogoLight.png";

const styles = theme => createStyles({
  root: {
    width: "250px",
    height: "100vh",
    padding: theme.spacing(4),
    backgroundColor: theme.tabList.listContainer.backgroundColor,
    position: "fixed",
  },
  listContainer: {
    flexDirection: "column",
    flex: 1,
    textAlign: "center",
    "& > img": {
      maxWidth: 120
    }
  },
  listContainerInner: {
    marginBottom: theme.spacing(8),
    paddingTop: "70%",
  },
  listItemRoot: {
    alignItems: "flex-start",
    marginBottom: theme.spacing(3),
    color: theme.palette.common.black,
    fontWeight: 600,
    opacity: 0.6,
    padding: 0,
    "&$selected": {
      opacity: 1,
      backgroundColor: "inherit",
      "& $listItemText": {
        fontWeight: 600,
      },
      "&:hover": {
        backgroundColor: "inherit",
      }
    },
    '&:hover': {
      cursor: "auto",
    },
  },
  listItemText: {
    fontSize: 16,
    fontWeight: 400,
    width: "100%",
    textTransform: "capitalize",
  },
  indicator: {
    display: "none"
  },
  selected: {}
});


interface Props {
  items: string[];
  activeStep: number;
  classes?: any;
  theme?: any;
}

const TabsList = React.memo<Props>((
  {
    classes,
    items,
    activeStep,
    theme
  }) => {

  const isChristmas = localStorage.getItem("theme") === "christmas";

  return (
    <Grid container className={classes.root}>
      <div className={clsx("relative",
        classes.listContainer,
        localStorage.getItem("theme") === "christmas" && "christmasHeader")}
      >
        {isChristmas ? (
            <img src={onCourseLogoChristmas} className={classes.logo} alt="Logo" />
        ) : (
            <img
                src={onCourseLogoDark}
                className={classes.logo}
                alt="Logo"
            />
        )}
        <div className={classes.listContainerInner}>
          {items.map((i, index) => (
            <ListItem
              selected={index === activeStep}
              classes={{
                root: classes.listItemRoot,
                selected: classes.selected
              }}
              key={index}
            >
              <Typography variant="body2" component="div" color="inherit">
                <div className={classes.listItemText}>{i}</div>
              </Typography>
            </ListItem>
          ))}
        </div>
      </div>
    </Grid>
  );
});

export default withStyles(styles, { withTheme: true })(TabsList);
