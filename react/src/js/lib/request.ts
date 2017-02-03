export default function(options = {}) {
    return jQuery.ajax({
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        ...options
    });
}

