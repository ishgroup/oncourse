/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { change } from "redux-form";
import Grid from "@mui/material/Grid";
import { Collapse, FormControlLabel, Typography } from "@mui/material";
import Divider from "@mui/material/Divider";
import { Discount, DiscountType, MoneyRounding } from "@api/model";
import { connect } from "react-redux";
import Decimal from "decimal.js-light";
import EditInPlaceField from "../../../../common/components/form/formFields/EditInPlaceField";
import FormField from "../../../../common/components/form/formFields/FormField";
import Subtitle from "../../../../common/components/layout/Subtitle";
import { validateNonNegative, validateRangeInclusive, validateSingleMandatoryField } from "../../../../common/utils/validation";
import { State } from "../../../../reducers/state";
import { Switch } from "../../../../common/components/form/formFields/Switch";
import CustomSelector, { CustomSelectorOption } from "../../../../common/components/custom-selector/CustomSelector";
import EditInPlaceDateTimeField from "../../../../common/components/form/formFields/EditInPlaceDateTimeField";
import { mapSelectItems } from "../../../../common/utils/common";

interface DiscountGeneralProps {
  values?: Discount;
  twoColumn?: boolean;
  form: string;
  dispatch: any;
  cosAccounts?: { id: number; description: string }[];
  manualLink?: string;
}

interface DiscountGeneralState {
  codeOn?: boolean;
  discountType?: DiscountType;
  validFromIndex?: number;
  validToIndex?: number;
}

const discountTypes = [
  {
    value: DiscountType.Percent,
    label: "Discount percent"
  },
  {
    value: DiscountType.Dollar,
    label: "Discount dollar"
  },
  {
    value: DiscountType["Fee override"],
    label: "Override fee"
  }
];

const roundingModeTypes = Object.keys(MoneyRounding).map(mapSelectItems);

const validateZero = value => (!value ? "Must be more or less then 0" : undefined);

const validateRangeDiscountPercent = value => validateRangeInclusive(value, -1, 1);
const validateRangePredictedStudentsPercentage = value => validateRangeInclusive(value, 0, 1);

class DiscountGeneral extends React.Component<DiscountGeneralProps, DiscountGeneralState> {
  constructor(props: DiscountGeneralProps) {
    super(props);

    const values = props.values || {};

    this.state = {
      codeOn: DiscountGeneral.IS_CODE_ON(props.values),
      discountType: values.discountType ? values.discountType : DiscountType.Percent,
      validFromIndex: values.validFrom ? 3 : values.validFromOffset < 0 ? 1 : values.validFromOffset > 0 ? 2 : 0,
      validToIndex: values.validTo ? 3 : values.validToOffset < 0 ? 1 : values.validToOffset > 0 ? 2 : 0
    };
  }

  onSelectValidFrom = value => {
    const { dispatch, values, form } = this.props;
    this.setState({ validFromIndex: value });
    if (value === 0) {
      dispatch(change(form, "validFrom", null));
      dispatch(change(form, "validFromOffset", null));
    }
    if (value === 1 || value === 2) {
      const offset = values["validFromOffset"];
      let updatedOffset = offset;
      if ((value === 1 && offset > 0) || (value === 2 && offset < 0)) {
        updatedOffset = -offset;
      }
      dispatch(change(form, "validFromOffset", updatedOffset));
      dispatch(change(form, "validFrom", null));
    }
    if (value === 3) {
      dispatch(change(form, "validFromOffset", null));
    }
  };

  onSelectValidTo = value => {
    const { dispatch, values, form } = this.props;
    this.setState({ validToIndex: value });
    if (value === 0) {
      dispatch(change(form, "validTo", null));
      dispatch(change(form, "validToOffset", null));
    }
    if (value === 1 || value === 2) {
      const offset = values["validToOffset"];
      let updatedOffset = offset;
      if ((value === 1 && offset > 0) || (value === 2 && offset < 0)) {
        updatedOffset = -offset;
      }
      dispatch(change(form, "validToOffset", updatedOffset));
      dispatch(change(form, "validTo", null));
    }
    if (value === 3) {
      dispatch(change(form, "validToOffset", null));
    }
  };

