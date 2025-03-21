/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassTutor } from '@api/model';
import DeleteIcon from '@mui/icons-material/Delete';
import { Button, FormControlLabel, Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import {
  AppTheme,
  DD_MM_YYYY_SLASHED,
  EEE_D_MMM_YYYY,
  LinkAdornment,
  normalizeNumber,
  openInternalLink,
  WarningMessage
} from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import { ContactLinkAdornment } from '../../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../../common/components/form/formFields/FormField';
import ExpandableItem from '../../../../../common/components/layout/expandable/ExpandableItem';
import ContactSelectItemRenderer from '../../../contacts/components/ContactSelectItemRenderer';
import { getContactFullName } from '../../../contacts/utils';
import { CourseClassTutorsTabProps } from './CourseClassTutorsTab';

const styles = (theme: AppTheme) => ({
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
      expandButtonId={`course-class-tutor-${index}`}
      buttonsContent={(
        <div className="centeredFlex zIndex1 text-nowrap">
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
                {tutor.tutorName}
              </Typography>
            ) : (
              <Typography color="error" noWrap>
                {$t('contact_is_required')}
              </Typography>
            )}
            {tutor.roleName ? (
              <Typography variant="caption" noWrap>
                {tutor.roleName}
              </Typography>
            ) : (
              <Typography variant="caption" color="error" noWrap>
                {$t('tutor_role_is_required')}
              </Typography>
            )}
          </div>
          <div className={clsx("d-grid gridAutoFlow-column align-items-baseline", classes.tutorColumn)}>
            {tutor.isInPublicity ? (
              <Typography variant="caption" noWrap>
                {$t('visible')}
              </Typography>
            ) : (
              <Typography variant="caption" color="textSecondary" noWrap>
                {$t('not_visible')}
              </Typography>
            )}
            {tutor.confirmedOn ? (
              <Typography variant="caption" noWrap>
                {$t('Confirmed')}
                {' '}
                {format(new Date(tutor.confirmedOn), EEE_D_MMM_YYYY)}
              </Typography>
            ) : (
              <Typography variant="caption" color="textSecondary" noWrap>
                {$t('not_confirmed')}
              </Typography>
            )}
          </div>
        </div>
      )}
      detailsContent={(
        <div>
          <FormField
            type="remoteDataSelect"
            name={`tutors[${index}].contactId`}
            label={$t('Contact')}
            entity="Contact"
            aqlFilter={`isTutor is true and (tutor.dateFinished > ${today} or tutor.dateFinished is null)`}
            selectValueMark="id"
            selectLabelCondition={getContactFullName}
            defaultValue={tutor.tutorName}
            labelAdornment={<ContactLinkAdornment id={tutor?.contactId} />}
            itemRenderer={ContactSelectItemRenderer}
            onInnerValueChange={onTutorIdChange}
            disabled={Boolean(tutor.id)}
            rowHeight={48}
            className="mb-2"
            required
          />

          <FormField
            type="select"
            name={`tutors[${index}].roleId`}
            label={$t('role')}
            selectValueMark="id"
            selectLabelMark="name"
            normalize={normalizeNumber}
            defaultValue={tutor.roleName}
            items={tutorRoles}
            onInnerValueChange={onRoleIdChange}
            disabled={Boolean(tutor.id || hasWage)}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openTutorRoleLink}
                link={tutor.roleId}
                disabled={!tutor.roleId && tutor.roleId !== 0}
              />
              )}
            alwaysDisplayDefault
            className={!(!tutor.id && hasWage) && "mb-2"}
            warning={nameWarning}
            required
          />
          {!tutor.id && hasWage
              && (
              <WarningMessage
                className="mb-2"
                warning="Tutor wage should be removed before changing role"
              />
            )}
          <FormField
            type="date"
            name={`tutors[${index}].confirmedOn`}
            label={$t('confirmed_on')}
            className="mb-2"
          />

          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name={`tutors[${index}].isInPublicity`} />}
            label={$t('make_tutor_visible_on_web_site')}
          />
        </div>
      )}
    />
  );
};

export default withStyles(CourseClassTutorItem, styles);