/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {useState} from "react";
import { createStyles, makeStyles, withStyles } from "@material-ui/core/styles";
import { GridList, GridListTile, Typography, Radio, RadioGroup, FormControlLabel, FormControl } from "@material-ui/core";
import Navigation from "../Navigations";
import { setTemplateValue } from "../../../redux/actions";
import { connect, Dispatch } from "react-redux";
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
      height: 400,
    },
    coloredHeaderText: {
      color: theme.statistics.coloredHeaderText.color,
    },
    tileRoot: {
      height: "174px!important",
    },
  }),
);

const TemplateForm = (props: any) => {
  const classes = useStyles();

  const { activeStep, steps, handleBack, handleNext, templateStore, setTemplateValue } = props;

  const [webSiteTemplate, setWebSiteTemplate] = useState(templateStore);

  const handleClick = (event) => {
    if (event.target.value === webSiteTemplate) {
      setWebSiteTemplate("");
    } else {
      setWebSiteTemplate(event.target.value);
    }
  }

  const CustomRadio = withStyles((theme: any) => ({
    root: {
      color: theme.statistics.coloredHeaderText.color,
      '&$checked': {
        color: theme.statistics.coloredHeaderText.color,
      },
      '&:hover': {
        backgroundColor: "inherit",
      },
    },
    checked: {},
  }))((props: any) => <Radio color="default" {...props} onClick={handleClick} />);

  const handleBackCustom = () => {
    if (webSiteTemplate !== templateStore) setTemplateValue(webSiteTemplate);
    handleBack();
  }

  const handleNextCustom = () => {
    if (webSiteTemplate !== templateStore) setTemplateValue(webSiteTemplate);
    handleNext();
  }

  return (
    <>
      <FormControl component="fieldset">
        <h2 className={classes.coloredHeaderText}>Choose your website template</h2>

        <div className={classes.root}>
          <RadioGroup aria-label="template" name="template" value={webSiteTemplate}>
            <GridList cellHeight={160} className={classes.gridList} cols={3}>
              {imgData.map((tile: any) => (
                <GridListTile key={tile.img} cols={tile.cols || 1} className={classes.tileRoot}>
                  <a href={tile.link} target="_blank">
                    <img src={tile.img} alt={tile.title} />
                  </a>
                  <FormControlLabel value={tile.value} control={<CustomRadio />} label={tile.title}/>
                </GridListTile>
              ))}
            </GridList>
          </RadioGroup>
        </div>

        <Typography>Don't sweat it, you can change it later</Typography>
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

const mapStateToProps = (state: any) => ({
  templateStore: state.creatingCollege.webSiteTemplate,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setTemplateValue: (template: string) => dispatch(setTemplateValue(template)),
});

export default connect(mapStateToProps, mapDispatchToProps)(TemplateForm as any);