/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import React from 'react';
import SideBar from '../../js/common/components/list-view/components/side-bar/SideBar';
import { TestEntry } from '../TestEntry';

const filterGroups = [{
  title: 'Custom Filters',
  filters: [{ id: 312, name: '500', entity: 'Invoice', expression: 'amountOwing > 500', active: false }]
}];

const deleteFromSideBar = (filterEntity?: string) => {
  const deleteFilter = jest.fn();

  render(
    <TestEntry>
      <SideBar
        onChangeFilters={jest.fn()}
        filterGroups={filterGroups}
        deleteFilter={deleteFilter}
        rootEntity="AbstractInvoice"
        filterEntity={filterEntity}
        savingFilter={null}
        fetching={false}
      />
    </TestEntry>
  );

  fireEvent.click(screen.getByRole('button', { name: 'Delete Filter' }));

  return deleteFilter;
};

afterEach(cleanup);

it('deletes a custom filter against the filter entity the list loads them with', () => {
  // the filters are reloaded for this entity after the delete; AbstractInvoice has no saved
  // filters of its own, so asking for it would empty the whole Custom Filters group
  expect(deleteFromSideBar('Invoice')).toHaveBeenCalledWith(312, 'Invoice', false, undefined);
});

it('deletes a custom filter against the list entity when the list has no filter entity', () => {
  expect(deleteFromSideBar()).toHaveBeenCalledWith(312, 'AbstractInvoice', false, undefined);
});
