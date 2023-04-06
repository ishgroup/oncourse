/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import Grid from "@mui/material/Grid";
import { change, Field } from "redux-form";
import { SurveyItem } from "@api/model";
import Link from "@mui/material/Link";
import Typography from "@mui/material/Typography";
import FormField from "../../../../common/components/form/formFields/FormField";
import { openInternalLink } from "../../../../common/utils/links";
import Score from "./Score";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { HeaderContactTitle } from "../../../../common/components/form/FieldAdornments";

interface Props {
  classes?: any;
  twoColumn?: boolean;
  values?: SurveyItem;
  dispatch?: any;
  form?: string;
}

const visibilityItems = [
  { label: "Waiting review", value: "Waiting review" },
  { label: "Public testimonial", value: "Public testimonial" },
  { label: "Not testimonial", value: "Not testimonial" },
  { label: "Hidden by student", value: "Hidden by student" }
];

const SurveyEditView = (props: Props) => {
  const {
    twoColumn, values, dispatch, form
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
    <Grid container columnSpacing={3} rowSpacing={2} className="saveButtonTableOffset p-3" alignContent="flex-start" alignItems="center">
      <Grid item xs={12}>
        <FullScreenStickyHeader
          disableInteraction
          twoColumn={twoColumn}
          title={(
            <HeaderContactTitle name={values?.studentName} id={values?.studentContactId} />
          )}
        />
      </Grid>
      <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
        <Grid item xs={twoColumn ? 4 : 12} className="mb-2">
          <Grid item>
            <Field name="netPromoterScore" label="Net Promoter Score" max={10} component={Score} />
          </Grid>
        </Grid>
        <Grid item xs={twoColumn ? 8 : 12}>
          <Grid container columnSpacing={3} rowSpacing={2} wrap={twoColumn ? "nowrap" : "wrap"}>
            <Grid item xs={twoColumn ? 8 : 12}>
              <Field name="courseScore" label="Course" component={Score} />
              <Link href="#" onClick={openClass} color="textSecondary">
                {values.className}
              </Link>
            </Grid>
            <Grid item xs={twoColumn ? 8 : 12}>
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
            <Grid item xs={twoColumn ? 8 : 12}>
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
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField type="multilineText" label="Comment" name="comment" disabled />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="select"
          name="visibility"
          onChange={(value: any) => {
            if (value === "Public testimonial" && !values.testimonial) {
              dispatch(change(form, "testimonial", values.comment));
            }
          }}
          label="Visibility"
          items={visibilityItems}
        />
      </Grid>
      <Grid item xs={twoColumn ? 6 : 12}>
        <FormField
          type="multilineText"
          label="Testimonial"
          name="testimonial"
          disabled={values.visibility !== "Public testimonial"}
        />
      </Grid>
      <CustomFields
        entityName="Survey"
        fieldName="customFields"
        entityValues={values}
        form={form}
        gridItemProps={{
          xs: twoColumn ? 6 : 12,
        }}
      />
    </Grid>
  ) : null;
};

export default SurveyEditView;
