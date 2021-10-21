/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo } from 'react';
import { connect } from 'react-redux';
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
import { NewCustomerSteps, Step } from '../../models/User';
import { State } from '../../models/State';
import LeftStepper from '../common/LeftStepper';

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
    maxWidth: {
      maxWidth: 1200,
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
  }
};

interface Props {
  serverError?: any;
}

const Stepper = (
  {
    serverError
  }: Props
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
    if (activeStep === NewCustomerSteps.length - 1) return;
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const childrenProps = {
    activeStep, handleBack, handleNext
  };

  const activePage = useMemo(() => getComponent(NewCustomerSteps[activeStep], childrenProps), [activeStep]);
  const completed = activeStep === NewCustomerSteps.length - 1;

  return (
    <div className="root">
      <LeftMenu>
        <LeftStepper
          items={NewCustomerSteps as any}
          activeStep={activeStep}
          completed={completed}
        />
      </LeftMenu>

      <div className="formWrapper">
        <div className={cx('content', classes.maxWidth)}>
          {serverError ? (
            <div className={cx('contentInner', classes.hasError)}>
              <div className={classes.formItem}>
                <ErrorPage />
              </div>
            </div>
          ) : (
            <div className={cx('contentInner', completed && classes.stepsCompleted)}>
              <div className={classes.formItem}>
                <Typography variant="subtitle2" gutterBottom className={classes.formStep}>
                  Step&nbsp;
                  {activeStep + 1}
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
  serverError: state.college.serverError
});


export default connect(mapStateToProps)(Stepper);
