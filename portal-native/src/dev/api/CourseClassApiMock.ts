import { CourseClass } from '@api/model';
import { promiseResolve } from '../utils';
import { MockAdapterType } from '../types';

export default function CourseClassApiMock(this: MockAdapterType) {
  this.api.onGet('/v1/courseClass')
    .reply((config) => promiseResolve<CourseClass>(config, {
      id: '11',
      name: 'ACT -RSA course',
      color: '#1abc9c',
      start: '2021-08-14T12:40:44.273Z',
      end: '2021-08-14T18:40:44.273Z',
      siteTimezone: 'Australia/Sydney',
      classLink: 'https://template-a.oncourse.cc/class/CHC30113-4',
      description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus tincidunt ante enim, ut ultricies eros faucibus sed. Praesent nisi enim, convallis quis erat vitae, tincidunt tristique tellus. Fusce fermentum, libero sit amet lobortis iaculis, quam purus fermentum eros, ut egestas arcu nibh at massa. Suspendisse id tempus urna. Vestibulum elementum mollis dignissim. Aenean nisi risus, tristique vitae odio ut, aliquet tristique sapien. Ut suscipit rhoncus porta. ',
      attendance: [
        {
          id: '1244',
          studentName: 'Charlie Aarons',
          studentEmail: 'charlieAarons@yahoo.com.au',
          studentPicture: 'https://randomuser.me/api/portraits/men/73.jpg',
          arriveTime: '2021-08-04T12:40:44.273Z',
          departureTime: '2021-08-04T18:40:44.273Z',
          notes: '',
          attendance: 'Attended'
        },
        {
          id: '1344',
          studentName: 'Donna Aarons',
          studentEmail: 'donnaAarons@yahoo.com.au',
          studentPicture: 'https://randomuser.me/api/portraits/women/73.jpg',
          arriveTime: '2021-08-04T12:40:44.273Z',
          departureTime: '2021-08-04T18:40:44.273Z',
          notes: '',
          attendance: 'Absent without reason'
        },
        {
          id: '35525',
          studentName: 'Elizabeth Abbott',
          studentEmail: 'elizabethAbbott@yahoo.com.au',
          studentPicture: 'https://randomuser.me/api/portraits/women/79.jpg',
          arriveTime: '2021-08-04T12:40:44.273Z',
          departureTime: '2021-08-04T18:40:44.273Z',
          notes: '',
          attendance: 'Partial'
        },
        {
          id: '6743',
          studentName: 'Peter Abelson',
          studentEmail: 'peterAbelson@gmail.com',
          studentPicture: 'https://randomuser.me/api/portraits/men/74.jpg',
          arriveTime: '2021-08-04T12:40:44.273Z',
          departureTime: '2021-08-04T18:40:44.273Z',
          notes: '',
          attendance: 'Absent with reason'
        },
        {
          id: '6732',
          studentName: 'Farzaneh Abb',
          studentEmail: 'farzanehAbb@yahoo.com.au',
          studentPicture: 'https://randomuser.me/api/portraits/men/66.jpg',
          arriveTime: '2021-08-04T12:40:44.273Z',
          departureTime: '2021-08-04T18:40:44.273Z',
          notes: '',
          attendance: 'Unmarked'
        }
      ],
      resources: [
        {
          id: '345743',
          name: 'Recource 1',
          link: '444',
          mimeType: 'pdf'
        },
        {
          id: '7654',
          name: 'Recource 2',
          link: '444',
          mimeType: 'jpeg'
        },
        {
          id: '87654',
          name: 'Recource 3',
          link: '444',
          mimeType: 'png'
        },
        {
          id: '54334',
          name: 'Recource 4',
          link: '444',
          mimeType: 'docx'
        }
      ]
    }));
}
