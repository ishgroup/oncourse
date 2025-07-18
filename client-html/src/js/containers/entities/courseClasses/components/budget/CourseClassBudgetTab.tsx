/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ClassCost, CourseClassTutor, Discount, Tax } from '@api/model';
import { Grid, Popover, Typography } from '@mui/material';
import { darken } from '@mui/material/styles';
import $t from '@t';
import { isAfter, isBefore, isEqual } from 'date-fns';
import Decimal from 'decimal.js-light';
import {
  AppTheme,
  BooleanArgFunction,
  decimalMinus,
  decimalMul,
  decimalPlus,
  formatCurrency,
  stopEventPropagation,
  StringArgFunction,
  stubFunction
} from 'ish-ui';
import React, { useCallback, useEffect, useMemo } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { arrayInsert, arraySplice, change, initialize } from 'redux-form';
import { makeStyles, withStyles } from 'tss-react/mui';
import { addActionToQueue, removeActionsFromQueue } from '../../../../../common/actions';
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from '../../../../../common/actions/CommonPlainRecordsActions';
import { IAction } from '../../../../../common/actions/IshAction';
import instantFetchErrorHandler from '../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import NestedList from '../../../../../common/components/form/nestedList/NestedList';
import ExpandableContainer from '../../../../../common/components/layout/expandable/ExpandableContainer';
import { PLAIN_LIST_MAX_PAGE_SIZE } from '../../../../../constants/Config';
import history from '../../../../../constants/History';
import { EditViewProps } from '../../../../../model/common/ListView';
import { ClassCostExtended, CourseClassExtended, CourseClassRoom } from '../../../../../model/entities/CourseClass';
import { DefinedTutorRoleExtended } from '../../../../../model/preferences/TutorRole';
import { State } from '../../../../../reducers/state';
import PreferencesService from '../../../../preferences/services/PreferencesService';
import DiscountService from '../../../discounts/services/DiscountService';
import {
  discountSort,
  getDiscountAmountExTax,
  getRoundingByType,
  mapPlainDiscounts,
  transformDiscountForNestedList
} from '../../../discounts/utils';
import { getCurrentTax } from '../../../taxes/utils';
import { setCourseClassBudgetModalOpened } from '../../actions';
import { COURSE_CLASS_COST_DIALOG_FORM } from '../../constants';
import { classCostInitial } from '../../CourseClasses';
import { CourseClassState } from '../../reducers';
import { getTutorPayInitial } from '../tutors/utils';
import { deleteCourseClassCost, postCourseClassCost, putCourseClassCost } from './actions';
import AddBudgetMenu from './AddBudgetMenu';
import BudgetEnrolmentsFields from './BudgetEnrolmentsFields';
import BudgetExpandableItemRenderer from './BudgetExpandableItemRenderer';
import BudgetInvoiceItemRenderer from './BudgetInvoiceItemRenderer';
import BudgetItemRow from './BudgetItemRow';
import BudgetCostModal from './modal/BudgetCostModal';
import ClassCostService from './services/ClassCostService';
import { dateForCompare, excludeOnEnrolPaymentPlan, getClassFeeTotal, includeOnEnrolPaymentPlan } from './utils';

const styles = (theme: AppTheme, p, classes) =>
  ({
    root: {
      paddingTop: "10px",
      paddingBottom: "10px"
    },
    panel: {
      boxShadow: "none"
    },
    tableTab: {
      padding: theme.spacing(0.5, 1),
      borderRadius: theme.shape.borderRadius,
      "&:hover": {
        background: darken(theme.palette.background.paper, 0.1),
        cursor: "pointer",
        [`& .${classes.tableTabButtons}`]: {
          visibility: "visible"
        }
      },
      "&:nth-of-type(odd)": {
        background: theme.table.contrastRow.main
      },
      "&:nth-of-type(odd):hover": {
        background: darken(theme.table.contrastRow.main, 0.1)
      },
      "&:last-child": {
        background: "inherit",
        "&:hover": {
          background: "inherit"
        }
      }
    },
    tableTabButtons: {
      visibility: "hidden"
    },
    headerItem: {
      paddingLeft: "22px",
      textAlign: "right"
    },
    stub: {
      height: theme.spacing(6),
      width: theme.spacing(4.75)
    },
    rowItemCol1: {
      paddingRight: 18
    },
    rowItemCol2: {
      paddingRight: 14
    },
    rowItemCol3: {
      paddingRight: 12
    },
    rowItemCol4: {
      display: "flex",
      justifyContent: "flex-end",
      paddingRight: 10
    },
    panelSumRoot: {
      [`&.${classes.panelSumFocus}`]: {
        background: "inherit"
      }
    },
    panelSumFocus: {}
  });

