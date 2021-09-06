import * as React from 'react';
import renderer from 'react-test-renderer';
import NotFoundScreen from '../screens/NotFoundScreen';

it('renders correctly', () => {
  const tree = renderer.create(<NotFoundScreen />).toJSON();

  expect(tree).toMatchSnapshot();
});
