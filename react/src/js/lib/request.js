export default function(options = {}) {
    return (global.$ || $).ajax({
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        ...options
    });
}

