import React from 'react';
import Button from '@mui/material/Button';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import CallMadeIcon from '@mui/icons-material/CallMade';
import { connect } from 'react-redux';
import { LoadingButton } from '@mui/lab';
import { makeAppStyles } from '../../styles/makeStyles';
import { State } from '../../redux/reducers';

const useStyles = makeAppStyles()((theme) => ({
  button: {
    marginRight: theme.spacing(1),
  },
  buttonsWrapper: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '30px',
    paddingBottom: '20px',
    position: 'relative',
  },
  loading: {
    height: '25px!important',
    width: '25px!important',
  },
  declineButton: {
    color: '#646464',
    textTransform: 'initial',
    '&:hover': {
      backgroundColor: 'rgba(248, 169, 74, 0.2)'
    }
  },
  nextButton: {
    display: 'flex',
    alignItems: 'center',
    textTransform: 'capitalize',
    '& > svg': {
      transform: 'rotate(45deg)',
    },
  },
}));

const Navigation = (props) => {
  const { classes, cx } = useStyles();
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
                className={cx(classes.button, classes.declineButton)}
                disabled={loading}
                startIcon={<ChevronLeftIcon fontSize="medium" />}
              >
                Back
              </Button>
            )}
          {!hideNextButton && (
            <LoadingButton
              variant="contained"
              color="primary"
              onClick={handleNext}
              className={classes.button}
              disabled={disabled || loading}
              loading={loading}
              disableElevation
            >
              {activeStep === steps.length - 2
                ? 'Finish'
                : (
                  <div className={classes.nextButton}>
                    Next Step&nbsp;&nbsp;
                    <CallMadeIcon fontSize="small" />
                  </div>
                )}
            </LoadingButton>
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

export default connect(mapStateToProps)(Navigation);