const usePopoverStyles = makeStyles()((theme: AppTheme) => ({
  popover: {
    pointerEvents: 'none',
  },
  paper: {
    padding: theme.spacing(1),
  },
}));

const DiscountRows = props => {
  const {
   rowsValues, openEditModal, onDeleteClassCost, currencySymbol, classes
  } = props;

  const discountsSort = (a, b) => (a.value.description > b.value.description ? 1 : -1);

  const discountItems = rowsValues.items.filter(({ value }) => value.flowType === "Discount"
    && (!value.courseClassDiscount.discount.code && !value.courseClassDiscount.discount.relationDiscount));
  discountItems.sort(discountsSort);
  const discountsPromo = rowsValues.items.filter(({ value }) => value.flowType === "Discount"
    && (value.courseClassDiscount.discount.code && !value.courseClassDiscount.discount.relationDiscount));
  discountsPromo.sort(discountsSort);
  const discountsRelations = rowsValues.items.filter(({ value }) => value.flowType === "Discount"
    && (!value.courseClassDiscount.discount.code && value.courseClassDiscount.discount.relationDiscount));
  discountsRelations.sort(discountsSort);

  const mapDiscount = (item, i) => (
    <BudgetItemRow
      key={i}
      openEditModal={openEditModal}
      onDeleteClassCost={onDeleteClassCost}
      value={item.value}
      currencySymbol={currencySymbol}
      classes={classes}
      projectedBasedValue={item.projected}
      actualBasedValue={item.actual}
      maxBasedValue={item.max}
    />
  );

  const discountHeader = header => <div className="mt-3 mb-2 secondaryHeading">{header}</div>;

  return (
    <>
      {discountItems.map(mapDiscount)}
      {Boolean(discountsPromo.length) && discountHeader("PROMOTIONAL CODES")}
      {discountsPromo.map(mapDiscount)}
      {Boolean(discountsRelations.length) && discountHeader("RELATION DISCOUNT")}
      {discountsRelations.map(mapDiscount)}
    </>
  );
};

const MouseOverPopover = ({
  enrolments,
  income,
  discounts,
  costs,
  profit,
  anchorEl,
  handlePopoverClose,
}) => {
  const { classes } = usePopoverStyles();
  const open = Boolean(anchorEl);

  return (
    <div>
      <Popover
        id="budget-popover"
        className={classes.popover}
        classes={{
          paper: classes.paper,
        }}
        open={open}
        anchorEl={anchorEl}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'left',
        }}
        onClose={handlePopoverClose}
        disableRestoreFocus
      >
        <Typography component="div" variant="body2" color="textSecondary" noWrap>
          <span className="mr-1">{$t('enrolments2')}</span>
          <span>{enrolments}</span>
        </Typography>
        <Typography component="div" variant="body2" color="textSecondary" noWrap>
          <span className="mr-1">{$t('income')}</span>
          <span>{income}</span>
        </Typography>
        <Typography component="div" variant="body2" color="textSecondary" noWrap>
          <span className="mr-1">{$t('discounts2')}</span>
          <span>{discounts}</span>
        </Typography>
        <Typography component="div" variant="body2" color="textSecondary" noWrap>
          <span className="mr-1">{$t('costs')}</span>
          <span>{costs}</span>
        </Typography>
        <Typography component="div" variant="body2" color="textSecondary" noWrap>
          <span className="mr-1">{$t('profit')}</span>
          <span>{profit}</span>
        </Typography>
      </Popover>
    </div>
  );
};

