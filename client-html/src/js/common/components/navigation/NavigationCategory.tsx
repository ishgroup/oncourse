/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Grid, IconButton, Typography } from "@mui/material";
import Close from "@mui/icons-material/Close";
import React, { useMemo } from "react";
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import DescriptionOutlinedIcon from '@mui/icons-material/DescriptionOutlined';
import { makeAppStyles } from "../../styles/makeStyles";
import navigation from "./navigation.json";
import CatalogItem from "../layout/catalog/CatalogItem";
import { useAppSelector } from "../../utils/hooks";
import { BooleanArgFunction, NumberArgFunction } from "../../../model/common/CommonFunctions";
import { openInternalLink } from "../../utils/links";
import { getPrivisioningLink } from "../../../routes/routesMapping";
import StructureGraph from "../../../containers/dashboard/StructureGraph";

const useStyles = makeAppStyles(theme => ({
  description: {
    "& ul": {
      paddingLeft: theme.spacing(2)
    }
  }
}));

interface Props {
  selected: string;
  onClose: any;
  favorites: string[];
  favoriteScripts: string[];
  setExecMenuOpened: BooleanArgFunction,
  setScriptIdSelected: NumberArgFunction,
  updateFavorites: (key: string, type: "category" | "automation") => void;
}

const NavigationItem = ({
 favorites, item, onOpen, onFavoriteClick 
}) => {
  const isFavorite = favorites.includes(item.key);
  return (
    <CatalogItem
      onOpen={onOpen}
      item={{
        title: item.title,
        titleAdornment: item.titleAdornment,
        shortDescription: item.description
      }}
      secondaryAction={(
        <IconButton
          onMouseDown={e => e.stopPropagation()}
          onClick={onFavoriteClick}
          className="lightGrayIconButton"
          size="small"
        >
          <FavoriteBorderIcon fontSize="inherit" color={isFavorite ? "primary" : "inherit"} />
        </IconButton>
      )}
      hoverSecondary={!isFavorite}
    />
  );
};

const root = [
  {
    "name": "Class",
    "children": [
      {
        "name": "Room",
        "children": [
          {
            "name": "Site",
            "relation": "Holidays"
          }
        ]
      },
      {
        "name": "Waiting list",
      },
      {
        "name": "Timetable",
      },
      {
        "name": "Tutor",
        "children": [
          {
            "name": "Holidays",
          }
        ]
      }
    ]
  },
  {
    "name": "Tutor pay rates",
    "children": [
      {
        "name": "Tutor pay",
      }
    ]
  }
];

const NavigationCategory = (
  {
    selected,
    onClose,
    favorites,
    favoriteScripts,
    setScriptIdSelected,
    setExecMenuOpened,
    updateFavorites
  }:Props
) => {
  const classes = useStyles();

  const scripts = useAppSelector(state => state.dashboard.scripts);
  
  const category = useMemo(() => navigation.categories.find(c => c.key === selected), [selected]);
  
  const features = useMemo(() => (category 
    ? navigation.features.filter(f => category.features.includes(f.key))
    : []), [category]);

  const onOpen = (link: string) => openInternalLink(getPrivisioningLink(link));

  return (
    <div className="flex-fill p-3 overflow-y-auto">
      <div className="d-flex">
        <Typography variant="h4" className="flex-fill">{category?.title}</Typography>
        <IconButton size="large" onClick={onClose}>
          <Close />
        </IconButton>
      </div>
      <Grid container className="mt-3 mb-3" columnSpacing={2} rowSpacing={2}>
        <Grid item xs={12} xl={6}>
          <Typography className={classes.description} variant="body2" dangerouslySetInnerHTML={{ __html: category?.description }} />
        </Grid>
        <Grid item xs={12} xl={6}>
          <StructureGraph category={root} />
        </Grid>
      </Grid>

      <div className="heading mb-2">
        Features
      </div>
      {features.map(f => (
        <NavigationItem
          onOpen={() => onOpen(f.link)}
          key={f.key}
          item={f}
          favorites={favorites}
          onFavoriteClick={e => {
            e.stopPropagation();
            updateFavorites(f.key, "category");
          }}
        />
      ))}
      {category?.key === "automation" && (
        <div>
          <div className="heading mb-2 mt-4">
            Automations
          </div>
          {scripts.map(s => (
            <NavigationItem
              onOpen={() => {
                setScriptIdSelected(s.id);
                setExecMenuOpened(true);
              }}
              key={s.id}
              item={{
                key: String(s.id),
                title: s.name,
                titleAdornment: <DescriptionOutlinedIcon className="lightGrayIconButton" />,
                description: s.description
              }}
              favorites={favoriteScripts}
              onFavoriteClick={e => {
                e.stopPropagation();
                updateFavorites(String(s.id), "automation");
              }}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default NavigationCategory;