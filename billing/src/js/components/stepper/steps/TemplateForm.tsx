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
import FormControl from '@mui/material/FormControl';
import Typography from '@mui/material/Typography';
import { makeAppStyles } from '../../../styles/makeStyles';
import { setTemplateValue } from '../../../redux/actions';
import { addEventListenerWithDeps } from '../../../hooks/addEventListnerWithDeps';
import { TemplateChoser } from '../../common/TemplateChoser';
import Navigation from '../Navigations';
import { State } from '../../../models/State';

const useStyles = makeAppStyles()((theme) => ({
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
  const { classes } = useStyles();

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
      <FormControl component="fieldset" className="w-100">
        <Typography variant="h4" component="h4" className={classes.coloredHeaderText} color="primary" gutterBottom>
          Choose your website template
        </Typography>
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
  templateStore: state.college.webSiteTemplate,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setTemplateValue: (template: string) => dispatch(setTemplateValue(template)),
});

export default connect(mapStateToProps, mapDispatchToProps)(TemplateForm);
