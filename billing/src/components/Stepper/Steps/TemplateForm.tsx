/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { Theme, createStyles, makeStyles, withStyles } from "@material-ui/core/styles";
import { GridList, GridListTile } from "@material-ui/core";
import a from "../../../images/a.png";
import b from "../../../images/b.png";
import c from "../../../images/c.png";
import d from "../../../images/d.png";
import e from "../../../images/e.png";
import f from "../../../images/f.png";

const imgData = [
  {
    img: a,
    title: "template-a",
    cols: 1,
  },
  {
    img: b,
    title: "template-b",
    cols: 2,
  },
  {
    img: c,
    title: "template-c",
    cols: 3,
  },
  {
    img: d,
    title: "template-d",
    cols: 1,
  },
  {
    img: e,
    title: "template-e",
    cols: 2,
  },
  {
    img: f,
    title: "template-f",
    cols: 3,
  },
]

// const styles: any = (theme: Theme) => ({
//   root: {
//     display: "flex",
//     flexWrap: "wrap",
//     justifyContent: "space-around",
//     overflow: "hidden",
//     backgroundColor: theme.palette.background.paper,
//   },
//   gridList: {
//     width: 500,
//     height: 450,
//   },
// })

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
      width: 500,
      height: 450,
    },
    coloredHeaderText: {
      color: theme.statistics.coloredHeaderText.color
    },
  }),
);

const TemplateForm = (props: any) => {
  const classes = useStyles();
  // const { classes } = props;

  return (
    <>
      <h2 className={classes.coloredHeaderText}>Choose your website template</h2>

      <div className={classes.root}>
        <GridList cellHeight={160} className={classes.gridList} cols={3}>
          {imgData.map((tile: any) => (
            <GridListTile key={tile.img} cols={tile.cols || 1}>
              <img src={tile.img} alt={tile.title} />
            </GridListTile>
          ))}
        </GridList>
      </div>

      <p>Don't sweat it, you can change it later</p>
    </>
  )
}

// export default withStyles(styles, { withTheme: true })(TemplateForm);
export default TemplateForm;