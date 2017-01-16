import config from 'config';
import request from 'js/lib/request';

export function addCourse(courseId) {
    return request({
        type: 'put',
        url: config.api_root + '/cart/courses/' + courseId
    });
}

export function removeCourse(courseId) {
    return request({
        type: 'delete',
        url: config.api_root + '/cart/courses/' + courseId
    });
}

export default {
    addCourse, removeCourse
};