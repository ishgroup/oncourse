import React, {
  useCallback, useEffect, useRef, useState
} from "react";
import clsx from "clsx";
import makeStyles from "@mui/styles/makeStyles";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { AppTheme } from "../../../../../model/common/Theme";
import { STICKY_HEADER_EVENT } from "../../../../../constants/Config";

const useStyles = makeStyles((theme: AppTheme) => ({
  fullScreenTitleWrapper: {
    minHeight: 51,
  },
  fullScreenTitleItem: {
    position: "fixed",
    left: 0,
    top: 0,
    maxWidth: "calc(100% - 224px)",
    width: "100%",
    zIndex: 1200,
  },
  titleFields: {
    transform: "translateY(200%)",
    visibility: "hidden",
    transition: theme.transitions.create("all", {
      duration: theme.transitions.duration.standard,
      easing: theme.transitions.easing.easeInOut
    }),
  },
  titleText: {
    position: "absolute",
    left: 0,
    transform: "translateY(-200%)",
    visibility: "hidden",
    "&, & > div": {
      transition: theme.transitions.create("all", {
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      }),
    }
  },
  titleTextAlternate: {
    color: `${theme.appBar.headerAlternate.color} !important`,
  },
  titleIn: {
    transform: "translateY(0)",
    visibility: "visible",
  },
  titleWrapper: {
    minHeight: 51,
  },
  hasAvatar: {
    minHeight: 90,
  },
  hasAvatarScrollIn: {
    marginTop: theme.spacing(0),
  },
}));

interface Props {
  Avatar?: React.FC<{
    avatarSize: number,
    disabled: boolean
  }>;
  title: any;
  twoColumn: boolean,
  fields?: any;
  hide?: boolean;
  otherClasses?: any;
  hideGap?: boolean;
  warpperGap?: number;
  titleGap?: number;
  truncateTitle?: boolean;
}

const FullScreenStickyHeader = React.memo<Props>(props => {
  const {
    Avatar,
    title,
    fields,
    twoColumn,
    hide,
    otherClasses,
    hideGap,
    warpperGap = 51,
    titleGap = 51,
    truncateTitle,
  } = props;

  const classes = { ...useStyles(), ...otherClasses };

  const wrapperRef = useRef<any>();
  const [onItemHover, setOnItemHover] = useState<boolean>(false);
  const [onItemClick, setOnItemClick] = useState<boolean>(false);
  const [isStuck, setIsStuck] = useState<boolean>(false);

  const handlerWrappper = useCallback((e, eventType) => {
    const hasCurrentContainer = wrapperRef.current && wrapperRef.current.contains(e.target);

    if (hasCurrentContainer) {
      if (eventType === "mousedown") setOnItemClick(true);
      if (eventType === "mouseover") setOnItemHover(true);
    } else {
      if (eventType === "mousedown") setOnItemClick(false);
      if (eventType === "mouseover") setOnItemHover(false);
    }
  }, []);

  const onWrapperClick = useCallback(e => {
    handlerWrappper(e, "mousedown");
  }, []);

  const onWrapperHover = useCallback(e => {
    handlerWrappper(e, "mouseover");
  }, []);

  const onStickyChange = useCallback(e => {
    if (isStuck !== e.detail.stuck) {
      setIsStuck(e.detail.stuck);
    }
  }, [isStuck]);

  useEffect(() => {
    document.addEventListener(STICKY_HEADER_EVENT, onStickyChange);
    return () => {
      document.removeEventListener(STICKY_HEADER_EVENT, onStickyChange);
    };
  }, [onStickyChange]);

  useEffect(() => {
    window.addEventListener("mousedown", onWrapperClick);
    window.addEventListener("mouseover", onWrapperHover);
    return () => {
      window.removeEventListener("mousedown", onWrapperClick);
      window.removeEventListener("mouseover", onWrapperHover);
    };
  }, []);

  const showTitleText = !onItemHover && !onItemClick;

  const showTitleOnly = twoColumn && isStuck;

  return (
    <Grid
      container
      columnSpacing={3}
      className={clsx("align-items-center", hide && "d-none")}
      ref={wrapperRef}
      style={{ minHeight: !Avatar && !hideGap ? `${warpperGap}px` : Avatar ? "60px" : "auto" }}
    >
      <Grid
        item
        xs={12}
        className={clsx("centeredFlex", twoColumn && classes.fullScreenTitleItem, "mt-3", showTitleOnly && "mt-1")}
        columnSpacing={3}
      >
        <Avatar
          avatarSize={showTitleOnly ? 40 : 90}
          disabled={showTitleOnly}
        />
        <Grid
          columnSpacing={3}
          container
          item
          xs={Avatar ? 10 : 12}
          className={clsx("relative overflow-hidden align-items-center")}
          style={{ minHeight: `${titleGap}px` }}
        >
          <Grid
            container
            item
            xs={12}
            className={clsx(classes.titleText, { [classes.titleIn]: showTitleText || showTitleOnly || !fields })}
          >
            <Typography
              variant="h5"
              display="block"
              component="div"
              className={clsx("w-100", showTitleOnly && "appHeaderFontSize",
                { [classes.titleTextAlternate]: showTitleOnly },
                { "text-truncate text-nowrap pr-2": truncateTitle })}
            >
              {title}
            </Typography>
          </Grid>
          {fields && (
            <Grid
              container
              item
              xs={12}
              className={clsx(classes.titleFields, { [classes.titleIn]: !showTitleText && !showTitleOnly })}
            >
              {fields}
            </Grid>
          )}
        </Grid>
      </Grid>
    </Grid>
  );
});

export default FullScreenStickyHeader;
