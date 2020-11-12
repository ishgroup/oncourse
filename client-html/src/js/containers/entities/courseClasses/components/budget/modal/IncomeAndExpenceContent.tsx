/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import Grid from "@material-ui/core/Grid";
import Collapse from "@material-ui/core/Collapse";
import { change } from "redux-form";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { Contact, PayRateType } from "@api/model";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { Divider } from "@material-ui/core";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../../../reducers/state";
import {
 clearContacts, clearContactsSearch, getContacts, setContactsSearch
} from "../../../../contacts/actions";
import { AnyArgFunction, NoArgFunction, StringArgFunction } from "../../../../../../model/common/CommonFunctions";
import { BudgetCostModalContentProps } from "../../../../../../model/entities/CourseClass";
import { contactLabelCondition, openContactLink } from "../../../../contacts/utils";
import { LinkAdornment } from "../../../../../../common/components/form/FieldAdornments";
import ContactSelectItemRenderer from "../../../../contacts/components/ContactSelectItemRenderer";
import { decimalDivide, decimalMul, decimalPlus } from "../../../../../../common/utils/numbers/decimalCalculation";
import { getCurrentTax } from "../../../../taxes/utils";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../../../constants";
import { PayRateTypes } from "./BudgetCostModal";
import { greaterThanNullValidation } from "../../../../../../common/utils/validation";
import { normalizeNumberToZero } from "../../../../../../common/utils/numbers/numbersNormalizing";

interface Props extends BudgetCostModalContentProps {
  contacts?: Contact[];
  contactsLoading?: boolean;
  contactsRowsCount?: number;
  getContacts?: AnyArgFunction;
  setContactsSearch?: StringArgFunction;
  clearContactsSearch?: NoArgFunction;
  clearContacts?: AnyArgFunction;
}

const getFeeIncTax = (exTax, taxes, taxId) => decimalMul(exTax, decimalPlus(1, getCurrentTax(taxes, taxId).rate));

const IncomeAndExpenceContent: React.FC<Props> = ({
  taxes,
  values,
  contacts,
  contactsLoading,
  contactsRowsCount,
  getContacts,
  setContactsSearch,
  dispatch,
  costLabel,
  hasMinMaxFields,
  hasCountField,
  clearContacts
}) => {
  const isIncome = useMemo(() => values.flowType === "Income", [values.flowType]);

  const currentTax = useMemo(() => getCurrentTax(taxes, values.taxId), [values.taxId, taxes]);

  const onFeeIncTaxChange = useCallback(
    value => {
      dispatch(
        change(
          COURSE_CLASS_COST_DIALOG_FORM,
          "perUnitAmountExTax",
          decimalDivide(value, decimalPlus(1, currentTax.rate))
        )
      );
    },
    [currentTax]
  );

  const onFeeExTaxChange = useCallback(
    value => {
      dispatch(
        change(COURSE_CLASS_COST_DIALOG_FORM, "perUnitAmountIncTax", decimalMul(value, decimalPlus(1, currentTax.rate)))
      );
    },
    [currentTax]
  );

  const onTaxIdChange = useCallback(
    id => {
      dispatch(
        change(COURSE_CLASS_COST_DIALOG_FORM, "perUnitAmountIncTax", getFeeIncTax(values.perUnitAmountExTax, taxes, id))
      );
    },
    [values.perUnitAmountExTax, taxes]
  );

  const onRepetitionChange = useCallback<any>((val: PayRateType, v, prev: PayRateType) => {
    if (val === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", 1));
    }
    if (prev === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", null));
    }
  }, []);

  return (
    <Grid container>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name="description"
          label="Description"
          fullWidth
          required
        />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="remoteDataSearchSelect"
          name="contactId"
          label="Contact"
          selectValueMark="id"
          selectLabelCondition={contactLabelCondition}
          defaultDisplayValue={values.contactName}
          labelAdornment={
            <LinkAdornment linkHandler={openContactLink} link={values.contactId} disabled={!values.contactId} />
          }
          items={contacts}
          onSearchChange={setContactsSearch}
          onLoadMoreRows={getContacts}
          onClearRows={clearContacts}
          loading={contactsLoading}
          remoteRowCount={contactsRowsCount}
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          allowEmpty
        />
      </Grid>
      <Grid container item xs={12}>
        <Grid item xs={hasCountField ? 2 : 3}>
          <FormField
            type="select"
            name="repetitionType"
            label="Type"
            items={PayRateTypes}
            onChange={onRepetitionChange}
          />
        </Grid>
        {hasCountField && (
          <Grid item xs={2}>
            <FormField
              type="number"
              name="unitCount"
              label="Count"
              validate={greaterThanNullValidation}
            />
          </Grid>
        )}
        <Grid item xs={hasCountField ? 2 : 3}>
          <FormField
            type="money"
            name="perUnitAmountExTax"
            label={isIncome ? "Amount" : costLabel}
            onChange={onFeeExTaxChange}
          />
        </Grid>
        <Grid item xs={3}>
          <FormField
            type="select"
            name="taxId"
            label="Tax"
            selectValueMark="id"
            selectLabelMark="code"
            onChange={onTaxIdChange}
            items={taxes}
          />
        </Grid>
        <Grid item xs={3}>
          <FormField
            type="money"
            name="perUnitAmountIncTax"
            label="Amount inc tax"
            normalize={normalizeNumberToZero}
            onChange={onFeeIncTaxChange}
          />
        </Grid>
      </Grid>

      {!isIncome && (
        <Grid item xs={12}>
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="isSunk" color="secondary" fullWidth />}
            label="Sunk cost (not recoverable if class cancelled)"
          />
        </Grid>
      )}

      <Grid item xs={12}>
        <Collapse in={hasMinMaxFields}>
          <Grid container>
            <Grid item xs={12} className="pt-2">
              <Divider />
            </Grid>
            <Grid item xs={12}>
              <div className="heading pt-2 pb-2">Total amount for this class</div>
            </Grid>
            <Grid item xs={3}>
              <FormField type="money" name="minimumCost" label="At least" />
            </Grid>
            <Grid item xs={3}>
              <FormField type="money" name="maximumCost" label="Limited to" />
            </Grid>
          </Grid>
        </Collapse>
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  contacts: state.contacts.items,
  contactsLoading: state.contacts.loading,
  contactsRowsCount: state.contacts.rowsCount
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getContacts: (offset?: number) => dispatch(getContacts(offset, null, true)),
    setContactsSearch: (search: string) => dispatch(setContactsSearch(search)),
    clearContactsSearch: () => dispatch(clearContactsSearch()),
    clearContacts: () => dispatch(clearContacts())
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(IncomeAndExpenceContent);
