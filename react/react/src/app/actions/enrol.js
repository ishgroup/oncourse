export const ADD_STUDENT = 'ADD_STUDENT';

export function addStudent(student) {
    return {
        type: 'ADD_STUDENT',
        data: student
    };
}