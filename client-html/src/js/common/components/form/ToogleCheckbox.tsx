import { Button, ButtonGroup, Typography } from '@mui/material';
import { alpha } from '@mui/material/styles';
import clsx from 'clsx';
import { makeAppStyles } from 'ish-ui';
import React, { useCallback, useRef } from 'react';
import { WrappedFieldProps } from 'redux-form';

interface Props extends WrappedFieldProps {
  chackedLabel: string;
  uncheckedLabel: string;
  className?: string;
  disabled?: boolean;
}

const useStyles = makeAppStyles<void, 'button' | 'checked'>()((theme, p, classes) => ({
  root: {
    [`& .${classes.button}`]: {
      textTransform: "none",
      fontWeight: 400,
      [`&:not(:last-of-type):not(.${classes.checked}):hover, &:not(:last-of-type):not(:hover).${classes.checked}`]: {
        borderRightColor: alpha(theme.palette.primary.main, 0.5),
      },
      [`&:not(.${classes.checked})`]: {
        color: theme.palette.text.primary,
        borderColor: theme.palette.divider
      },
      [`&.${classes.checked}`]: {
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

  const { classes } = useStyles();

  return (
    <ButtonGroup
      classes={{
        root: classes.root
      }}
      variant="outlined"
      disabled={disabled}
      className={className}
    >
      <input type="checkbox" ref={inputRef} checked={input.value} hidden onChange={input.onChange}/>
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
