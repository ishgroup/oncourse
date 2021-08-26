/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import clsx from "clsx";
import { createStyles, makeStyles } from '@material-ui/core/styles';
import Typography from "@material-ui/core/Typography";
import LeftMenu from '../common/LeftMenu';
import NameForm from './steps/NameForm';
import TemplateForm from './steps/TemplateForm';
import ContactForm from './steps/ContactForm';
import OrganisationForm from './steps/OrganisationForm';
import FinishPage from './steps/FinishPage';
import { setCaptchaToken } from '../../redux/actions';
import ErrorPage from '../ErrorPage';
import { SITE_KEY } from '../../constant/common';
import { SitesPage } from './steps/SitesPage';
import { State } from '../../redux/reducers';
import { ExistingCustomerSteps, NewCustomerSteps, Step } from '../../models/User';
import { AppTheme } from '../../models/Theme';
import { getCookie } from '../../utils';
import iconDots from "../../../images/icon-dots.png";

export const useStyles = makeStyles((theme: AppTheme) => createStyles({
  root: {
    width: '100%',
    marginTop: '0',
    height: '100vh',
    display: 'flex',
    paddingLeft: 250
  },
  content: {
    margin: 'auto',
    maxWidth: 1200,
    padding: theme.spacing(10),
    width: "100%",
  },
  contentInner: {
    backgroundImage: `url(${iconDots})`,
    backgroundPosition: "0 0",
    backgroundSize: 18,
    padding: "48px 48px 130px",
  },
  formItem: {
    position: "relative",
    backgroundColor: "#fff",
    padding: "48px 48px 8px",
  },
  formStep: {
    color: "#888",
  },
  formWrapper: {
    flex: 1,
    display: 'flex',
    alignItems: 'center',
    padding: '0px 20px 0px 20px'
  },
  instructions: {
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  actionsContainer: {
    marginBottom: theme.spacing(2),
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color,
  },
  hasError: {
    padding: theme.spacing(6),
    "& $formItem": {
      padding: theme.spacing(6),
      backgroundColor: theme.palette.error.main,
      color: theme.palette.error.contrastText,
      borderRadius: 4,
    }
  },
  stepsCompleted: {
    padding: theme.spacing(6),
    "& $formItem": {
      padding: theme.spacing(6),
      backgroundColor: theme.palette.primary.main,
      color: theme.palette.error.contrastText,
      borderRadius: 4,
      "& $formStep": {
        color: theme.palette.error.contrastText,
      }
    }
  }
}));

const getComponent = (type: Step, props: any) => {
  switch (type) {
    default:
    case 'Site name':
      return <NameForm {...props} />;
    case 'Templates':
      return <TemplateForm {...props} />;
    case 'Contact':
      return <ContactForm {...props} />;
    case 'Organisation':
      return <OrganisationForm {...props} />;
    case 'All done!':
      return <FinishPage {...props} />;
    case 'Sites':
      return <SitesPage {...props} />;
  }
};

interface Props {
  serverError?: any;
  setCaptchaToken?: any;
  userKey?: string;
}

const Stepper: React.FC<Props> = (
  {
    serverError
  }
) => {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const [steps, setSteps] = React.useState<Step[]>([]);

  useEffect(() => {
    const token = getCookie('JSESSIONID');
    if (token) {
      setSteps([...ExistingCustomerSteps]);
    } else {
      setSteps([...NewCustomerSteps]);
    }
  }, []);

  useEffect(() => {
    const loadScriptByURL = (id, url) => {
      const isScriptExist = document.getElementById(id);

      if (!isScriptExist) {
        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = url;
        script.id = id;
        document.body.appendChild(script);
      }
    };

    loadScriptByURL('recaptcha-key', `https://www.google.com/recaptcha/api.js?render=${SITE_KEY}`);

    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&family=Merriweather:ital,wght@0,300;0,400;0,600;1,300&family=Port+Lligat+Slab&display=swap';
    document.head.appendChild(link);
  }, []);

  const handleNext = () => {
    if (activeStep === steps.length - 1) return;
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const childrenProps = {
    activeStep, steps, handleBack, handleNext
  };

  const activePage = React.useMemo(() => getComponent(steps[activeStep], childrenProps), [activeStep, steps]);
  const completed = activeStep === steps.length - 1;

  return (
    <div className={classes.root}>
      <LeftMenu
        items={steps}
        activeStep={activeStep}
        completed={completed}
      />

      <div className={classes.formWrapper}>
        <div className={classes.content}>
          {serverError ? (
            <div className={clsx(classes.contentInner, classes.hasError)}>
              <div className={classes.formItem}>
                <ErrorPage />
              </div>
            </div>
          ) : (
            <div className={clsx(classes.contentInner, completed && classes.stepsCompleted)}>
              <div className={classes.formItem}>
                <Typography variant="subtitle2" gutterBottom className={classes.formStep}>
                  Step&nbsp;{activeStep + 1}
                </Typography>
                {activePage}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  serverError: state.serverError
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setCaptchaToken: (token) => dispatch(setCaptchaToken(token)),
});

export default connect(mapStateToProps, mapDispatchToProps)(Stepper);
