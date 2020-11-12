import * as React from 'react';
import { create } from 'react-test-renderer';
import Gravatar from '../src/Gravatar';
import { GravatarOptions } from '../src/lib';

describe('getGravatarUrl', () => {
  const email:string = 'example@example.com';

  it('Renders the default img tag with the correct url', () => {
    const component = create(
      <Gravatar email={email}>{url => (<img src={url} />)}</Gravatar>,
    );
    expect(component.toJSON()).toMatchSnapshot();
  });

  it('Renders the img tag with the correct url and options', () => {
    const options: GravatarOptions = {
      default: 'monsterid',
      size: 250,
    };
    const component = create(
      <Gravatar email={email} options={options}>{url => (<img src={url} />)}</Gravatar>,
    );
    expect(component.toJSON()).toMatchSnapshot();
  });
});
