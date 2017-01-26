export default function(options = {}) {
    return (global.jQuery || $).ajax({
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        ...options
    });
}

