/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import { createStyles, withStyles } from "@material-ui/core/styles";
import { Theme } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { change, Field } from "redux-form";
import { SurveyItem } from "@api/model";
import Link from "@material-ui/core/Link";
import Typography from "@material-ui/core/Typography";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { openInternalLink } from "../../../../common/utils/links";
import Score from "./Score";
import Uneditable from "../../../../common/components/form/Uneditable";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";

interface Props {
  classes?: any;
  twoColumn?: boolean;
  values?: SurveyItem;
  dispatch?: any;
  form?: string;
}

const styles = createStyles(({ spacing }: Theme) => ({
  root: {
    padding: spacing(3, 3, 6, 3),
    height: "100%"
  }
}));

const visibilityItems = [
  { label: "Waiting review", value: "Waiting review" },
  { label: "Public testimonial", value: "Public testimonial" },
  { label: "Not testimonial", value: "Not testimonial" },
  { label: "Hidden by student", value: "Hidden by student" }
];

const SurveyEditView = (props: Props) => {
  const {
    classes, twoColumn, values, dispatch, form
  } = props;
  const siteId = values && values.siteId;
  const roomId = values && values.roomId;
  const classId = values && values.classId;

  const openSite = useCallback(() => {
    openInternalLink(`/site/${siteId}`);
  }, [siteId]);

  const openRoom = useCallback(() => {
    openInternalLink(`/room/${roomId}`);
  }, [roomId]);

  const openClass = useCallback( () => {
    openInternalLink(`/class/${classId}`);
  }, [classId]);

  return values ? (
    <Grid container className={classes.root} alignContent="flex-start">
      <Grid item xs={12}>
        <Uneditable
          value={values.studentName}
          label="Student"
          url={`/contact/${values.studentContactId}`}
        />
      </Grid>
      <Grid container justify="space-between" className="mw-800 pb-2" spacing={2}>
        <Grid item xs={twoColumn ? 4 : 12} className="mb-2">
          <Grid item>
            <Field name="netPromoterScore" label="Net Promoter Score" max={10} component={Score} />
          </Grid>
        </Grid>
        <Grid item xs={twoColumn ? 8 : 12}>
          <Grid container justify="flex-end" spacing={2} wrap={twoColumn ? "nowrap" : "wrap"}>
            <Grid item xs={twoColumn ? "auto" : 12}>
              <Field name="courseScore" label="Course" component={Score} />
              <Link href="#" onClick={openClass} color="textSecondary">
                {values.className}
              </Link>
            </Grid>
            <Grid item xs={twoColumn ? "auto" : 12}>
              <Field name="venueScore" label="Venue" component={Score} />
              <Typography variant="body2" component="div">
                <Link href="#" className="pr-1" onClick={openSite} color="textSecondary">
                  {values.siteName}
                </Link>
                <Link href="#" onClick={openRoom} color="textSecondary">
                  {values.roomName}
                </Link>
              </Typography>
            </Grid>
            <Grid item xs={twoColumn ? "auto" : 12}>
              <Field name="tutorScore" label="Tutor" component={Score} />
              {Object.keys(values.tutors).map(id => (
                <Link key={id} href={`/contact/${id}`} target="_blank" color="textSecondary" className="pr-1">
                  {values.tutors[id]}
                </Link>
              ))}
            </Grid>
          </Grid>
        </Grid>
      </Grid>
      <Grid item xs={12}>
        <FormField type="text" label="Comment" name="comment" disabled />
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="select"
          name="visibility"
          onChange={(value: any) => {
            if (value === "Public testimonial" && !values.testimonial) {
              dispatch(change(form, "testimonial", values.comment));
            }
          }}
          props={{
            label: "Visibility",
            items: visibilityItems
          }}
        />
      </Grid>
      <Grid item xs={12}>
        <FormField
          type="multilineText"
          label="Testimonial"
          name="testimonial"
          disabled={values.visibility !== "Public testimonial"}
          fullWidth
        />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12} className={twoColumn ? undefined : "saveButtonTableOffset"}>
        <CustomFields
          entityName="Survey"
          fieldName="customFields"
          entityValues={values}
          dispatch={dispatch}
          form={form}
          fullWidth
        />
      </Grid>
    </Grid>
  ) : null;
};

export default withStyles(styles)(SurveyEditView);
