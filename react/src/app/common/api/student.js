import api from '../utils/request';

export default {
    find: (params) => {
        return api.get('/students/find', params);
    },

    create: (data) => {
        return api.post('/students', data);
    }
};