import { createTheme, ThemeProvider } from '@mui/material/styles';
import { cleanup, fireEvent, render } from '@testing-library/react';
import React from 'react';
import FilterGroupComp from '../../js/common/components/list-view/components/side-bar/components/FilterGroup';

const filter = name => ({ name, expression: `${name} is true`, active: false });

const theme = createTheme();
const Group = props => <ThemeProvider theme={theme}><FilterGroupComp {...props} /></ThemeProvider>;

afterEach(cleanup);

it('reuses no item id across two live filter items', () => {
  const props = { title: 'Custom Filters', groupIndex: 0, rootEntity: 'Contact', onUpdate: jest.fn(), deleteFilter: jest.fn() };

  const { rerender } = render(<Group {...props} filters={[filter('X'), filter('Y')]} />);

  // X is deleted: Y keeps its component instance and slides from 0/1 to 0/0
  rerender(<Group {...props} filters={[filter('Y')]} />);

  // a filter is saved back into the group and takes the slot Y left
  expect(() => rerender(<Group {...props} filters={[filter('Z'), filter('Y')]} />)).not.toThrow();
});

it('deletes a custom filter by its own id, not by its tree item id', () => {
  const deleteFilter = jest.fn();
  const props = { title: 'Custom Filters', groupIndex: 1, rootEntity: 'CourseClass', onUpdate: jest.fn(), deleteFilter };

  const { getAllByTestId } = render(<Group {...props} filters={[{ ...filter('X'), id: 7 }, { ...filter('Y'), id: 42, showForCurrentOnly: true }]} />);

  fireEvent.click(getAllByTestId('DeleteIcon')[1]);

  expect(deleteFilter).toHaveBeenCalledWith(42, 'CourseClass', false, true);
});
