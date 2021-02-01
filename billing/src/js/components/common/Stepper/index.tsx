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

const styles = theme => createStyles({
  root: {
    width: "170px",
    height: "calc(100vh - 64px)",
    padding: theme.spacing(4),
    backgroundColor: theme.tabList.listContainer.backgroundColor,
  },
  listContainer: {
    flexDirection: "column",
  },
  listContainerInner: {
    marginBottom: theme.spacing(8),
  },
  listItemRoot: {
    alignItems: "flex-start",
    marginBottom: theme.spacing(3),
    color: theme.palette.common.white,
    fontWeight: 600,
    opacity: 0.6,
    padding: 0,
    "&$selected": {
      opacity: 1,
      backgroundColor: "inherit"
    }
  },
  listItemText: {
    fontSize: "14px",
    fontWeight: 600,
    width: "100%",
    textTransform: "uppercase"
  },
  indicator: {
    display: "none"
  },
  selected: {}
});

export interface TabsListItem {
  label: string;
  component: (props: any) => React.ReactNode;
  labelAdornment?: React.ReactNode;
  expandable?: boolean;
}

interface Props {
  items: TabsListItem[];
  activeStep: number;
  setActiveStep: (step: any) => void;
  classes?: any;
  itemProps?: any;
  customLabels?: any;
  customAppBar?: boolean;
}

const TabsList = React.memo<Props>(({ classes, items, customLabels, customAppBar,
                                      itemProps = {}, activeStep, setActiveStep }) => {
  return (
    <Grid container className={classes.root}>
      <div className={clsx("relative",
        classes.listContainer,
        localStorage.getItem("theme") === "christmas" && "christmasHeader")}
      >
        <div className={classes.listContainerInner}>
          {items.map((i, index) => (
            <ListItem
              button
              selected={index === activeStep}
              classes={{
                root: classes.listItemRoot,
                selected: classes.selected
              }}
              onClick={() => setActiveStep(index)}
              key={index}
            >
              <Typography variant="body2" component="div" color="inherit">
                <div className={classes.listItemText}>{customLabels && customLabels[index] ? customLabels[index] : i.label}</div>
                {i.labelAdornment && (
                  <Typography variant="caption" component="div" className={classes.listItemText}>
                    {i.labelAdornment}
                  </Typography>
                )}
              </Typography>
            </ListItem>
          ))}
        </div>
      </div>
    </Grid>
  );
});

export default withStyles(styles)(TabsList);
