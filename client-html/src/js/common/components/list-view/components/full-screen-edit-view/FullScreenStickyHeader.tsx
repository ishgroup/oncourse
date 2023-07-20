import React, { useCallback, useEffect, useRef, useState } from "react";
import clsx from "clsx";
import { ClickAwayListener, Collapse, Grid, Typography } from "@mui/material";
import { Edit } from "@mui/icons-material";
import { APP_BAR_HEIGHT } from "../../../../../constants/Config";
import { makeAppStyles } from "../../../../../../ish-ui/styles/makeStyles";

const useStyles = makeAppStyles(theme => ({
  root: {
    "& $fullScreenTitleItem": {
      background: theme.appBar.headerAlternate.background,
      position: "fixed",
      top: 0,
      zIndex: theme.zIndex.appBar + 1,
      marginTop: 0,
      height: APP_BAR_HEIGHT
    }
  },
  fullScreenTitleItem: {},
  titleFields: {
    transition: theme.transitions.create("all", {
      duration: theme.transitions.duration.standard,
      easing: theme.transitions.easing.easeInOut
    }),
  },
  titleText: {
    maxWidth: "100%",
    transition: theme.transitions.create("all", {
      duration: theme.transitions.duration.standard,
      easing: theme.transitions.easing.easeInOut
    }),
  },
  titleTextOnStuck: {
    color: `${theme.appBar.headerAlternate.color}`,
    "& button": {
      color: `${theme.appBar.headerAlternate.color}`,
    }
  },
  title: {
    cursor: "text",
    position: "relative",
    display: "inline-flex",
    justifyContent: "space-between",
    "&:not($disableInteraction)": {
      paddingBottom: theme.spacing(0.5),
    },
    "&:not($disableInteraction):before": {
      borderBottom: '1px solid transparent',
      left: 0,
      bottom: 0,
      content: "' '",
      position: "absolute",
      right: 0,
      transition: theme.transitions.create("border-bottom-color", {
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      }),
      pointerEvents: "none"
    },
    "&:not($disableInteraction):hover:before": {
      borderBottom: `1px solid ${theme.palette.primary.main}`,
    },
    "&:not($disableInteraction):hover $titleIcon": {
      visibility: 'visible',
    }
  },
  titleIcon: {
    opacity: 0.5,
    visibility: "hidden",
    alignSelf: "flex-end",
    marginBottom: theme.spacing(0.5),
    marginLeft: theme.spacing(1)
  },
  isFixed: {
    marginTop: theme.spacing(4),
    position: "fixed",
    top: 0,
    zIndex: theme.zIndex.appBar + 1,
  },
  disableInteraction: {}
}));

interface Props {
  twoColumn: boolean,
  Avatar?: React.FC<{
    avatarSize: number,
    disabled: boolean
  }>;
  title?: any;
  disableInteraction?: boolean,
  opened?: boolean,
  isFixed?: boolean,
  customStuck?: boolean,
  fields?: any,
  className?: string,
  leftOffset?: number
}

const FullScreenStickyHeader = React.memo<Props>(props => {
  const {
    Avatar,
    title,
    opened,
    fields,
    twoColumn,
    disableInteraction,
    isFixed =  true,
    customStuck,
    leftOffset
  } = props;

  const classes = useStyles();
  
  const [isEditing, setIsEditing] = useState<boolean>(opened);
  const [isStuck, setIsStuck] = useState<boolean>(false);

  const rootRef = useRef<HTMLDivElement>();
  
  useEffect(() => {
    if (!isEditing && opened) {
      setIsEditing(opened);
    }
  }, [opened]);

  const onClickAway = () => {
    if (isEditing && !opened) {
      setIsEditing(false);
    }
  };

  const onStickyChange = useCallback(() => {
    if (typeof customStuck === "boolean") return;

    const top = rootRef.current?.getBoundingClientRect().top;
    if (top < APP_BAR_HEIGHT && !isStuck) {
      setIsStuck(true);
    }
    if (top >= APP_BAR_HEIGHT && isStuck) {
      setIsStuck(false);
    }
    onClickAway();
  }, [isStuck, customStuck]);

  useEffect(() => {
    document.addEventListener("scroll", onStickyChange, true);
    return () => {
      document.removeEventListener("scroll", onStickyChange);
    };
  }, [onStickyChange]);

  const showTitleOnly = twoColumn && (isStuck || customStuck);

  const titleExpanded = opened ? false : !isEditing;

  return (
    <ClickAwayListener onClickAway={onClickAway}>
      <Grid
        ref={rootRef}
        container
        columnSpacing={3}
        className={clsx("align-items-center", Avatar && opened && "mb-2", classes.root)}
        style={Avatar ? { minHeight: "60px" } : null}
      >
        <Grid
          item
          xs={12}
          columnSpacing={3}
          className={clsx(
            "centeredFlex",
            twoColumn && !opened && (isStuck || customStuck) && classes.fullScreenTitleItem,
            !opened && isFixed && twoColumn && classes.isFixed,
          )}
          style={{
            width: `calc(100% - 250px - ${leftOffset || 0}px)`
          }}
        >
          {Avatar && (
            <Avatar
              avatarSize={showTitleOnly && !opened ? 40 : 90}
              disabled={showTitleOnly}
            />
          )}
          <Grid
            columnSpacing={3}
            container
            item
            xs={Avatar ? 10 : 12}
            className="relative overflow-hidden align-items-center"
          >
            <Grid
              item
              xs={12}
            >
              <Collapse in={titleExpanded}>
                <Typography
                  variant="h5"
                  className={clsx(
                    classes.titleText,
                    !twoColumn && !opened && 'mb-1',
                    { [classes.titleTextOnStuck]: showTitleOnly },
                    showTitleOnly ? "appHeaderFontSize centeredFlex" : classes.title,
                    disableInteraction && classes.disableInteraction
                  )}
                  onClick={(showTitleOnly || disableInteraction || opened) ? null : () => setIsEditing(true)}
                >
                  <span className="text-truncate">
                    {title}
                  </span>
                  <Edit color="primary" className={classes.titleIcon} />
                </Typography>
              </Collapse>
            </Grid>
            <Grid
              item
              xs={12}
              className={classes.titleFields}
            >
              <Collapse in={opened || isEditing}>
                {fields}
              </Collapse>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </ClickAwayListener>
  );
});

export default FullScreenStickyHeader;