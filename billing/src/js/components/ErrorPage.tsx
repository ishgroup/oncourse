import React from 'react';
import ErrorIcon from '@mui/icons-material/Error';
import Typography from '@mui/material/Typography';
import { makeAppStyles } from '../styles/makeStyles';

const useStyles = makeAppStyles()(() => ({
  errorTitle: {
    display: 'flex',
    alignItems: 'center',
    marginBottom: 20,
  },
}));

const ErrorPage = () => {
  const { classes } = useStyles();

  return (
    <div>
      <Typography variant="h3" component="h3" className={classes.errorTitle}>
        <ErrorIcon fontSize="large" />
        &nbsp;Oops!
      </Typography>
      <Typography variant="subtitle1" component="div">
        Something went wrong in creating your new server. Our team are onto it and will be in touch sort it out.
      </Typography>
    </div>
  );
};

export default ErrorPage;
