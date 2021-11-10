/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect
} from "react";
import {
  ConcessionType,
  Contact,
  ContactRelationType,
  Student,
  Tag,
  Tutor
} from "@api/model";
import {
 change, Field, getFormInitialValues
} from "redux-form";
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import { connect } from "react-redux";
import { Grid } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Divider from '@mui/material/Divider';
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import AvatarRenderer from "./AvatarRenderer";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { getContactFullName } from "../utils";
import { openInternalLink } from "../../../../common/utils/links";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import { EditViewProps } from "../../../../model/common/ListView";
import { StyledCheckbox } from "../../../../common/components/form/formFields/CheckboxField";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

const TutorInitial: Tutor = {
  wwChildrenStatus: "Not checked"
};

interface ContactsGeneralProps extends EditViewProps<Contact> {
  classes?: any;
  initialValues?: Contact;
  isStudent?: boolean;
  isTutor?: boolean;
  isCompany?: boolean;
  setIsStudent?: any;
  setIsTutor?: any;
  setIsCompany?: any;
  tags?: any;
  countries?: any;
  relationTypes?: ContactRelationType[];
  concessionTypes?: ConcessionType[];
  usiLocked?: boolean;
  fullScreenEditView?: boolean;
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

export const ProfileHeading: React.FC<any> = props => {
  const {
    form,
    dispatch,
    showConfirm,
    values,
    twoColumn,
    isCompany,
    usiLocked,
    isNew,
    invalid
  } = props;

  return (
    <FullScreenStickyHeader
      opened={isNew || invalid}
      twoColumn={twoColumn}
      Avatar={aProps => (
        <Field
          name="profilePicture"
          label="Profile picture"
          component={AvatarRenderer}
          props={{
            form,
            dispatch,
            showConfirm,
            email: values.email,
            twoColumn,
            ...aProps
          }}
        />
      )}
      title={(
        <>
          {values && !isCompany && values.title && values.title.trim().length > 0 ? `${values.title} ` : ""}
          {values ? (!isCompany ? getContactFullName(values) : values.lastName) : ""}
        </>
      )}
      fields={(
        <Grid container>
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
          <Grid item xs={twoColumn ? 4 : 6}>
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

  const validateTagList = useCallback((value, allValues, props) => validateTagsList(tags, value, allValues, props), [tags]);

  const onCalendarClick = () => {
    openInternalLink(
      `/timetable/search?query=attendance.student.contact.id=${values.id}&title=Timetable for ${getContactFullName(
        values
      )}`
    );
  };

  const getFilteredTags = useCallback(() => {
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

  const filteredTags = getFilteredTags();

  return (
    <div className="pt-3 pl-3 pr-3">
      <ProfileHeading {...props} isNew={isNew} />
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
      <Grid container columnSpacing={3} className="flex-nowrap align-items-center mb-1">
        <Grid item xs={12}>
          <FormField
            type="tags"
            name="tags"
            tags={filteredTags}
            validate={tags && tags.length ? validateTagList : undefined}
          />
        </Grid>
      </Grid>
      {isStudent && (
        <>
          <Divider className="mt-3 mb-3" />
          <Grid container columnSpacing={3}>
            <Grid item xs={12}>
              <TimetableButton onClick={onCalendarClick} />
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
