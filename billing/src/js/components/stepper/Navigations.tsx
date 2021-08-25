import React from 'react';
import { connect } from 'react-redux';
import clsx from 'clsx';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import CallMadeIcon from '@material-ui/icons/CallMade';
import CustomButton from '../common/Button';
import { State } from '../../redux/reducers';

const useStyles = makeStyles((theme: Theme) => createStyles({
  button: {
    marginRight: theme.spacing(1),
  },
  buttonsWrapper: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '30px',
    paddingBottom: '20px',
    position: "relative",
  },
  loading: {
    height: '25px!important',
    width: '25px!important',
  },
  declineButton: {
    color: "#646464",
    textTransform: "initial",
    '&:hover': {
      backgroundColor: 'rgba(248, 169, 74, 0.2)'
    }
  },
  nextButton: {
    display: "flex",
    alignItems: "center",
    textTransform: "capitalize",
    "& > svg": {
      transform: "rotate(45deg)",
    },
  },
}));

const Navigation = (props) => {
  const classes = useStyles();
  const {
    activeStep, steps, handleBack, handleNext, disabled, loading, hideNextButton
  } = props;

  return (
    <div>
      {activeStep !== steps.length - 1 && (
        <div className={classes.buttonsWrapper}>
          {activeStep === 0 ? (<span />)
            : (
              <Button
                onClick={handleBack}
                className={clsx(classes.button, classes.declineButton)}
                disabled={loading}
                startIcon={<ChevronLeftIcon fontSize="medium" />}
                color="default"
              >
                Back
              </Button>
            )}
          {!hideNextButton && (
            <CustomButton
              variant="contained"
              color="primary"
              onClick={handleNext}
              className={classes.button}
              disabled={disabled || loading}
              loading={loading}
            >
              {activeStep === steps.length - 2
                  ? 'Finish'
                  : (
                    <div className={classes.nextButton}>
                      Next Step&nbsp;&nbsp;<CallMadeIcon fontSize="small" />
                    </div>
                  )}
            </CustomButton>
          )}
        </div>
      )}
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  loading: state.loading,
  serverError: state.serverError
});

export default connect(mapStateToProps, null)(Navigation);
