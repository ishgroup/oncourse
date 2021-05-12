import React from "react";
import { connect, Dispatch } from "react-redux";
import { makeStyles } from "@material-ui/core/styles";
import { resetStore, setServerErrorValue } from "../redux/actions";
import {State} from "../redux/reducers";

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
      Oops! Something went wrong in creating your new server. <br/>
      Our team are onto it and will be in touch sort it out.
    </p>
  )
}

const mapStateToProps = (state: State) => ({
  serverError: state.serverError
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setServerErrorValue: (value: boolean) => dispatch(setServerErrorValue(value)),
  resetStore: () => dispatch(resetStore())
});

export default connect(mapStateToProps, mapDispatchToProps)(ErrorPage);
