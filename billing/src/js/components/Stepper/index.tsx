/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect } from "react";
import { makeStyles, Theme, createStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { Steps, items } from "./Steps";
import CustomButton from "../common/Button";
import CustomStepper from "../common/Stepper";

// const SITE_KEY = "6Lcbk0YaAAAAAM5_TdMXM3Grl0CgbbURqgJMnqVf";

declare global {
  interface Window { grecaptcha: any; }
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: "100%",
      marginTop: "64px",
      height: "calc(100vh - 64px)",
      display: "flex",
      alignItems: "center",
    },
    minWidth1200: {
      minWidth: "1200px",
    },
    minWidth800: {
      minWidth: "800px",
    },
    formWrapper: {
      width: "100%",
      display: "flex",
      justifyContent: "center",
    },
    stepWrapper: {
      width: "400px",
    },
    imageStepWrapper: {
      width: "1000px",
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
    actionsContainer: {
      marginBottom: theme.spacing(2),
    },
  }),
);

const getSteps = () => {
  return ["Site name", "Templates", "Contact", "Organisation", "All done!"];
}

export default function CustomizedSteppers() {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const steps = getSteps();

  // useEffect(() => {
  //   const loadScriptByURL = (id, url, callback) => {
  //     const isScriptExist = document.getElementById(id);
  //
  //     if (!isScriptExist) {
  //       let script = document.createElement("script");
  //       script.type = "text/javascript";
  //       script.src = url;
  //       script.id = id;
  //       script.onload = function () {
  //         if (callback) callback();
  //       };
  //       document.body.appendChild(script);
  //     }
  //
  //     if (isScriptExist && callback) callback();
  //   }
  //
  //   loadScriptByURL("recaptcha-key", `https://www.google.com/recaptcha/api.js?render=${SITE_KEY}`, function () {
  //     console.log("Script loaded!");
  //   });
  // }, []);

  const handleOnClick = () => {
    // // setLoading(true);
    // window.grecaptcha.ready(() => {
    //   console.log(1);
    //   window.grecaptcha.execute(SITE_KEY, { action: 'submit' }).then(token => {
    //     console.log(2);
    //     submitData(token);
    //   });
    // });
  }

  const submitData = token => {
    console.log('token', token)
  }

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    handleOnClick();
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  return (
    <div className={clsx(classes.root, activeStep === 1 ? classes.minWidth1200 : classes.minWidth800)}>
      <CustomStepper
        items={items}
        activeStep={activeStep}
        setActiveStep={setActiveStep}
      />

      <div className={classes.formWrapper}>
        <div className={activeStep === 1 ? classes.imageStepWrapper : classes.stepWrapper}>
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
    </div>
  );
}