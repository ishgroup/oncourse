import { AccessStatus } from '@api/model';
import Lock from '@mui/icons-material/Lock';
import Slider from '@mui/material/Slider';
import { lighten } from '@mui/material/styles';
import clsx from 'clsx';
import React, { useEffect, useRef, useState } from 'react';
import { withStyles } from 'tss-react/mui';

const AccessStatusKeys = Object.keys(AccessStatus);

const styles = (theme, p, classes) =>
  ({
    root: {
      padding: "15px 0"
    },
    stepperMarks: {
      position: "absolute",
      width: "100%",
      height: "100%",
      left: "0",
      top: "0",
      display: "flex"
    },
    stepperMark: {
      width: "12px",
      height: "12px",
      zIndex: 2,
      position: "absolute",
      transform: "translate(-50%, -50%)",
      top: "50%",
      borderRadius: "50%",
      backgroundColor: lighten(theme.palette.primary.main, 0.76),
      pointerEvents: "none"
    },
    checkedMark: {
      backgroundColor: theme.palette.primary.main
    },
    neverAllowedMark: {
      fontSize: "9px",
      position: "absolute",
      left: "50%",
      top: "50%",
      transform: "translate(-50%, -50%)"
    },
    overlapped: {
      zIndex: 3
    },
    stepperMarkWrapper: {
      [`&:first-child .${classes.stepperMark}`]: {
        left: "5px"
      }
    },
    stepperThumb: {
      zIndex: 2
    },
    stepperThumbFirst: {
      zIndex: 2,
      marginLeft: "5px"
    }
  });

const SliderStepper = React.memo<any>(props => {
  const prevValue = useRef<number>(undefined);
  
  const [innerValue, setInnerValue] = useState<string>();
  
  const {
    min = 0,
    step = 1,
    className,
    style,
    classes,
    input: { value, onChange },
    item: { alwaysAllowed, neverAllowed },
    headers
  } = props;
  
  useEffect(() => {
    setInnerValue(value);
  }, [value]);

  const numValue = AccessStatusKeys.indexOf(innerValue);

  if (!prevValue.current) prevValue.current = numValue;

  return (
    <div className={clsx(className, "relative")} style={style}>
      <div className={classes.stepperMarks}>
        {headers.map((i, index) => {
          const isNeverAllowed = neverAllowed && neverAllowed.includes(i);
          const isAlwaysAllowed = alwaysAllowed && alwaysAllowed.includes(i);

          return (
            <div key={index} className={clsx("flex-fill relative", classes.stepperMarkWrapper)}>
              <div
                className={clsx(classes.stepperMark, {
                  [classes.checkedMark]:
                  index < numValue
                  || (prevValue.current > numValue && index === numValue)
                  || isAlwaysAllowed
                  || isNeverAllowed,
                  [classes.overlapped]: isNeverAllowed
                })}
              >
                {isNeverAllowed && <Lock className={classes.neverAllowedMark} />}
              </div>
            </div>
          );
        })}
      </div>
      <Slider
        classes={{
          thumb: numValue === 0 ? classes.stepperThumbFirst : classes.stepperThumb,
          root: classes.root
        }}
        value={numValue > -1 ? numValue : 0}
        min={min}
        max={headers.length - 1}
        step={step}
        style={{
          flex: headers.length - 1
        }}
        onChange={(e, val: number) => {
          if (neverAllowed && neverAllowed.includes(headers[val])) {
            return;
          }
          setInnerValue(AccessStatusKeys[val]);
        }}
        onChangeCommitted={(e, val: number) => {
          if (neverAllowed && neverAllowed.includes(headers[val])) {
            return;
          }
          onChange(AccessStatusKeys[val]);
        }}
        marks
      />
      <div className="flex-fill" />
    </div>);
});

export default withStyles(SliderStepper, styles);
