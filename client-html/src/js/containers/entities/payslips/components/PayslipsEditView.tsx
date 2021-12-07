/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid, { GridSize } from "@mui/material/Grid";
import clsx from "clsx";
import {
  arrayInsert, arrayRemove, change, FieldArray
} from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Contact, PayslipPayType, PayslipStatus } from "@api/model";
import Typography from "@mui/material/Typography";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { getListNestedEditRecord } from "../../../../common/components/list-view/actions";
import { getEntityTags } from "../../../tags/actions";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import PayslipPaylineRenderrer from "./PayslipPaylineRenderrer";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { PayLineWithDefer } from "../../../../model/entities/Payslip";
import { mapSelectItems } from "../../../../common/utils/common";
import AddButton from "../../../../common/components/icons/AddButton";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { IconButton } from "@mui/material";
import Launch from "@mui/icons-material/Launch";

const getLayoutArray = (threeColumn: boolean): { [key: string]: boolean | GridSize }[] => (threeColumn
    ? [
        { md: 12 },
        { xs: 12 },
        { xs: 6 },
        { xs: 6 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 12 },
        { xs: 4 },
        { xs: 8 },
        { xs: false },
        { xs: 12 },
        { xs: 12 }
      ]
    : [
        { md: 9 },
        { xs: 4 },
        { xs: 12 },
        { xs: 12 },
        { xs: 4 },
        { xs: 4 },
        { xs: 4 },
        { xs: 3 },
        { xs: 8 },
        { xs: 4 },
        { xs: 4 },
        { xs: 4 },
        { xs: 6 }
      ]);

const payslipPayTypes = Object.keys(PayslipPayType).map(mapSelectItems);

class PayslipsEditView extends React.PureComponent<any, any> {
  componentDidMount() {
    const { getNestedEditViewTags, isNested, tags } = this.props;

    if (isNested) {
      if (!tags || !tags.length) {
        getNestedEditViewTags();
      }
    }
  }

  validateTagList = (value, allValues, props) => {
    const { tags } = this.props;

    return validateTagsList(tags, value, allValues, props);
  };

  calculateTotal = (accumulator: number, current: PayLineWithDefer): number => accumulator + (current.deferred ? current.quantity * current.value : 0);

  calculateTotalBudget = (accumulator: number, current: PayLineWithDefer): number => accumulator + (current.deferred ? current.budgetedQuantity * current.budgetedValue : 0);

  selectLabelCondition = data => `${data["lastName"]}, ${data["firstName"]} `;

  addCustomPayLine = () => {
    const { dispatch, form } = this.props;

    const initialPayline: PayLineWithDefer = {
      description: "",
      value: 0,
      quantity: 1,
      deferred: true
    };

    dispatch(arrayInsert(form, "paylines", 0, initialPayline));

    setTimeout(() => {
      const domNode = document.getElementById("paylines[0].description");

      if (domNode) {
        domNode.scrollIntoView({ behavior: "smooth" });
      }
    }, 300);
  };

  removeCustomPayLine = (index: number) => {
    const { dispatch, form } = this.props;

    dispatch(arrayRemove(form, "paylines", index));
  };

  onTutorIdChange = (value: Contact) => {
    const { dispatch, form } = this.props;

    dispatch(change(form, "tutorFullName", contactLabelCondition(value)));
  };

