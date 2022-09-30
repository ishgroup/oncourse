/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { Grid } from "@mui/material";
import { FieldArray, initialize } from "redux-form";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { openInternalLink } from "../../../../common/utils/links";
import { State } from "../../../../reducers/state";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { EditViewProps } from "../../../../model/common/ListView";

interface ContactsEducationProps extends EditViewProps {
  educationValues?: any;
  access?: AccessState;
}

const certificatesColumns: NestedTableColumn[] = [
  {
    name: "fullQualification",
    title: "Full qualification or skill set",
    type: "checkbox",
    disabledHandler: true,
    width: 220
  },
  {
    name: "nationalCode",
    title: "National code",
    width: 160
  },
  {
    name: "qualificationName",
    title: "Qualification name"
  },
  {
    name: "certificateNumber",
    title: "Certificate number",
    width: 160
  },
  {
    name: "createdOn",
    title: "Created",
    type: "date",
    width: 160
  },
  {
    name: "lastPrintedOn",
    title: "Last printed",
    type: "date",
    width: 160
  }
];

const enrolmentColumns: NestedTableColumn[] = [
  {
    name: "invoiceNumber",
    title: "Invoice #",
    width: 160
  },
  {
    name: "createdOn",
    title: "Created",
    type: "date",
    width: 160
  },
  {
    name: "uniqueCode",
    title: "Unique code",
    width: 160
  },
  {
    name: "courseName",
    title: "Course name"
  },
  {
    name: "status",
    title: "Status",
    width: 160
  }
];

const priorLearningsColumns: NestedTableColumn[] = [
  {
    name: "title",
    title: "Title"
  },
  {
    name: "externalRef",
    title: "External Reference"
  },
  {
    name: "qualNationalCode",
    title: "Qual National Code"
  },
  {
    name: "qualLevel",
    title: "Qual Level"
  },
  {
    name: "qualName",
    title: "Qual Name"
  }
];

const outcomesColumns: NestedTableColumn[] = [
  {
    name: "nationalCode",
    title: "National code",
    width: 160
  },
  {
    name: "course",
    title: "Module/Course"
  },
  {
    name: "status",
    title: "Status",
    width: 160
  },
  {
    name: "startDate",
    title: "Start date",
    type: "date",
    width: 160
  },
  {
    name: "endDate",
    title: "End date",
    type: "date",
    width: 160
  },
  {
    name: "deliveryMode",
    title: "Delivery mode",
    width: 160
  }
];

const openEnrolmentsRow = value => {
  const { id } = value;
  openInternalLink("/enrolment/" + id);
};

const openPriorLearningsRow = value => {
  const { id } = value;
  openInternalLink("/priorLearning/" + id);
};

const openOutcomesRow = value => {
  const { id } = value;
  openInternalLink("/outcome/" + id);
};

const openCertificatesRow = value => {
  const { id } = value;
  openInternalLink("/certificate/" + id);
};

