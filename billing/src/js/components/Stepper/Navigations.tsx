import React from "react";
import { Button } from "@material-ui/core";
import { connect } from "react-redux";
import clsx from "clsx";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import CustomButton from "../common/Button";
import {State} from "../../redux/reducers";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    button: {
      marginRight: theme.spacing(1),
    },
    buttonsWrapper: {
      display: "flex",
      justifyContent: "flex-end",
      marginTop: "30px",
      paddingBottom: "20px"
    },
    loading: {
      height: "25px!important",
      width: "25px!important",
    },
    declineButton: {
      color: theme.palette.primary.main,
      "&:hover": {
        backgroundColor: "rgba(248, 169, 74, 0.2)"
      }
    },
  }),
);

const Navigation = (props) => {
  const classes = useStyles();
  const { activeStep, steps, handleBack, handleNext, disabled, loading } = props;

  return (
    <div>
      {activeStep !== steps.length - 1 && (
        <div className={classes.buttonsWrapper}>
          {activeStep === 0 ? (<span/>)
          : (
            <Button
              onClick={handleBack}
              className={clsx(classes.button, classes.declineButton)}
              disabled={loading}
            >
              Back
            </Button>
          )}
            <CustomButton
              variant="contained"
              color="primary"
              onClick={handleNext}
              className={classes.button}
              disabled={disabled || loading}
              loading={loading}
            >
              {activeStep === steps.length - 2 ? "Finish" : "Next"}
            </CustomButton>
        </div>
      )}
    </div>
  )
}


const mapStateToProps = (state: State) => ({
  loading: state.loading,
  serverError: state.serverError
});

export default connect(mapStateToProps, null)(Navigation);
