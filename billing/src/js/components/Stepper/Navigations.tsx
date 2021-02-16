import React from "react";
import { Button, CircularProgress } from "@material-ui/core";
import { connect } from "react-redux";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import CustomButton from "../common/Button";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    button: {
      marginRight: theme.spacing(1),
    },
    buttonsWrapper: {
      display: "flex",
      justifyContent: "space-between",
      marginTop: "30px",
    },
    loading: {
      height: "25px!important",
      width: "25px!important",
    }
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
              className={classes.button}
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
            >
              {loading ?
                <CircularProgress className={classes.loading}/> :
                activeStep === steps.length - 2 ? "Finish" : "Next"}
            </CustomButton>
        </div>
      )}
    </div>
  )
}

const mapStateToProps = (state: any) => ({
  loading: state.creatingCollege.loading,
  serverError: state.creatingCollege.serverError
});

export default connect(mapStateToProps, null)(Navigation);
