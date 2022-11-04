/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import Grid from "@mui/material/Grid";
import Collapse from "@mui/material/Collapse";
import { change } from "redux-form";
import FormControlLabel from "@mui/material/FormControlLabel";
import { ClassCostRepetitionType } from "@api/model";
import { Divider } from "@mui/material";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import { BudgetCostModalContentProps } from "../../../../../../model/entities/CourseClass";
import { getContactFullName } from "../../../../contacts/utils";
import { ContactLinkAdornment } from "../../../../../../common/components/form/FieldAdornments";
import ContactSelectItemRenderer from "../../../../contacts/components/ContactSelectItemRenderer";
import { decimalDivide, decimalMul, decimalPlus } from "../../../../../../common/utils/numbers/decimalCalculation";
import { getCurrentTax } from "../../../../taxes/utils";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../../../constants";
import { PayRateTypes, validatePayRateTypes } from "./BudgetCostModal";
import { greaterThanNullValidation } from "../../../../../../common/utils/validation";
import { normalizeNumberToZero } from "../../../../../../common/utils/numbers/numbersNormalizing";

const getFeeIncTax = (exTax, taxes, taxId) => decimalMul(exTax, decimalPlus(1, getCurrentTax(taxes, taxId)?.rate));

const IncomeAndExpenceContent: React.FC<BudgetCostModalContentProps> = ({
  taxes,
  values,
  dispatch,
  costLabel,
  hasMinMaxFields,
  hasCountField
}) => {
  const isIncome = useMemo(() => values.flowType === "Income", [values.flowType]);

  const currentTax = useMemo(() => getCurrentTax(taxes, values.taxId), [values.taxId, taxes]);

  const onFeeIncTaxChange = useCallback(
    value => {
      if (currentTax) {
        dispatch(
          change(
            COURSE_CLASS_COST_DIALOG_FORM,
            "perUnitAmountExTax",
            decimalDivide(value, decimalPlus(1, currentTax?.rate))
          )
        );
      }
    },
    [currentTax]
  );

  const onFeeExTaxChange = useCallback(
    value => {
      if (currentTax) {
        dispatch(
          change(
            COURSE_CLASS_COST_DIALOG_FORM,
            "perUnitAmountIncTax",
            decimalMul(value, decimalPlus(1, currentTax?.rate))
          )
        );
      }
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

  const onRepetitionChange = useCallback<any>((val: ClassCostRepetitionType, v, prev: ClassCostRepetitionType) => {
    if (val === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", 1));
    }
    if (prev === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", null));
    }
  }, []);

  return (
    <Grid container columnSpacing={3}>
      <Grid item xs={6}>
        <FormField
          type="multilineText"
          name="description"
          label="Description"
          required
        />
      </Grid>
      <Grid item xs={6}>
        <FormField
          type="remoteDataSearchSelect"
          entity="Contact"
          name="contactId"
          label="Contact"
          selectValueMark="id"
          selectLabelCondition={getContactFullName}
          defaultDisplayValue={values.contactName}
          labelAdornment={
            <ContactLinkAdornment id={values?.contactId} />
          }
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          allowEmpty
        />
      </Grid>
      <Grid container columnSpacing={3} item xs={12}>
        <Grid item xs={hasCountField ? 2 : 3}>
          <FormField
            type="select"
            name="repetitionType"
            label="Type"
            items={PayRateTypes}
            onChange={onRepetitionChange}
            debounced={false}
            validate={validatePayRateTypes}
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
            debounced={false}
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
            debounced={false}
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
            debounced={false}
          />
        </Grid>
      </Grid>

      {!isIncome && (
        <Grid item xs={12}>
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="isSunk" color="secondary" />}
            label="Sunk cost (not recoverable if class cancelled)"
          />
        </Grid>
      )}

      <Grid item xs={12}>
        <Collapse in={hasMinMaxFields}>
          <Grid container columnSpacing={3}>
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

export default IncomeAndExpenceContent;