interface Props extends Partial<EditViewProps> {
  classRooms?: CourseClassRoom[];
  values?: CourseClassExtended;
  tutorRoles?: DefinedTutorRoleExtended[];
  classes?: any;
  enrolments?: CourseClassState["enrolments"];
  taxes?: Tax[];
  currencySymbol?: string;
  setCourseClassBudgetModalOpened?: (opened: boolean, onCostRate?: number) => void;
  expandedBudget?: string[];
  expandBudgetItem?: StringArgFunction;
  currentTax?: Tax;
  discounts?: Discount[];
  pending?: boolean;
  discountsError?: boolean;
  getSearchResult?: StringArgFunction;
  clearSearchResult?: BooleanArgFunction;
  classCostTypes?: any;
  netValues?: any;
}

const CourseClassBudgetTab = React.memo<Props>(
  ({
    tabIndex,
    taxes,
     expanded,
     setExpanded,
     toogleFullScreenEditView,
     twoColumn,
     classes,
     enrolments,
     currencySymbol,
     values,
     dispatch,
     form,
     showConfirm,
     tutorRoles,
     setCourseClassBudgetModalOpened,
     isNew,
     classRooms,
     expandedBudget,
     expandBudgetItem,
     currentTax,
     discounts,
     pending,
     discountsError,
     getSearchResult,
     clearSearchResult,
     classCostTypes,
     netValues
   }) => {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const [popoverAnchor, setPopoverAnchor] = React.useState(null);
    const [tutorsMenuOpened, setTutorsMenuOpened] = React.useState(false);

    const classFee = useMemo(() => getClassFeeTotal(values.budget || [], taxes), [taxes, values.budget]);

    const openAddBudgetMenu = useCallback(e => {
      setAnchorEl(e.currentTarget);
    }, []);

    const closeAddBudgetMenu = useCallback(() => {
      setAnchorEl(null);
      setTutorsMenuOpened(false);
    }, []);
    
    useEffect(() => {
      if (!twoColumn && expanded.includes(tabIndex)) {
        setExpanded(prev => prev.filter(p => p !== tabIndex));
      }
    }, [twoColumn, expanded, tabIndex]);

    const onExpand = useCallback(
      (e, expanded) => {
        if (!twoColumn && expanded) {
          e.preventDefault();

          const search = new URLSearchParams(window.location.search);
          search.append("expandTab", tabIndex.toString());

          history.replace({
            pathname: history.location.pathname,
            search: decodeURIComponent(search.toString())
          });
          toogleFullScreenEditView(true);
        }
      },
      [twoColumn, tabIndex]
    );

    const openAddModal = useCallback(
      e => {
        const initial = { ...classCostInitial, taxId: values.taxId };

        switch (e.target.getAttribute("role")) {
          case "expense": {
            initial.flowType = "Expense";
            break;
          }

          case "income": {
            initial.flowType = "Income";
            break;
          }

          case "tutorPay": {
            setTutorsMenuOpened(true);
            return;
          }
        }
        dispatch(initialize(COURSE_CLASS_COST_DIALOG_FORM, initial));
        setCourseClassBudgetModalOpened(true);
        closeAddBudgetMenu();
        if (!expanded.includes(tabIndex)) {
          setExpanded(prev => [...prev, tabIndex]);
        }
      },
      [values.taxId, expanded, tabIndex, twoColumn]
    );

    const openAddTutorModal = useCallback(
      async (tutor: CourseClassTutor) => {
        const fullRole = await PreferencesService.getTutorRole(tutor.roleId);

        let payRate;
        if (values.startDateTime && fullRole.payRates.length) {
          fullRole.payRates.some(e => {
            if (isEqual(dateForCompare(e.validFrom, "yyyy-MM"),
              dateForCompare(values.startDateTime, "yyyy-MM"))) {
              payRate = e;
              return true;
            }

            if (isBefore(dateForCompare(e.validFrom, "yyyy-MM"),
              dateForCompare(values.startDateTime, "yyyy-MM"))) {
              if (!payRate || isAfter(dateForCompare(e.validFrom, "yyyy-MM"),
                dateForCompare(payRate.validFrom, "yyyy-MM"))) {
                payRate = e;
              }
            }
            return false;
          });
        }

        const role = tutorRoles.find(r => r.id === tutor.roleId);
        const onCostRate = payRate ? payRate.oncostRate : 0;
        const perUnitAmountExTax = payRate ? payRate.rate : 0;
        const initial: ClassCost = getTutorPayInitial(tutor, values.id, values.taxId, role, perUnitAmountExTax);

        setCourseClassBudgetModalOpened(true, isNaN(onCostRate) ? 0 : onCostRate);
        dispatch(initialize(COURSE_CLASS_COST_DIALOG_FORM, initial));
        closeAddBudgetMenu();
      },
      [values.taxId, values.id, tutorRoles]
    );

    const openEditModal = (data: ClassCostExtended) => {
      let onCostRate = null;
      if (data.flowType === "Wages") {
        const tutor = values.tutors.find(t =>
          (data.courseClassTutorId ? t.id === data.courseClassTutorId : t.temporaryId === data.temporaryTutorId));
        const role = tutor ? tutorRoles.find(r => r.id === tutor.roleId) : null;
        onCostRate = (role && role["currentPayrate.oncostRate"]) ? parseFloat(role["currentPayrate.oncostRate"]) : 0;
      }
      setCourseClassBudgetModalOpened(true, isNaN(onCostRate) ? 0 : onCostRate);
      dispatch(initialize(COURSE_CLASS_COST_DIALOG_FORM, excludeOnEnrolPaymentPlan(data, currentTax)));
      closeAddBudgetMenu();
    };

    const closeModal = useCallback(() => {
      setCourseClassBudgetModalOpened(false);
    }, []);

    const saveModalData = useCallback(
      (data: ClassCostExtended) => {
        data.courseClassid = values.id;

        let bindedActionId = null;

        if (data.flowType === "Wages") {
          if (!data.description) {
            data.description = `Wage for ${data.contactName}`;
          }
          const tutor = values.tutors.find(t =>
            (data.courseClassTutorId ? t.id === data.courseClassTutorId : t.temporaryId === data.temporaryTutorId));

          bindedActionId = tutor && tutor.temporaryId;
        }

        const postData = includeOnEnrolPaymentPlan(data, taxes);
        delete postData.index;

        if (postData.id === null) {
          const { temporaryId, temporaryTutorId, ...validateData } = postData;

          ClassCostService.validatePost(validateData)
            .then(() => {
              if (!postData.id) {
                dispatch(arrayInsert(form, "budget", 0, postData));
              } else {
                dispatch(change(form, `budget[${data.index}]`, postData));
              }
              dispatch(
                addActionToQueue(postCourseClassCost(postData), "POST", "ClassCost", temporaryId, bindedActionId)
              );
              closeModal();
            })
            .catch(response => instantFetchErrorHandler(dispatch, response));

          return;
        }

        ClassCostService.validatePut(postData)
          .then(() => {
            if (postData.flowType === "Income" && postData.invoiceToStudent) {
              dispatch(change(form, "feeExcludeGST", postData.perUnitAmountExTax));
              dispatch(change(form, "taxId", postData.taxId));

              const currentTax = getCurrentTax(taxes, postData.taxId);
              const feeWithTax = decimalMul(postData.perUnitAmountExTax, decimalPlus(1, currentTax.rate));

              classCostTypes.discount.items.forEach(d => {
                const isPersent = d.value.courseClassDiscount.discount.discountType === "Percent";
                const isFeeOverride = d.value.courseClassDiscount.discount.discountType === "Fee override";

                const taxMul = decimalPlus(1, currentTax.rate);

                if ((isPersent || isFeeOverride) && d.value.courseClassDiscount.discountOverride === null) {
                  const discountValue = new Decimal(feeWithTax)
                    .minus(
                      isPersent
                        ? getRoundingByType(
                            d.value.courseClassDiscount.discount.rounding,
                            new Decimal(feeWithTax).mul(
                              decimalMinus(1, d.value.courseClassDiscount.discount.discountPercent)
                            )
                          )
                        : getRoundingByType(
                            d.value.courseClassDiscount.discount.rounding,
                            new Decimal(d.value.courseClassDiscount.discount.discountValue).mul(taxMul)
                          )
                    )
                    .div(taxMul)
                    .toDecimalPlaces(2)
                    .toNumber();

                  dispatch(change(form, `budget[${d.value.index}].perUnitAmountExTax`, discountValue));
                }
              });
            }

            dispatch(addActionToQueue(putCourseClassCost(postData), "PUT", "ClassCost", data.id));
            dispatch(arraySplice(form, "budget", data.index, 1, postData));
            closeModal();
          })
          .catch(response => instantFetchErrorHandler(dispatch, response));
      },
      [classCostTypes, taxes, values.id, values.tutors]
    );

    const onDeleteClassCost = useCallback(
      id => {
        const index = values.budget.findIndex(c => c.id === id || c["temporaryId"] === id);

        const isStudentFee = values.budget[index].invoiceToStudent;

        if (isStudentFee) {
          showConfirm(
            {
              title: null,
              confirmMessage: `The class must have at least one income fee line. Fee amount can be set to ${currencySymbol}0.00`,
              cancelButtonText: "Ok"
            }
          );
          return;
        }

        showConfirm({
          onConfirm: () => {
            if (!values.budget[index]["temporaryId"]) {
              ClassCostService.validateDelete(id)
                .then(() => {
                  const updated = [...values.budget];
                  updated.splice(index, 1);
                  dispatch(change(form, "budget", updated));
                  dispatch(addActionToQueue(deleteCourseClassCost(id), "DELETE", "ClassCost", id));
                })
                .catch(response => instantFetchErrorHandler(dispatch, response));
            } else {
              const updated = [...values.budget];
              updated.splice(index, 1);
              dispatch(change(form, "budget", updated));
              dispatch(removeActionsFromQueue([{ entity: "ClassCost", id: values.budget[index]["temporaryId"] }]));
            }
          },
          confirmMessage: "Budget item will be deleted permanently",
          confirmButtonText: "DELETE"
        });
      },
      [values.budget, form]
    );

    const commonCostListProps = {
      currencySymbol,
      openEditModal,
      onDeleteClassCost,
      classes
    };

    const headerLabel = useMemo(() =>
      `Budget ( ${formatCurrency(Math.abs(netValues.profit.actual), currencySymbol)} ${netValues.profit.actual >= 0 ? "profit" : "loss"} )`,
       [netValues.profit.actual, currencySymbol]);

    const handlePopoverOpen = event => {
      setPopoverAnchor(event.currentTarget);
    };

    const handlePopoverClose = () => {
      setPopoverAnchor(null);
    };

    const searchValues = useMemo(() => (discounts
      ? discounts.filter(d => !values.budget.some(b => b.courseClassDiscount
        && (b.courseClassDiscount.discount.id === d.id))).map(transformDiscountForNestedList)
      : []),
    [discounts, values.budget]);

    const onAddDiscount = d => {
      DiscountService.getDiscount(d[0].id)
        .then(discount => {
          saveModalData(
            {
              ...classCostInitial,
              courseClassid: values.id,
              description: discount.name,
              perUnitAmountExTax: getDiscountAmountExTax(discount, currentTax, classFee),
              taxId: values.taxId,
              flowType: "Discount",
              repetitionType: "Discount",
              index: values.budget.length,
              courseClassDiscount: {
                discount,
                forecast: null,
                discountOverride: null
              }
            }
          );
        });
    };

    return (
      <>
        {values.budget && (
          <BudgetCostModal
            onClose={closeModal}
            onSave={saveModalData}
            onSubmit={saveModalData}
            classFee={classFee}
            currentTax={currentTax}
            taxes={taxes}
            classValues={values}
            currencySymbol={currencySymbol}
          />
        )}

        <MouseOverPopover
          enrolments={enrolments.length}
          income={formatCurrency(classCostTypes.income.actual, currencySymbol)}
          discounts={formatCurrency(classCostTypes.discount.actual, currencySymbol)}
          costs={formatCurrency(classCostTypes.cost.actual, currencySymbol)}
          profit={formatCurrency(netValues.profit.actual, currencySymbol)}
          anchorEl={popoverAnchor}
          handlePopoverClose={handlePopoverClose}
        />

        <AddBudgetMenu
          openAddTutorModal={openAddTutorModal}
          tutorsMenuOpened={tutorsMenuOpened}
          anchorEl={anchorEl}
          closeAddBudgetMenu={closeAddBudgetMenu}
          openAddModal={openAddModal}
          tutors={values.tutors}
        />

        <div className="pl-3 pr-3">
          {isNew ? (
            <div className="pb-1 pt-2">
              <div
                className="heading pb-3"
                aria-haspopup="true"
                onMouseEnter={handlePopoverOpen}
                onMouseLeave={handlePopoverClose}
              >
                {headerLabel}
              </div>

              <div className="mb-2">
                <BudgetEnrolmentsFields
                  values={values}
                  enrolmentsCount={enrolments.length}
                  dispatch={dispatch}
                  form={form}
                  classRooms={classRooms}
                />
              </div>

              <BudgetExpandableItemRenderer
                header="Total Cost"
                expanded={expandedBudget.includes("Total Cost")}
                setExpanded={expandBudgetItem}
                rowsValues={classCostTypes.cost}
                {...commonCostListProps}
              />

              <Typography variant="caption" color="textSecondary" className="pt-1">
                {$t('please_save_your_new_class_before_adding_budget_fe')}
              </Typography>
            </div>
          ) : (
            <ExpandableContainer
              onChange={onExpand}
              onAdd={twoColumn ? openAddBudgetMenu : undefined}
              index={tabIndex}
              expanded={expanded}
              setExpanded={setExpanded}
              header={(
                <span
                  aria-haspopup="true"
                  onMouseEnter={handlePopoverOpen}
                  onMouseLeave={handlePopoverClose}
                >
                  {headerLabel}
                </span>
              )}
            >
              <div className="pt-2 pb-3">
                <BudgetEnrolmentsFields
                  values={values}
                  enrolmentsCount={enrolments.length}
                  dispatch={dispatch}
                  form={form}
                  classRooms={classRooms}
                />
              </div>
              <div className="pt-2 pb-3">
                <BudgetExpandableItemRenderer
                  header="Income"
                  expanded={expandedBudget.includes("Income")}
                  setExpanded={expandBudgetItem}
                  rowsValues={classCostTypes.income}
                  {...commonCostListProps}
                />

                <BudgetInvoiceItemRenderer
                  header="Custom invoices"
                  currencySymbol={currencySymbol}
                  rowsValues={classCostTypes.customInvoices}
                  classes={classes}
                />

                <BudgetExpandableItemRenderer
                  header="Discounts"
                  expanded={expandedBudget.includes("Discounts")}
                  setExpanded={expandBudgetItem}
                  rowsValues={classCostTypes.discount}
                  customRowsRenderer={DiscountRows}
                  headerComponent={(
                    <div onClick={stopEventPropagation}>
                      <NestedList
                        formId={values.id}
                        title={$t('discounts')}
                        searchPlaceholder="Find discounts"
                        values={[]}
                        searchValues={searchValues}
                        pending={pending}
                        onAdd={onAddDiscount}
                        onDelete={stubFunction}
                        onSearch={getSearchResult}
                        clearSearchResult={clearSearchResult}
                        sort={discountSort}
                        aqlEntities={["Discount"]}
                        aqlQueryError={discountsError}
                        secondaryHeading
                        disableAddAll
                      />
                    </div>
                  )}
                  showEmpty
                  {...commonCostListProps}
                />

                <BudgetNetRow
                  header="Net Income"
                  classes={classes}
                  netValues={netValues.income}
                  currencySymbol={currencySymbol}
                />

                <BudgetExpandableItemRenderer
                  header="Total Cost"
                  expanded={expandedBudget.includes("Total Cost")}
                  setExpanded={expandBudgetItem}
                  rowsValues={classCostTypes.cost}
                  {...commonCostListProps}
                />

                <BudgetNetRow
                  header="Profit"
                  netValues={netValues.profit}
                  classes={classes}
                  currencySymbol={currencySymbol}
                />

                {values.enrolmentsToProfitLeftCount > 0 && (
                  <Typography variant="body2" color="textSecondary" className="pl-3 pt-2">
                    {values.enrolmentsToProfitLeftCount}
                    {$t('more_enrolments_required_before_running_costs_are')}
                  </Typography>
                )}
              </div>
            </ExpandableContainer>
          )}
        </div>
      </>
    );
  }
);

