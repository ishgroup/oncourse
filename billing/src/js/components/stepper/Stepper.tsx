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
import Typography from '@mui/material/Typography';
import { makeAppStyles } from '../../styles/makeStyles';
import LeftMenu from '../common/LeftMenu';
import NameForm from './steps/NameForm';
import TemplateForm from './steps/TemplateForm';
import ContactForm from './steps/ContactForm';
import OrganisationForm from './steps/OrganisationForm';
import FinishPage from './steps/FinishPage';
import ErrorPage from '../ErrorPage';
import { SITE_KEY } from '../../constant/common';
import { SitesPage } from './steps/sites/SitesPage';
import { Step } from '../../models/User';
import iconDots from '../../../images/icon-dots.png';
import { State } from '../../models/State';
import { setCaptchaToken } from '../../redux/actions/College';

export const useStyles = makeAppStyles()((theme, prop, createRef) => {
  const formItem = {
    ref: createRef(),
    position: 'relative',
    backgroundColor: '#fff',
    padding: '48px 48px 8px',
  } as const;

  const formStep = {
    ref: createRef(),
    color: '#888'
  } as const;

  return {
    root: {
      width: '100%',
      marginTop: '0',
      height: '100vh',
      display: 'flex',
      paddingLeft: 250
    },
    content: {
      margin: 'auto',
      padding: theme.spacing(10),
      width: '100%',
    },
    maxWidth: {
      maxWidth: 1200,
    },
    contentInner: {
      backgroundImage: `url(${iconDots})`,
      backgroundPosition: '0 0',
      backgroundSize: 18,
      padding: '48px 48px 130px',
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
      [`& .${formItem.ref}`]: {
        padding: theme.spacing(6),
        backgroundColor: theme.palette.error.main,
        color: theme.palette.error.contrastText,
        borderRadius: 4,
      }
    },
    stepsCompleted: {
      padding: theme.spacing(6),
      [`& .${formItem.ref}`]: {
        padding: theme.spacing(6),
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.error.contrastText,
        borderRadius: 4,
        [`& .${formStep.ref}`]: {
          color: theme.palette.error.contrastText,
        }
      }
    },
    formItem,
    formStep
  };
});

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
  isNewUser?: boolean;
  steps?: Step[];
}

const Stepper: React.FC<Props> = (
  {
    isNewUser,
    serverError,
    steps
  }
) => {
  const { classes, cx } = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);

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
  const hasSites = steps[activeStep] === 'Sites';
  const completed = hasSites ? false : activeStep === steps.length - 1;

  return (
    <div className={classes.root}>
      <LeftMenu
        items={steps}
        activeStep={activeStep}
        completed={completed}
      />

      <div className={classes.formWrapper}>
        <div className={cx(classes.content, isNewUser && classes.maxWidth)}>
          {serverError ? (
            <div className={cx(classes.contentInner, classes.hasError)}>
              <div className={classes.formItem}>
                <ErrorPage />
              </div>
            </div>
          ) : (
            <div className={cx(classes.contentInner, completed && classes.stepsCompleted)}>
              <div className={classes.formItem}>
                {!hasSites && (
                  <Typography variant="subtitle2" gutterBottom className={classes.formStep}>
                    Step&nbsp;
                    {activeStep + 1}
                  </Typography>
                )}
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
  serverError: state.college.serverError
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setCaptchaToken: (token) => dispatch(setCaptchaToken(token)),
});

export default connect(mapStateToProps, mapDispatchToProps)(Stepper);
