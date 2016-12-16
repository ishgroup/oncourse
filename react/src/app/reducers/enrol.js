import { ADD_STUDENT } from '../actions/enrol';

const getInitialState = () => {
    return {
        students: [],
        studentsHash: []
    };
};

const reducer = {
    [ADD_STUDENT](state, action) {
        let student = action.data;
        state.students = [...state.students, action.data];
        state.studentsHash = [...state.studentsHash, student.first_name + '|' + student.last_name + '|' + student.email];
        return Object.assign({}, state);
    }
};

export default function enrol(state = getInitialState(), action) {
    return reducer[action.type] ? reducer[action.type](state, action) : state;
}
