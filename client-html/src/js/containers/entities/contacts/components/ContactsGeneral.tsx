/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect, useMemo } from "react";
import {
  Contact, Student, Tag, Tutor
} from "@api/model";
import { change, Field, getFormInitialValues } from "redux-form";
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import { connect } from "react-redux";
import { Grid } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Divider from '@mui/material/Divider';
import { Dispatch } from "redux";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import AvatarRenderer from "./AvatarRenderer";
import { getContactFullName } from "../utils";
import { openInternalLink } from "../../../../common/utils/links";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { EntityChecklists } from "../../../tags/components/EntityChecklists";

const TutorInitial: Tutor = {
  wwChildrenStatus: "Not checked"
};

interface ContactsGeneralProps extends EditViewProps<Contact> {
  initialValues?: Contact;
  isStudent?: boolean;
  isTutor?: boolean;
  isCompany?: boolean;
  setIsStudent?: any;
  setIsTutor?: any;
  setIsCompany?: any;
  tags?: any;
  usiLocked?: boolean;
}

export const studentInitial: Student = {
  labourForceStatus: "Not stated",
  englishProficiency: "Not stated",
  highestSchoolLevel: "Not stated",
  citizenship: "No information",
  feeHelpEligible: false,
  indigenousStatus: "Not stated",
  priorEducationCode: "Not stated",
  isOverseasClient: false,
  disabilityType: "Not stated",
  clientIndustryEmployment: "Not Stated",
  clientOccupationIdentifier: "Not Stated"
};

const filterStudentTags = (tag: Tag) => {
  const { requirements } = tag;
  if (Array.isArray(requirements)) {
    return requirements.some(r => r.type === "Student") || requirements.some(r => r.type === "Contact");
  }
  return true;
};

const filterTutorTags = (tag: Tag) => {
  const { requirements } = tag;
  if (Array.isArray(requirements)) {
    return (
      requirements.some(r => r.type === "Tutor")
      || requirements.some(r => r.type === "Contact")
      || requirements.some(r => r.type === "Course")
    );
  }
  return true;
};

const filterCompanyTags = (tag: Tag) => {
  const { requirements } = tag;
  if (Array.isArray(requirements)) {
    return !(requirements.some(r => r.type === "Student") || requirements.some(r => r.type === "Tutor"));
  }
  return true;
};

interface Props {
  form: string;
  dispatch: Dispatch;
  showConfirm: ShowConfirmCaller;
  values: Contact;
  twoColumn: boolean;
  isCompany: boolean;
  usiLocked: boolean;
  syncErrors: any;
  isNew: boolean;
}

export const ProfileHeading = (props: Props) => {
  const {
    form,
    dispatch,
    showConfirm,
    values,
    twoColumn,
    isCompany,
    usiLocked,
    syncErrors,
    isNew
  } = props;
  
  const Avatar = useCallback(aProps => (
    <Field
      name="profilePicture"
      label="Profile picture"
      component={AvatarRenderer}
      showConfirm={showConfirm}
      email={values.email}
      twoColumn={twoColumn}
      props={{
        dispatch,
        form
      }}
      {...aProps}
    />
  ), [values.email]);

  return (
    <FullScreenStickyHeader
      opened={isNew || Object.keys(syncErrors).some(k => ['title', 'firstName', 'middleName', 'lastName'].includes(k))}
      twoColumn={twoColumn}
      Avatar={Avatar}
      title={(
        <>
          {values && !isCompany && values.title && values.title.trim().length > 0 ? `${values.title} ` : ""}
          {values ? (!isCompany ? getContactFullName(values) : values.lastName) : ""}
        </>
      )}
      fields={(
        <Grid container item xs={12} rowSpacing={2} columnSpacing={3}>
          {!isCompany && (
            <>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="title" label="Title" />
              </Grid>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="firstName" label="First name" disabled={usiLocked} required />
              </Grid>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="middleName" label="Middle name" />
              </Grid>
            </>
          )}
          <Grid item xs={isCompany ? 12 : twoColumn ? 2 : 6}>
            <FormField type="text" name="lastName" label={isCompany ? "Company name" : "Last name"} disabled={usiLocked} required />
          </Grid>
        </Grid>
      )}
    />
  );
};

