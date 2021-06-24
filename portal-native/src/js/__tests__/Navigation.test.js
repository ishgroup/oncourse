import * as React from 'react';
import renderer from 'react-test-renderer';
import Navigation from '../components/navigation';

it('renders correctly', () => {
  const tree = renderer.create(<Navigation />).toJSON();

  expect(tree).toMatchSnapshot();
});
