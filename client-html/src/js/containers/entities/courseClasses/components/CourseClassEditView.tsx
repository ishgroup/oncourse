/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import makeStyles from "@material-ui/core/styles/makeStyles";
import clsx from "clsx";
import Decimal from "decimal.js-light";
import React, {
 useEffect, useMemo, useState, Fragment
} from "react";
import { format } from "date-fns";
import { connect, useSelector } from "react-redux";
import { Tax } from "@api/model";
import debounce from "lodash.debounce";
import Edit from "@material-ui/icons/Edit";
import IconButton from "@material-ui/core/IconButton";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { Typography } from "@material-ui/core";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
import { decimalMul } from "../../../../common/utils/numbers/decimalCalculation";
import { StringArgFunction } from "../../../../model/common/CommonFunctions";
import { AppTheme } from "../../../../model/common/Theme";
import { getRoundingByType } from "../../discounts/utils";
import { getCurrentTax } from "../../taxes/utils";
import CourseClassGeneralTab from "./general/CourseClassGeneralTab";
import CourseClassVetTab from "./vet/CourseClassVetTab";
import CourseClassWebTab from "./web/CourseClassWebTab";
import CourseClassBudgetTab from "./budget/CourseClassBudgetTab";
import CourseClassTutorsTab from "./tutors/CourseClassTutorsTab";
import CourseClassTimetableTab from "./timetable/CourseClassTimetableTab";
import CourseClassAttendanceTab from "./attendance/CourseClassAttendanceTab";
import { D_MMM } from "../../../../common/utils/dates/format";
import { EditViewProps } from "../../../../model/common/ListView";
import CourseClassOutcomesTab from "./outcomes/CourseClassOutcomesTab";
import CourseClassAssessmentsTab from "./assessments/CourseClassAssessmentsTab";
import CourseClassEnrolmentsTab from "./enrolments/CourseClassEnrolmentsTab";
import EntityService from "../../../../common/services/EntityService";
import { setCourseClassBudgetModalOpened, setCourseClassLatestSession } from "../actions";
import { ClassCostExtended, CourseClassExtended, CourseClassRoom } from "../../../../model/entities/CourseClass";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../reducers/state";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import CourseClassDetTab from "./det/CourseClassDetTab";
import { getCustomColumnsMap } from "../../../../common/utils/common";
import { getLabelWithCount } from "../../../../common/utils/strings";
import CourseClassDocumentsTab from "./documents/CourseClassDocumentsTab";
import history from "../../../../constants/History";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../constants";
import { appendTimezone } from "../../../../common/utils/dates/formatTimezone";

const items: TabsListItem[] = [
  {
    label: "GENERAL",
    component: props => <CourseClassGeneralTab {...props} />
  },
  {
    label: "WEB",
    component: props => <CourseClassWebTab {...props} />
  },
  {
    label: "VET",
    component: props => <CourseClassVetTab {...props} />,
    expandable: true
  },
  {
    label: "TUTORS",
    component: props => <CourseClassTutorsTab {...props} />
  },
  {
    label: "BUDGET",
    component: props => <CourseClassBudgetTab {...props} />,
    expandable: true
  },
  {
    label: "ASSESSMENTS",
    component: props => <CourseClassAssessmentsTab {...props} />
  },
  {
    label: "TIMETABLE",
    component: props => <CourseClassTimetableTab {...props} />,
    expandable: true
  },
  {
    label: "ATTENDANCE",
    component: props => <CourseClassAttendanceTab {...props} />,
    expandable: true
  },
  {
    label: "DOCUMENTS",
    component: props => <CourseClassDocumentsTab {...props} />
  },
  {
    label: "NOTES",
    component: ({ classes, ...rest }) => <OwnApiNotes {...rest} className="pb-2" />
  },
  {
    label: "ENROLMENTS",
    component: props => <CourseClassEnrolmentsTab {...props} />
  },
  {
    label: "OUTCOMES",
    component: props => <CourseClassOutcomesTab {...props} />
  }
];

const traineeshipItems: TabsListItem[] = [
  {
    label: "DET export",
    component: props => <CourseClassDetTab {...props} />
  }
];

interface Props extends EditViewProps<CourseClassExtended> {
  currencySymbol?: string;
  taxes?: Tax[];
}

const getSessionsData = debounce((sessions, setClassRooms, setSessionsData, dispatch) => {
  if (!sessions || !sessions.length) {
    setSessionsData(null);
    return;
  }

  let earliest = sessions[0].siteTimezone
    ? appendTimezone(new Date(sessions[0].start), sessions[0].siteTimezone )
    : new Date(sessions[0].start);
  let latest = earliest;

  const rooms = new Set([]);

  sessions.forEach(s => {
    const startDate = s.siteTimezone ? appendTimezone(new Date(s.start), s.siteTimezone ) : new Date(s.start);
    const endDate = s.siteTimezone ? appendTimezone(new Date(s.end), s.siteTimezone ) : new Date(s.end);

    if (startDate < earliest) {
      earliest = startDate;
    }
    if (endDate > latest) {
      latest = endDate;
    }
    if (s.roomId) {
      rooms.add(s.roomId);
    }
  });

  if (rooms.size !== 0) {
    EntityService.getPlainRecords("Room", "name,seatedCapacity", `id in (${Array.from(rooms).toString()})`).then(
      res => {
        setClassRooms(res.rows.map(getCustomColumnsMap("name,seatedCapacity") as any));
      }
    );
  } else {
    setClassRooms([]);
  }

  dispatch(setCourseClassLatestSession(latest));

  setSessionsData({ earliest, latest, rooms });
}, 2000);

