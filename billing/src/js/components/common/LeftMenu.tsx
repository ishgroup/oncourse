/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import Grid from '@mui/material/Grid';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import { makeAppStyles } from '../../styles/makeStyles';
import onCourseLogoChristmas from '../../../images/onCourseLogoChristmas.png';
import onCourseLogoDark from '../../../images/onCourseLogoDark.png';

const useStyles = makeAppStyles()((theme, _params, createRef) => {
  const stepLabelIconContainer = {
    ref: createRef(),
  };

  const stepLabelCompleted = {
    ref: createRef(),
  };

  return {
    root: {
      width: '250px',
      height: '100vh',
      padding: theme.spacing(4),
      backgroundColor: theme.tabList.listContainer.backgroundColor,
      position: 'fixed',
      left: 0,
      top: 0,
      bottom: 0,
    },
    listContainer: {
      flexDirection: 'column',
      flex: 1,
      textAlign: 'center',
      '& > img': {
        maxWidth: 160,
        position: 'relative',
        left: -5
      }
    },
    listContainerInner: {
      marginBottom: theme.spacing(8),
      paddingTop: '70%',
      paddingLeft: 20,
      textAlign: 'left'
    },
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
      padding: 0,
    },
    stepLabelIconContainer,
    stepLabelCompleted,
  };
});

interface Props {
  items: string[];
  activeStep: number;
  classes?: any;
  completed?: boolean;
}

const TabsList = React.memo<Props>((
  {
    items,
    activeStep,
    completed,
  }
) => {
  const isChristmas = localStorage.getItem('theme') === 'christmas';

  const { classes, cx } = useStyles();

  return (
    <Grid container className={classes.root}>
      <div className={cx('relative',
        classes.listContainer,
        localStorage.getItem('theme') === 'christmas' && 'christmasHeader')}
      >
        {isChristmas ? (
          <img src={onCourseLogoChristmas} alt="Logo" />
        ) : (
          <img
            src={onCourseLogoDark}
            alt="Logo"
          />
        )}
        <div className={classes.listContainerInner}>
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
        </div>
      </div>
    </Grid>
  );
});

export default TabsList;
