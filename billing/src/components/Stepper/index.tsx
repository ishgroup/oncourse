/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { makeStyles, Theme, createStyles, withStyles } from "@material-ui/core/styles";
import { Container } from "@material-ui/core";
import clsx from "clsx";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Check from "@material-ui/icons/Check";
import SettingsIcon from "@material-ui/icons/Settings";
import GroupAddIcon from "@material-ui/icons/GroupAdd";
import VideoLabelIcon from "@material-ui/icons/VideoLabel";
import StepConnector from "@material-ui/core/StepConnector";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { StepIconProps } from "@material-ui/core/StepIcon";
import { Steps } from "./Steps";
import CustomButton from "../common/Button";

const QontoConnector = withStyles({
  alternativeLabel: {
    top: 10,
    left: "calc(-50% + 16px)",
    right: "calc(50% + 16px)",
  },
  active: {
    "& $line": {
      borderColor: "#784af4",
    },
  },
  completed: {
    "& $line": {
      borderColor: "#784af4",
    },
  },
  line: {
    borderColor: "#eaeaf0",
    borderTopWidth: 3,
    borderRadius: 1,
  },
})(StepConnector);

const useQontoStepIconStyles = makeStyles({
  root: {
    color: "#eaeaf0",
    display: "flex",
    height: 22,
    alignItems: "center",
  },
  active: {
    color: "#784af4",
  },
  circle: {
    width: 8,
    height: 8,
    borderRadius: "50%",
    backgroundColor: "currentColor",
  },
  completed: {
    color: "#784af4",
    zIndex: 1,
    fontSize: 18,
  },
});

function QontoStepIcon(props: StepIconProps) {
  const classes = useQontoStepIconStyles();
  const { active, completed } = props;

  return (
    <div
      className={clsx(classes.root, {
        [classes.active]: active,
      })}
    >
      {completed ? <Check className={classes.completed} /> : <div className={classes.circle} />}
    </div>
  );
}

const ColorlibConnector = withStyles({
  alternativeLabel: {
    top: 22,
  },
  active: {
    "& $line": {
      backgroundImage:
        "linear-gradient( 95deg,rgb(242,113,33) 0%,rgb(233,64,87) 50%,rgb(138,35,135) 100%)",
    },
  },
  completed: {
    "& $line": {
      backgroundImage:
        "linear-gradient( 95deg,rgb(242,113,33) 0%,rgb(233,64,87) 50%,rgb(138,35,135) 100%)",
    },
  },
  line: {
    height: 3,
    border: 0,
    backgroundColor: "#eaeaf0",
    borderRadius: 1,
  },
})(StepConnector);

const useColorlibStepIconStyles = makeStyles({
  root: {
    backgroundColor: "#ccc",
    zIndex: 1,
    color: "#fff",
    width: 50,
    height: 50,
    display: "flex",
    borderRadius: "50%",
    justifyContent: "center",
    alignItems: "center",
  },
  active: {
    backgroundImage:
      "linear-gradient( 136deg, rgb(242,113,33) 0%, rgb(233,64,87) 50%, rgb(138,35,135) 100%)",
    boxShadow: "0 4px 10px 0 rgba(0,0,0,.25)",
  },
  completed: {
    backgroundImage:
      "linear-gradient( 136deg, rgb(242,113,33) 0%, rgb(233,64,87) 50%, rgb(138,35,135) 100%)",
  },
});

function ColorlibStepIcon(props: StepIconProps) {
  const classes = useColorlibStepIconStyles();
  const { active, completed } = props;

  const icons: { [index: string]: React.ReactElement } = {
    1: <SettingsIcon />,
    2: <GroupAddIcon />,
    3: <VideoLabelIcon />,
  };

  return (
    <div
      className={clsx(classes.root, {
        [classes.active]: active,
        [classes.completed]: completed,
      })}
    >
      {icons[String(props.icon)]}
    </div>
  );
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: "100%",
      marginTop: "64px",
      height: "calc(100vh - 64px)",
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
    },
    stepWrapper: {
      width: "400px",
    },
    button: {
      marginRight: theme.spacing(1),
    },
    instructions: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
    buttonsWrapper: {
      display: "flex",
      justifyContent: "space-between",
      marginTop: "30px",
    },
  }),
);

const getSteps = () => {
  return ["Choose a name...", "Choose template...", "Contact details...", "Organisation details...", "All done!"];
}

// const getStepContent = (step: number) => {
//   switch (step) {
//     case 0:
//       return "Choose a name...";
//     case 1:
//       return "Choose template...";
//     case 2:
//       return "Contact details...";
//     case 3:
//       return "Organisation details...";
//     case 4:
//       return "All done!";
//     default:
//       return "Unknown step";
//   }
// }

export default function CustomizedSteppers() {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const steps = getSteps();

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  return (
    <div className={classes.root}>
      {/*<Stepper alternativeLabel activeStep={activeStep}>*/}
      {/*  {steps.map((label) => (*/}
      {/*    <Step key={label}>*/}
      {/*      <StepLabel>{label}</StepLabel>*/}
      {/*    </Step>*/}
      {/*  ))}*/}
      {/*</Stepper>*/}
      {/*<Stepper alternativeLabel activeStep={activeStep} connector={<QontoConnector />}>*/}
      {/*  {steps.map((label) => (*/}
      {/*    <Step key={label}>*/}
      {/*      <StepLabel StepIconComponent={QontoStepIcon}>{label}</StepLabel>*/}
      {/*    </Step>*/}
      {/*  ))}*/}
      {/*</Stepper>*/}
      {/*<Stepper alternativeLabel activeStep={activeStep} connector={<ColorlibConnector />}>*/}
      {/*  {steps.map((label) => (*/}
      {/*    <Step key={label}>*/}
      {/*      <StepLabel StepIconComponent={ColorlibStepIcon}>{label}</StepLabel>*/}
      {/*    </Step>*/}
      {/*  ))}*/}
      {/*</Stepper>*/}

      <div className={classes.stepWrapper}>
        {/*{Steps[activeStep] && Steps[activeStep]()}*/}
        {Steps[activeStep]}

        <div>
          {activeStep === steps.length ? (
            <div>
              <Typography className={classes.instructions}>
                All steps completed - you&apos;re finished
              </Typography>
              <Button onClick={handleReset} className={classes.button}>
                Reset
              </Button>
            </div>
          ) : (
            <div className={classes.buttonsWrapper}>
              <Button disabled={activeStep === 0} onClick={handleBack} className={classes.button}>
                Back
              </Button>
              <CustomButton
                variant="contained"
                color="primary"
                onClick={handleNext}
                className={classes.button}
              >
                {activeStep === steps.length - 1 ? "Finish" : "Next"}
              </CustomButton>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}