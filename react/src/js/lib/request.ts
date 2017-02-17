import $ from 'jquery';

export default function(options = {}) {
    return $.ajax({
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        ...options
    });
}

