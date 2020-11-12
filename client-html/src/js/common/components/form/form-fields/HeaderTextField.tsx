/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  withStyles, Typography, IconButton, Input, FormControl, FormHelperText
} from "@material-ui/core";
import clsx from "clsx";
import Edit from "@material-ui/icons/Edit";
import React, {
  useCallback, useMemo, useRef, useState
} from "react";
import ButtonBase from "@material-ui/core/ButtonBase/ButtonBase";

const styles = theme => ({
  titleControls: {
    display: "flex",
    "align-items": "center",
    "min-width": "0px"
  },
  editButton: {
    color: "#FFF",
    padding: "4px"
  },
  editIcon: {
    fontSize: "17px"
  },
  error: {
    color: theme.palette.error.main,
    marginBottom: "6px"
  },
  cssUnderline: {
    "&:after": {
      borderBottomColor: "#FFF"
    }
  },
  cssUnderlineError: {
    "&:after": {
      borderBottomColor: theme.palette.error.main
    }
  },
  root: {
    color: "#FFF"
  },
  hiddenContainer: {
    display: "none"
  },
  fitWidth: {
    width: "50%"
  }
});

const HeaderTextField: React.FC<any> = ({
  classes,
  margin,
  placeholder,
  input,
  meta: { error, invalid },
  className,
  autoFocus,
  disabled,
  fullWidth
}) => {
  const inputNode = useRef<any>();

  const [isEditing, setIsEditing] = useState(false);

  const onChange = useCallback(
    e => {
      input.onChange(e);
    },
    [input]
  );

  const editField = useCallback(() => {
    setIsEditing(true);
    setTimeout(() => {
      if (inputNode.current) inputNode.current.focus();
    }, 100);
  }, []);

  const onFieldBlur = useCallback(() => {
    setIsEditing(false);
  }, []);

  const hideInput = useMemo(() => isEditing || invalid || !input.value, [isEditing, invalid, input.value]);

  return (
    <>
      <FormControl
        id={input && input.name}
        margin={margin}
        fullWidth={fullWidth}
        className={clsx(className, {
          [classes.hiddenContainer]: !hideInput,
          [classes.fitWidth]: !fullWidth
        })}
      >
        <Input
          classes={{
            root: classes.root,
            underline: clsx({
              [classes.cssUnderline]: !invalid,
              [classes.cssUnderlineError]: invalid
            })
          }}
          inputRef={inputNode}
          value={input.value || ""}
          placeholder={placeholder}
          onChange={onChange}
          onBlur={onFieldBlur}
          onFocus={editField}
          error={invalid}
          autoFocus={autoFocus}
          fullWidth={fullWidth}
          disabled={disabled}
        />
        {error && (
          <FormHelperText
            classes={{
            error: "shakingError"
          }}
            error={invalid}
            className={classes.error}
          >
            {error}
          </FormHelperText>
        )}
      </FormControl>

      <div
        id={input && input.name}
        className={clsx(className, classes.titleControls, {
          [classes.hiddenContainer]: hideInput
        })}
      >
        <ButtonBase onClick={editField} component="div" className="w-100 justify-content-start">
          <Typography color="inherit" className="appHeaderFontSize" noWrap>
            {input.value}
          </Typography>
          {!disabled && (
            <IconButton className={classes.editButton}>
              <Edit className={classes.editIcon} />
            </IconButton>
          )}
        </ButtonBase>
      </div>
    </>
  );
};

export default withStyles(styles)(HeaderTextField);
