import React from "react";
import { ChromePicker } from "react-color";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import IconButton from "@material-ui/core/IconButton";
import PaletteIcon from '@material-ui/icons/Palette';
import Button from "@material-ui/core/Button";
import InputAdornment from "@material-ui/core/InputAdornment";
import { InputTextField } from "./InputTextField";

export const ColorPicker: React.FC<any> = props => {
  const {
    input, disabled, onChangeHandler, className
  } = props;

  const [dialogOpen, setDialogOpen] = React.useState<boolean>(false);
  const [color, setColor] = React.useState<any>(input.value);

  const onChange = React.useCallback((e, value) => {
    if (typeof onChangeHandler === "function") {
      onChangeHandler(e, value);
    }
    if (!e.isDefaultPrevented()) {
      input.onChange(value);
    }
  }, [input, onChangeHandler]);

  const onSelectColor = React.useCallback(e => {
    onChange(e, color);
    handleCloseColorDialog();
  }, [color]);

  const handleCloseColorDialog = React.useCallback(() => {
    setDialogOpen(false);
  }, []);

  const onChangeColor = React.useCallback(value => {
    let newColor = value.hex;
    const rgb = value.rgb;

    if (rgb && rgb.a < 1) {
      newColor = `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${rgb.a})`;
    }

    setColor(newColor);
  }, []);

  const onInputChangeColor = React.useCallback(e => {
    const value = e.target.value;
    const { onChange } = input;

    if (onChange) {
      onChange(value);
    }

    setColor(value);
  }, [input]);

  return (
    <div>
      <div className="d-flex">
        <InputTextField
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton aria-label="choose color" onClick={() => !disabled && setDialogOpen(true)}>
                  <PaletteIcon />
                </IconButton>
              </InputAdornment>
            ),
          }}
          {...props}
          onChange={value => onInputChangeColor(value)}
        />
      </div>
      <Dialog onClose={handleCloseColorDialog} aria-labelledby="color-picker-title" open={dialogOpen}>
        <DialogTitle id="color-picker-title">Choose color</DialogTitle>
        <DialogContent>
          <ChromePicker
            color={color}
            onChangeComplete={value => onChangeColor(value)}
            disabled={typeof disabled === "function" ? disabled() : disabled}
            className={className}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseColorDialog} color="primary">
            Close
          </Button>
          <Button onClick={onSelectColor} color="primary" autoFocus>
            Ok
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};
