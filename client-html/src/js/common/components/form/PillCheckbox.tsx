import React, { useCallback, useRef } from "react";
import Chip from "@material-ui/core/Chip";
import clsx from "clsx";
import { withStyles, createStyles } from "@material-ui/core/styles";
import { darken } from "@material-ui/core/styles/colorManipulator";
import { grey } from "@material-ui/core/colors";

const styles = theme =>
  createStyles({
    input: {
      position: "absolute",
      opacity: 0,
      zIndex: -1
    },
    unChecked: {
      background: grey[200],
      "&:focus": {
        background: grey[200]
      },
      "&:hover": {
        background: darken(grey[200], 0.1)
      },
      color: theme.palette.getContrastText(grey[200])
    },
    checked: {
      background: grey[400],
      "&:focus": {
        background: grey[400]
      },
      "&:hover": {
        background: darken(grey[400], 0.1)
      },
      color: theme.palette.getContrastText(grey[400])
    },
    disabled: {
      pointerEvents: "none"
    }
  });

const PillCheckboxInput = props => {
  const inputRef = useRef<HTMLInputElement>();

  const {
    classes, chackedLabel, uncheckedLabel, className, checked, onChange, disabled
  } = props;

  const onClick = useCallback(() => {
    inputRef.current.click();
  }, [inputRef.current]);

  return (
    <div className="cursor-pointer">
      <input type="checkbox" ref={inputRef} checked={checked} className={classes.input} onChange={onChange} />
      <Chip
        label={checked ? chackedLabel : uncheckedLabel}
        className={clsx(className, {
          [classes.checked]: checked,
          [classes.unChecked]: !checked,
          [classes.disabled]: disabled
        })}
        onClick={onClick}
      />
    </div>
  );
};

export const PillCheckbox = withStyles(styles)(PillCheckboxInput);

export const PillCheckboxField = props => {
  const {
    input, color, chackedLabel, uncheckedLabel, className, disabled
  } = props;

  return (
    <PillCheckbox
      checked={input.value}
      chackedLabel={chackedLabel}
      uncheckedLabel={uncheckedLabel}
      className={className}
      onChange={input.onChange}
      color={color}
      disabled={disabled}
    />
  );
};
