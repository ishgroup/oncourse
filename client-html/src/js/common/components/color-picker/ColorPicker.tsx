/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
 useCallback, useEffect, useMemo, useRef, useState 
} from "react";
import ButtonBase from "@mui/material/ButtonBase";
import Popper from "@mui/material/Popper";
import { AlphaPicker, HuePicker } from "react-color";
import { ClickAwayListener, Theme } from "@mui/material";
import { createStyles, withStyles } from "@mui/styles";
import Paper from "@mui/material/Paper";
import Grow from "@mui/material/Grow";
import { WrappedFieldProps } from "redux-form";
import { stopEventPropagation } from "../../utils/events";

interface ColorPickerWrapperProps {
  input?: WrappedFieldProps["input"];
  classes?: any;
  theme?: Theme;
}

interface ColorPickerBaseProps extends ColorPickerWrapperProps {
  color: any;
  TransitionProps: any;
  setAnchorEl: any;
}

const styles = createStyles(({
 spacing, transitions, palette, zIndex 
}: Theme) => ({
  paper: {
    width: 200,
    padding: spacing(2),
    position: "relative",
    bottom: "12px",
    "&:after": {
      bottom: "0px",
      left: "50%",
      width: "22px",
      height: "10px",
      position: "absolute",
      transform: "translateX(-50%)",
      background: palette.background.paper,
      content: "''",
      zIndex: 1
    }
  },
  colorPickerButton: {
    width: "14px",
    height: "14px",
    borderRadius: "100%",
    transition: transitions.create("transform", {
      duration: transitions.duration.shorter,
      easing: transitions.easing.easeInOut
    }),
    "&:hover": {
      transform: "scale(1.1)"
    }
  },
  popper: {
    zIndex: zIndex.tooltip
  },
  corner: {
    bottom: "6px",
    left: "50%",
    width: "12px",
    height: "12px",
    position: "absolute",
    transform: "translateX(-50%) rotate(45deg)",
    background: palette.background.paper,
    boxShadow: "1px 1px 2px 0px rgba(0,0,0,0.2), 2px 1px 2px -1px rgba(0,0,0,0.12), 1px 1px 1px 0px rgba(0,0,0,0.12);"
  },
  bottomOffset: {
    marginBottom: spacing(1.25)
  },
  customAlfa: {
    "& > div > div:first-child > div": {
      background: "none !important"
    }
  }
}));

const ColorPickerBase = React.memo<ColorPickerBaseProps>(
  ({
 classes, color, input: { value, onChange }, TransitionProps, setAnchorEl 
}) => {
    const [hueColor, setHueColor] = useState(color);
    const [alfaColor, setAlfaColor] = useState(color);

    const hueRef = useRef<any>();
    
    const onHueChange = useCallback(color => {
      setHueColor(color.hsv);
      setAlfaColor(color.hsv);
    }, []);

    const onAlfaChange = useCallback(
      color => {
        setHueColor({
         h: color.hsv.h, s: color.hsv.a, v: color.hsv.v, a: 1
        });
        setAlfaColor(color.hsv);
      },
      []
    );

    const handleClickAway = useCallback(() => {
      setAnchorEl(null);
    }, []);

    useEffect(
      () => {
        if (!hueColor && value) {
          setHueColor("#" + value);
        }
      },
      [value, hueColor]
    );

    useEffect(
      () => {
        const hueHsv = hueRef.current.state.hsv;
        setAlfaColor({
         h: hueHsv.h, s: 1, v: hueHsv.v, a: hueHsv.s
        });
      },
      [hueRef.current]
    );
    
    const onChangeComplete = () => {
      onChange(hueRef.current.state.hex.replace("#", ""));
    };

    return (
      <ClickAwayListener onClickAway={handleClickAway}>
        <Grow {...TransitionProps} timeout={200}>
          <div>
            <Paper className={classes.paper}>
              <HuePicker
                ref={hueRef}
                color={hueColor}
                onChange={onHueChange}
                onChangeComplete={onChangeComplete}
                width={168}
                className={classes.bottomOffset}
              />
              <AlphaPicker
                className={classes.customAlfa}
                color={alfaColor}
                width={168}
                onChange={onAlfaChange}
                onChangeComplete={onChangeComplete}
              />
            </Paper>
            <div className={classes.corner} />
          </div>
        </Grow>
      </ClickAwayListener>
    );
  }
);

const ColorPickerWrapper = React.memo<ColorPickerWrapperProps>(({ classes, theme, input }) => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleClick = useCallback(
    (event: React.MouseEvent<HTMLElement>) => {
      setAnchorEl(anchorEl ? null : event.currentTarget);
    },
    [anchorEl]
  );

  const color = useMemo(() => (input.value ? "#" + input.value : theme.palette.primary.main), [theme, input.value]);

  return (
    <div className="d-flex" onClick={stopEventPropagation}>
      <ButtonBase onClick={handleClick} className={classes.colorPickerButton} style={{ backgroundColor: color }} />
      <Popper open={Boolean(anchorEl)} anchorEl={anchorEl} placement="top" className={classes.popper} transition>
        {({ TransitionProps }) => (
          <ColorPickerBase
            classes={classes}
            color={color}
            input={input}
            TransitionProps={TransitionProps}
            setAnchorEl={setAnchorEl}
          />
        )}
      </Popper>
    </div>
  );
});

export default withStyles(styles, { withTheme: true })(ColorPickerWrapper);
