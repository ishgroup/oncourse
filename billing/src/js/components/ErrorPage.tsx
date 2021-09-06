import React from "react";
import { connect } from "react-redux";
import { makeStyles } from "@material-ui/core/styles";
import ErrorIcon from "@material-ui/icons/Error";
import { resetStore, setServerErrorValue } from "../redux/actions";
import {State} from "../redux/reducers";
import {Dispatch} from "redux";
import Typography from "@material-ui/core/Typography";
import {AppTheme} from "../models/Theme";

const useStyles = makeStyles((theme: AppTheme) => ({
  errorTitle: {
    display: "flex",
    alignItems: "center",
    marginBottom: 20,
  },
}));

const ErrorPage = (props: any) => {
  const { resetStore, setServerErrorValue } = props;

  const classes = useStyles();

  const clearForm = () => {
    setServerErrorValue(true);
    resetStore()
  };

  return (
    <div>
      <Typography variant="h3" component="h3" className={classes.errorTitle}>
        <ErrorIcon fontSize="large" />&nbsp;Oops!
      </Typography>
      <Typography variant="subtitle1" component="div">
        Something went wrong in creating your new server. Our team are onto it and will be in touch sort it out.
      </Typography>
    </div>
  )
}

const mapStateToProps = (state: State) => ({
  serverError: state.serverError
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setServerErrorValue: (value: boolean) => dispatch(setServerErrorValue(value)),
  resetStore: () => dispatch(resetStore())
});

export default connect(mapStateToProps, mapDispatchToProps)(ErrorPage);
