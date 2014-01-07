(function (notes) {

    notes.util = new function () {

        this.jsonCall = function (_type, url, urlReplacements, jsonObject, onSuccess, onError) {
            var $this = this;
            try {

                //noinspection JSUnusedLocalSymbols
                $.ajax({
                    type: _type,
                    url: $this.replaceInUrl(url, urlReplacements),
                    dataType: 'json',
                    contentType: 'application/json',
                    data: jsonObject,
                    cache: false,
                    processData: false,
                    success: function (data) {
                        switch (data.statusCode) {
                            case 0:
                                if (typeof(onSuccess) === 'function') {
                                    onSuccess.call(this, data.result);
                                }
                                break;
                            default:
                                alert('Error: ' + data.errorMessage);
                                if (typeof(onError) === 'function') {
                                    onError.call(this, data);
                                }
                                break;
                        }
                    },
                    error: function (data) {
                        if (typeof(onError) === 'function') {
                            onError.call(this);
                        }
                    }
                });
            } catch (err) {
                if (typeof(onError) === 'function') {
                    onError.call(this, err);
                }
            }
        };

        this.replaceInUrl = function (url, urlReplacements) {
            if (urlReplacements == null)
                return url;
            for (var key in urlReplacements) {
                var value = urlReplacements[key];
                if (value == null || value == "") {
                    url = url.replace(key, " ");
                }
                else {
                    //noinspection JSPotentiallyInvalidConstructorUsages
                    url = url.replace(key, encodeURI(value));
                }
            }
            return url;
        };

        this.strToDate = function (dateStr) {
            // 2012-08-23 22:27:15
            var arr = dateStr.replace(/:/g, ' ').replace(/-/g, ' ').split(' ');
            return new Date(arr[0], arr[1] - 1, arr[2], arr[3], arr[4], arr[5]);
        };

        this.sortArray = function (array) {
            var n = array.length;

            for (var i = 0; i < n; i++) {
                for (var j = n - 1; j > i; j--) {
                    if (array[j - 1] > array[j]) {
                        var o = array[j - 1];
                        array[j - 1] = array[j];
                        array[j] = o;
                    }
                }
            }
        };

        this.sortJSONArrayASC = function (array, key) {
            var n = array.length;

            for (var i = 0; i < n; i++) {
                for (var j = n - 1; j > i; j--) {
                    if (array[j - 1][key] > array[j][key]) {
                        var o = array[j - 1];
                        array[j - 1] = array[j];
                        array[j] = o;
                    }
                }
            }
        };

        this.sortJSONArrayDESC = function (array, key) {
            var n = array.length;

            for (var i = 0; i < n; i++) {
                for (var j = n - 1; j > i; j--) {
                    if (array[j - 1][key] < array[j][key]) {
                        var o = array[j - 1];
                        array[j - 1] = array[j];
                        array[j] = o;
                    }
                }
            }
        };

        this.equal = function (x, y) {
            for (var p in y) {
                if (typeof(y[p]) !== typeof(x[p])) return false;
                if ((y[p] === null) !== (x[p] === null)) return false;
                switch (typeof(y[p])) {
                    case 'undefined':
                        if (typeof(x[p]) != 'undefined') return false;
                        break;
                    case 'object':
                        if (y[p] !== null && x[p] !== null && (y[p].constructor.toString() !== x[p].constructor.toString() || !y[p].equals(x[p]))) return false;
                        break;
                    case 'function':
                        if (p != 'equals' && y[p].toString() != x[p].toString()) return false;
                        break;
                    default:
                        if (y[p] !== x[p]) return false;
                }
            }
            return true;
        };

        this.formatDate = function (date) {

            var millis = date.getTime();
            var now = new Date().getTime();

            var offset_day = 1000 * 60 * 60 * 24;

            var aDayAgoOrLess = millis + offset_day > now;
            if (aDayAgoOrLess) {
                return $.timeago(date);
            }

            var offset_week = offset_day * 7;
            var aWeekAgoOrLess = millis + offset_week > now;
            var hours = date.getHours() + ':' + (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
            if (aWeekAgoOrLess) {
                return $.timeago(date) + ', ' + hours;
            }

            return $.datepicker.formatDate('DD d.m.yy', date) + ' ' + hours;
        };

        this.formatBytesNum = function (bytes) {
            if (bytes < 0) {
                return 0 + 'b';
            }
            var oneKb = 1024;
            var oneMb = oneKb * oneKb;
            if (bytes < oneKb) {
                return bytes + ' bytes';
            }
            if (bytes < oneMb) {
                return parseInt(bytes / oneKb) + ' KB';
            }
            return parseInt(bytes / oneMb) + ' MB';
        };

        this.toTimestamp = function (str) {
            var d = str.match(/\d+/g); // extract date parts
            return new Date(d[0], d[1] - 1, d[2], d[3], d[4], d[5]); // build Date object
        }
    }

})(notes);