  validFromOptions: CustomSelectorOption[] = [
    { caption: "any date", body: "Any date", type: null },
    {
      caption: "days before",
      body: "days before class starts",
      type: "number",
      component: EditInPlaceField,
      fieldName: "validFromOffset",
      format: value => -value,
      normalize: value => -value,
      min: 0
    },
    {
      caption: "days after",
      body: "days after class starts",
      type: "number",
      component: EditInPlaceField,
      fieldName: "validFromOffset",
      min: 0
    },
    {
      caption: "date",
      body: "Date",
      type: "date",
      component: EditInPlaceDateTimeField,
      fieldName: "validFrom"
    }
  ];

  validToOptions: CustomSelectorOption[] = [
    { caption: "any date", body: "Any date", type: null },
    {
      caption: "days before",
      body: "days before class starts",
      type: "number",
      component: EditInPlaceField,
      fieldName: "validToOffset",
      format: value => -value,
      normalize: value => -value,
      min: 0
    },
    {
      caption: "days after",
      body: "days after class starts",
      type: "number",
      component: EditInPlaceField,
      fieldName: "validToOffset",
      min: 0
    },
    {
      caption: "date",
      body: "Date",
      type: "date",
      component: EditInPlaceDateTimeField,
      fieldName: "validTo"
    }
  ];

  componentDidUpdate(
    prevProps: Readonly<DiscountGeneralProps>,
  ): void {
    const cur = this.props.values && this.props.values.code;
    const pre = prevProps.values && prevProps.values.code;

    if (cur !== pre) {
      this.setState({ codeOn: DiscountGeneral.IS_CODE_ON(this.props.values) });
    }
    if (
      (this.props.values && !prevProps.values)
      || (this.props.values && prevProps.values && this.props.values.discountType !== prevProps.values.discountType)
      || (this.props.values && prevProps.values && this.props.values.id !== prevProps.values.id)
    ) {
      this.setState({ discountType: this.props.values.discountType });
    }
    if (
      (this.props.values && !prevProps.values)
      || (this.props.values && prevProps.values && this.props.values.id !== prevProps.values.id)
    ) {
      const values = this.props.values;
      this.setState({
        validFromIndex: values.validFrom ? 3 : values.validFromOffset < 0 ? 1 : values.validFromOffset > 0 ? 2 : 0,
        validToIndex: values.validTo ? 3 : values.validToOffset < 0 ? 1 : values.validToOffset > 0 ? 2 : 0
      });
    }
  }

  static IS_CODE_ON(discount: Discount): boolean {
    return !!(discount && discount.code);
  }

  cleanValueFields = () => {
    const { dispatch, form } = this.props;
    [
      {
        field: "discountPercent",
        value: null
      },
      {
        field: "discountValue",
        value: null
      },
      {
        field: "discountMin",
        value: null
      },
      {
        field: "discountMax",
        value: null
      },
      {
        field: "rounding",
        value: MoneyRounding["No Rounding"]
      }
    ].forEach((item: { field: string; value?: any }) => dispatch(change(form, item.field, item.value)));
  };

  currencySymbol = "$";

  parseFloatValue = value => (value ? parseFloat(value) : value);

  onCodeSwitchToggle = (e, checked) => {
    const { dispatch, form } = this.props;
    if (checked === false) {
      dispatch(change(form, "code", null));
    }
    this.setState({ ...this.state, codeOn: checked });
  };

  formatPercent = value => {
    if (value && value !== "-") {
      return parseInt(String(value * 100), 10);
    }
    return value;
  };

  parsePercent = value => {
    if (value && value !== "-") {
      return new Decimal(value / 100).toDecimalPlaces(2).toNumber();
    }
    return value;
  };

  normalizePercent = (value, prevValue) => {
    if (value === "-") {
      return value;
    }
    if (isNaN(value) && prevValue) {
      return prevValue;
    }
    if (value === "" || isNaN(value)) {
      return "";
    }
    return value;
  };

  formatDiscountPercent = value => {
    if (value && value !== "-") {
      return new Decimal(String(value * 100)).toDecimalPlaces(1).toNumber();
    }
    return value;
  };

  parseDiscountPercent = value => {
    if (value && value !== "-") {
      return new Decimal(value / 100).toDecimalPlaces(3).toNumber();
    }
    return value;
  };

  normalizeDiscountPercent = (value, prevValue) => {
    if (value === "-") {
      return value;
    }
    if (isNaN(value) && prevValue) {
      return prevValue;
    }
    if (value === "" || isNaN(value)) {
      return "";
    }
    return value;
  };

