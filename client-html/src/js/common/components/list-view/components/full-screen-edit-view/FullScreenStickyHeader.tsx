import React, {
  useCallback, useEffect, useState
} from "react";
import clsx from "clsx";
import makeStyles from "@mui/styles/makeStyles";
import {
 ClickAwayListener, Collapse, Typography, Grid 
} from "@mui/material";
import { Edit } from "@mui/icons-material";
import Chip from "@mui/material/Chip";
import { AppTheme } from "../../../../../model/common/Theme";
import { APP_BAR_HEIGHT, STICKY_HEADER_EVENT } from "../../../../../constants/Config";

const useStyles = makeStyles((theme: AppTheme) => ({
  fullScreenTitleItem: {
    marginTop: theme.spacing(4),
    position: "fixed",
    top: 0,
    maxWidth: "calc(100% - 224px)",
    zIndex: theme.zIndex.appBar + 1,
  },
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
  titleWrapper: {
    minHeight: 51,
  },
  hasAvatar: {
    minHeight: 90,
  },
  hasAvatarScrollIn: {
    marginTop: theme.spacing(0),
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
  isStuck: {
    marginTop: 0,
    height: APP_BAR_HEIGHT
  },
  disableInteraction: {},
  newChip: {
    backgroundColor: "rgba(69, 170, 5, 0.1)",
    color: "#45AA05",
  }
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
  fields?: any;
  defaultEditable?: boolean;
  isNew?: boolean;
}

const FullScreenStickyHeader = React.memo<Props>(props => {
  const {
    Avatar,
    title,
    opened,
    fields,
    twoColumn,
    disableInteraction,
    defaultEditable = false,
    isNew,
  } = props;

  const classes = useStyles();
  
  const [isEditing, setIsEditing] = useState<boolean>(defaultEditable);
  const [isStuck, setIsStuck] = useState<boolean>(false);

  const onClickAway = () => {
    if (isEditing) {
      setIsEditing(false);
    }
  };

  const onStickyChange = useCallback(e => {
    if (isStuck !== e.detail.stuck) {
      setIsStuck(e.detail.stuck);
    }
    onClickAway();
  }, [isStuck, isEditing]);

  useEffect(() => {
    document.addEventListener(STICKY_HEADER_EVENT, onStickyChange);
    return () => {
      document.removeEventListener(STICKY_HEADER_EVENT, onStickyChange);
    };
  }, [onStickyChange]);
  
  const showTitleOnly = twoColumn && isStuck;

  const titleExpanded = opened ? false : !isEditing;

  return (
    <Grid
      container
      columnSpacing={3}
      className={clsx("align-items-center", Avatar && opened && "mb-2")}
      style={Avatar ? { minHeight: "60px" } : null}
    >
      <ClickAwayListener onClickAway={onClickAway}>
        <Grid
          item
          xs={12}
          className={clsx(
            "centeredFlex",
            twoColumn && !opened && classes.fullScreenTitleItem,
            isStuck && !opened && classes.isStuck
          )}
          columnSpacing={3}
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
                    "align-items-center",
                    classes.titleText,
                    !twoColumn && !opened && 'mb-1',
                    { [classes.titleTextOnStuck]: showTitleOnly },
                    showTitleOnly ? "appHeaderFontSize centeredFlex" : classes.title,
                    disableInteraction && classes.disableInteraction
                  )}
                  onClick={showTitleOnly || disableInteraction || opened ? null : () => setIsEditing(true)}
                >
                  <span className="text-truncate">
                    {title}
                  </span>
                  {isNew && (
                    <Chip label="New" size="small" className={clsx("fontWeight600 text-uppercase ml-1", classes.newChip)} />
                  )}
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
      </ClickAwayListener>
    </Grid>
  );
});

export default FullScreenStickyHeader;
