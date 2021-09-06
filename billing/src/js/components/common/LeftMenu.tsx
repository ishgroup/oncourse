/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import { withStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import createStyles from "@material-ui/core/styles/createStyles";
import onCourseLogoChristmas from "../../../images/onCourseLogoChristmas.png";
import onCourseLogoDark from "../../../images/onCourseLogoDark.png";

const styles = theme => createStyles({
  root: {
    width: "250px",
    height: "100vh",
    padding: theme.spacing(4),
    backgroundColor: theme.tabList.listContainer.backgroundColor,
    position: "fixed",
    left: 0,
    top: 0,
    bottom: 0,
  },
  listContainer: {
    flexDirection: "column",
    flex: 1,
    textAlign: "center",
    "& > img": {
      maxWidth: 160,
      position: "relative",
      left: -5
    }
  },
  listContainerInner: {
    marginBottom: theme.spacing(8),
    paddingTop: "70%",
    paddingLeft: 20,
    textAlign: "left"
  },
  stepRoot: {
    marginBottom: 20,
  },
  stepCompleted: {
    "& $stepLabelCompleted": {
      color: "#37caad",
    },
    "& $stepLabelIconContainer": {
      "& > svg": {
        color: "#37caad",
      },
    },
  },
  stepperRoot: {
    backgroundColor: "transparent",
    padding: 0,
  },
  stepLabelDisabled: {
    "& $stepLabelIconContainer": {
      "& > svg": {
        color: theme.palette.primary.contrastText,
        "& > text": {
          fill: theme.palette.primary.main
        }
      }
    }
  },
  stepLabelIconContainer: {},
  stepLabelCompleted: {},
});


interface Props {
  items: string[];
  activeStep: number;
  classes?: any;
  completed?: boolean;
}

const TabsList = React.memo<Props>((
  {
    classes,
    items,
    activeStep,
    completed,
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
          <Stepper
            activeStep={completed ? items.length : activeStep}
            orientation="vertical"
            connector={<></>}
            classes={{
              root: classes.stepperRoot,
            }}
          >
            {items.map((label, index) => (
              <Step
                key={label}
                classes={{
                  root: classes.stepRoot,
                  completed: classes.stepCompleted,
                }}
              >
                <StepLabel
                  classes={{
                    disabled: classes.stepLabelDisabled,
                    iconContainer: classes.stepLabelIconContainer,
                    completed: classes.stepLabelCompleted,
                  }}
                >
                  {label}
                </StepLabel>
              </Step>
            ))}
          </Stepper>
        </div>
      </div>
    </Grid>
  );
});

export default withStyles(styles)(TabsList);