  render() {
    const { twoColumn, cosAccounts } = this.props;
    const { validFromIndex, validToIndex } = this.state;

    const gridXS = twoColumn ? 6 : 12;

    return (
      <div className="d-grid pt-2 pl-3 pr-3 pb-0">
        <Grid container spacing={0}>
          <Grid item xs={gridXS}>
            <FormField
              type="text"
              name="name"
              label="Name"
              required
            />
          </Grid>
          <Grid item xs={gridXS}>
            <FormField
              type="select"
              name="discountType"
              label="Value type"
              items={discountTypes}
              onChange={this.cleanValueFields}
            />
          </Grid>
          <Grid item xs={gridXS}>
            {this.state.discountType === DiscountType.Percent ? (
              <FormField
                type="number"
                name="discountPercent"
                label="Value"
                validate={[validateSingleMandatoryField, validateRangeDiscountPercent]}
                format={this.formatDiscountPercent}
                parse={this.parseDiscountPercent}
                normalize={this.normalizeDiscountPercent}
                preformatDisplayValue={value => value + "%"}
              />
            ) : (
              <FormField
                type="money"
                name="discountValue"
                label="Value"
                validate={[validateSingleMandatoryField, validateZero]}
              />
            )}
          </Grid>
          <Grid item xs={gridXS}>
            <FormField type="select" name="rounding" label="Rounding" items={roundingModeTypes} />
          </Grid>

          <Grid item xs={12}>
            <Collapse in={this.state.discountType === DiscountType.Percent} mountOnEnter unmountOnExit>
              <Grid container>
                <Grid item xs={gridXS}>
                  <FormField
                    type="money"
                    name="discountMin"
                    label="Min"
                    validate={validateNonNegative}
                  />
                </Grid>
                <Grid item xs={gridXS}>
                  <FormField
                    type="money"
                    name="discountMax"
                    label="Max"
                    validate={validateNonNegative}
                  />
                </Grid>
              </Grid>
            </Collapse>
          </Grid>

          <Grid item xs={12} className="mt-2 mb-2">
            <Subtitle label="ACCOUNTING" />
          </Grid>
          <Grid item xs={gridXS}>
            <FormField
              type="select"
              name="cosAccount"
              label="Post discount to COS Account"
              items={cosAccounts || []}
              selectLabelMark="description"
              selectValueMark="id"
              allowEmpty
            />
          </Grid>
          <Grid item xs={gridXS}>
            <FormField
              type="number"
              name="predictedStudentsPercentage"
              label="Default forecast take-up"
              validate={[validateSingleMandatoryField, validateRangePredictedStudentsPercentage]}
              format={this.formatPercent}
              parse={this.parsePercent}
              normalize={this.normalizePercent}
              preformatDisplayValue={value => value + "%"}
            />
          </Grid>

          <Grid item xs={12}>
            <Divider className="mt-2 mb-2" />
            <div className="d-grid justify-content-start align-items-center gridAutoFlow-column mb-2">
              <Typography className="heading">Require promotional code</Typography>
              <Switch onChange={this.onCodeSwitchToggle} checked={this.state.codeOn} />
              {this.state.codeOn && <FormField type="text" name="code" formatting="inline" />}
            </div>
          </Grid>

          <Grid item xs={gridXS} className="mb-2">
            <CustomSelector
              caption="Valid from"
              options={this.validFromOptions}
              onSelect={this.onSelectValidFrom}
              initialIndex={validFromIndex}
            />
          </Grid>

          <Grid item xs={gridXS} className="mb-2">
            <CustomSelector
              caption="Valid to"
              options={this.validToOptions}
              onSelect={this.onSelectValidTo}
              initialIndex={validToIndex}
            />
          </Grid>
        </Grid>

        <Divider className="mt-2 mb-2" />

        <div className="mb-2">
          <Subtitle label="WEB" />
        </div>

        <FormControlLabel
          className="checkbox pr-3"
          control={<FormField type="checkbox" name="availableOnWeb" color="secondary" fullWidth />}
          label="Available for online enrolment"
        />
        <FormControlLabel
          className="checkbox pr-3 mb-2"
          control={<FormField type="checkbox" name="hideOnWeb" color="secondary" fullWidth />}
          label="Hide discounted price on web"
        />
        <FormField type="multilineText" name="description" label="Public description" fullWidth />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  cosAccounts: state.discounts.cosAccounts
});

export default connect<any, any, any>(mapStateToProps, null)(DiscountGeneral);
