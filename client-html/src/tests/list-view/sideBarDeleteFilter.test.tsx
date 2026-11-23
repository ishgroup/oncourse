/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createTheme, ThemeProvider } from '@mui/material/styles';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import React from 'react';
import SideBar from '../../js/common/components/list-view/components/side-bar/SideBar';

// the connected side bar children are not under test, and loading them from here pulls in
// constants/Config, which reaches back into the list view through an import cycle
jest.mock('../../js/common/components/list-view/components/side-bar/components/ChecklistsFilters', () => ({
  __esModule: true,
  default: () => null
}));
jest.mock('../../js/common/components/list-view/components/side-bar/components/ListTagGroups', () => ({
  __esModule: true,
  default: () => null
}));
jest.mock('../../js/common/components/list-view/components/side-bar/components/StubFilterItem', () => ({
  __esModule: true,
  default: () => null
}));
jest.mock('../../js/common/components/layout/swipeable-sidebar/components/HamburgerMenu', () => ({
  __esModule: true,
  default: () => null
}));

const theme = createTheme();

const filterGroups = [{
  title: 'Custom Filters',
  filters: [{ id: 312, name: '500', entity: 'Invoice', expression: 'amountOwing > 500', active: false }]
}];

const deleteFromSideBar = (filterEntity?: string) => {
  const deleteFilter = jest.fn();

  render(
    <ThemeProvider theme={theme}>
      <SideBar
        onChangeFilters={jest.fn()}
        filterGroups={filterGroups}
        deleteFilter={deleteFilter}
        rootEntity="AbstractInvoice"
        filterEntity={filterEntity}
        savingFilter={null}
        fetching={false}
      />
    </ThemeProvider>
  );

  // the delete button is `visibility: hidden` until its row is hovered, which leaves it without an
  // accessible name, so it is found by its aria-label rather than by role and name
  fireEvent.click(screen.getByLabelText('Delete Filter'));

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