const ContactsEducation: React.FC<ContactsEducationProps> = props => {
  const {
    values,
    access,
    educationValues,
    form,
    tabIndex,
    expanded,
    setExpanded,
    syncErrors
  } = props;

  const hasQePermissions = access["ENROLMENT_CREATE"];

  useEffect(() => {
    if (!values || !values.student) return;

    const newValues = { ...values };

    if (!educationValues) {
      newValues.student.education = null;
      initialize(form, newValues);
      return;
    }

    if (newValues.student.education) {
      newValues.student.education.enrolments = educationValues.enrolments;
      newValues.student.education.priorLearnings = educationValues.priorLearnings;
      newValues.student.education.outcomes = educationValues.outcomes;
      newValues.student.education.certificates = educationValues.certificates;
    } else {
      newValues.student.education = {
        enrolments: educationValues.enrolments,
        priorLearnings: educationValues.priorLearnings,
        outcomes: educationValues.outcomes,
        certificates: educationValues.certificates
      };
    }

    initialize(form, newValues);
  }, [
    values,
    educationValues,
    educationValues && educationValues.enrolments,
    educationValues && educationValues.priorLearnings,
    educationValues && educationValues.outcomes,
    educationValues && educationValues.certificates
  ]);

  const enrolmentsCount = useMemo(
    () => (educationValues && educationValues.enrolments ? educationValues.enrolments.length : 0),
    [educationValues, educationValues && educationValues.enrolments]
  );

  const priorLearningsCount = useMemo(
    () => (educationValues && educationValues.priorLearnings ? educationValues.priorLearnings.length : 0),
    [educationValues, educationValues && educationValues.priorLearnings]
  );

  const outcomesCount = useMemo(
    () => (educationValues && educationValues.outcomes ? educationValues.outcomes.length : 0),
    [educationValues, educationValues && educationValues.outcomes]
  );

  const certificatesCount = useMemo(
    () => (educationValues && educationValues.certificates ? educationValues.certificates.length : 0),
    [educationValues, educationValues && educationValues.certificates]
  );

  const onAddEnrolment = useCallback(() => {
    openInternalLink(`/checkout?contactId=${values.id}`);
  }, [values.id]);

  const onAddPriorLearning = useCallback(() => {
    openInternalLink(`/priorLearning/new?contactId=${values.id}`);
  }, [values.id]);

  const onAddCertificates = useCallback(() => {
    openInternalLink(`/certificate/new?contactId=${values.id}`);
  }, [values.id]);

  const enrolmentsPermissions = access["/a/v1/list/plain?entity=Enrolment"] && access["/a/v1/list/plain?entity=Enrolment"]["GET"];
  const priorLearningsPermissions = access["/a/v1/list/plain?entity=PriorLearning"] && access["/a/v1/list/plain?entity=PriorLearning"]["GET"];
  const outcomesPermissions = access["/a/v1/list/plain?entity=Outcome"] && access["/a/v1/list/plain?entity=Outcome"]["GET"];
  const certificatesPermissions = access["/a/v1/list/plain?entity=Certificate"] && access["/a/v1/list/plain?entity=Certificate"]["GET"];

  return (
    <div className="pl-3 pr-3">
      <ExpandableContainer index={tabIndex} expanded={expanded} formErrors={syncErrors} setExpanded={setExpanded} header="Education">
        <Grid container columnSpacing={3}>
          {enrolmentsPermissions && (
          <Grid
            item
            xs={12}
          >
            <FieldArray
              name="student.education.enrolments"
              goToLink="/enrolment"
              title={enrolmentsCount === 1 ? "enrolment" : "enrolments"}
              component={NestedTable}
              columns={enrolmentColumns}
              onRowDoubleClick={openEnrolmentsRow}
              onAdd={hasQePermissions ? onAddEnrolment : undefined}
              rerenderOnEveryChange
              sortBy={(a, b) => b.invoiceNumber - a.invoiceNumber}
              calculateHeight
            />
          </Grid>
        )}
        </Grid>
        <Grid container columnSpacing={3}>
          {priorLearningsPermissions
            && (
            <Grid
              item
              xs={12}
              className="mt-2"
            >
              <FieldArray
                name="student.education.priorLearnings"
                goToLink="/priorLearning"
                title={priorLearningsCount === 1 ? "prior learning" : "prior learnings"}
                component={NestedTable}
                columns={priorLearningsColumns}
                onRowDoubleClick={openPriorLearningsRow}
                onAdd={onAddPriorLearning}
                rerenderOnEveryChange
                calculateHeight
              />
            </Grid>
          )}
        </Grid>
        <Grid container columnSpacing={3}>
          {outcomesPermissions
            && (
            <Grid
              item
              xs={12}
              className="mt-2"
            >
              <FieldArray
                name="student.education.outcomes"
                goToLink="/outcomes"
                title={outcomesCount === 1 ? "outcome" : "outcomes"}
                component={NestedTable}
                columns={outcomesColumns}
                onRowDoubleClick={openOutcomesRow}
                rerenderOnEveryChange
                sortBy={(a, b) => new Date(b.endDate).getTime() - new Date(a.endDate).getTime()}
                calculateHeight
              />
            </Grid>
          )}
        </Grid>
        <Grid container columnSpacing={3} className="saveButtonTableOffset">
          {certificatesPermissions
            && (
            <Grid
              item
              xs={12}
              className="mt-2"
            >
              <FieldArray
                name="student.education.certificates"
                goToLink="/certificate"
                title={certificatesCount === 1 ? "certificate" : "certificates"}
                component={NestedTable}
                columns={certificatesColumns}
                onRowDoubleClick={openCertificatesRow}
                onAdd={onAddCertificates}
                rerenderOnEveryChange
                sortBy={(a, b) => b.certificateNumber - a.certificateNumber}
                calculateHeight
              />
            </Grid>
          )}
        </Grid>
      </ExpandableContainer>
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  educationValues: state.contacts.education,
  access: state.access
});

export default connect<any, any, any>(mapStateToProps, null)(ContactsEducation);
