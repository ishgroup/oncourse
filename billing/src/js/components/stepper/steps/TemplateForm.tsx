/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { useCallback, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import makeStyles from '@material-ui/core/styles/makeStyles';
import createStyles from '@material-ui/core/styles/createStyles';
import FormControl from '@material-ui/core/FormControl';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import { FormGroup } from '@material-ui/core';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import { setTemplateValue } from '../../../redux/actions';
import { State } from '../../../redux/reducers';
import { addEventListenerWithDeps } from '../../../hooks/addEventListnerWithDeps';
import { TemplateChoser } from '../../common/TemplateChoser';
import Navigation from '../Navigations';

const useStyles = makeStyles((theme: any) => createStyles({
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color,
    marginBottom: 30,
  },
  siteFilterPaper: {
    backgroundColor: theme.tabList.listContainer.backgroundColor,
    padding: '10px 16px 14px',
    marginTop: theme.spacing(2),
    marginBottom: theme.spacing(4),
  },
}));

const TemplateForm = (props: any) => {
  const classes = useStyles();

  const {
    activeStep, steps, handleBack, handleNext, templateStore, setTemplateValue
  } = props;

  const [webSiteTemplate, setWebSiteTemplate] = useState(templateStore);

  const onChange = (template: string) => {
    if (template === webSiteTemplate) {
      setWebSiteTemplate('');
      setTemplateValue('');
    } else {
      setWebSiteTemplate(template);
      setTemplateValue(template);
      handleNext();
    }
  };

  const handleBackCustom = () => {
    if (webSiteTemplate !== templateStore) setTemplateValue(webSiteTemplate);
    handleBack();
  };

  const handleNextCustom = () => {
    if (webSiteTemplate !== templateStore) setTemplateValue(webSiteTemplate);
    handleNext();
  };

  const keyPressHandler = useCallback((e) => {
    if (e.keyCode === 13) handleNextCustom();
  }, [webSiteTemplate]);

  addEventListenerWithDeps([keyPressHandler], keyPressHandler);

  return (
    <>
      <FormControl component="fieldset">
        <Typography variant="h4" component="h4" className={classes.coloredHeaderText} color="primary" gutterBottom>
          Choose your website template
        </Typography>

        {/*<Typography variant="subtitle1" gutterBottom>
          To start, select one of our templates. Don't sweat it, you can change this later.
        </Typography>

        <Paper elevation={0} className={classes.siteFilterPaper}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Typography variant="caption" color="primary">Topic</Typography>
              <FormGroup row>
                <FormControlLabel
                  label="Corporate"
                  control={<Checkbox checked name="corporate" color="primary" />}
                />
                <FormControlLabel
                  label="Education"
                  control={<Checkbox name="education" color="primary" />}
                />
                <FormControlLabel
                  label="Technology"
                  control={<Checkbox name="technology" color="primary" />}
                />
                <FormControlLabel
                  label="Creative"
                  control={<Checkbox name="creative" color="primary" />}
                />
                <FormControlLabel
                  label="Entertainment"
                  control={<Checkbox name="entertainment" color="primary" />}
                />
              </FormGroup>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Typography variant="caption" color="primary">Suited for</Typography>
              <FormGroup row>
                <FormControlLabel
                  label="Small amount of courses"
                  control={<Checkbox name="small_courses" color="primary" />}
                />
              </FormGroup>
              <FormGroup row>
                <FormControlLabel
                  label="Large amount of courses"
                  control={<Checkbox name="large_courses" color="primary" />}
                />
              </FormGroup>
            </Grid>
          </Grid>
        </Paper>*/}

        <TemplateChoser value={webSiteTemplate} onChange={onChange} />
      </FormControl>
      <Navigation
        activeStep={activeStep}
        steps={steps}
        handleBack={handleBackCustom}
        handleNext={handleNextCustom}
        hideNextButton={webSiteTemplate === ''}
      />
    </>
  );
};

const mapStateToProps = (state: State) => ({
  templateStore: state.webSiteTemplate,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setTemplateValue: (template: string) => dispatch(setTemplateValue(template)),
});

export default connect(mapStateToProps, mapDispatchToProps)(TemplateForm);
