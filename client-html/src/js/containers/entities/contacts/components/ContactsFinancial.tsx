/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import React, {
  useCallback, useMemo, useState
} from "react";
import {
  Cart, ConcessionType, Contact, ContactRelationType, PaymentMethod, StudentConcession, Tax
} from "@api/model";
import { arrayInsert, arrayRemove, change, FieldArray } from "redux-form";
import IconButton from "@mui/material/IconButton";
import LockOpen from "@mui/icons-material/LockOpen";
import Lock from "@mui/icons-material/Lock";
import { connect } from "react-redux";
import { Alert, Grid } from "@mui/material";
import FormField from "../../../../common/components/form/formFields/FormField";
import { AccessState } from "../../../../common/reducers/accessReducer";
import { openInternalLink } from "../../../../common/utils/links";
import { State } from "../../../../reducers/state";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { ContactsState } from "../reducers";
import { EditViewProps } from "../../../../model/common/ListView";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import Divider from "@mui/material/Divider";
import MinifiedEntitiesList from "../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList";
import { MembershipContent, MembershipHeader } from "./MembershipLines";
import { RelationsContent, RelationsHeader } from "./RelationsLines";
import { getContactFullName } from "../utils";
import { ConcessionsContent, ConcessionsHeader } from "./ConcessionsLines";

interface ContactsFinancialProps extends EditViewProps<Contact> {
  taxTypes?: Tax[];
  defaultTerms?: number;
  paymentTypes?: PaymentMethod[];
  storedCard?: ContactsState["storedCard"];
  access?: AccessState;
  concessionTypes?: ConcessionType[];
  relationTypes?: ContactRelationType[];
}

const getFormattedTaxes = (taxes: Tax[]) => taxes.map(tax => ({
  value: tax.id,
  label: `${tax.code}`
}));

const financialColumns: NestedTableColumn[] = [
  {
    name: "description",
    title: "Type",
    width: 300
  },
  {
    name: "date",
    title: "Created on",
    type: "date",
    width: 160
  },
  {
    name: "referenceNumber",
    title: "Reference"
  },
  {
    name: "status",
    title: "Status",
    width: 100
  },
  {
    name: "owing",
    title: "Owing",
    type: "currency"
  },
  {
    name: "amount",
    title: "Amount",
    type: "currency"
  },
  {
    name: "balance",
    title: "Balance",
    type: "currency"
  }
];

const shopingCartColumns: NestedTableColumn[] = [
  {
    name: "createdOn",
    title: "Created on",
    type: "date",
    width: 160
  },
  {
    name: "totalValue",
    title: "Total",
    type: "currency"
  }
];

const openRow = value => {
  const { type, relatedEntityId } = value;

  const route = type[0].toLowerCase() + type.substring(1);
  openInternalLink(`/${route}/${relatedEntityId}`);
};

const openShopingCartRow = (row: Cart) => {
  openInternalLink(`/checkout?cartId=${row.id}`);
};

