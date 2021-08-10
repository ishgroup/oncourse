import { Session } from '@api/model';
import { promiseResolve } from '../utils';
import { MockAdapterType } from '../types';

export default function LoginApiMock(this: MockAdapterType) {
  this.api.onGet('/v1/session')
    .reply((config) => promiseResolve<Session[]>(config, [{
      id: '11',
      classId: '123',
      name: 'ACT -RSA course',
      collegeName: 'Acme College',
      siteName: 'Sydney Campus',
      roomName: 'Newtown Learning',
      start: '2021-08-04T12:40:44.273Z',
      end: '2021-08-04T18:40:44.273Z',
      classColor: '#1abc9c'
    },
    {
      id: '12',
      classId: '124',
      name: 'Introduction to Quick Enrol',
      collegeName: 'Acme College',
      siteName: 'Sydney Campus',
      roomName: 'Newtown Learning',
      start: '2021-08-11T12:40:44.273Z',
      end: '2021-08-11T14:40:44.273Z',
      classColor: '#5339f3'
    },
    {
      id: '13',
      classId: '125',
      name: 'Certificate III in Early Childhood Education and Care (no units)',
      collegeName: 'Acme College',
      siteName: 'Sydney Campus',
      roomName: 'Newtown Learning',
      start: '2021-08-11T12:40:44.273Z',
      end: '2021-08-11T14:40:44.273Z',
      classColor: '#f25b3a'
    }]));
}
