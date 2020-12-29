/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect } from "react";
import debounce from "lodash.debounce";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import EditInPlaceSearchSelect from "./EditInPlaceSearchSelect";
import { AnyArgFunction, NumberArgFunction, StringArgFunction } from "../../../../model/common/CommonFunctions";
import { State } from "../../../../reducers/state";
import {
  clearContacts,
  getContacts,
  setContactsSearch
} from "../../../../containers/entities/contacts/actions";
import {
  clearPlainQualificationItems,
  getPlainQualifications, setPlainQualificationSearch
} from "../../../../containers/entities/qualifications/actions";
import { clearModuleItems, getModules, setModuleSearch } from "../../../../containers/entities/modules/actions";
import { getSites, setPlainSites, setPlainSitesSearch } from "../../../../containers/entities/sites/actions";
import {
  fundingInvoicePlainSearchCompanyKey,
  getFundingInvoiceCompanies,
  onClearFundingInvoiceCompaniesSearch,
  setFundingInvoiceCompaniesSearch
} from "../../../../containers/checkout/actions/checkoutFundingInvoice";
import { getCommonPlainRecordFromState } from "../../../actions/CommonPlainRecordsActions";
import { getPlainCourses, setPlainCourses, setPlainCoursesSearch } from "../../../../containers/entities/courses/actions";
import {
  clearAssessmentItems,
  getAssessments,
  setAssessmentSearch
} from "../../../../containers/entities/assessments/actions";
import {
  clearPlainCourseClasses,
  getPlainCourseClasses,
  setPlainCourseClassSearch
} from "../../../../containers/entities/courseClasses/actions";

interface Props {
  onSearchChange: StringArgFunction;
  onLoadMoreRows: NumberArgFunction;
  onClearRows: AnyArgFunction;
  rowHeight?: number;
  items: any[];
  loading?: boolean;
}

const EditInPlaceRemoteDataSearchSelect: React.FC<Props> = ({ onLoadMoreRows, onSearchChange, ...rest }) => {
  const onInputChange = useCallback(debounce((input: string) => {
    onSearchChange(input);
    if (input) {
      onLoadMoreRows(0);
    }
  }, 800), []);

  const onLoadMoreRowsOwn = startIndex => {
    if (!rest.loading) {
      onLoadMoreRows(startIndex);
    }
  };

  return (
    <EditInPlaceSearchSelect {...rest as any} onInputChange={onInputChange} loadMoreRows={onLoadMoreRowsOwn} remoteData />
  );
};

