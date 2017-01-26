export function camelCase(str) {
    let words = str.split('-');

    return words[0] + words.slice(1).map((str) => {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }).join();
}

export function plural(count, values) {
    if(count === 1) {
        return values[0];
    } else {
        return values[1];
    }
}

export function getError() {
    return {};
}

export function stopPropagation(e) {
    e.stopPropagation();
    e.nativeEvent.stopImmediatePropagation();
}