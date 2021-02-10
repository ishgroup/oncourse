import React from "react";
import Button from "@material-ui/core/Button";
import CustomButton from "../common/Button";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";

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
  }),
);

const Navigation = (props) => {
  const classes = useStyles();
  const { activeStep, steps, handleBack, handleNext, disabled } = props;

  return (
    <div>
      {activeStep !== steps.length - 1 && (
        <div className={classes.buttonsWrapper}>
          {activeStep === 0 ? (<span/>)
          : (
            <Button onClick={handleBack} className={classes.button}>
              Back
            </Button>
          )}
            <CustomButton
              variant="contained"
              color="primary"
              onClick={handleNext}
              className={classes.button}
              disabled={disabled}
            >
              {activeStep === steps.length - 2 ? "Finish" : "Next"}
            </CustomButton>
        </div>
      )}
    </div>
  )
}

export default Navigation;