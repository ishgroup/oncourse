/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import CircularProgress from '@mui/material/CircularProgress';
import { green } from '@mui/material/colors';
import InputAdornment from '@mui/material/InputAdornment';
import TextField from '@mui/material/TextField';
import ClassNames from 'clsx';
import { debounce } from 'es-toolkit/compat';
import React from 'react';
import { withStyles } from 'tss-react/mui';

const styles = (theme, p, classes) => ({
  disabled: {},
  focused: {},
  error: {},
  weakPassword: {
    marginBottom: "4px",
    "&:after": {
      borderBottomColor: theme.palette.error.main
    },
    "&:before": {
      width: "25%",
      height: "5px",
      bottom: "-5px",
      backgroundColor: theme.palette.error.main,
      borderBottom: "none"
    }
  },
  weakPasswordHover: {
    [`&:hover:not(.${classes.disabled}):not(.${classes.focused}):not(.${classes.error}):before`]: {
      borderBottom: "none"
    }
  },
  mediumPassword: {
    marginBottom: "4px",
    "&:after": {
      borderBottomColor: theme.palette.primary.main
    },
    "&:before": {
      width: "50%",
      height: "5px",
      bottom: "-5px",
      backgroundColor: theme.palette.primary.main,
      borderBottom: "none"
    }
  },
  mediumPasswordHover: {
    [`&:hover:not(.${classes.disabled}):not(.${classes.focused}):not(.${classes.error}):before`]: {
      height: "5px",
      backgroundColor: theme.palette.primary.main,
      borderBottom: "none"
    }
  },
  strongPassword: {
    marginBottom: "4px",
    "&:after": {
      borderBottomColor: green[600],
      transform: "scaleX(1)"
    },
    "&:before": {
      width: "100%",
      height: "5px",
      bottom: "-5px",
      backgroundColor: green[600],
      borderBottom: "none"
    }
  },
  strongPasswordHover: {
    [`&:hover:not(.${classes.disabled}):not(.${classes.focused}):not(.${classes.error}):before`]: {
      height: "5px",
      backgroundColor: green[600],
      borderBottom: "none"
    }
  },
  input: {
    borderBottom: "1px solid rgba(0, 0, 0, 0.42)"
  },
  loader: {
    marginBottom: "5px"
  },
  root: {
    transition: `opacity ${theme.transitions.duration.enteringScreen}ms ${theme.transitions.easing.easeInOut}`
  }
});

class NewPasswordFieldBase extends React.Component<any, any> {
  state = {
    value: ""
  };

  onInputChange = e => {
    this.setState({
      value: e.target.value
    });
  };

  setInputNode = node => {
    const {
      input: { onChange }
    } = this.props;

    if (node) {
      node.onkeyup = debounce(() => {
        onChange(this.state.value);
      }, 500);
    }
  };

  render() {
    const {
      input,
      classes,
      meta: {
        error, invalid, touched, asyncValidating
      },
      helperText,
      passwordScore,
      ...custom
    } = this.props;

    return (
      <TextField
        fullWidth
        variant="standard"
        value={this.state.value}
        error={touched && invalid}
        slotProps={{
          input: {
            classes: {
              disabled: classes.disabled,
              underline: ClassNames({
                [classes.mediumPasswordHover]: input.value && passwordScore > 1 && passwordScore < 4,
                [classes.strongPasswordHover]: input.value && passwordScore > 3,
                [classes.weakPasswordHover]: input.value && passwordScore < 2
              }),
              root: ClassNames(classes.root, {
                [classes.mediumPassword]: input.value && passwordScore > 1 && passwordScore < 4,
                [classes.strongPassword]: input.value && passwordScore > 3,
                [classes.weakPassword]: input.value && passwordScore < 2,
                "disabled": asyncValidating
              }),
              input: ClassNames({
                [classes.input]:
                  (input.value && passwordScore > 1 && passwordScore < 4) || (input.value && passwordScore > 3)
              })
            },
            endAdornment: asyncValidating ? (
              <InputAdornment position="end">
                <CircularProgress
                  size={24}
                  classes={{
                    root: classes.loader
                  }}
                />
              </InputAdornment>
            ) : undefined
          }
        }}
        inputRef={this.setInputNode}
        helperText={touched && error}
        onChange={this.onInputChange}
        onFocus={input.onFocus}
        onBlur={input.onBlur}
        {...custom}
      />
    );
  }
}

export default withStyles(NewPasswordFieldBase, styles);
