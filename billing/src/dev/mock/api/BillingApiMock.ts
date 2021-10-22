import { SettingsDTO, SiteDTO } from '@api/model';
import { MockAdapter, promiseResolve } from '../MockAdapter';

export function BillingApiMock(this: MockAdapter) {
  this.api.onPost('/v1/college')
    .reply((config) => promiseResolve(config, true));

  this.api.onGet('/v1/college/key')
    .reply((config) => promiseResolve(config, 'nida'));

  this.api.onGet('/v1/college/site')
    .reply((config) => promiseResolve<SiteDTO[]>(config, [
      {
        id: 1,
        name: 'Site 1',
        key: 'nida-1',
        domains: { 'www.corporate.nida.edu.au': 'error', 'www.open.nida.edu.au': 'error' },
        webSiteTemplate: null
      },
      {
        id: 2,
        name: 'Site 2',
        key: 'nida-2',
        gtmContainerId: '111111',
        configuredByInfo: {
          firstname: 'John',
          lastname: 'Smith',
          email: 'john@yahoo.com'
        },
        domains: { 'www.corporate.nida.edu.au': 'error', 'www.open.nida.edu.au': 'error' },
        webSiteTemplate: null
      },
      {
        id: 3,
        name: 'Site 3',
        key: 'nidatest',
        domains: { 'www.corporate.nida.edu.au': '', 'www.open.nida.edu.au': '' },
        webSiteTemplate: null
      },
      {
        id: 4,
        name: 'Site 4',
        key: 'nidatest2',
        domains: { 'www.corporate.nida.edu.au': '', 'www.open.nida.edu.au': '' },
        webSiteTemplate: null
      }
    ]));

  this.api.onPost('/v1/college/sites')
    .reply((config) => promiseResolve(config));

  this.api.onGet('/v1/college/settings')
    .reply((config) => promiseResolve<SettingsDTO>(config, {
      contactFullName: 'John',
      contactEmail: 'Smith',
      invoiceReference: '1234',
      usersCount: 4,
      billingPlan: 'basic-21',
      requestedUsersCount: 2,
      requestedBillingPlan: 'premium-21'
    }));

  this.api.onPost('/v1/college/settings')
    .reply((config) => promiseResolve(config));
}
