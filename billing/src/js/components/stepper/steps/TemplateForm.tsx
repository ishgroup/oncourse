/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { useCallback, useState } from 'react';
import { connect } from 'react-redux';
import { createStyles, FormControl, makeStyles, Typography } from '@material-ui/core';
import { Dispatch } from 'redux';
import Navigation from '../Navigations';
import { setTemplateValue } from '../../../redux/actions';
import { State } from '../../../redux/reducers';
import { addEventListenerWithDeps } from '../../../hooks/addEventListnerWithDeps';
import { TemplateChoser } from '../../common/TemplateChoser';

const useStyles = makeStyles((theme: any) => createStyles({
  coloredHeaderText: {
    color: theme.statistics.coloredHeaderText.color,
  }
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
    } else {
      setWebSiteTemplate(template);
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
        <Typography variant="h4" component="h4" className={classes.coloredHeaderText} color="primary" gutterBottom={true}>
          Choose your website template
        </Typography>

        <Typography variant="subtitle1" gutterBottom={true}>
          To start, select one of our templates. Don't sweat it, you can change this later.
        </Typography>

        <TemplateChoser value={webSiteTemplate} onChange={onChange} />
      </FormControl>
      <Navigation
        activeStep={activeStep}
        steps={steps}
        handleBack={handleBackCustom}
        handleNext={handleNextCustom}
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
