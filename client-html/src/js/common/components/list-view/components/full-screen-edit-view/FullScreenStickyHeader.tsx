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
    marginTop: theme.spacing(3),
    position: "fixed",
    left: 0,
    top: 0,
    maxWidth: "calc(100% - 224px)",
    width: "100%",
    zIndex: theme.zIndex.appBar + 1,
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
  disableInteraction?: boolean,
  opened?: boolean,
  fields?: any;
  otherClasses?: any;
  titleGap?: number;
  truncateTitle?: boolean;
}

const FullScreenStickyHeader = React.memo<Props>(props => {
  const {
    Avatar,
    title,
    opened,
    fields,
    twoColumn,
    otherClasses,
    titleGap = 51,
    truncateTitle,
    disableInteraction
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
    if (!disableInteraction) {
      window.addEventListener("mousedown", onWrapperClick);
      window.addEventListener("mouseover", onWrapperHover);
    }
    return () => {
      window.removeEventListener("mousedown", onWrapperClick);
      window.removeEventListener("mouseover", onWrapperHover);
    };
  }, [disableInteraction]);

  const showTitleText = !onItemHover && !onItemClick;

  const showTitleOnly = !opened && twoColumn && isStuck;

  return (
    <Grid
      container
      columnSpacing={3}
      className="align-items-center"
      ref={wrapperRef}
      style={Avatar ? { minHeight: "60px" } : null}
    >
      <Grid
        item
        xs={12}
        className={clsx(
          "centeredFlex",
          !opened && twoColumn ? classes.fullScreenTitleItem : 'mb-2',
          showTitleOnly && "mt-1"
        )}
        columnSpacing={3}
      >
        {Avatar && (
          <Avatar
            avatarSize={showTitleOnly ? 40 : 90}
            disabled={showTitleOnly}
          />
        )}
        <Grid
          columnSpacing={3}
          container
          item
          xs={Avatar ? 10 : 12}
          style={{ minHeight: `${titleGap}px` }}
          className="relative overflow-hidden align-items-center"
        >
          <Grid
            item
            xs={12}
            className={clsx(classes.titleText, { [classes.titleIn]: !opened && (showTitleText || showTitleOnly) })}
          >
            <Typography
              variant="h5"
              display="block"
              component="div"
              className={clsx("w-100", showTitleOnly && "appHeaderFontSize",
                { "text-truncate text-nowrap pr-2": truncateTitle })}
            >
              {title}
            </Typography>
          </Grid>
          <Grid
            item
            xs={12}
            className={clsx(classes.titleFields, { [classes.titleIn]: opened || (!showTitleText && !showTitleOnly) })}
          >
            {fields}
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
});

export default FullScreenStickyHeader;
