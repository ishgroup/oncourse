import React from "react";
import { connect, Dispatch } from "react-redux";
import { makeStyles } from "@material-ui/core/styles";
import { resetStore, setServerErrorValue } from "../redux/actions";

const useStyles = makeStyles((theme: any) => ({
  link: {
    color: theme.statistics.coloredHeaderText.color,
    '&:hover': {
      cursor: "pointer",
    },
  },
}));

const ErrorPage = (props: any) => {
  const { resetStore, setServerErrorValue } = props;

  const classes = useStyles();

  const clearForm = () => {
    setServerErrorValue(true);
    resetStore()
  }

  return (
    <p>
      Ooops. Something unexpected has happened, please contact ish support or <a className={classes.link} onClick={clearForm}>try again</a>
    </p>
  )
}

const mapStateToProps = (state: any) => ({
  serverError: state.creatingCollege.serverError
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setServerErrorValue: (value: boolean) => dispatch(setServerErrorValue(value)),
  resetStore: dispatch(resetStore())
});

export default connect(mapStateToProps, mapDispatchToProps)(ErrorPage);