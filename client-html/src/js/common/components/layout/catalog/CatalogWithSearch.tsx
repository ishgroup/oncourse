/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  ReactNode, useMemo, useState
} from "react";
import AddIcon from "@mui/icons-material/Add";
import CloseIcon from "@mui/icons-material/Close";
import Fab from "@mui/material/Fab";
import { Fade, List, Typography } from "@mui/material";
import clsx from "clsx";
import SidebarSearch from "../sidebar-with-search/components/SidebarSearch";
import { makeAppStyles } from "../../../styles/makeStyles";
import AddButton from "../../icons/AddButton";
import CatalogItem from "./CatalogItem";
import { CatalogItemType } from "../../../../model/common/Catalog";

interface Props {
  addNewItem: CatalogItemType;
  items: CatalogItemType[];
  setSearch: any;
  title: string;
  itemsListTitle: string;
  description?: string;
}

const useStyles = makeAppStyles(theme => ({
  root: {
    padding: theme.spacing(4),
    marginTop: theme.spacing(1)
  },
  fabContainer: {
    zIndex: 1,
    position: "relative",
    "&$fabOpened": {
      "& $fabTip": {
        paddingLeft: theme.spacing(2),
        transform: "translateX(0)",
        visibility: "visible"
      },
      "& $fab": {
        backgroundColor: theme.palette.primary.main,
        transform: "rotate(45deg)"
      }
    },
  },
  fabTip: {
    zIndex: -1,
    top: 12,
    visibility: "hidden",
    position: "absolute",
    transform: "translateX(-30px)",
    willChange: "transform,visibility",
    transition: "transform 0.2s ease-in-out,visibility 0.2s ease-in-out",
  },
  fab: {
    willChange: "transform,backgroundColor",
    backgroundColor: "black",
    transition: "backgroundColor 0.2s ease-in-out, transform 0.2s ease-in-out",
  },
  fabOpened: {},
}));

const CatalogWithSearch = (
  {
    title,
    items,
    itemsListTitle,
    setSearch,
    description
  }:Props
) => {
  const [opened, setOpened] = useState(false);
  const classes = useStyles();

  const open = () => setOpened(prev => !prev);

  const filteredItems = useMemo(() => {
    const result = {
      installed: [],
      categories: {
        other: []
      }
    };

    items.forEach(i => {
      if (i.installed) {
        result.installed.push(i);
      }
      if (i.category) {
        if (!result.categories[i.category]) {
          result.categories[i.category] = [];
        }
        result.categories[i.category].push(i);
      } else {
        result.categories.other.push(i);
      }
    });

   return result;
  }, [items]);
  
  const renderedInstalled = useMemo(() => filteredItems.installed.map((i, key) => <CatalogItem key={key} {...i} />), [filteredItems]);

  return (
    <div className={classes.root}>
      <div className="centeredFlex">
        <div className={clsx(classes.fabContainer, opened && classes.fabOpened)}>
          <Fab
            type="button"
            size="large"
            color="primary"
            className={classes.fab}
            onClick={open}
          >
            <AddIcon />
          </Fab>
          <Typography className={classes.fabTip} variant="overline" color="primary" fontWeight="bold">Close</Typography>
        </div>
        <div className="flex-fill" />
        <SidebarSearch setParentSearch={setSearch} />
      </div>
      <Typography variant="h4" className="mt-5 mb-3">{title}</Typography>
      <Fade in={!opened} unmountOnExit mountOnEnter>
        <div>
          <div className="centeredFlex">
            <div className="heading">
              {itemsListTitle}
            </div>
            <div className="flex-fill" />
            <div className="centeredFlex primaryColor">
              <Typography variant="button">
                Add new
              </Typography>
              <AddButton className="p-1" onClick={open} />
            </div>
          </div>
          <List>
            {renderedInstalled}
          </List>
        </div>
      </Fade>
      <Fade in={opened} unmountOnExit mountOnEnter>
        <div>
          {description}
        </div>
      </Fade>
    </div>
);
};

export default CatalogWithSearch;