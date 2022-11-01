import React, { useCallback, useRef } from "react";
import { WrappedFieldProps } from "redux-form";
import { Button, ButtonGroup, Typography } from "@mui/material";
import clsx from "clsx";
import { makeAppStyles } from "../../styles/makeStyles";
import { alpha } from "@mui/material/styles";

interface Props extends WrappedFieldProps {
  chackedLabel: string;
  uncheckedLabel: string;
  className?: string;
  disabled?: boolean;
}

const useStyles = makeAppStyles(theme => ({
  root: {
    "& $button": {
      textTransform: "none",
      fontWeight: 400,
      "&:not(:last-of-type):not($checked):hover, &:not(:last-of-type):not(:hover)$checked": {
        borderRightColor: alpha(theme.palette.primary.main, 0.5),
      },
      "&:not($checked)": {
        color: theme.palette.text.primary,
        borderColor: theme.palette.divider
      },
      "&$checked": {
        fontWeight: 500,
        color: theme.palette.primary.main,
      }
    }
  },
  button: {},
  checked: {}
}));

export const ToogleCheckbox = ({
 input, chackedLabel, uncheckedLabel, className, disabled 
}: Props) => {
  const inputRef = useRef<HTMLInputElement>();

  const onClick = useCallback(() => {
    inputRef.current.click();
  }, [inputRef.current]);
  
  const classes = useStyles();

  return (
    <ButtonGroup
      classes={{
        root: classes.root
      }} 
      variant="outlined" 
      disabled={disabled} 
      className={className}
    >
      <input type="checkbox" ref={inputRef} checked={input.value} hidden onChange={input.onChange} />
      <Button
        classes={{
          root: clsx(classes.button, !input.value && classes.checked)
        }}
        onClick={onClick}
      >
        <Typography variant="body2" fontWeight="inherit">
          {uncheckedLabel}
        </Typography>
      </Button>
      <Button
        classes={{
          root: clsx(classes.button, input.value && classes.checked)
        }}
        onClick={onClick}
      >
        <Typography variant="body2" noWrap fontWeight="inherit">
          {chackedLabel}
        </Typography>
      </Button>
    </ButtonGroup>
  );
};
