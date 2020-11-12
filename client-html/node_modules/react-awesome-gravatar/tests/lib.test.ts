import { getGravatarUrl, GravatarOptions } from '../src/lib';

describe('getGravatarUrl', () => {
  const email: string = 'example@example.com';
  const baseExpectedUrl: string =
    'https://www.gravatar.com/avatar/23463b99b62a72f26ed677cc556c44e8?';

  it('Returns the gravatar url', () => {
    expect(getGravatarUrl(email)).toBe(baseExpectedUrl);
  });

  it('Returns the gravatar url with the correct option as a query string param', () => {
    const options: GravatarOptions = {
      size: 150,
    };
    expect(getGravatarUrl(email, options)).toBe(`${baseExpectedUrl}size=150`);
  });

  it('Returns the gravatar url with multiple options are passed', () => {
    const options: GravatarOptions = {
      rating: 'x',
      size: 150,
    };
    expect(getGravatarUrl(email, options)).toBe(`${baseExpectedUrl}rating=x&size=150`);
  });

  it('Returns the gravatar url with defaultUrl as the default query param', () => {
    const options: GravatarOptions = {
      default: 'monsterid',
      defaultUrl: 'https://example.com/superimage.png',
    };
    expect(getGravatarUrl(email, options))
      .toBe(`${baseExpectedUrl}default=https%3A%2F%2Fexample.com%2Fsuperimage.png`);
  });
});
