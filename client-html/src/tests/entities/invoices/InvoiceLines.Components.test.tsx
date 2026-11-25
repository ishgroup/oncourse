/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tax } from '@api/model';
import { act, fireEvent, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import * as React from 'react';
import { connect } from 'react-redux';
import { getFormValues, reduxForm } from 'redux-form';
import EntityService from '../../../js/common/services/EntityService';
import TestStore from '../../../js/constants/Store';
import { InvoiceLines } from '../../../js/containers/entities/invoices/components/InvoiceLines';
import { TestEntry } from '../../TestEntry';

const LINE = 'invoiceLines[0]';

// every test renders its own form, so that a change dispatched late by a select can't reach the next test
let form: string;
let formIndex = 0;

const taxes = [
  { id: 1, code: 'GST', rate: 0.1 },
  { id: 2, code: 'N', rate: 0 }
] as Tax[];

const accountTypes = {
  income: [
    { id: 10, description: 'Course fees', accountCode: '41000', 'tax.id': 1 },
    { id: 11, description: 'Tax free fees', accountCode: '41100', 'tax.id': 2 },
    { id: 12, description: 'Unknown tax fees', accountCode: '41200', 'tax.id': 99 }
  ],
  all: []
} as any;

// Dollar discount of the line, values in the order of INVOICE_LINE_DISCOUNT_COLUMNS
const discountRows = { rows: [{ id: '7', values: ['Ten off', 'Dollar', '10.00', null, 'No Rounding', null, null] }] };

const InvoiceLine: any = InvoiceLines;

const LineFields = connect((state, props: any) => ({ values: getFormValues(props.form)(state) as any }))(({ values, dispatch, form: lineForm }: any) => (
  values?.invoiceLines ? (
    <InvoiceLine
      row={values.invoiceLines[0]}
      rows={values.invoiceLines}
      item={LINE}
      isNew
      type="Invoice"
      currency={{ shortCurrencySymbol: '$' }}
      twoColumn
      form={lineForm}
      taxes={taxes}
      accountTypes={accountTypes}
      dispatch={dispatch}
    />
  ) : null
));

const LineForm = reduxForm<any, any>({})((props: any) => <LineFields form={props.form} />);

const line = (values = {}) => ({
  title: 'Course fee',
  quantity: 1,
  incomeAccountId: 10,
  incomeAccountName: 'Course fees 41000',
  taxId: 1,
  priceEachExTax: 100,
  discountEachExTax: 0,
  taxEach: 10,
  total: 110,
  ...values
});

const renderLine = (values = {}) => {
  form = `INVOICE_LINES_TEST_FORM_${++formIndex}`;

  return render(
    <TestEntry state={{ invoices: { selectedLineCourseClasses: [], selectedLineCourse: null, selectedLineEnrolments: null, selectedContact: null } }}>
      <LineForm form={form} initialValues={{ invoiceLines: [line(values)] }} />
    </TestEntry>
  );
};

const savedLine = () => (getFormValues(form)(TestStore.getState()) as any).invoiceLines[0];

// select changes the form value asynchronously
const expectSavedLine = async (values: object) => waitFor(() => expect(savedLine()).toMatchObject(values));

const field = (name: string) => document.querySelector(`input[name="${LINE}.${name}"]`) as HTMLInputElement;

const chooseOption = async (name: string, option: string) => {
  await userEvent.click(field(name));
  await userEvent.click(await screen.findByRole('option', { name: option }));
};

// typing key by key into the money field's number format loses keys in jsdom, so the value is changed at once
const typeIn = async (name: string, value: string) => {
  await userEvent.click(field(name));
  fireEvent.change(field(name), { target: { value } });
  await userEvent.tab();
};

const discountLockButton = () => document
  .querySelector('[data-testid="LockOpenIcon"], [data-testid="LockIcon"]')
  ?.closest('button');

describe('InvoiceLines', () => {
  let getPlainRecords: jest.SpyInstance;

  // only the line's own discount lookup returns its discount, other lookups (remote selects) return nothing
  const mockLineDiscount = (lookup: () => Promise<any>) => getPlainRecords.mockImplementation((entity: string, columns: string, search: string) => (
    entity === 'Discount' && search?.startsWith('id is ') ? lookup() : Promise.resolve({ rows: [] })
  ));

  beforeEach(() => {
    getPlainRecords = jest.spyOn(EntityService, 'getPlainRecords').mockResolvedValue({ rows: [] } as any);
  });

  afterEach(() => {
    getPlainRecords.mockRestore();
  });

  it('disables amount fields and discount while the line discount is loading, so it is not applied as a fixed amount', async () => {
    let resolveDiscount: (value) => void;
    mockLineDiscount(() => new Promise(resolve => {
      resolveDiscount = resolve;
    }));

    renderLine({ discountId: 7, discountName: 'Ten off', discountEachExTax: 10, taxEach: 9, total: 99 });

    ['quantity', 'incomeAccountId', 'priceEachExTax', 'discountId', 'discountEachExTax', 'taxId', 'total']
      .forEach(name => expect(field(name)).toBeDisabled());
    expect(discountLockButton()).toBeTruthy();
    expect(discountLockButton()).toBeDisabled();

    await act(async () => resolveDiscount(discountRows));

    ['quantity', 'incomeAccountId', 'priceEachExTax', 'discountId', 'taxId', 'total']
      .forEach(name => expect(field(name)).not.toBeDisabled());
    expect(discountLockButton()).not.toBeDisabled();
  });

  it('keeps the total when tax type of an opened line is changed', async () => {
    renderLine();

    await chooseOption('taxId', 'N');

    await expectSavedLine({ taxId: 2, priceEachExTax: 110, taxEach: 0, total: 110 });
  });

  it('recalculates the line like a tax change when income account sets another tax type', async () => {
    renderLine();

    await chooseOption('incomeAccountId', 'Tax free fees 41100');

    await expectSavedLine({ incomeAccountId: 11, taxId: 2, priceEachExTax: 110, taxEach: 0, total: 110 });
  });

  it('keeps tax type and GST of the line when income account has a tax that is not known', async () => {
    renderLine();

    await chooseOption('incomeAccountId', 'Unknown tax fees 41200');

    await expectSavedLine({ incomeAccountId: 12, taxId: 1, priceEachExTax: 100, taxEach: 10, total: 110 });

    // the next price change is still calculated with GST, not as tax free
    await typeIn('priceEachExTax', '200');

    await expectSavedLine({ taxId: 1, priceEachExTax: 200, taxEach: 20, total: 220 });
  });

  it('sets discount amount to 0 when discount is removed from a line that is not recalculated', async () => {
    mockLineDiscount(() => Promise.resolve(discountRows));

    // line without price and not edited, so removing the discount doesn't recalculate it
    renderLine({ priceEachExTax: null, discountId: 7, discountName: 'Ten off', discountEachExTax: 10, taxEach: 9, total: 99 });

    await waitFor(() => expect(field('priceEachExTax')).not.toBeDisabled());

    // clear button of the discount select
    const clearButton = field('discountId').closest('.MuiAutocomplete-root')?.querySelector('[data-testid="CloseIcon"]');
    expect(clearButton).toBeTruthy();

    await userEvent.click(clearButton);

    await expectSavedLine({ discountId: null, discountEachExTax: 0, total: 99 });
  });
});
