import React, {
  useCallback, useEffect, useRef, useState
} from "react";
import clsx from "clsx";
import makeStyles from "@mui/styles/makeStyles";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { AppTheme } from "../../../../../model/common/Theme";

const useStyles = makeStyles((theme: AppTheme) => ({
  fullScreenTitleWrapper: {
    minHeight: 51,
  },
  fullScreenTitleItem: {
    position: "fixed",
    top: 0,
    maxWidth: "calc(100% - 224px)",
    width: "100%",
    zIndex: 9999,
  },
  titleFields: {
    transform: "translateY(200%)",
    visibility: "hidden",
    transition: theme.transitions.create("all", {
      duration: "0.8s",
      easing: theme.transitions.easing.easeInOut
    }),
  },
  titleText: {
    position: "absolute",
    left: 0,
    transform: "translateY(-200%)",
    visibility: "hidden",
    "&, & > h5": {
      transition: theme.transitions.create("all", {
        duration: "0.8s",
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
  avatarBlock: {},
  avatarWrapper: {
    "&, & img": {
      transition: theme.transitions.create("all", {
        duration: "0.8s",
        easing: theme.transitions.easing.easeInOut
      }),
    },
  },
  avatarBackdrop: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: theme.palette.action.active,
    position: "absolute",
    height: "100%",
    width: "100%",
    opacity: 0,
    transition: theme.transitions.create("opacity", {
      duration: theme.transitions.duration.standard,
      easing: theme.transitions.easing.easeInOut
    }),
    borderRadius: "100%",
    zIndex: 1,
    color: "#fff",
  },
  avatarRoot: {
    transition: theme.transitions.create("all", {
      duration: "0.8s",
      easing: theme.transitions.easing.easeInOut
    }),
  },
  profileThumbnail: {
    "&:hover $avatarBackdrop": {
      opacity: 1,
    },
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
  avatar?: (classes?: any) => any;
  title: any;
  twoColumn: boolean,
  isScrolling?: boolean;
  fields?: any;
  hide?: boolean;
  otherClasses?: any;
  hideGap?: boolean;
}

const FullScreenStickyHeader = React.memo<Props>(props => {
  const {
    avatar,
    title,
    fields,
    twoColumn,
    hide,
    otherClasses,
    isScrolling,
    hideGap,
  } = props;

  const classes = { ...useStyles(), ...otherClasses };

  const wrapperRef = useRef<any>();
  const [onItemHover, setOnItemHover] = useState<boolean>(false);
  const [onItemClick, setOnItemClick] = useState<boolean>(false);

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

  useEffect(() => {
    if (twoColumn) {
      window.addEventListener("mousedown", onWrapperClick);
      window.addEventListener("mouseover", onWrapperHover);
    }

    return () => {
      window.removeEventListener("mousedown", onWrapperClick);
      window.removeEventListener("mouseover", onWrapperHover);
    };
  }, [twoColumn]);

  const showTitleText = twoColumn && !onItemHover && !onItemClick;
  const showTitleOnly = isScrolling && twoColumn;

  return (
    <Grid
      container
      columnSpacing={3}
      className={clsx("align-items-center", hide && "d-none", !twoColumn && "mb-3", {
        [classes.hasAvatar]: twoColumn && avatar,
        [classes.fullScreenTitleWrapper]: twoColumn && !avatar && !hideGap,
      })}
      ref={wrapperRef}
    >
      <Grid
        item
        xs={12}
        className={clsx("centeredFlex", !twoColumn && "flex-column",
          { [classes.fullScreenTitleItem]: twoColumn, "pt-2": twoColumn, "pt-1": showTitleOnly })}
      >
        {avatar && (
          <div className={clsx(classes.avatarBlock, !twoColumn && "w-100", twoColumn && "mr-3")}>
            {avatar(classes)}
          </div>
        )}
        <Grid container item xs={twoColumn ? 10 : 12} className={clsx("relative overflow-hidden align-items-center", classes.titleWrapper)}>
          <Grid
            container
            item
            xs={12}
            className={clsx(classes.titleText, { [classes.titleIn]: showTitleText || showTitleOnly || !fields })}
          >
            <Typography
              variant="h5"
              display="block"
              className={clsx(!twoColumn && "mt-1", showTitleOnly && "appHeaderFontSize", { [classes.titleTextAlternate]: showTitleOnly })}
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
