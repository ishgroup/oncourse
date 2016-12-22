import { mount } from 'enzyme';
import { expect } from 'chai';
import StudentList from './StudentList';

describe('StudentList', () => {
    it('should hide student list', () => {
        let studentList = mount(<StudentList students={[]}/>);
        expect(studentList.html()).to.be.null;
    });

    it('should display two students', () => {
        let studentList = mount(<StudentList students={[{
            id: 1,
            first_name: 'FirstName1',
            last_name: 'LastName1',
            email: 'user1@email.com'
        }, {
            id: 2,
            first_name: 'FirstName2',
            last_name: 'LastName2',
            email: 'user2@email.com'
        }]}/>);

        expect(studentList.find('.enrol-list__row')).to.have.length(2);
    });

    it('should display student data', () => {
        let student = {
                id: 1,
                first_name: 'FirstName1',
                last_name: 'LastName1',
                email: 'user1@email.com'
            },
            studentList = mount(<StudentList students={[student]}/>);

        expect(studentList.find('.enrol-list__row').text()).to.be.equal(`${student.first_name} ${student.last_name} (${student.email})`);
    });
});

