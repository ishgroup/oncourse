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
import LeftMenu from "../common/LeftMenu";
import NameForm from "./Steps/NameForm";
import TemplateForm from "./Steps/TemplateForm";
import ContactForm from "./Steps/ContactForm";
import OrganisationForm from "./Steps/OrganisationForm";
import FinishPage from "./Steps/FinishPage";
import { setCaptchaToken } from "../../redux/actions";
import ErrorPage from "../ErrorPage";
import { SITE_KEY } from "../../constant/common";
import {SitesPage} from "./Steps/SitesPage";
import {State} from "../../redux/reducers";

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

const NewCustomerSteps = ["Site name", "Templates", "Contact", "Organisation", "All done!"] as const;
const ExistingCustomerSteps = ["Sites"] as const;

type NewCustomerStep = typeof NewCustomerSteps[number];
type ExistingCustomerStep = typeof ExistingCustomerSteps[number];
type Step = NewCustomerStep | ExistingCustomerStep;

const getComponent = (type: Step, props: any) => {
  switch (type) {
    default:
    case "Site name":
      return <NameForm {...props}/>
    case "Templates":
      return <TemplateForm {...props}/>
    case "Contact":
      return  <ContactForm {...props}/>
    case "Organisation":
      return  <OrganisationForm {...props}/>
    case "All done!":
      return <FinishPage {...props}/>
    case "Sites":
      return <SitesPage {...props}/>
  }
}

interface Props {
  serverError?: any;
  setCaptchaToken?: any;
}

const CustomizedSteppers: React.FC<Props> = (
  {
    serverError,
    setCaptchaToken
  }) => {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const [steps, setSteps] = React.useState<Step[]>([]);

  useEffect(() => {
    setSteps([...NewCustomerSteps]);
  }, []);

  useEffect(() => {
    const loadScriptByURL = (id, url) => {
      const isScriptExist = document.getElementById(id);

      if (!isScriptExist) {
        let script = document.createElement("script");
        script.type = "text/javascript";
        script.src = url;
        script.id = id;
        document.body.appendChild(script);
      }
    }

    loadScriptByURL("recaptcha-key", `https://www.google.com/recaptcha/api.js?render=${SITE_KEY}`);
  }, []);

  const handleNext = () => {
    if (activeStep === steps.length - 1) return
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const childrenProps = { activeStep, steps, handleBack, handleNext };

  const activePage = React.useMemo(() => getComponent(steps[activeStep], childrenProps), [activeStep]);

  return (
    <div className={clsx(classes.root, activeStep === 1 ? classes.minWidth1200 : classes.minWidth800)}>
      <LeftMenu
        items={steps}
        activeStep={activeStep}
      />

      <div className={classes.formWrapper}>
        {serverError ? <ErrorPage/> : (
          <div className={activeStep === 1 ? classes.imageStepWrapper : classes.stepWrapper}>
            {activePage}
          </div>
        )}
      </div>
    </div>
  );
}

const mapStateToProps = (state: State) => ({
  serverError: state.serverError
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setCaptchaToken: (token) => dispatch(setCaptchaToken(token)),
});

export default connect(mapStateToProps, mapDispatchToProps)(CustomizedSteppers);
