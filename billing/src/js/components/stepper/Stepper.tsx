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
import { createStyles, makeStyles } from '@material-ui/core/styles';
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

export const useStyles = makeStyles((theme: AppTheme) => createStyles({
  root: {
    width: '100%',
    marginTop: '64px',
    height: 'calc(100vh - 64px)',
    display: 'flex'
  },
  content: {
    margin: 'auto',
    minWidth: '400px',
  },
  formWrapper: {
    flex: 1,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '0px 20px 0px 190px'
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

  return (
    <div className={classes.root}>
      <LeftMenu
        items={steps}
        activeStep={activeStep}
      />

      <div className={classes.formWrapper}>
        {serverError ? <ErrorPage /> : (
          <div className={classes.content}>
            {activePage}
          </div>
        )}
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