const FeeEditButton = ({ onClick, className }) => (
  <IconButton color="inherit" className={clsx("smallIconButton fsInherit", className)} onClick={onClick}>
    <Edit fontSize="inherit" />
  </IconButton>
);

const useBudgetAdornmentStyles = makeStyles((theme: AppTheme) => ({
  root: {
    display: "grid",
    gridTemplateColumns: "1fr auto",
    alignItems: "end",
    position: "relative",
    rowGap: `${theme.spacing(0.5)}px`,
    columnGap: theme.spacing(1),
    paddingTop: theme.spacing(0.5)
  },
  feeEdit: {
    position: "absolute",
    top: "-3px",
    right: "-30px"
  }
}));

const getDiscountedFee = (discount, currentTax, classFee) => {
  const taxOnDiscount = decimalMul(discount.courseClassDiscount.discountOverride || discount.perUnitAmountExTax || 0, currentTax.rate);

  const decimal = new Decimal(classFee).minus(discount.perUnitAmountExTax || 0).minus(taxOnDiscount);

  return getRoundingByType(discount.courseClassDiscount.discount.rounding, decimal);
};

interface BudgetAdornmentProps {
  budget: ClassCostExtended[],
  studentFee: ClassCostExtended;
  currencySymbol: string;
  isNew: boolean;
  dispatch: Dispatch;
  expandedBudget: string[];
  expandBudgetItem: StringArgFunction;
  currentTax: Tax;
}

const BudgetAdornment: React.FC<BudgetAdornmentProps> = ({
 budget,
 studentFee,
 currencySymbol,
 isNew,
 dispatch,
 expandedBudget,
 expandBudgetItem,
 currentTax
}) => {
  const classes = useBudgetAdornmentStyles();

  const discounts = useMemo(() => {
    const discountsSort = (a, b) => (a.description > b.description ? 1 : -1);

    const discountItems = budget.filter(b => b.flowType === "Discount"
      && (!b.courseClassDiscount.discount.code && !b.courseClassDiscount.discount.relationDiscount));
    discountItems.sort(discountsSort);
    const discountsPromo = budget.filter(b => b.flowType === "Discount"
      && (b.courseClassDiscount.discount.code && !b.courseClassDiscount.discount.relationDiscount));
    discountsPromo.sort(discountsSort);
    const discountsRelations = budget.filter(b => b.flowType === "Discount"
      && (!b.courseClassDiscount.discount.code && b.courseClassDiscount.discount.relationDiscount));
    discountsRelations.sort(discountsSort);

    console.log(budget, discountItems);

    const mapDiscount = d => (
      <Fragment key={d.id}>
        <div>
          {d.courseClassDiscount.discount.name}
        </div>
        <div className="money">
          {formatCurrency(
            getDiscountedFee(d, currentTax, studentFee.perUnitAmountIncTax),
            currencySymbol
          )}
        </div>
      </Fragment>
    );

    const discountHeader = header => <Typography variant="button" component="div" className="mt-3">{header}</Typography>;

    return (
      <>
        {Boolean(discountItems.length) && discountHeader("DISCOUNTS")}
        <div className={classes.root}>
          {discountItems.map(mapDiscount)}
        </div>
        {Boolean(discountsPromo.length) && discountHeader("PROMOTIONAL CODES")}
        <div className={classes.root}>
          {discountsPromo.map(mapDiscount)}
        </div>
        {Boolean(discountsRelations.length) && discountHeader("RELATION DISCOUNT")}
        <div className={classes.root}>
          {discountsRelations.map(mapDiscount)}
        </div>
      </>
    );
  }, [budget]);

  return (
    <div>
      <div className={classes.root}>
        <div>Class fee</div>
        <div className="money">
          {
            formatCurrency(
              studentFee ? studentFee.perUnitAmountIncTax : 0,
              currencySymbol
            )
          }
          {!isNew && (
            <FeeEditButton
              className={classes.feeEdit}
              onClick={e => {
                e.stopPropagation();

                dispatch(setCourseClassBudgetModalOpened(true));
                dispatch(
                  initialize(
                    COURSE_CLASS_COST_DIALOG_FORM,
                    studentFee
                  )
                );

                const search = new URLSearchParams(window.location.search);
                search.append("expandTab", "4");

                history.replace({
                  pathname: history.location.pathname,
                  search: decodeURIComponent(search.toString())
                });
                if (!expandedBudget.includes("Income")) {
                  expandBudgetItem("Income");
                }
              }}
            />
          )}
        </div>
      </div>
      {discounts}
    </div>

);
};

