/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { createStyles, withStyles } from "@material-ui/core/styles";
import { Theme } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { change, Field } from "redux-form";
import { Category, SurveyItem } from "@api/model";
import Link from "@material-ui/core/Link";
import Typography from "@material-ui/core/Typography";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { openInternalLink } from "../../../../common/utils/links";
import Score from "./Score";
import Uneditable from "../../../../common/components/form/Uneditable";
import { getMainRouteUrl } from "../../../../routes/routesMapping";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";

interface Props {
  classes?: any;
  twoColumn?: boolean;
  values?: SurveyItem;
  dispatch?: any;
  form?: string;
}

const buildUrl = (id: number | string, category: Category) => getMainRouteUrl(category) + `/${id}`;

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
    openInternalLink(buildUrl(siteId, Category.Sites));
  }, [siteId]);

  const openRoom = useCallback(() => {
    openInternalLink(buildUrl(roomId, Category.Rooms));
  }, [roomId]);

  const openClass = useCallback( () => {
    openInternalLink(buildUrl(classId, Category.Classes));
  }, [classId]);

  return values ? (
    <Grid container className={classes.root} alignContent="flex-start">
      <Grid item xs={12}>
        <Uneditable
          value={values.studentName}
          label="Student"
          url={buildUrl(values.studentContactId, "Contacts")}
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
                <Link key={id} href={buildUrl(id, "Contacts")} target="_blank" color="textSecondary" className="pr-1">
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
