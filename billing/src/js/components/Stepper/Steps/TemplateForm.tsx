/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { createStyles, makeStyles } from "@material-ui/core/styles";
import { GridList, GridListTile, Typography } from "@material-ui/core";
import a from "../../../../images/a-small.png";
import b from "../../../../images/b-small.png";
import c from "../../../../images/c-small.png";
import d from "../../../../images/d-small.png";
import e from "../../../../images/e-small.png";
import f from "../../../../images/f-small.png";

const imgData = [
  {
    img: a,
    title: "template-a",
    link: "https://template-a.oncourse.cc/courses/creative",
  },
  {
    img: b,
    title: "template-b",
    link: "https://template-b.oncourse.cc/courses/creative",
  },
  {
    img: c,
    title: "template-c",
    link: "https://template-c.oncourse.cc/courses/creative",
  },
  {
    img: d,
    title: "template-d",
    link: "https://template-d.oncourse.cc/courses/creative",
  },
  {
    img: e,
    title: "template-e",
    link: "https://template-e.oncourse.cc/courses/creative",
  },
  {
    img: f,
    title: "template-f",
    // cols: 1,
    link: "https://template-f.oncourse.cc/courses/creative",
  },
]

const useStyles = makeStyles((theme: any) =>
  createStyles({
    root: {
      display: "flex",
      flexWrap: "wrap",
      justifyContent: "space-around",
      overflow: "hidden",
      backgroundColor: theme.palette.background.paper,
    },
    gridList: {
      width: 1000,
      height: 400,
    },
    coloredHeaderText: {
      color: theme.statistics.coloredHeaderText.color
    },
  }),
);

const TemplateForm = (props: any) => {
  const classes = useStyles();

  return (
    <>
      <h2 className={classes.coloredHeaderText}>Choose your website template</h2>

      <div className={classes.root}>
        <GridList cellHeight={160} className={classes.gridList} cols={3}>
          {imgData.map((tile: any) => (
            <GridListTile key={tile.img} cols={tile.cols || 1}>
              <a href={tile.link} target="_blank">
                <img src={tile.img} alt={tile.title} />
              </a>
            </GridListTile>
          ))}
        </GridList>
      </div>

      <Typography>Don't sweat it, you can change it later</Typography>
    </>
  )
}

export default TemplateForm;