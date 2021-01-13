import { ActionsObservable, Epic, ofType } from "redux-observable";
import { debounce, mergeMap } from "rxjs/operators";
import { interval } from "rxjs";
import { format, isSameDay } from "date-fns";
import uniqid from "uniqid";
import { change, getFormValues } from "redux-form";
import { Session, TrainingPlan } from "@api/model";
import { State } from "../../../../reducers/state";
import {
  CHECKOUT_ADD_CONTACT,
  CHECKOUT_ADD_ITEM,
  CHECKOUT_REMOVE_CONTACT,
  CHECKOUT_REMOVE_ITEM, CHECKOUT_TOGGLE_SUMMARY_ITEM, CHECKOUT_UPDATE_CLASS_ITEM,
  CHECKOUT_UPDATE_CONTACT
} from "../../actions";
import CourseClassAttendanceService
  from "../../../entities/courseClasses/components/attendance/services/CourseClassAttendanceService";
import CourseClassTimetableService
  from "../../../entities/courseClasses/components/timetable/services/CourseClassTimetableService";
import { CheckoutFundingInvoice } from "../../../../model/checkout/fundingInvoice";
import { D_MMM_YYYY, YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "../../components/fundingInvoice/CheckoutFundingInvoiceSummaryList";
import EntityService from "../../../../common/services/EntityService";
import { CHECKOUT_CONTACT_COLUMNS } from "../../constants";
import { getCustomColumnsMap } from "../../../../common/utils/common";
import { appendTimezone } from "../../../../common/utils/dates/formatTimezone";

const getAndMergePlans = async (fundingInvoice: CheckoutFundingInvoice) => {
  let plans: TrainingPlan[] = [];
  let sessions: Session[] = [];

  await fundingInvoice.item.enrolment.items.map(item => () =>
    CourseClassAttendanceService.getTrainingPlans(item.class.id).then(plansRes =>
      CourseClassTimetableService.findTimetableSessionsForCourseClasses(item.class.id).then(sessionsRes => {
        plans = plans.concat(plansRes);
        sessions = sessions.concat(sessionsRes);
      }))).reduce(async (a, b) => {
    await a;
    await b();
  }, Promise.resolve());

  if (!plans.length || !sessions.length) {
    return;
  }

  sessions.forEach(s => {
    if (s.siteTimezone) {
      s.start = appendTimezone(new Date(s.start), s.siteTimezone).toISOString();
    }
  });

  sessions.sort((a, b) => (new Date(a.start) > new Date(b.start) ? 1 : -1));

  plans.forEach(pl => {
    pl.sessionIds = pl.sessionIds.map(sId => sessions.find(s => s.id === sId).start) as any;
    pl.sessionIds.sort((a, b) => (new Date(a) > new Date(b) ? 1 : -1));
  });

  const checkedDates: any = {};

  sessions = sessions.filter(s => {
    let formatted: any = new Date(s.start);
    formatted.setHours(0, 0, 0, 0);
    formatted = formatted.toISOString();

    if (checkedDates[formatted]) {
      return false;
    }
    checkedDates[formatted] = true;
    return true;
  });

  const checkedIds: any = {};
  const commencedIds = [];
  const completedIds = [];
  const trainingPlans = [];
  const trainingPlansBase = [];

  sessions.forEach(({ start }, index) => {
    const sDate = new Date(start);
    const sDateFormatted = format(sDate, D_MMM_YYYY);

    plans.forEach(plan => {
      if (!checkedIds[plan.moduleId]) {
        checkedIds[plan.moduleId] = 0;
      }

      plan.sessionIds.forEach(pStart => {
        const checkCompleted = () => {
          if (checkedIds[plan.moduleId] > 0
            && checkedIds[plan.moduleId] === plan.sessionIds.length
            && !completedIds.includes(plan.moduleId)) {
            completedIds.push(plan.moduleId);
          }
        };

        if (index !== sessions.length - 1) {
          checkCompleted();
        }

        if (isSameDay(new Date(pStart), new Date(start))) {
          if (!commencedIds.includes(plan.moduleId)) {
            commencedIds.push(plan.moduleId);
          }
          checkedIds[plan.moduleId]++;
        }

        if (index === sessions.length - 1) {
          checkCompleted();
        }
      });
    });

    trainingPlansBase.push({
      date: sDateFormatted,
      unitsCommenced: commencedIds.length,
      unitsCompleted: completedIds.length
    });
  });

  trainingPlansBase.forEach((tp, index, arr) => {
    const prev = arr[index - 1];

    if (!prev || !(prev.unitsCommenced === tp.unitsCommenced && prev.unitsCompleted === tp.unitsCompleted)) {
      trainingPlans.push(tp);
    }
  });

  let paymentPlans = [...fundingInvoice.paymentPlans];

  if (trainingPlans.length) {
    paymentPlans = [
      paymentPlans[0],
    ].concat(trainingPlans.map(({ date }) => ({
      amount: 0,
      date: format(new Date(date), YYYY_MM_DD_MINUSED),
      entityName: "InvoiceDueDate",
      id: uniqid() as any,
      successful: false,
      type: "Payment due"
    })));
    paymentPlans[paymentPlans.length - 1].amount = fundingInvoice.total;
  }

  fundingInvoice.paymentPlans = paymentPlans;
  fundingInvoice.trainingPlans = trainingPlans;
};

export const EpicTriggerFundingInvoiceCalculate: Epic<any, any, State> = (action$: ActionsObservable<any>, state$): any =>
  action$.pipe(
    ofType(
      CHECKOUT_ADD_ITEM,
      CHECKOUT_ADD_CONTACT,
      CHECKOUT_REMOVE_CONTACT,
      CHECKOUT_UPDATE_CONTACT,
      CHECKOUT_REMOVE_ITEM,
      CHECKOUT_TOGGLE_SUMMARY_ITEM,
      CHECKOUT_UPDATE_CLASS_ITEM
    ),
    debounce(() => interval(500)),
    mergeMap(async () => {
      const formValues = getFormValues(
        CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM
      )(state$.value) as { fundingInvoices: CheckoutFundingInvoice[] };

      const summaryList = state$.value.checkout.summary.list;

      const added = {};

      const fundingInvoicesUpdated = [];

      formValues.fundingInvoices.forEach(fi => {
        const updated = JSON.parse(JSON.stringify(fi));

        updated.item.enrolment.items = fi.item.enrolment.items.filter(i =>
          summaryList.some(li => li.items.some(l => l.checked && l.type === "course" && l.class.id === i.class.id)));

        if (updated.item.enrolment.items.length) {
          fundingInvoicesUpdated.push(updated);
          added[fi.relatedFundingSourceId] = true;
        }
      });

      if (summaryList.length === 1 && !summaryList[0].contact.isCompany) {
        const items = [...summaryList[0].items];

        await items.map(i => async () => {
          if (i.checked
            && i.type === "course"
            && i.class.relatedFundingSourceId !== null
          ) {
            if (added[i.class.relatedFundingSourceId]) {
              const sourceInvoice = fundingInvoicesUpdated
                .find(r => r.relatedFundingSourceId === i.class.relatedFundingSourceId);

              if (sourceInvoice.item.enrolment.items.some(invIt => invIt.class.id === i.class.id)) {
                return;
              }

              sourceInvoice.item.enrolment.items.push(i);
            } else {
              const newInvoice: CheckoutFundingInvoice = {
                active: false,
                company: null,
                item: {
                  enrolment: {
                    ...summaryList[0],
                    items: [i]
                  }
                },
                trackAmountOwing: true,
                fundingProviderId: i.class.fundingProviderId,
                relatedFundingSourceId: i.class.relatedFundingSourceId,
                vetPurchasingContractID: i.class.vetPurchasingContractID,
                trainingPlans: [],
                paymentPlans: [
                  {
                    amount: 0,
                    date: format(new Date(), YYYY_MM_DD_MINUSED),
                    entityName: "Invoice",
                    id: uniqid() as any,
                    successful: true,
                    type: "Invoice office"
                  },
                  {
                    amount: 0,
                    date: format(new Date(), YYYY_MM_DD_MINUSED),
                    entityName: "InvoiceDueDate",
                    id: uniqid() as any,
                    successful: false,
                    type: "Payment due"
                  }
                ],
                total: 0
              };
              fundingInvoicesUpdated.push(newInvoice);

              if (typeof i.class.fundingProviderId === "number") {
                await EntityService.getPlainRecords(
                  "Contact",
                  CHECKOUT_CONTACT_COLUMNS,
                  `id is ${i.class.fundingProviderId}`
                ).then(res => {
                  const contacts = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS));
                  if (contacts[0]) {
                    newInvoice.company = contacts[0];
                  }
                });
              }
              added[i.class.relatedFundingSourceId] = true;
            }
          }
        }).reduce(async (a, b) => {
          await a;
          await b();
        }, Promise.resolve());
      }

      await fundingInvoicesUpdated.map(inv => async () => {
        await getAndMergePlans(inv);
      }).reduce(async (a, b) => {
        await a;
        await b();
      }, Promise.resolve());

      return change(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM, "fundingInvoices", fundingInvoicesUpdated );
    }),
  );
