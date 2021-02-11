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
import { connect, Dispatch } from "react-redux";
import { items } from "./Steps";
import LeftMenu from "../common/LeftMenu";
import NameForm from "./Steps/NameForm";
import TemplateForm from "./Steps/TemplateForm";
import ContactForm from "./Steps/ContactForm";
import OrganisationForm from "./Steps/OrganisationForm";
import FinishPage from "./Steps/FinishPage";
import { setCaptchaToken } from "../../redux/actions";

const SITE_KEY = "6Lcbk0YaAAAAAM5_TdMXM3Grl0CgbbURqgJMnqVf";

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
      padding: "40px 20px 40px 190px",
      maxHeight: "calc(100vh - 64px)",
    },
    stepWrapper: {
      width: "400px",
    },
    imageStepWrapper: {
      width: "1000px",
    },
    instructions: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
    actionsContainer: {
      marginBottom: theme.spacing(2),
    },
  }),
);

const getSteps = () => {
  return ["Site name", "Templates", "Contact", "Organisation", "All done!"];
}

const CustomizedSteppers = (props: any) => {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const { setCaptchaToken } = props;

  const steps = getSteps();

  useEffect(() => {
    const loadScriptByURL = (id, url, callback) => {
      const isScriptExist = document.getElementById(id);

      if (!isScriptExist) {
        let script = document.createElement("script");
        script.type = "text/javascript";
        script.src = url;
        script.id = id;
        script.onload = function () {
          if (callback) callback();
        };
        document.body.appendChild(script);
      }

      if (isScriptExist && callback) callback();
    }

    loadScriptByURL("recaptcha-key", `https://www.google.com/recaptcha/api.js?render=${SITE_KEY}`, () => {
      window.grecaptcha.ready(() => {
        window.grecaptcha.execute(SITE_KEY, { action: 'submit' }).then(token => {
          setCaptchaToken(token);
        });
      });
    });
  }, []);

  const handleNext = () => {
    if (activeStep === steps.length - 1) return
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const childrenProps = { activeStep, steps, handleBack, handleNext };

  const stepsComponents = [
    <NameForm {...childrenProps}/>,
    <TemplateForm {...childrenProps}/>,
    <ContactForm {...childrenProps}/>,
    <OrganisationForm {...childrenProps}/>,
    <FinishPage {...childrenProps}/>
  ];

  return (
    <div className={clsx(classes.root, activeStep === 1 ? classes.minWidth1200 : classes.minWidth800)}>
      <LeftMenu
        items={items}
        activeStep={activeStep}
        setActiveStep={setActiveStep}
      />

      <div className={classes.formWrapper}>
        <div className={activeStep === 1 ? classes.imageStepWrapper : classes.stepWrapper}>
          {stepsComponents[activeStep]}
        </div>
      </div>
    </div>
  );
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setCaptchaToken: (token) => dispatch(setCaptchaToken(token)),
});

export default connect(null, mapDispatchToProps)(CustomizedSteppers as any);