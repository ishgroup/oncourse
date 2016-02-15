window.onload = function () {
    var input = document.getElementById('cardnumber');
    var parts = [];
    for (var i = 0, len = input.value.length + 1; i < len; i += 4) {
        parts.push(input.value.substring(i, i + 4));
    }
    input.value = input.lastValue = parts.join(' ').substring(0, 19);
    input.blur();

    input.onkeyup = function (e) {
        if (this.value == this.lastValue) return;
        var caretPosition = this.selectionStart;
        var sanitizedValue = this.value.replace(/[^0-9]/gi, '');
        var parts = [];

        for (var i = 0, len = sanitizedValue.length + 1; i < len; i += 4) {
            parts.push(sanitizedValue.substring(i, i + 4));
        }

        for (var i = caretPosition - 1; i >= 0; i--) {
            var c = this.value[i];
            if (c < '0' || c > '9') {
                caretPosition--;
            }
        }
        caretPosition += Math.floor(caretPosition / 4);

        this.value = this.lastValue = parts.join(' ').substring(0, 19);
        this.selectionStart = this.selectionEnd = caretPosition;
    }
};
