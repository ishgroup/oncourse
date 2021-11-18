/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import useTheme from "@mui/styles/useTheme";
import useMediaQuery from "@mui/material/useMediaQuery";
import React from "react";
import ListSubheader from '@mui/material/ListSubheader';
import { areEqual, FixedSizeList as List } from 'react-window';
import { createStyles } from "@mui/styles";
import { green } from "@mui/material/colors";
import clsx from "clsx";
import { AppTheme } from "../../../../model/common/Theme";
import { Typography } from "@mui/material";

export const selectStyles = theme => createStyles({
    textField: {
      paddingBottom: `${theme.spacing(2) + 1}px`
    },
    bottomMargin: {
      marginBottom: `${theme.spacing(1) + 1}px`
    },
    bottomPadding: {
      paddingBottom: `${theme.spacing(1) + 1}px`
    },
    topMargin: {
      marginTop: `${theme.spacing(1)}px`,
      paddingLeft: "0"
    },
    editIcon: {
      fontSize: "24px",
      color: theme.palette.divider,
      verticalAlign: "middle",
      margin: 0
    },
    editable: {
      "&:hover": {
        color: theme.palette.primary.main,
        fill: theme.palette.primary.main
      }
    },
    label: {
      whiteSpace: "nowrap",
      "&$valid": {
        color: green[500]
      }
    },
    valid: {},
    autoWidthSelect: {
      width: "calc(100% + 30px)"
    },
    emptySelect: {
      color: "#fff"
    },
    error: {
      color: theme.palette.error.main
    },
    validUnderline: {
      "&:after": {
        borderBottomColor: green[500]
      }
    },
    menuItem: {
      "&:hover .placeholderContent": {
        color: "#fff"
      },
      overflow: "hidden",
      textOverflow: "ellipsis"
    },
    menuItemActive: {
      "& .placeholderContent": {
        color: "#fff"
      }
    },
    menuList: {
      background: theme.palette.background.paper,
      borderRadius: theme.shape.borderRadius,
    },
    readonly: {
      fontWeight: 300,
      pointerEvents: "none"
    }
  });

const listRef = React.createRef<any>();

export const ListRow = React.memo<any>(({ data, index, style }) => {
  const inlineStyle = {
    ...style,
    top: (style.top as number) + 8,
  };

  return (
    <Typography component="li" noWrap style={inlineStyle}>
      {data[index]}
    </Typography>
  );
}, areEqual);

const OuterElementContext = React.createContext({});

const OuterElementType = React.forwardRef<any, any>((props, ref) => {
  const outerProps = React.useContext(OuterElementContext);
  return <div ref={ref} {...props} {...outerProps} />;
});

export const ListboxComponent = React.forwardRef<any, any>((props, ref) => {
  const {
   children, rowHeight, remoteRowCount, loadMoreRows, classes, fieldClasses, ...other
  } = props;

  const itemData = React.Children.toArray(children);
  const theme = useTheme() as AppTheme;
  const smUp = useMediaQuery(theme.breakpoints.up('sm'), { noSsr: true });
  const itemCount = itemData.length;
  const itemSize = rowHeight || (smUp ? 36 : 48);

  const getChildSize = child => {
    if (React.isValidElement(child) && child.type === ListSubheader) {
      return 48;
    }

    return itemSize;
  };

  const getHeight = () => {
    if (itemCount > 8) {
      return 8 * itemSize;
    }
    return itemData.map(getChildSize).reduce((a, b) => a + b, 0);
  };

  const onScroll = ({ scrollOffset }) => {
    const allHeight = itemData.length * itemSize;

    const scrolledPercent = scrollOffset / (allHeight / 100);

    if (remoteRowCount > 0 && scrolledPercent > 70) {
      loadMoreRows(itemData.length);
    }
  };

  return (
    <div ref={ref}>
      <OuterElementContext.Provider value={other}>
        <List
          onScroll={onScroll}
          height={getHeight() + 2 * 8}
          width="100%"
          ref={listRef}
          className={clsx(classes.menuList, fieldClasses.selectMenu)}
          itemData={itemData}
          outerElementType={OuterElementType}
          itemSize={itemSize}
          itemCount={itemCount}
        >
          {ListRow}
        </List>
      </OuterElementContext.Provider>
    </div>
  );
});
