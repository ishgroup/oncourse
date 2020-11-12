import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import ButtonBase from "@material-ui/core/ButtonBase";
import Popper from "@material-ui/core/Popper";
import { HuePicker, AlphaPicker } from "react-color";
import { ClickAwayListener, Theme } from "@material-ui/core";
import { createStyles, withStyles } from "@material-ui/core/styles";
import Paper from "@material-ui/core/Paper";
import Grow from "@material-ui/core/Grow";
import { WrappedFieldProps } from "redux-form";
import { usePrevious } from "../../utils/hooks";

interface ColorPickerWrapperProps {
  input?: WrappedFieldProps["input"];
  classes?: any;
  theme?: Theme;
}

interface ColorPickerBaseProps extends ColorPickerWrapperProps {
  color: string;
  TransitionProps: any;
  setAnchorEl: any;
}

const styles = createStyles(({ spacing, transitions, palette, zIndex }: Theme) => ({
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
    width: spacing(3),
    height: spacing(3),
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
  ({ classes, color, input: { value, onChange }, TransitionProps, setAnchorEl }) => {
    const [hueColor, setHueColor] = useState(null);
    const [alfaColor, setAlfaColor] = useState(null);

    const hueRef = useRef<any>();
    const alfaRef = useRef<any>();

    const prevHue = usePrevious(hueColor);

    const onHueChange = useCallback(color => {
      setHueColor(color.hsv);
      setAlfaColor(color.hsv);
    }, []);

    const onAlfaChange = useCallback(
      color => {
        setHueColor({ h: color.hsv.h, s: color.hsv.a, v: color.hsv.v, a: 1 });
        setAlfaColor(color.hsv);
      },
      [hueRef.current]
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
        if (!alfaColor && hueColor) {
          const hueHsv = hueRef.current.state.hsv;

          setAlfaColor({ h: hueHsv.h, s: 1, v: hueHsv.v, a: hueHsv.s });
        }
      },
      [alfaColor, hueColor]
    );

    useEffect(
      () => {
        if (prevHue && prevHue !== hueColor) {
          onChange(hueRef.current.state.hex.replace("#", ""));
        }
      },
      [hueColor, prevHue]
    );

    return (
      <ClickAwayListener onClickAway={handleClickAway}>
        <Grow {...TransitionProps} timeout={200}>
          <div>
            <Paper className={classes.paper}>
              <HuePicker
                ref={hueRef}
                color={hueColor || color}
                onChangeComplete={onHueChange}
                width={168}
                className={classes.bottomOffset}
              />
              <AlphaPicker
                ref={alfaRef}
                className={classes.customAlfa}
                color={alfaColor || color}
                width={168}
                onChangeComplete={onAlfaChange}
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
    <>
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
    </>
  );
});

export default withStyles(styles, { withTheme: true })(ColorPickerWrapper);
