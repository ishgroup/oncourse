import { SiteDTO } from '@api/model';
import { MockAdapter, promiseResolve } from '../MockAdapter';

export function BillingApiMock(this: MockAdapter) {
  this.api.onGet(new RegExp('v1/college/(?!site)(?!key)[^/]+$'))
    .reply((config) => promiseResolve(config, true));

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
        domains: ['www.corporate.nida.edu.au', 'www.open.nida.edu.au'],
        webSiteTemplate: null
      },
      {
        id: 2,
        name: 'Site 2',
        key: 'nida-2',
        domains: ['www.corporate.nida.edu.au', 'www.open.nida.edu.au'],
        webSiteTemplate: null
      },
      {
        id: 3,
        name: 'Site 3',
        key: 'nidasss',
        domains: ['www.corporate.nida.edu.au', 'www.open.nida.edu.au'],
        webSiteTemplate: null
      }
    ]));

  this.api.onPost('/v1/college/sites')
    .reply((config) => promiseResolve(config));
}
