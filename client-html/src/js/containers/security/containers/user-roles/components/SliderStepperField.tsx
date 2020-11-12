import React, { ComponentClass } from "react";
import { withStyles, createStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import Slider from "@material-ui/core/Slider";
import * as colorManipulator from "@material-ui/core/styles/colorManipulator";
import Lock from "@material-ui/icons/Lock";

import { AccessStatus } from "@api/model";

const AccessStatusKeys = Object.keys(AccessStatus);

const styles = theme =>
  createStyles({
    root: {
      padding: `${theme.spacing(2) - 1}px ${theme.spacing(0)}px`
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
      backgroundColor: colorManipulator.lighten(theme.palette.primary.main, 0.76),
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
      "&:first-child $stepperMark": {
        left: "5px"
      }
    },
    stepperThumb: {
      zIndex: 2
    },
    stepperThumbFirst: {
      zIndex: 2,
      marginLeft: "-1px"
    }
  });

class SliderStepper extends React.PureComponent<any, any> {
  private prevValue: number;

  render() {
    const {
      min = 0,
      step = 1,
      className,
      style,
      classes,
      input: { value, onChange },
      item: { alwaysAllowed, neverAllowed },
      headers
    } = this.props;

    const numValue = AccessStatusKeys.indexOf(value);

    if (!this.prevValue) this.prevValue = numValue;

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
                      || (this.prevValue > numValue && index === numValue)
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

            onChange(AccessStatusKeys[val]);
          }}
          marks
        />
        <div className="flex-fill" />
      </div>
    );
  }
}

export default withStyles(styles)(SliderStepper) as ComponentClass<any>;