const ContactsGeneral: React.FC<ContactsGeneralProps> = props => {
  const {
    twoColumn,
    values,
    initialValues,
    form,
    dispatch,
    isStudent,
    isTutor,
    isCompany,
    setIsStudent,
    setIsTutor,
    setIsCompany,
    tags,
    isNew,
    syncErrors,
    showConfirm,
    usiLocked,
  } = props;

  const isInitiallyStudent = initialValues && !!initialValues.student;
  const isInitiallyTutor = initialValues && !!initialValues.tutor;
  const isInitiallyCompany = initialValues && !!initialValues.isCompany;

  const toggleStudentRole = useCallback(() => {
    if (!isInitiallyStudent) {
      setIsStudent(!isStudent);
      dispatch(change(form, "student", isStudent ? null : { ...studentInitial }));
    }
    if (isNew) {
      dispatch(change(form, "student", isStudent ? null : { ...studentInitial }));
    }
  }, [isInitiallyStudent, isNew, isStudent]);

  const toggleTutorRole = useCallback(() => {
    if (!isInitiallyTutor) {
      setIsTutor(!isTutor);
      dispatch(change(form, "tutor", isTutor ? null : { ...TutorInitial }));
    }
    if (isNew) {
      dispatch(change(form, "tutor", isTutor ? null : { ...TutorInitial }));
    }
  }, [isInitiallyTutor, isNew, isTutor]);

  const toggleCompanyRole = useCallback(() => {
    if (!isInitiallyCompany) {
      setIsCompany(!isCompany);
      dispatch(change(form, "isCompany", isCompany ? null : true));
    }
    if (isNew) {
      dispatch(change(form, "isCompany", isCompany ? null : true));
    }
  }, [isInitiallyCompany, isNew, isCompany]);

  useEffect(() => {
    setIsCompany(isInitiallyCompany);
    setIsStudent(isInitiallyStudent);
    setIsTutor(isInitiallyTutor);
  }, [isInitiallyCompany, isInitiallyStudent, isInitiallyTutor]);

  const onStudentCalendarClick = () => openInternalLink(
    `/timetable?search=attendance.student.contact.id=${values.id}&title=Timetable for ${getContactFullName(
      values
    )}`
  );

  const onTutorCalendarClick = () => openInternalLink(
    `/timetable?search=tutor.contact.id=${values.id}&title=Timetable for ${getContactFullName(
      values
    )}`
  );

  const filteredTags = useMemo(() => {
    if (Array.isArray(tags)) {
      if (isStudent && isTutor) {
        return Array.from(new Set([...tags.filter(filterStudentTags), ...tags.filter(filterTutorTags)]));
      }
      if (isStudent) {
        return tags.filter(filterStudentTags);
      }
      if (isTutor) {
        return tags.filter(filterTutorTags);
      }
      if (isCompany) {
        return tags.filter(filterCompanyTags);
      }
    }

    return [];
  }, [tags, isStudent, isTutor, isCompany]);

  return (
    <div className="pt-3 pl-3 pr-3">
      <ProfileHeading
        isNew={isNew}
        form={form}
        dispatch={dispatch}
        showConfirm={showConfirm}
        values={values}
        twoColumn={twoColumn}
        isCompany={isCompany}
        usiLocked={usiLocked}
        syncErrors={syncErrors}
      />
      <Grid container columnSpacing={3}>
        <Grid item xs={12} md={twoColumn ? 7 : 12}>
          <Typography variant="caption" display="block" gutterBottom>
            Type
          </Typography>
          <ButtonGroup variant="outlined" className="mb-3 mt-1">
            <Button
              startIcon={isStudent ? <CheckBoxIcon /> : <CheckBoxOutlineBlankIcon />}
              className={isStudent ? "primaryColor" : "textSecondaryColor"}
              disabled={isCompany}
              onClick={toggleStudentRole}
            >
              Student
            </Button>
            <Button
              className={isTutor ? "primaryColor" : "textSecondaryColor"}
              startIcon={isTutor ? <CheckBoxIcon /> : <CheckBoxOutlineBlankIcon />}
              onClick={toggleTutorRole}
            >
              Tutor
            </Button>
            <Button
              className={isCompany ? "primaryColor" : "textSecondaryColor"}
              startIcon={isCompany ? <CheckBoxIcon /> : <CheckBoxOutlineBlankIcon />}
              disabled={isStudent}
              onClick={toggleCompanyRole}
            >
              Company
            </Button>
          </ButtonGroup>
        </Grid>
      </Grid>
      <Grid container columnSpacing={3} rowSpacing={2}>
        <Grid item xs={twoColumn ? 8 : 12}>
          <FormField
            type="tags"
            name="tags"
            tags={filteredTags}
          />
        </Grid>
        <Grid item xs={twoColumn ? 4 : 12}>
          <EntityChecklists
            entity="Contact"
            form={form}
            entityId={values.id}
            checked={values.tags}
          />
        </Grid>
      </Grid>
      {isStudent && (
        <>
          <Divider className="mt-3 mb-2"/>
          <Grid container columnSpacing={3} className="pt-0-5 pb-0-5">
            <Grid item xs={12}>
              <TimetableButton onClick={onStudentCalendarClick} title="Student timetable"/>
            </Grid>
          </Grid>
        </>
      )}
      {isTutor && (
        <>
          <Divider className="mt-3 mb-2"/>
          <Grid container columnSpacing={3} className="pt-0-5 pb-0-5">
            <Grid item xs={12}>
              <TimetableButton onClick={onTutorCalendarClick} title="Tutor timetable"/>
            </Grid>
          </Grid>
        </>
      )}
    </div>
);
};

const mapStateToProps = (state: State, props) => ({
  initialValues: getFormInitialValues(props.form)(state),
  tags: state.tags.entityTags["Contact"],
  countries: state.countries,
  concessionTypes: state.contacts.contactsConcessionTypes,
  fullScreenEditView: state.list.fullScreenEditView
});

export default connect<any, any, any>(
  mapStateToProps,
)((props: any) => (props.values ? <ContactsGeneral {...props} /> : null)) as React.FC<
  ContactsGeneralProps
>;