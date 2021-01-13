/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import { createStyles, withStyles } from "@material-ui/core/styles";
import { CourseClassTutor } from "@api/model";
import { format } from "date-fns";
import Grid from "@material-ui/core/Grid";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import IconButton from "@material-ui/core/IconButton";
import DeleteIcon from "@material-ui/icons/Delete";
import clsx from "clsx";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import ExpandableItem from "../../../../../common/components/layout/expandable/ExpandableItem";
import { openInternalLink } from "../../../../../common/utils/links";
import { AppTheme } from "../../../../../model/common/Theme";
import { DD_MM_YYYY_SLASHED, EEE_D_MMM_YYYY } from "../../../../../common/utils/dates/format";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../../contacts/utils";
import { LinkAdornment } from "../../../../../common/components/form/FieldAdornments";
import ContactSelectItemRenderer from "../../../contacts/components/ContactSelectItemRenderer";
import { CourseClassTutorsTabProps } from "./CourseClassTutorsTab";
import { normalizeNumber } from "../../../../../common/utils/numbers/numbersNormalizing";
import WarningMessage from "../../../../../common/components/form/fieldMessage/WarningMessage";

const styles = (theme: AppTheme) => createStyles({
  tutorRoot: {
    gridTemplateRows: "auto auto"
  },
  tutorColumn: {
    gridGap: theme.spacing(1),
    justifyContent: "start"
  }
});

interface Props extends Partial<CourseClassTutorsTabProps> {
  index: number;
  expandedIndex: number;
  tutor: CourseClassTutor;
  tutorRoles?: any;
  onChange?: any;
  onDelete?: any;
  onTutorIdChange?: any;
  onRoleIdChange?: any;
  classes?: any;
  nameWarning?: string;
  openTutorWage?: any;
  clearTutors?: any;
  hasWage?: boolean;
}

const openTutorRoleLink = (id: number) => {
  openInternalLink(`/preferences/tutorRoles/${id}`);
};

const CourseClassTutorItem: React.FC<Props> = ({
  index,
  expandedIndex,
  onChange,
  onDelete,
  tutor,
  tutorRoles,
  classes,
  onTutorIdChange,
  onRoleIdChange,
  openTutorWage,
  nameWarning,
  hasWage
}) => {
  const onDeleteClick = e => {
    e.stopPropagation();
    onDelete(index);
  };

  const onWageClick = e => {
    e.stopPropagation();
    openTutorWage(tutor);
  };

  const today = format(new Date(), DD_MM_YYYY_SLASHED);

  return (
    <ExpandableItem
      expanded={index === expandedIndex}
      onChange={onChange}
      buttonsContent={(
        <div className="centeredFlex zIndex1">
          <Button color="primary" onClick={onWageClick} disabled={(!tutor.roleId && tutor.roleId !== 0) || !tutor.contactId}>
            {hasWage ? "Edit pay" : "Add pay"}
          </Button>
          <IconButton onClick={onDeleteClick}>
            <DeleteIcon fontSize="inherit" />
          </IconButton>
        </div>
      )}
      collapsedContent={(
        <div className={clsx("d-grid", classes.tutorRoot)}>
          <div className={clsx("d-grid gridAutoFlow-column align-items-baseline", classes.tutorColumn)}>
            {tutor.tutorName ? (
              <Typography noWrap className={clsx(nameWarning && "warningColor")}>
                {defaultContactName(tutor.tutorName)}
              </Typography>
            ) : (
              <Typography color="error" noWrap>
                Contact is required
              </Typography>
            )}
            {tutor.roleName ? (
              <Typography variant="caption" noWrap>
                {tutor.roleName}
              </Typography>
            ) : (
              <Typography variant="caption" color="error" noWrap>
                Tutor role is required
              </Typography>
            )}
          </div>
          <div className={clsx("d-grid gridAutoFlow-column align-items-baseline", classes.tutorColumn)}>
            {tutor.isInPublicity ? (
              <Typography variant="caption" noWrap>
                Visible
              </Typography>
            ) : (
              <Typography variant="caption" color="textSecondary" noWrap>
                Not visible
              </Typography>
            )}
            {tutor.confirmedOn ? (
              <Typography variant="caption" noWrap>
                Confirmed
                {' '}
                {format(new Date(tutor.confirmedOn), EEE_D_MMM_YYYY)}
              </Typography>
            ) : (
              <Typography variant="caption" color="textSecondary" noWrap>
                Not confirmed
              </Typography>
            )}
          </div>
        </div>
      )}
      detailsContent={(
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <FormField
              type="remoteDataSearchSelect"
              name={`tutors[${index}].contactId`}
              props={{
                label: "Contact",
                entity: "Contact",
                aqlFilter: `isTutor is true and (tutor.dateFinished > ${today} or tutor.dateFinished is null)`,
                selectValueMark: "id",
                selectLabelCondition: contactLabelCondition,
                defaultDisplayValue: defaultContactName(tutor.tutorName),
                labelAdornment: (
                  <LinkAdornment linkHandler={openContactLink} link={tutor.contactId} disabled={!tutor.contactId} />
                ),
                itemRenderer: ContactSelectItemRenderer,
                onInnerValueChange: onTutorIdChange,
                disabled: tutor.id,
                rowHeight: 48
              }}
              onInnerValueChange={onTutorIdChange}
              required
            />

            {nameWarning && <WarningMessage warning={nameWarning} />}

            <FormField
              type="searchSelect"
              name={`tutors[${index}].roleId`}
              label="Role"
              selectValueMark="id"
              selectLabelMark="name"
              normalize={normalizeNumber}
              defaultDisplayValue={tutor.roleName}
              items={tutorRoles}
              onInnerValueChange={onRoleIdChange}
              disabled={tutor.id || hasWage}
              labelAdornment={(
                <LinkAdornment
                  linkHandler={openTutorRoleLink}
                  link={tutor.roleId}
                  disabled={!tutor.roleId && tutor.roleId !== 0}
                />
              )}
              alwaysDisplayDefault
              required
            />
            {!tutor.id && hasWage
              && (
              <WarningMessage
                warning="Tutor wage should be removed before changing role"
              />
            )}
            <FormField
              type="date"
              name={`tutors[${index}].confirmedOn`}
              label="Confirmed On"
            />

            <FormControlLabel
              className="checkbox"
              control={<FormField type="checkbox" name={`tutors[${index}].isInPublicity`} />}
              label="Make tutor visible on web site"
            />
          </Grid>
        </Grid>
      )}
    />
  );
};

export default withStyles(styles)(CourseClassTutorItem);
