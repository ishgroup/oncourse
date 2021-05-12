/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useState } from "react";
import { createStyles, makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import { connect, Dispatch } from "react-redux";
import { GridList, GridListTile, Typography, FormControl } from "@material-ui/core";
import Navigation from "../Navigations";
import { setTemplateValue } from "../../../redux/actions";
import { addEventListenerWithDeps } from "../../Hooks/addEventListnerWithDeps";
import a from "../../../../images/a.png";
import b from "../../../../images/b.png";
import c from "../../../../images/c.png";
import d from "../../../../images/d.png";
import e from "../../../../images/e.png";
import f from "../../../../images/f.png";
import {State} from "../../../redux/reducers";

const imgData = [
  {
    img: a,
    title: "template-a",
    link: "https://template-a.oncourse.cc/courses/creative",
    value: "a",
  },
  {
    img: b,
    title: "template-b",
    link: "https://template-b.oncourse.cc/courses/creative",
    value: "b",
  },
  {
    img: c,
    title: "template-c",
    link: "https://template-c.oncourse.cc/courses/creative",
    value: "c",
  },
  {
    img: d,
    title: "template-d",
    link: "https://template-d.oncourse.cc/courses/creative",
    value: "d",
  },
  {
    img: e,
    title: "template-e",
    link: "https://template-e.oncourse.cc/courses/creative",
    value: "e",
  },
  {
    img: f,
    title: "template-f",
    link: "https://template-f.oncourse.cc/courses/creative",
    value: "f",
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
    },
    coloredHeaderText: {
      color: theme.statistics.coloredHeaderText.color,
    },
    tileRoot: {
      height: "285px!important",
    },
    image: {
      border: "2px solid transparent",
      width: "300px",
      height: "230px",
      "&:hover": {
        cursor: "pointer",
      }
    },
    selectedImage: {
      borderColor: theme.statistics.coloredHeaderText.color,
    },
    link: {
      color: theme.statistics.coloredHeaderText.color,
      display: "inline-block",
      textDecoration: "none",
      "&:hover": {
        color: "rgb(172, 103, 20)",
      }
    }
  }),
);

const TemplateForm = (props: any) => {
  const classes = useStyles();

  const { activeStep, steps, handleBack, handleNext, templateStore, setTemplateValue } = props;

  const [webSiteTemplate, setWebSiteTemplate] = useState(templateStore);

  const handleClick = (template: string) => {
    if (template === webSiteTemplate) {
      setWebSiteTemplate("");
    } else {
      setWebSiteTemplate(template);
    }
  }

  const handleBackCustom = () => {
    if (webSiteTemplate !== templateStore) setTemplateValue(webSiteTemplate);
    handleBack();
  }

  const handleNextCustom = () => {
    if (webSiteTemplate !== templateStore) setTemplateValue(webSiteTemplate);
    handleNext();
  }

  const keyPressHandler = useCallback((e) => {
    if (e.keyCode === 13) handleNextCustom();
  }, [webSiteTemplate])

  addEventListenerWithDeps([keyPressHandler], keyPressHandler);

  return (
    <>
      <FormControl component="fieldset">
        <h2 className={classes.coloredHeaderText}>Choose your website template</h2>

        <div className={classes.root}>
          <GridList cellHeight={160} className={classes.gridList} cols={3}>
            {imgData.map((tile: any) => (
              <GridListTile key={tile.img} cols={tile.cols || 1} className={classes.tileRoot}>
                <>
                  <img
                    src={tile.img}
                    alt={tile.title}
                    className={clsx(classes.image, tile.value === webSiteTemplate ? classes.selectedImage : null)}
                    onClick={() => handleClick(tile.value)}
                    loading="lazy"
                  />
                  <a href={tile.link} target="_blank" className={classes.link}>
                    live preview...
                  </a>
                </>
              </GridListTile>
            ))}
          </GridList>
        </div>

        <Typography>Don't sweat it, you can change this later</Typography>
      </FormControl>
      <Navigation
        activeStep={activeStep}
        steps={steps}
        handleBack={handleBackCustom}
        handleNext={handleNextCustom}
      />
    </>
  )
}

const mapStateToProps = (state: State) => ({
  templateStore: state.webSiteTemplate,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setTemplateValue: (template: string) => dispatch(setTemplateValue(template)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(TemplateForm as any);
