/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import useTheme from "@mui/styles/useTheme";
import useMediaQuery from "@mui/material/useMediaQuery";
import React from "react";
import ListSubheader from '@mui/material/ListSubheader';
import InfiniteLoader from "react-window-infinite-loader";
import { areEqual, FixedSizeList as List } from 'react-window';
import { createStyles } from "@mui/styles";
import { green } from "@mui/material/colors";
import clsx from "clsx";
import { ListItem } from "@mui/material";
import { AppTheme } from "../../../../model/common/Theme";
import Tooltip from "@mui/material/Tooltip";

export const selectStyles = theme => createStyles({
    root: {},
    bottomMargin: {
      marginBottom: `${theme.spacing(1) + 1}`
    },
    bottomPadding: {
      paddingBottom: `${theme.spacing(1) + 1}`
    },
    topMargin: {
      marginTop: `${theme.spacing(1)}`,
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
    },
    clearIcon: {
      fontSize: "1.2rem",
      "&:hover": {
        cursor: "pointer",
      }
    },
    inputWrapper: {
      "&:hover $inputEndAdornment": {
        visibility: 'visible'
      },
      "&:focus $inputEndAdornment": {
        visibility: 'hidden',
      }
    },
    hasPopup: {
      "&$root $inputWrapper": {
        paddingRight: 0
      },
      "&$root$hasClear $inputWrapper": {
        paddingRight: 0
      }
    },
    hasClear: {
      "&$root $inputWrapper": {
        paddingRight: 0
      }
    },
  });

export const ListRow = React.memo<any>(({ data: { children, selectAdornment }, index, style }) => {
  const inlineStyle = {
    ...style,
    top: (style.top as number) + 8,
    paddingLeft: 0,
    paddingRight: 0,
    whiteSpace: "nowrap"
  };

  return (
    <ListItem button style={inlineStyle}>
      {((selectAdornment?.position === "start" && index === 0)
        || (selectAdornment?.position === "end" && index === children.length))
        ? selectAdornment.content
        : children[index]
      }
    </ListItem>
  );
}, areEqual);

const OuterElementContext = React.createContext({});

const OuterElementType = React.forwardRef<any, any>((props, ref) => {
  const outerProps = React.useContext(OuterElementContext);
  return <div ref={ref} {...props} {...outerProps} />;
});

export const ListboxComponent = React.memo<any>(props => {
  const {
   children, rowHeight, remoteRowCount, loadMoreRows, classes, loading, selectAdornment, fieldClasses, ...other
  } = props;

  const itemData = { selectAdornment, children: React.Children.toArray(children) };
  const theme = useTheme() as AppTheme;
  const smUp = useMediaQuery(theme.breakpoints.up('sm'), { noSsr: true });
  const itemSize = rowHeight || (smUp ? 36 : 48);
  const itemCount = (remoteRowCount > children.length ? children.length + 3 : children.length) + (selectAdornment ? 1 : 0);

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
    return itemData.children.map(getChildSize).reduce((a, b) => a + b, 0);
  };

  const loadMoreItems = () => (loading ? undefined : loadMoreRows(itemData.children.length));

  const isItemLoaded = index => index < (children.length + (selectAdornment ? 1 : 0));
  
  return (
    <OuterElementContext.Provider value={other}>
      <InfiniteLoader
        isItemLoaded={isItemLoaded}
        itemCount={itemCount}
        loadMoreItems={loadMoreItems}
      >
        {({ onItemsRendered }) => (
          <List
            height={getHeight() + 2 * 8}
            width="100%"
            className={clsx(classes.menuList, fieldClasses.selectMenu)}
            itemData={itemData}
            outerElementType={OuterElementType}
            onItemsRendered={onItemsRendered}
            itemSize={itemSize}
            itemCount={itemCount}
          >
            {ListRow}
          </List>
        )}
      </InfiniteLoader>
    </OuterElementContext.Provider>
  );
});

export const NoWrapOption = (content, data, search, parentProps) => (
  <div {...parentProps || {}}>
    <Tooltip title={content}>
      <div className="text-nowrap text-truncate">
        {content}
      </div>
    </Tooltip>
  </div>
);
