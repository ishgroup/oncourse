/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState, useRef } from 'react';
import Rx, {Subject} from "rxjs";
import { debounceTime } from "rxjs/operators";
import moment from "moment";
import { makeStyles } from "@material-ui/core/styles";
import { Typography } from "@material-ui/core";
import { connect, Dispatch } from "react-redux";
import Navigation from "../Navigations";
import { checkSiteName, setLoadingValue, setSitenameValue } from "../../../redux/actions";

const useStyles = makeStyles((theme:any) => ({
  textFieldWrapper: {
    minHeight: "61px"
  },
  mainWrapper: {
    minHeight: "50px",
    marginTop: "20px",
  },
  textFieldWrapper2: {
    display: "flex",
    alignItems: "baseline"
  },
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color
  },
  errorMessage: {
    fontSize: "12px",
    color: "#f44336"
  }
}));

const NameForm = (props: any) => {
  const { activeStep,
    steps,
    handleBack,
    handleNext,
    token,
    checkSiteName,
    isValidName,
    sendTokenAgain,
    setSitenameValue,
    setLoadingValue
  } = props;

  const [collegeKey, setCollegeKey] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [subject] = useState(() => new Subject());

  const inputRef = useRef(null);

  const classes = useStyles();

  useEffect(() => {
    inputRef.current.focus();
  }, [inputRef]);

  const setNewCollegeName = (e) => {
    const name: string = e.target.innerText;
    // const matches = name.match(/([\w*-]+)([\w*-]+)([\w*-]+)/gi);
    const matches = name.match(/([a-z0-9*-]+)/gi);
    const validName = name && matches && matches[0] === name;

    if (!validName) {
      setErrorMessage('You can only use letters, numbers and dashes')
    } else {
      if (errorMessage) setErrorMessage("");
    }
    subject.next({ name, token, validName, sendTokenAgain, setLoadingValue })
  }

  useEffect(() => {
    const subscription = subject
      .pipe(debounceTime(500))
      .subscribe((values: {
        name: string,
        token: string,
        validName: boolean,
        sendTokenAgain: boolean,
        setLoadingValue: (value: boolean) => void
      }) => {
        setCollegeKey(values.name);

        if (values.validName) {
          checkSiteName({
            name: values.name,
            token: sendTokenAgain ? values.token : null
          });
          setLoadingValue(true);
        }
    });

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  const handleNextCustom = () => {
    setSitenameValue(collegeKey);
    handleNext();
  }

  const getDate = () => {
    const currentDate = moment(new Date());
    const currentDay = currentDate.date();

    if (currentDay < 15) {
      return currentDate.add(1, 'months').startOf('month').format("D MMMM");
    } else {
      return currentDate.add(2, 'months').startOf('month').format("D MMMM");
    }
  }

  return (
    <form>
      <h2 className={classes.coloredHeaderText}>Give your site a name</h2>
      <Typography>Choose a name for your team or company</Typography>

      <div className={classes.mainWrapper}>
        <div className={classes.textFieldWrapper2}>
          <Typography>https://</Typography>
          <span className="input" onInput={(e) => setNewCollegeName(e)} ref={inputRef} contentEditable/>
          <Typography>.oncourse.cc</Typography>
        </div>
        {errorMessage && (<Typography className={classes.errorMessage}>{errorMessage}</Typography>)}
      </div>

      <Typography>- No credit card required</Typography>
      <Typography>- Free until {getDate()}, then $10/month</Typography>
      <Navigation
        activeStep={activeStep}
        steps={steps}
        handleBack={handleBack}
        handleNext={handleNextCustom}
        disabled={!isValidName}
      />
    </form>
  )
}

const mapStateToProps = (state: any) => ({
  isValidName: state.creatingCollege.isValidName,
  sendTokenAgain: state.creatingCollege.sendTokenAgain,
  token: state.creatingCollege.token,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setSitenameValue: (name: string) => dispatch(setSitenameValue(name)),
  setLoadingValue: (value) => dispatch(setLoadingValue(value)),
  checkSiteName: ({ name, token }) => dispatch(checkSiteName({ name, token })),
});

export default connect(mapStateToProps, mapDispatchToProps)(NameForm as any);