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
import { makeStyles } from "@material-ui/core/styles";
import { Typography } from "@material-ui/core";
import { connect, Dispatch } from "react-redux";
import Navigation from "../Navigations";
import { checkSiteName, setSitenameValue } from "../../../redux/actions";

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
  const { activeStep, steps, handleBack, handleNext, token, checkSiteName, isValidName, sendTokenAgain, setSitenameValue } = props;

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
    const matches = name.match(/([\w*-]+)/gi);
    const validName = name && matches && matches[0] === name;

    if (!validName) {
      setErrorMessage('Must contain letters, numbers and symbols "-" and "_"')
    } else {
      if (errorMessage) setErrorMessage("");
    }
    subject.next({ name, token, validName, sendTokenAgain })
  }

  useEffect(() => {
    const subscription = subject
      .pipe(debounceTime(1500))
      .subscribe((values: { name: string, token: string, validName: boolean, sendTokenAgain: boolean }) => {
        setCollegeKey(values.name);
        values.validName && checkSiteName({
        name: values.name,
        token: sendTokenAgain ? values.token : null
      })
    });

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  const handleNextCustom = () => {
    setSitenameValue(collegeKey);
    handleNext();
  }

  return (
    <form>
      <h2 className={classes.coloredHeaderText}>Give your site a name</h2>
      <Typography>Choose your name for your team or company</Typography>

      <div className={classes.mainWrapper}>
        <div className={classes.textFieldWrapper2}>
          <Typography>https://</Typography>
          <span className="input" onInput={(e) => setNewCollegeName(e)} ref={inputRef} contentEditable/>
          <Typography>.oncourse.cc</Typography>
        </div>
        {errorMessage && (<Typography className={classes.errorMessage}>{errorMessage}</Typography>)}
      </div>

      <Typography>- No credit card required</Typography>
      <Typography>- Free until 1 month, then $10/month</Typography>
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
  checkSiteName: ({ name, token }) => dispatch(checkSiteName({ name, token })),
});

export default connect(mapStateToProps, mapDispatchToProps)(NameForm as any);