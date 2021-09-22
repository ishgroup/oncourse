/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useEffect, useMemo, useRef, useState
} from 'react';
import Typography from '@mui/material/Typography';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import moment from 'moment';
import { makeAppStyles } from '../../../styles/makeStyles';
import Navigation from '../Navigations';
import { checkSiteName, setCollegeKey, setLoadingValue } from '../../../redux/actions';
import { SITE_KEY } from '../../../constant/common';
import { State } from '../../../redux/reducers';
import { usePrevious } from '../../../hooks/usePrevious';

const useStyles = makeAppStyles()((theme:any) => ({
  root: {
    minWidth: '400px'
  },
  textFieldWrapper: {
    minHeight: '61px'
  },
  mainWrapper: {
    minHeight: '50px',
    marginTop: '20px',
    paddingBottom: 40,
    position: 'relative',
  },
  textFieldWrapper2: {
    display: 'flex',
    alignItems: 'baseline'
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color,
    fontFamily: theme.typography.fontFamily2,
    fontSize: theme.spacing(4),
    fontWeight: 100,
    marginTop: 0,
    marginBottom: theme.spacing(2),
  },
  errorMessage: {
    fontSize: 14,
    color: '#f44336',
    position: 'absolute',
    bottom: 12,
    left: 0,
  },
  input: {
    minWidth: 200
  },
  domainName: {
    fontSize: 35,
    fontFamily: theme.typography.fontFamily2
  },
  info: {
    position: 'absolute',
    bottom: -70,
    padding: 5,
    backgroundColor: '#fff',
    left: 0,
    right: 0,
    maxWidth: '60%',
    margin: '0 auto',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: 12
  }
}));

const getDate = () => {
  const currentDate = moment(new Date());
  const currentDay = currentDate.date();

  if (currentDay < 15) {
    return currentDate.add(1, 'months').startOf('month').format('D MMMM');
  }
  return currentDate.add(2, 'months').startOf('month').format('D MMMM');
};

const NameForm = (props: any) => {
  const {
    activeStep,
    steps,
    handleBack,
    handleNext,
    checkSiteName,
    collegeKeyFromState,
    isValidName,
    loading,
    sendTokenAgain,
    setSitenameValue,
    setLoadingValue
  } = props;

  const [collegeKey, setCollegeKey] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const inputRef = useRef(collegeKeyFromState);

  const prevProps: {
    loading: boolean,
  } = usePrevious({ loading });

  const { classes, cx } = useStyles();

  useEffect(() => {
    if (prevProps && prevProps.loading && !loading && isValidName) {
      handleNext();
    }
  }, [loading, isValidName]);

  useEffect(() => {
    inputRef.current.focus();
  }, [inputRef]);

  useEffect(() => {
    if (!collegeKey && collegeKeyFromState) inputRef.current.innerText = collegeKeyFromState;
  }, []);

  const setNewCollegeName = (e) => {
    const name: string = e.target.innerText;
    const matches = name.match(/([a-z0-9*]+)/gi);
    const validName = name && matches && matches[0] === name;

    if (name.length > 40) {
      setErrorMessage('Maximum length of college name is 40 characters');
    } else if (!validName) {
      setErrorMessage('You can only use letters and numbers');
    } else if (errorMessage) setErrorMessage('');

    setCollegeKey(name);
  };

  const createCollege = (token?: string | null) => {
    checkSiteName({
      name: collegeKey,
      token
    });
    setLoadingValue(true);
    setSitenameValue(collegeKey);
  };

  const handleNextCustom = () => {
    if (errorMessage || (collegeKeyFromState === collegeKey && !isValidName)) return null;
    if ((collegeKey === collegeKeyFromState || !collegeKey && collegeKeyFromState) && isValidName) return handleNext();

    if (sendTokenAgain) {
      (window as any).grecaptcha.execute(SITE_KEY, { action: 'submit' }).then((token) => {
        // setCaptchaToken(token);
        createCollege(token);
      });
    } else {
      createCollege(null);
    }
  };

  const keyPress = (e: any) => {
    if (e.charCode === 13) {
      e.preventDefault();
      handleNextCustom();
    }
  };

  const date = useMemo(() => getDate(), []);

  return (
    <form className={classes.root}>
      <h2 className={classes.coloredHeaderText}>Let's go! Give your site a name</h2>
      <Typography>Choose a name for your team or company</Typography>

      <div className={classes.mainWrapper}>
        <div className={classes.textFieldWrapper2}>
          <Typography className={classes.domainName}>https://</Typography>
          <span
            className={cx(classes.input, 'input', classes.domainName)}
            onInput={(e) => setNewCollegeName(e)}
            ref={inputRef}
            contentEditable
            onKeyPress={(e) => keyPress(e)}
          />
          <Typography className={classes.domainName}>.oncourse.cc</Typography>
        </div>
        {errorMessage && (<Typography className={classes.errorMessage}>{errorMessage}</Typography>)}
      </div>

      <Typography className={classes.info} component="div">
        <ErrorOutlineIcon fontSize="small" color="primary" />
&nbsp;&nbsp;No credit card required / Free until&nbsp;
        {date}
        , then $10/month
      </Typography>
      <Navigation
        activeStep={activeStep}
        steps={steps}
        handleBack={handleBack}
        handleNext={handleNextCustom}
        disabled={errorMessage}
      />
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  collegeKeyFromState: state.collegeKey,
  isValidName: state.isValidName,
  loading: state.loading,
  sendTokenAgain: state.sendTokenAgain,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setSitenameValue: (name: string) => dispatch(setCollegeKey(name)),
  setLoadingValue: (value) => dispatch(setLoadingValue(value)),
  checkSiteName: ({ name, token }) => dispatch(checkSiteName({ name, token })),
});

export default connect(mapStateToProps, mapDispatchToProps)(NameForm);