const ContactsFinancial: React.FC<ContactsFinancialProps> = props => {
  const {
    twoColumn,
    values,
    defaultTerms,
    taxTypes,
    form,
    dispatch,
    tabIndex,
    expanded,
    setExpanded,
    storedCard,
    access,
    syncErrors,
    relationTypes,
    concessionTypes
  } = props;

  const [lockedTerms, setLockedTerms] = useState(true);
  
  const removeShopingCartRow = id => {
    dispatch(change(form, "abandonedCarts", values.abandonedCarts.filter(c => c.id !== id)));
  };

  const onLockClick = useCallback(e => {
    e.preventDefault();
    if (!lockedTerms) {
      dispatch(change(form, "invoiceTerms", null));
    }

    if (lockedTerms) {
      dispatch(change(form, "invoiceTerms", defaultTerms));
    }

    setLockedTerms(prev => !prev);
  }, [defaultTerms, lockedTerms]);
  
  const financialTableTitle = useMemo(() => {
    const financialRecordsCount = (values && Array.isArray(values.financialData) ? values.financialData.length : 0);

    return financialRecordsCount === 1 ? "financial record" : "financial records";
  }, [values.financialData]);

  const removeStoredCreditCard = () => {
    dispatch(change(form, "removeCChistory", true));
  };

  const paymentInPermissions = access["/a/v1/list/plain?entity=PaymentIn"] && access["/a/v1/list/plain?entity=PaymentIn"]["GET"];

  const membershipsCount = useMemo(() => (values.memberships && values.memberships.length) || 0, [values.memberships]);
  const relationsCount = useMemo(() => (values.relations && values.relations.length) || 0, [values.relations]);
  const concessionsCount = useMemo(
    () => (values.student && values.student.concessions && values.student.concessions.length) || 0,
    [values.student && values.student.concessions]
  );

  const deleteRelation = useCallback(
    index => {
      dispatch(arrayRemove(form, "relations", index));
    },
    [values && values.relations, form]
  );

  const addNewRelation = useCallback(() => {
    dispatch(
      arrayInsert(form, "relations", 0, {
        id: null,
        relationId: null,
        relatedContactId: null,
        relatedContactName: null
      })
    );
  }, [values && values.id, form]);

  const RelationsHeaderLine = useCallback(
    props => <RelationsHeader relationTypes={relationTypes} contactId={values.id} {...props} />,
    [values && values.id, relationTypes]
  );
  const RelationsContentLine = useCallback(
    props => (
      <RelationsContent
        form={form}
        dispatch={dispatch}
        relationTypes={relationTypes}
        contactId={values.id}
        contactFullName={getContactFullName(values)}
        {...props}
      />
    ),
    [values && values.firstName, values && values.lastName, values && values.id, form, relationTypes]
  );

  const deleteConcession = useCallback(
    index => {
      dispatch(arrayRemove(form, "student.concessions", index));
    },
    [values && values.student && values.student.concessions, form]
  );

  const addNewConcession = useCallback(() => {
    const newLine: StudentConcession = {
      number: null,
      expiresOn: null,
      type: null
    };

    dispatch(arrayInsert(form, "student.concessions", 0, newLine));
  }, [values && values.id, form]);

  const ConcessionsHeaderLine = useCallback(props => <ConcessionsHeader {...props} />, [
    values.student && values.student.concessions
  ]);
  const ConcessionsContentLine = useCallback(
    props => <ConcessionsContent concessionTypes={concessionTypes} {...props} />,
    []
  );

  return values ? (
    <div className="pl-3 pr-3">
      {values.student && (
        <>
          <Grid item xs={12} className="pb-1">
            <Divider className="mb-1" />
            <MinifiedEntitiesList
              name="student.concessions"
              header="Concessions"
              oneItemHeader="Concession"
              count={concessionsCount}
              FieldsContent={ConcessionsContentLine}
              HeaderContent={ConcessionsHeaderLine}
              onAdd={addNewConcession}
              onDelete={deleteConcession}
              syncErrors={syncErrors}
              accordion
            />
          </Grid>
        </>
      )}
      <Grid item xs={12} className="pb-1">
        <Divider className="mb-1" />
        <MinifiedEntitiesList
          name="memberships"
          header="Memberships"
          oneItemHeader="Membership"
          count={membershipsCount}
          FieldsContent={MembershipContent}
          HeaderContent={MembershipHeader}
          syncErrors={syncErrors}
          twoColumn={twoColumn}
          accordion
        />
      </Grid>
      <Grid item xs={12} className="pb-1">
        <Divider className="mb-1" />
        <MinifiedEntitiesList
          name="relations"
          header="Relations"
          oneItemHeader="Relation"
          count={relationsCount}
          FieldsContent={RelationsContentLine}
          HeaderContent={RelationsHeaderLine}
          onAdd={addNewRelation}
          onDelete={deleteRelation}
          syncErrors={syncErrors}
          accordion
        />
      </Grid>

      <ExpandableContainer formErrors={syncErrors} index={tabIndex} expanded={expanded} setExpanded={setExpanded} header="Financial">
        <Grid container columnSpacing={3} rowSpacing={2} className="pb-3">
          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="number"
              name="invoiceTerms"
              label="Invoice terms (days)"
              defaultValue={defaultTerms}
              disabled={lockedTerms}
              labelAdornment={(
                <span>
                  <IconButton className="inputAdornmentButton" onClick={e => onLockClick(e)}>
                    {!lockedTerms && <LockOpen className="inputAdornmentIcon" />}
                    {lockedTerms && <Lock className="inputAdornmentIcon" />}
                  </IconButton>
                </span>
              )}
            />
          </Grid>
          <Grid item xs={twoColumn ? 3 : 12}>
            <FormField
              type="select"
              name="taxId"
              label="Tax type"
              items={getFormattedTaxes(taxTypes) || []}
              placeholder="Not set"
            />
          </Grid>
          {paymentInPermissions && storedCard && !values.removeCChistory
          && (
            <Grid item xs={12} className="centeredFlex mb-3 mt-2">
              <Alert severity="info">
                <Typography variant="body2">
                  <div>
                    A credit card was collected on
                    {' '}
                    {storedCard.created}
                    {' '}
                    and is securely stored for this user.
                  </div>
                  <div className="centeredFlex">
                    {storedCard.creditCardType}
                    {" "}
                    {storedCard.creditCardNumber}
                    <Button
                      onClick={removeStoredCreditCard}
                      size="small"
                      variant="text"
                      className="errorColor ml-2"
                    >
                      delete
                    </Button>
                  </div>
                </Typography>
              </Alert>
            </Grid>
          )}
          <Grid
            item
            xs={12}
            className="flex-column"
          >
            <FieldArray
              name="financialData"
              title={financialTableTitle}
              component={NestedTable}
              columns={financialColumns}
              onRowDoubleClick={openRow}
              rerenderOnEveryChange
              calculateHeight
            />
          </Grid>

          <Grid
            item
            xs={12}
            className="flex-column"
          >
            <FieldArray
              name="abandonedCarts"
              title={`Abandoned shopping cart${values.abandonedCarts?.length !== 1 ? "s" : ""}`}
              component={NestedTable}
              columns={shopingCartColumns}
              onRowDoubleClick={openShopingCartRow}
              onRowDelete={removeShopingCartRow}
              sortBy={(a, b) => new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()}
              rerenderOnEveryChange
              calculateHeight
            />
          </Grid>
        </Grid>
      </ExpandableContainer>
    </div>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  defaultTerms: state.invoices.defaultTerms,
  taxTypes: state.contacts.taxTypes,
  storedCard: state.contacts.storedCard,
  access: state.access,
  relationTypes: state.contacts.contactsRelationTypes,
  concessionTypes: state.contacts.contactsConcessionTypes,
});

export default connect<any, any, any>(mapStateToProps)(ContactsFinancial);