const CourseClassEditView: React.FC<Props> = ({
  isNew,
  isNested,
  nestedIndex,
  values,
  dispatch,
  dirty,
  form,
  twoColumn,
  submitSucceeded,
  manualLink,
  showConfirm,
  syncErrors,
  openNestedEditView,
  onCloseClick,
  rootEntity,
  invalid,
  toogleFullScreenEditView,
  currencySymbol,
  taxes
}) => {
  const [classRooms, setClassRooms] = useState<CourseClassRoom[]>([]);
  const [sessionsData, setSessionsData] = useState<any>(null);
  const [expandedBudget, setExpandedBudget] = useState([]);

  const currentTax = useMemo(() => getCurrentTax(taxes, values.taxId), [values.taxId, taxes]);

  const expandBudgetItem = (item: string) => {
    setExpandedBudget(prev => {
      const index = prev.indexOf(item);

      if (index !== -1) {
        const updated = [...prev];
        updated.splice(index, 1);
        return updated;
      }
      return [...prev, item];
    });
  };

  useEffect(() => {
    getSessionsData(values.sessions, setClassRooms, setSessionsData, dispatch);
  }, [values.sessions, values.id]);

  const customLabels = useMemo(() => {
    const labels = [];
    if (values.documents) {
      labels[8] = getLabelWithCount("document", values.documents.length);
    }
    return labels;
  }, [values.documents && values.documents.length]);

  items[4].labelAdornment = useMemo(
    () => {
      const studentFee = values.budget && values.budget.find(
        b => b.flowType === "Income" && b.repetitionType === "Per enrolment" && b.invoiceToStudent
      );

      return (twoColumn ? (
        <BudgetAdornment
          studentFee={studentFee}
          currencySymbol={currencySymbol}
          isNew={isNew}
          dispatch={dispatch}
          expandedBudget={expandedBudget}
          expandBudgetItem={expandBudgetItem}
          budget={values.budget}
          currentTax={currentTax}
        />
      ) : null);
    },
    [values.budget, twoColumn, isNew, currencySymbol, expandedBudget, currentTax]
  );

  items[6].labelAdornment = useMemo(() => {
    if (!twoColumn) {
      return null;
    }

    if (!sessionsData || !values.sessions || !values.sessions.length) {
      return "No sessions have been set in the timetable";
    }

    return `${values.sessions.length} session${values.sessions.length > 1 ? "s" : ""} ${format(
      sessionsData.earliest,
      D_MMM
    )} - ${format(sessionsData.latest, D_MMM)}\n${
      sessionsData.rooms.size > 1
        ? `First session in ${values.sessions[0].room}`
        : values.sessions[0].room
        ? "All sessions in " + values.sessions[0].room
        : "No rooms have been set in the timetable"
    }`;
  }, [values.sessions && values.sessions.length, values.id, sessionsData]);

  items[5].label = useMemo(() => getLabelWithCount("assessment", values.assessments ? values.assessments.length : 0), [
    values.assessments && values.assessments.length
  ]);

  items[9].label = useMemo(() => getLabelWithCount("note", values.notes ? values.notes.length : 0), [values.notes && values.notes.length]);
  items[10].label = useMemo(() => getLabelWithCount("enrolment", values.allEnrolmentsCount), [
    values.allEnrolmentsCount
  ]);
  items[11].label = useMemo(() => getLabelWithCount("outcome", values.allOutcomesCount), [values.allOutcomesCount]);

  const savedTutors = useMemo(() => (values.tutors ? values.tutors.filter(t => t.id) : []), [values.tutors]);

  const hasBudgetPermissions = useSelector<State, any>(
    state => state && state.access["/a/v1/list/entity/courseClass/budget/"] && state.access["/a/v1/list/entity/courseClass/budget/"]["GET"]
  );

  const usedItems = useMemo(() => (hasBudgetPermissions ? items : items.filter(i => i.label !== "BUDGET")), [
    hasBudgetPermissions
  ]);

  return (
    <TabsList
      items={values.isTraineeship ? [...usedItems, ...traineeshipItems] : usedItems}
      customLabels={customLabels}
      itemProps={{
        isNew,
        isNested,
        nestedIndex,
        values,
        dispatch,
        dirty,
        invalid,
        form,
        twoColumn,
        submitSucceeded,
        manualLink,
        showConfirm,
        syncErrors,
        openNestedEditView,
        onCloseClick,
        rootEntity,
        toogleFullScreenEditView,
        currencySymbol,
        taxes,
        classRooms,
        savedTutors,
        expandedBudget,
        expandBudgetItem,
        currentTax
      }}
      customAppBar
    />
  );
};

const mapStateToProps = (state: State) => ({
  taxes: state.taxes.items,
  currencySymbol: state.currency.shortCurrencySymbol
});

export default connect<any, any, any>(mapStateToProps)((props: any) => (props.values ? <CourseClassEditView {...props} /> : null));