  render() {
    const {
      isNew,
      values,
      tags,
      twoColumn,
      currency,
      syncErrors
    } = this.props;

    const total = values && values.paylines.reduce(this.calculateTotal, 0);

    const totalBudget = values && values.paylines.reduce(this.calculateTotalBudget, 0);

    const paislipsLayout = getLayoutArray(!twoColumn);

    const shortCurrencySymbol = currency != null ? currency.shortCurrencySymbol : "$";

    return values ? (
      <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
        <Grid item xs={12}>
          <FullScreenStickyHeader
            opened={isNew || Object.keys(syncErrors).includes("contactId")}
            disableInteraction={!isNew}
            twoColumn={twoColumn}
            title={(
              <div className="d-inline-flex-center">
                {values && defaultContactName(values.tutorFullName)}
                <IconButton disabled={!values?.tutorId} size="small" color="primary" onClick={() => openContactLink(values?.tutorId)}>
                  <Launch fontSize="inherit" />
                </IconButton>
              </div>
            )}
            fields={(
              <Grid item xs={twoColumn ? 6 : 12}>
                <FormField
                  type="remoteDataSearchSelect"
                  entity="Contact"
                  aqlFilter="isTutor is true"
                  name="tutorId"
                  label="Tutor"
                  selectValueMark="id"
                  selectLabelCondition={contactLabelCondition}
                  defaultDisplayValue={values && defaultContactName(values.tutorFullName)}
                  labelAdornment={
                    <LinkAdornment linkHandler={openContactLink} link={values.tutorId} disabled={!values.tutorId} />
                  }
                  disabled={!isNew}
                  onInnerValueChange={this.onTutorIdChange}
                  itemRenderer={ContactSelectItemRenderer}
                  rowHeight={55}
                  required
                />
              </Grid>
            )}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="select"
            name="payType"
            label="Pay type"
            items={payslipPayTypes}
            disabled={values && values.status === "Paid/Exported"}
            required
          />
        </Grid>

        <Grid item xs={12}>
          <FormField
            type="tags"
            name="tags"
            tags={tags}
            validate={tags && tags.length ? this.validateTagList : undefined}
          />
        </Grid>

        <Grid item xs={12} className="mw-800">
          <FieldArray
            name="paylines"
            component={PayslipPaylineRenderrer}
            onDelete={this.removeCustomPayLine}
            paylineLayout={paislipsLayout}
            threeColumn={!twoColumn}
            currency={currency}
          />
        </Grid>

        <Grid item xs={12}>
          <div
            className={clsx("centeredFlex mt-2", {
              "mw-800": twoColumn
            })}
          >
            <Grid container columnSpacing={3}>
              <Grid item xs={paislipsLayout[8].xs} className="centeredFlex">
                <span className="heading flex-fill money">Payrun total</span>
              </Grid>
              <Grid item xs={paislipsLayout[9].xs}>
                <Grid container columnSpacing={3}>
                  <Grid item xs={twoColumn ? paislipsLayout[10].xs : false} />

                  <Grid item xs={paislipsLayout[11].xs} className="centeredFlex justify-content-end">
                    <Typography
                      component="span"
                      className={clsx(
                        "heading",
                        "money",
                        twoColumn ? "pr-6" : "pr-4"
                      )}
                    >
                      {formatCurrency(total, shortCurrencySymbol)}
                    </Typography>
                  </Grid>

                  <Grid item xs={paislipsLayout[11].xs} className="centeredFlex justify-content-end">
                    <Typography
                      component="span"
                      variant="body1"
                      color="textSecondary"
                      className="pr-4 money"
                    >
                      {formatCurrency(totalBudget, shortCurrencySymbol)}
                    </Typography>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </div>
        </Grid>

        <Grid item xs={12}>
          <div className="centeredFlex">
            <Typography component="span" variant="body1" color="textSecondary">
              Add New Custom pay item
            </Typography>
            <AddButton
              onClick={this.addCustomPayLine}
              disabled={values && values.status === PayslipStatus["Paid/Exported"]}
              className="addButtonColor"
            />
          </div>
        </Grid>

        <Grid item xs={paislipsLayout[12].xs}>
          <FormField type="multilineText" name="publicNotes" label="Public notes" fullWidth />
        </Grid>

        <Grid item xs={paislipsLayout[12].xs}>
          <FormField type="multilineText" name="privateNotes" label="Private notes" fullWidth />
        </Grid>
      </Grid>
    ) : null;
  }
}

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Payslip"],
  currency: state.currency
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  openNestedEditView: (entity: string, id: number) => dispatch(getListNestedEditRecord(entity, id)),
  getNestedEditViewTags: () => {
    dispatch(getEntityTags("Payslip"));
  },
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PayslipsEditView);