const EntityResolver: React.FC<any> = (
  {
    entity,
    // Assessment:
    assessments = [],
    assessmentsLoading,
    assessmentsRowsCount,
    getAssessments,
    clearAssessmentItems,
    setAssessmentSearch,
    // Qualification:
    qualifications = [],
    setQualificationsSearch,
    getQualifications,
    clearQualifications,
    qualificationsLoading,
    qualificationsRowsCount,
    // Contact:
    contacts = [],
    setContactsSearch,
    getContacts,
    clearContacts,
    contactsLoading,
    contactsRowsCount,
    // Module:
    modules = [],
    modulesLoading,
    modulesRowsCount,
    getModules,
    clearModules,
    setModuleSearch,
    // Site:
    sites = [],
    setSitesSearch,
    getSites,
    clearSites,
    sitesLoading,
    sitesRowsCount,
    // FundingInvoiceCompany:
    listCompanies = [],
    setCompaniesSearch,
    getCompanies,
    companiesLoading,
    companiesRowCount,
    onClearCompaniesSearch,
    // Course:
    courses = [],
    coursesLoading,
    coursesRowsCount,
    getCourses,
    clearCourses,
    setCoursesSearch,
    // CourseClass:
    courseClasses = [],
    courseClassesLoading,
    courseClassesRowsCount,
    getCourseClasses,
    setCourseClassesSearch,
    clearCourseClasses,
    ...rest
  }
) => {
  useEffect(() => {
    switch (entity) {
      case "Assessment": {
        setAssessmentSearch("");
        return () => setAssessmentSearch("");
      }
      case "Qualification": {
        setQualificationsSearch("");
        return () => setQualificationsSearch("");
      }
      case "Contact": {
        setContactsSearch("");
        return () => setContactsSearch("");
      }
      case "Module": {
        setModuleSearch("");
        return () => setModuleSearch("");
      }
      case "Site": {
        setSitesSearch("");
        return () => setSitesSearch("");
      }
      case "FundingInvoiceCompany": {
        setCompaniesSearch("");
        return () => onClearCompaniesSearch();
      }
      case "Course": {
        setCoursesSearch("");
        return () => setCoursesSearch();
      }
      case "CourseClass": {
        setCourseClassesSearch("");
        return () => setCourseClassesSearch();
      }
    }
    return null;
  }, []);

  const getEntityProps = entity => {
    const props = {
      items: [],
      onSearchChange: null,
      onLoadMoreRows: null,
      onClearRows: null,
      loading: null,
      remoteRowCount: null
    };

    switch (entity) {
      case "Assessment": {
        props.items = assessments;
        props.onSearchChange = setAssessmentSearch;
        props.onLoadMoreRows = getAssessments;
        props.onClearRows = clearAssessmentItems;
        props.loading = assessmentsLoading;
        props.remoteRowCount = assessmentsRowsCount;
        break;
      }
      case "Qualification": {
        props.items = qualifications;
        props.onSearchChange = setQualificationsSearch;
        props.onLoadMoreRows = getQualifications;
        props.onClearRows = clearQualifications;
        props.loading = qualificationsLoading;
        props.remoteRowCount = qualificationsRowsCount;
        break;
      }
      case "Contact": {
        props.items = contacts;
        props.onSearchChange = setContactsSearch;
        props.onLoadMoreRows = getContacts;
        props.onClearRows = clearContacts;
        props.loading = contactsLoading;
        props.remoteRowCount = contactsRowsCount;
        break;
      }
      case "Module": {
        props.items = modules;
        props.onSearchChange = setModuleSearch;
        props.onLoadMoreRows = getModules;
        props.onClearRows = clearModules;
        props.loading = modulesLoading;
        props.remoteRowCount = modulesRowsCount;
        break;
      }
      case "Site": {
        props.items = sites;
        props.onSearchChange = setSitesSearch;
        props.onLoadMoreRows = getSites;
        props.onClearRows = clearSites;
        props.loading = sitesLoading;
        props.remoteRowCount = sitesRowsCount;
        break;
      }
      case "FundingInvoiceCompany": {
        props.items = listCompanies;
        props.onSearchChange = setCompaniesSearch;
        props.onLoadMoreRows = getCompanies;
        props.onClearRows = onClearCompaniesSearch;
        props.loading = companiesLoading;
        props.remoteRowCount = companiesRowCount;
        break;
      }
      case "Course": {
        props.items = courses;
        props.onSearchChange = setCoursesSearch;
        props.onLoadMoreRows = getCourses;
        props.onClearRows = clearCourses;
        props.loading = coursesLoading;
        props.remoteRowCount = coursesRowsCount;
        break;
      }
      case "CourseClass": {
        props.items = courseClasses;
        props.onSearchChange = setCourseClassesSearch;
        props.onLoadMoreRows = getCourseClasses;
        props.onClearRows = clearCourseClasses;
        props.loading = courseClassesLoading;
        props.remoteRowCount = courseClassesRowsCount;
        break;
      }
    }
    return props;
  };

  return <EditInPlaceRemoteDataSearchSelect {...rest} {...getEntityProps(entity)} />;
};

const getCompaniesPlainRecord = state => getCommonPlainRecordFromState(state, fundingInvoicePlainSearchCompanyKey);