interface CommonRowProps {
  header: any;
  headerAdornment?: string;
  classes: any;
  netValues: {
    max: number;
    projected: number;
    actual: number;
  };
  currencySymbol: string;
}

const BudgetNetRow: React.FC<CommonRowProps> = ({
 header, classes, netValues, currencySymbol, headerAdornment
}) => {
  const maxLabel = useMemo(() => formatCurrency(netValues.max, currencySymbol), [currencySymbol, netValues.max]);
  const projectedLabel = useMemo(() => formatCurrency(netValues.projected, currencySymbol), [
    currencySymbol,
    netValues.projected
  ]);
  const actualLabel = useMemo(() => formatCurrency(netValues.actual, currencySymbol), [
    currencySymbol,
    netValues.actual
  ]);

  return (
    <div className="centeredFlex">
      <Grid container>
        <Grid item xs={5} className="centeredFlex pl-2">
          <div className="secondaryHeading">{header}</div>
          {headerAdornment && (
            <Typography variant="caption" color="textSecondary" className="pl-1">
              {headerAdornment}
            </Typography>
          )}
        </Grid>
        <Grid item xs={2} className={classes.headerItem}>
          <Typography variant="body2" className="money">
            {maxLabel}
          </Typography>
        </Grid>
        <Grid item xs={2} className={classes.headerItem}>
          <Typography variant="body2" className="money">
            {projectedLabel}
          </Typography>
        </Grid>
        <Grid item xs={2} className={classes.headerItem}>
          <Typography variant="body2" className="money">{actualLabel}</Typography>
        </Grid>
      </Grid>
      <div className={classes.stub} />
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  tutorRoles: state.preferences.tutorRoles,
  enrolments: state.courseClass.enrolments,
  discounts: state.plainSearchRecords["Discount"].items,
  pending: state.plainSearchRecords["Discount"].loading,
  discountsError: state.plainSearchRecords["Discount"].error,
});

const mapDispatchToProps = (dispatch: Dispatch<IAction>) => ({
  setCourseClassBudgetModalOpened: (opened, onCostRate) => dispatch(setCourseClassBudgetModalOpened(opened, onCostRate)),
  getSearchResult: (search: string) => {
    dispatch(setCommonPlainSearch("Discount", search));
    dispatch(getCommonPlainRecords("Discount", 0, "name,discountType,discountDollar,discountPercent", null, null, PLAIN_LIST_MAX_PAGE_SIZE, items => items.map(mapPlainDiscounts)));
  },
  clearSearchResult: (pending: boolean) => dispatch(clearCommonPlainRecords("Discount", pending)),
});

export default connect<any, any, Props>(mapStateToProps, mapDispatchToProps)(withStyles(CourseClassBudgetTab, styles));
