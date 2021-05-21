import React from "react";
import {FieldArray} from "redux-form";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Typography from "@material-ui/core/Typography";
import ExpandMore from "@material-ui/icons/ExpandMore";
import Grid from "@material-ui/core/Grid";
import FormField from "../../../common/components/form/form-fields/FormField";

const SectionItems = props => {
  const {fields} = props;

  return (
    <Grid container spacing={4}>
      {fields.map((item, index) => {
        const field = fields.get(index);

        return (
          <Grid key={index} item xs={12} sm={6} md={4}>
            {field.type === "checkbox" ? (
              <FormControlLabel
                control={(
                  <FormField
                    type={field.type}
                    name={`${item}.value`}
                    label={field.label}
                    fullWidth
                  />
                )}
                label={field.label}
              />
            ) : (
              <FormField
                {...field}
                type={field.type}
                name={`${item}.value`}
                label={field.label}
                fullWidth
              />
            )}
          </Grid>
        );
      })}
    </Grid>
  );
};

const SettingSection: React.FC<any> = props => {
  const {fields, classes} = props;

  return (
    <div className={classes.root}>
      {fields.map((section, index) => {
        const sectionField = fields.get(index);
        return (
          <Accordion key={index} className={classes.settingsSection}>
            <AccordionSummary
              expandIcon={<ExpandMore />}
              aria-controls={`panel1a-content-${index}`}
              id={`panel1a-header-${index}`}
            >
              <Typography className={classes.heading}>{sectionField.title}</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <div className={classes.detailsRoot}>
                <FieldArray
                  name={`${section}.items`}
                  component={SectionItems}
                />
              </div>
            </AccordionDetails>
          </Accordion>
        );
      })}
    </div>
  );
};

export default SettingSection;
