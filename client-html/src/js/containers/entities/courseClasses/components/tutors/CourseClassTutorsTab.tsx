/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo, useState } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import {
 arrayInsert, change, FieldArray, initialize, WrappedFieldArrayProps
} from "redux-form";
import Grid from "@material-ui/core/Grid";
import { ClassCost, CourseClassTutor, DefinedTutorRole } from "@api/model";

import { EditViewProps } from "../../../../../model/common/ListView";
import { ClassCostExtended, CourseClassExtended, CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import { State } from "../../../../../reducers/state";
import { StringArgFunction } from "../../../../../model/common/CommonFunctions";
import { contactLabelCondition } from "../../../contacts/utils";
import CourseClassTutorsRenderer from "./CourseClassTutorsRenderer";
import { addActionToQueue, removeActionsFromQueue } from "../../../../../common/actions";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import CourseClassTutorService from "./services/CourseClassTutorService";
import { deleteCourseClassTutor, postCourseClassTutor, setCourseClassTutorNamesWarnings } from "./actions";
import { StringKeyAndValueObject } from "../../../../../model/common/CommomObjects";
import { getTutorNameWarning, getTutorPayInitial, isTutorWageExist } from "./utils";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../../constants";
import { setCourseClassBudgetModalOpened } from "../../actions";
import history from "../../../../../constants/History";
import uniqid from "../../../../../common/utils/uniqid";

export interface CourseClassTutorsTabProps extends Partial<EditViewProps> {
  values?: CourseClassExtended;
  setTutorNamesWarnings?: (warnings: StringKeyAndValueObject) => void;
  tutorRoles?: any;
  tutorNamesWarnings?: StringKeyAndValueObject;
  currencySymbol?: string;
  latestSession?: Date;
  setCourseClassBudgetModalOpened?: (opened: boolean, onCostRate?: number) => void;
  expandedBudget?: string[];
  expandBudgetItem?: StringArgFunction;
}

const TutorInitial: CourseClassTutor = {
  id: null,
  classId: null,
  contactId: null,
  roleId: null,
  tutorName: null,
  roleName: null,
  confirmedOn: null,
  isInPublicity: true
};

const CourseClassTutorsTab = React.memo<CourseClassTutorsTabProps>(
  ({
    values,
    tutorRoles,
    twoColumn,
    dispatch,
    form,
    showConfirm,
    currencySymbol,
    latestSession,
    setTutorNamesWarnings,
    tutorNamesWarnings,
    setCourseClassBudgetModalOpened,
    expandedBudget,
    expandBudgetItem
  }) => {
    const [expanded, setExpanded] = useState(null);

    const activeTutorRoles = useMemo(() => (tutorRoles ? tutorRoles.filter(t => t.active) : []), [
      tutorRoles
    ]);

    const onTutorIdChangeBase = useCallback(
      (index: number, value) => {
        const warningsUpdated = { ...tutorNamesWarnings };
        const warning = getTutorNameWarning(value, latestSession);

        if (warning) {
          warningsUpdated[value.id] = warning;
          setTutorNamesWarnings(warningsUpdated);
        } else if (tutorNamesWarnings[value.id]) {
          warningsUpdated[value.id] = null;
          setTutorNamesWarnings(warningsUpdated);
        }
        dispatch(change(form, `tutors[${index}].tutorName`, contactLabelCondition(value)));
      },
      [tutorNamesWarnings]
    );

    const onDeleteTutor = useCallback(
      (index: number, tutor: CourseClassTutorExtended, fields: WrappedFieldArrayProps["fields"]) => {
        const hasWages = isTutorWageExist(values.budget, tutor);

        showConfirm(
          () => {
            const upadted = [...fields.getAll()];
            upadted.splice(index, 1);

            const onDeleteConfirm = () => {
              if (expanded === index) {
                setExpanded(null);
              }
              dispatch(change(form, "tutors", upadted));

              if (hasWages) {
                dispatch(
                  change(
                    form,
                    "budget",
                    values.budget.filter(
                      b => !(
                          b.flowType === "Wages"
                          && (tutor.id ? b.courseClassTutorId === tutor.id : b.temporaryTutorId === tutor.temporaryId)
                        )
                    )
                  )
                );
              }
            };

            if (tutor.id) {
              CourseClassTutorService.validateDelete(tutor.id)
                .then(() => {
                  onDeleteConfirm();
                  dispatch(addActionToQueue(deleteCourseClassTutor(tutor.id), "DELETE", "CourseClassTutor", tutor.id));
                })
                .catch(response => instantFetchErrorHandler(dispatch, response));
              return;
            }
            dispatch(dispatch(removeActionsFromQueue([{ entity: "CourseClassTutor", id: tutor.temporaryId }])));

            onDeleteConfirm();
          },
          hasWages
            ? `Wages for ${tutor.tutorName} will be removed too, do you really want to continue?`
            : "Tutor will be deleted permanently",
          "Delete"
        );
      },
      [expanded, values.budget && values.budget.length, values.sessions]
    );

    const onAddTutor = useCallback(() => {
      const newTutor = { ...TutorInitial, classId: values.id };
      const temporaryId = uniqid();
      newTutor["temporaryId"] = temporaryId;

      dispatch(arrayInsert(form, "tutors", 0, newTutor));
      setExpanded(0);

      const postTutor = { ...newTutor };
      delete postTutor["temporaryId"];

      dispatch(
        addActionToQueue(
          postCourseClassTutor(postTutor),
          "POST",
          "CourseClassTutor",
          temporaryId,
          values.id ? null : temporaryId
        )
      );
    }, [values.tutors, values.id]);

    const onRoleIdChangeBase = useCallback((index: number, value: DefinedTutorRole) => {
      dispatch(change(form, `tutors[${index}].roleName`, value.name));
    }, []);

    const addTutorWage = useCallback(
      (tutor: CourseClassTutor, wage?: ClassCostExtended) => {
        const role = tutorRoles.find(r => r.id === tutor.roleId);
        const onCostRate = (role && role["currentPayrate.oncostRate"]) ? parseFloat(role["currentPayrate.oncostRate"]) : 0;
        const perUnitAmountExTax = (role && role["currentPayrate.rate"]) ? parseFloat(role["currentPayrate.rate"]) : 0;
        const initWage: ClassCost = wage || getTutorPayInitial(tutor, values.id, values.taxId, role, perUnitAmountExTax);

        setCourseClassBudgetModalOpened(true, isNaN(onCostRate) ? 0 : onCostRate);
        dispatch(initialize(COURSE_CLASS_COST_DIALOG_FORM, initWage));
        if (twoColumn) {
          const search = new URLSearchParams(window.location.search);
          search.append("expandTab", "4");
          history.replace({
            pathname: history.location.pathname,
            search: decodeURIComponent(search.toString())
          });

          if (!expandedBudget.includes("Total Cost")) {
            expandBudgetItem("Total Cost");
          }
        }
      },
      [tutorRoles, twoColumn, values.taxId, values.id, expandedBudget]
    );

    return (
      <Grid container className="pl-3 pr-3 pb-3">
        <Grid item xs={12} className="centeredFlex">
          <div className="heading">Tutors</div>
          <IconButton onClick={onAddTutor}>
            <AddCircle className="addButtonColor" />
          </IconButton>
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FieldArray
            name="tutors"
            component={CourseClassTutorsRenderer}
            setExpanded={setExpanded}
            expanded={expanded}
            onDeleteTutor={onDeleteTutor}
            onTutorIdChangeBase={onTutorIdChangeBase}
            onRoleIdChangeBase={onRoleIdChangeBase}
            activeTutorRoles={activeTutorRoles}
            addTutorWage={addTutorWage}
            tutorNamesWarnings={tutorNamesWarnings}
            currencySymbol={currencySymbol}
            latestSession={latestSession}
            budget={values.budget}
          />
        </Grid>
      </Grid>
    );
  }
);

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setTutorNamesWarnings: warnings => dispatch(setCourseClassTutorNamesWarnings(warnings)),
  setCourseClassBudgetModalOpened: (opened, onCostRate) => dispatch(setCourseClassBudgetModalOpened(opened, onCostRate))
});

const mapStateToProps = (state: State) => ({
  tutorNamesWarnings: state.courseClass.tutorNamesWarnings,
  latestSession: state.courseClass.latestSession,
  tutorRoles: state.preferences.tutorRoles,
  queuedActions: state.actionsQueue.queuedActions
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CourseClassTutorsTab);
