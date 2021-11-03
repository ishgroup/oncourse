/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Stepper from '@mui/material/Stepper';
import React from 'react';
import { makeAppStyles } from '../../styles/makeStyles';

const useStyles = makeAppStyles()((theme, _params, createRef) => {
  const stepLabelIconContainer = {
    ref: createRef(),
  };

  const stepLabelCompleted = {
    ref: createRef(),
  };

  return {
    stepRoot: {
      marginBottom: 20,
    },
    stepCompleted: {
      [`& .${stepLabelCompleted.ref}`]: {
        color: '#37caad',
      },
      [`& .${stepLabelIconContainer.ref}`]: {
        '& > svg': {
          color: '#37caad',
        },
      },
    },
    stepLabelDisabled: {
      [`& .${stepLabelIconContainer.ref}`]: {
        '& > svg': {
          color: theme.palette.primary.contrastText,
          '& > text': {
            fill: theme.palette.primary.main
          }
        }
      }
    },
    stepperRoot: {
      backgroundColor: 'transparent',
      marginBottom: theme.spacing(8),
      paddingTop: '70%',
      paddingLeft: 20,
      textAlign: 'left'
    },
    stepLabelIconContainer,
    stepLabelCompleted,
  };
});

interface Props {
  items: string[];
  activeStep: number;
  completed?: boolean;
}

const LeftStepper = ({ completed, items, activeStep }: Props) => {
  const { classes } = useStyles();

  return (
    <Stepper
      activeStep={completed ? items.length : activeStep}
      orientation="vertical"
      connector={<></>}
      classes={{
        root: classes.stepperRoot,
      }}
    >
      {items.map((label) => (
        <Step
          key={label}
          classes={{
            root: classes.stepRoot,
            completed: classes.stepCompleted,
          }}
        >
          <StepLabel
            classes={{
              root: 'p-0',
              disabled: classes.stepLabelDisabled,
              iconContainer: classes.stepLabelIconContainer,
              completed: classes.stepLabelCompleted,
            }}
          >
            {label}
          </StepLabel>
        </Step>
      ))}
    </Stepper>
  );
};

export default LeftStepper;