const mapStateToProps = (state: State) => ({
  // Assessment:
  assessments: state.assessments.items,
  assessmentsLoading: state.assessments.loading,
  assessmentsRowsCount: state.assessments.rowsCount,
  // Contact:
  contacts: state.contacts.items,
  contactsLoading: state.contacts.loading,
  contactsRowsCount: state.contacts.rowsCount,
  // Qualification:
  qualifications: state.qualification.items,
  qualificationsLoading: state.qualification.loading,
  qualificationsRowsCount: state.qualification.rowsCount,
  // Module:
  modules: state.modules.items,
  modulesLoading: state.modules.loading,
  modulesRowsCount: state.modules.rowsCount,
  // Site:
  sites: state.sites.items,
  sitesLoading: state.sites.loading,
  sitesRowsCount: state.sites.rowsCount,
  // FundingInvoiceCompany:
  listCompanies: getCompaniesPlainRecord(state).items,
  companiesLoading: getCompaniesPlainRecord(state).loading,
  companiesRowCount: getCompaniesPlainRecord(state).rowsCount,
  // Course:
  courses: state.courses.items,
  coursesLoading: state.courses.loading,
  coursesRowsCount: state.courses.rowsCount,
  // CourseClass:
  courseClasses: state.courseClasses.items,
  courseClassesLoading: state.courseClasses.loading,
  courseClassesRowsCount: state.courseClasses.rowsCount,
});

const mapDispatchToProps = (dispatch: Dispatch<any>, ownProps) => {
  const getSearch = search => (search ? `~"${search}"${ownProps.aqlFilter ? ` and ${ownProps.aqlFilter}` : ""}` : "");
  return {
    // Assessment:
    getAssessments: (offset?: number) => dispatch(getAssessments(offset, null, true)),
    clearAssessmentItems: () => dispatch(clearAssessmentItems()),
    setAssessmentSearch: (search: string) => dispatch(setAssessmentSearch(getSearch(search))),
    // Contact:
    getContacts: (offset?: number) => dispatch(getContacts(offset, "firstName,lastName,email,birthDate,street,suburb,state,postcode,invoiceTerms,taxOverride.id", true)),
    clearContacts: () => dispatch(clearContacts()),
    setContactsSearch: (search: string) => dispatch(setContactsSearch(getSearch(search))),
    // Qualification:
    getQualifications: (offset?: number) => dispatch(getPlainQualifications(offset, "", true)),
    clearQualifications: () => dispatch(clearPlainQualificationItems()),
    setQualificationsSearch: (search: string) => dispatch(setPlainQualificationSearch(getSearch(search))),
    // Module:
    getModules: (offset?: number) => dispatch(getModules(offset, "nationalCode,title,nominalHours", true, 100)),
    clearModules: () => dispatch(clearModuleItems()),
    setModuleSearch: (search: string) => dispatch(setModuleSearch(getSearch(search))),
    // Site:
    setSitesSearch: (search?: string) => dispatch(setPlainSitesSearch(getSearch(search))),
    getSites: (offset?: number, search?: string) => {
      dispatch(getSites(offset, "name,localTimezone", null, null, search));
    },
    clearSites: () => dispatch(setPlainSites([])),
    // FundingInvoiceCompany:
    setCompaniesSearch: (search: string) => dispatch(setFundingInvoiceCompaniesSearch(search)),
    onClearCompaniesSearch: () => dispatch(onClearFundingInvoiceCompaniesSearch()),
    getCompanies: (offset: number) => dispatch(getFundingInvoiceCompanies(offset)),
    // Course:
    getCourses: (offset?: number) => dispatch(getPlainCourses(offset, "code,name", true)),
    clearCourses: () => dispatch(setPlainCourses([])),
    setCoursesSearch: (search: string) => dispatch(setPlainCoursesSearch(getSearch(search))),
    // CourseClass:
    getCourseClasses: (offset?: number) => dispatch(getPlainCourseClasses(offset, "course.name,course.code,code", true)),
    setCourseClassesSearch: (search: string) => dispatch(setPlainCourseClassSearch(getSearch(search))),
    clearCourseClasses: () => dispatch(clearPlainCourseClasses())
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EntityResolver);
