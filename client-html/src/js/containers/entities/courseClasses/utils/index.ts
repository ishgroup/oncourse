/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssessmentClass, Session } from "@api/model";
import { openInternalLink } from "../../../../common/utils/links";
import {
  ClassCostExtended,
  ClassCostItem,
  ClassCostTypes,
  Classes,
  CourseClassStatus,
  CourseClassTutorExtended,
  TutorAttendanceExtended
} from "../../../../model/entities/CourseClass";
import { EntityType } from "../../../../model/common/NestedEntity";
import CourseClassTutorService from "../components/tutors/services/CourseClassTutorService";
import CourseClassCostService from "../components/budget/services/ClassCostService";
import { State } from "../../../../reducers/state";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import CourseClassTimetableService from "../components/timetable/services/CourseClassTimetableService";
import CourseClassAssessmentService from "../components/assessments/services/CourseClassAssessmentService";
import { getClassCostFee } from "../components/budget/utils";
import { decimalMul, decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { TimetableSession } from "../../../../model/timetable";
import CourseClassAttendanceService from "../components/attendance/services/CourseClassAttendanceService";

export const openCourseClassLink = (classId: number) => openInternalLink(`/${Classes.path}/${classId}`);

export const getNestedCourseClassItem = (status: CourseClassStatus, count: number, id: number): EntityType => {
  switch (status) {
    case "Current":
      return {
        name: "Current",
        count,
        link: `/${Classes.path}?search=course.id is ${id}&filter=@Current_classes`,
        // eslint-disable-next-line max-len
        timetableLink: `/timetable/search?query=courseClass.course.id=${id} and courseClass.startDateTime < tomorrow and courseClass.endDateTime >= today and courseClass.isCancelled is false`
      };
    case "Future":
      return {
        name: "Future",
        count,
        link: `/${Classes.path}?search=course.id is ${id}&filter=@Future_classes`,
        // eslint-disable-next-line max-len
        timetableLink: `/timetable/search?query=courseClass.course.id=${id} and courseClass.startDateTime >= tomorrow and courseClass.endDateTime >= tomorrow and courseClass.isCancelled is false`
      };
    case "Self Paced":
      return {
        name: "Self Paced",
        count,
        link: `/${Classes.path}?search=course.id is ${id}&filter=@Self_paced_classes`
      };
    case "Unscheduled":
      return {
        name: "Unscheduled",
        count,
        link: `/${Classes.path}?search=course.id is ${id}&filter=@Unscheduled_classes`
      };
    case "Finished":
      return {
        name: "Finished",
        count,
        link: `/${Classes.path}?search=course.id is ${id}&filter=@Finished_classes`,
        // eslint-disable-next-line max-len
        timetableLink: `/timetable/search?query=courseClass.course.id=${id} and courseClass.isCancelled is false and courseClass.endDateTime before today`
      };
    case "Cancelled":
      return {
        name: "Cancelled",
        count,
        link: `/${Classes.path}?search=course.id is ${id}&filter=@Cancelled_classes`,
        grayOut: true
      };
    default: {
      console.error(`Unknown Course Class status ${status} !`);
      return null;
    }
  }
};

const sortClassCosts = (a: ClassCostItem, b: ClassCostItem) => (
  (!a.value || !b.value)
    ? 0
    : a.value.description > b.value.description ? 1 : -1
);

export const getClassCostTypes = (
  budget: ClassCostExtended[],
  maximumPlaces: number,
  budgetedPlaces: number,
  successAndQueuedEnrolmentsCount: number,
  sessions: TimetableSession[],
  tutors: CourseClassTutorExtended[],
  tutorRoles: any[],
  tutorAttendance: TutorAttendanceExtended[]
) => {
  const types: ClassCostTypes = {
    income: {
      max: 0,
      projected: 0,
      actual: 0,
      items: []
    },
    customInvoices: {
      max: 0,
      projected: 0,
      actual: 0,
      items: []
    },
    discount: {
      max: 0,
      projected: 0,
      actual: 0,
      items: []
    },
    cost: {
      max: 0,
      projected: 0,
      actual: 0,
      items: []
    }
  };

  if (budget && budget.length) {
    budget.forEach((value, index) => {
      value.index = index;

      const item = {
        value,
        max: 0,
        projected: 0,
        actual: 0
      };

      const fee = getClassCostFee(
        value,
        maximumPlaces,
        budgetedPlaces,
        successAndQueuedEnrolmentsCount,
        sessions,
        tutorAttendance
      );

      item.max = fee.max;
      item.projected = fee.projected;
      item.actual = fee.actual;

      switch (value.flowType) {
        case "Income":
          types.income.items.push(item);
          types.income.max = decimalPlus(types.income.max, item.max);
          types.income.projected = decimalPlus(types.income.projected, item.projected);
          types.income.actual = decimalPlus(types.income.actual, item.actual);
          break;
        case "Discount":
          types.discount.items.push(item);
          types.discount.max = decimalPlus(types.discount.max, item.max);
          types.discount.projected = decimalPlus(types.discount.projected, item.projected);
          types.discount.actual = decimalPlus(types.discount.actual, item.actual);
          break;
        case "Wages":
          const tutor = tutors.find(
            t => (value.courseClassTutorId ? t.id === value.courseClassTutorId : t.temporaryId === value.temporaryTutorId)
          );
          const role = tutor && tutorRoles.find(r => r.id === tutor.roleId);
          const defaultOnCostRate = (role && role["currentPayrate.oncostRate"]) ? parseFloat(role["currentPayrate.oncostRate"]) : 0;

          item.max = decimalMul(item.max, decimalPlus(value.onCostRate || defaultOnCostRate, 1));
          item.projected = decimalMul(item.projected, decimalPlus(value.onCostRate || defaultOnCostRate, 1));
          item.actual = decimalMul(item.actual, decimalPlus(value.onCostRate || defaultOnCostRate, 1));

          types.cost.items.push(item);
          types.cost.max = decimalPlus(types.cost.max, item.max);
          types.cost.projected = decimalPlus(types.cost.projected, item.projected);
          types.cost.actual = decimalPlus(types.cost.actual, item.actual);
          break;
        case "Expense":
          types.cost.items.push(item);
          types.cost.max = decimalPlus(types.cost.max, item.max);
          types.cost.projected = decimalPlus(types.cost.projected, item.projected);
          types.cost.actual = decimalPlus(types.cost.actual, item.actual);
          break;
        case "Custom invoice":
          types.customInvoices.items.push(item);
          types.customInvoices.max = decimalPlus(types.customInvoices.max, value.actualAmount);
          types.customInvoices.projected = types.customInvoices.max;
          types.customInvoices.actual = types.customInvoices.max;
      }
    });
  }

  for (const k in types) {
    types[k].items.sort(sortClassCosts);
  }

  return types;
};

export const processCourseClassApiActions = async (s: State, createdClassId?: number) => {
  const unprocessedAsyncActions = s.actionsQueue.queuedActions;
  const savedTutorsIds = s.form[LIST_EDIT_VIEW_FORM_NAME].values.tutors.map(t => t.id);
  const sessionUpdateAction = unprocessedAsyncActions.find(s => s.entity === "Session");

  const tutorCreateActions = unprocessedAsyncActions.filter(
    a => a.method === "POST" && a.entity === "CourseClassTutor"
  );

  const createdTutorsIds = [];

  await tutorCreateActions
    .map(t => () => {
      const tutorBody = t.actionBody.payload.tutor;

      if (!tutorBody.classId) {
        tutorBody.classId = createdClassId;
      }

      return CourseClassTutorService.postCourseClassTutor(tutorBody).then(r => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(a => a.id === t.id),
          1
        );
        return { tempId: t.id, createdId: r };
      });
    })
    .reduce(async (a, b) => {
      await a;
      createdTutorsIds.push(await b());
    }, Promise.resolve());

  const tutorDeleteActions = unprocessedAsyncActions.filter(
    a => a.method === "DELETE" && a.entity === "CourseClassTutor"
  );

  await tutorDeleteActions
    .map(t => () => CourseClassTutorService.deleteCourseClassTutor(t.actionBody.payload).then(() => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(a => a.id === t.id),
          1
        );
      }))
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

  if (sessionUpdateAction) {
    sessionUpdateAction.actionBody.payload.sessions.forEach((session: Session) => {
      if (!session.classId) {
        session.classId = createdClassId;
      }

      session.courseClassTutorIds.forEach(id => {
        if (!savedTutorsIds.includes(id)) {
          session.courseClassTutorIds = session.courseClassTutorIds.filter(tId => tId !== id);
        }
      });

      if (session.temporaryTutorIds.length) {
        session.courseClassTutorIds = session.courseClassTutorIds.concat(
          session.temporaryTutorIds.map(id => createdTutorsIds.find(tId => tId.tempId === id).createdId)
        );
      }
    });

    if (!sessionUpdateAction.id) {
      sessionUpdateAction.id = createdClassId;
    }

    if (!sessionUpdateAction.actionBody.payload.classId) {
      sessionUpdateAction.actionBody.payload.classId = createdClassId;
    }

    await CourseClassTimetableService.updateTimetableSessionsForCourseClass(
      sessionUpdateAction.actionBody.payload.classId,
      sessionUpdateAction.actionBody.payload.sessions
    ).then(() => {
      unprocessedAsyncActions.splice(
        unprocessedAsyncActions.findIndex(a => a.id === sessionUpdateAction.id),
        1
      );
    });
  }

  const costUpdateAsyncActions = unprocessedAsyncActions.filter(
    a => a.entity === "ClassCost" && (a.method === "POST" || a.method === "PUT")
  );

  await costUpdateAsyncActions
    .map(c => () => {
      const costBody = c.actionBody.payload.cost;

      if (!costBody.courseClassid) {
        costBody.courseClassid = createdClassId;
      }

      return (c.method === "POST"
        ? CourseClassCostService.postCourseClassCost({
            ...costBody,
            courseClassTutorId: c.bindedActionId
              ? createdTutorsIds.find(i => i.tempId === c.bindedActionId).createdId
              : costBody.courseClassTutorId
          })
        : CourseClassCostService.putCourseClassCost(costBody)
      ).then(() => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(a => a.id === c.id),
          1
        );
      });
    })
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

  const assessmentCreateActions = unprocessedAsyncActions.filter(
    a => a.entity === "AssessmentClass" && a.method === "POST"
  );

  await assessmentCreateActions
    .map(a => () => {
      const assessmentBody: AssessmentClass = a.actionBody.payload.assessment;

      if (!assessmentBody.courseClassId) {
        assessmentBody.courseClassId = createdClassId;
      }

      return CourseClassAssessmentService.createCourseClassAssessment(assessmentBody).then(() => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(u => u.id === a.id),
          1
        );
      });
    })
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

  const assessmentUpdateActions = unprocessedAsyncActions.filter(
    a => a.entity === "AssessmentClass" && a.method === "PUT"
  );

  await assessmentUpdateActions
    .map(a => () => CourseClassAssessmentService.updateCourseClassAssessment(a.actionBody.payload.assessment).then(() => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(u => u.id === a.id),
          1
        );
      }))
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

  const tutorAttendanceActions = unprocessedAsyncActions.filter(
    a => a.entity === "TutorAttendance" && a.method === "POST"
  );

  await tutorAttendanceActions
    .map(a => () => {
      const { id, tutorAttendance } = a.actionBody.payload;

      return CourseClassAttendanceService.updateTutorAttendance(id, tutorAttendance).then(() => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(u => u.id === a.id),
          1
        );
      });
    })
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

  const sessionModuleActions = unprocessedAsyncActions.filter(
    a => a.entity === "SessionModule" && a.method === "POST"
  );

  await sessionModuleActions
    .map(a => () => {
      const { id, trainingPlans } = a.actionBody.payload;

      return CourseClassAttendanceService.updateTrainingPlans(id, trainingPlans).then(() => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(u => u.id === a.id),
          1
        );
      });
    })
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

  const studentAttendanceActions = unprocessedAsyncActions.filter(
    a => a.entity === "StudentAttendance" && a.method === "POST"
  );

  await studentAttendanceActions
    .map(a => () => {
      const { id, studentAttendance } = a.actionBody.payload;

      return CourseClassAttendanceService.updateStudentAttendance(id, studentAttendance).then(() => {
        unprocessedAsyncActions.splice(
          unprocessedAsyncActions.findIndex(u => u.id === a.id),
          1
        );
      });
    })
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

  return unprocessedAsyncActions;
};
