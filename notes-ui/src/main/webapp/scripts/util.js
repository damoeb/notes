/*global notes:false */

'use strict';

(function (notes) {

    notes.util = {

        jsonCall: function (_type, url, urlReplacements, jsonObject, onSuccess, onError) {
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
                                if (typeof(onError) === 'function') {
                                    onError.call(this, data);
                                } else {
                                    console.error(data.errorMessage);
                                }
                                break;
                        }
                    },
                    error: function () {
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
        },

        replaceInUrl: function (url, urlReplacements) {
            if (urlReplacements === null) {
                return url;
            }
            for (var key in urlReplacements) {
                var value = urlReplacements[key];
                if (value === null || value === '') {
                    url = url.replace(key, ' ');
                }
                else {
                    //noinspection JSPotentiallyInvalidConstructorUsages
                    url = url.replace(key, encodeURI(value));
                }
            }
            return url;
        },

        strToDate: function (dateStr) {
            // 2012-08-23 22:27:15
            var arr = dateStr.replace(/:/g, ' ').replace(/-/g, ' ').split(' ');
            return new Date(arr[0], arr[1] - 1, arr[2], arr[3], arr[4], arr[5]);
        },

        sortArray: function (array) {
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
        },

        sortJSONArrayASC: function (array, key) {
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
        },

        sortJSONArrayDESC: function (array, key) {
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
        },

        equal: function (x, y) {
            for (var p in y) {
                if (typeof(y[p]) !== typeof(x[p])) {
                    return false;
                }
                if ((y[p] === null) !== (x[p] === null)) {
                    return false;
                }
                switch (typeof(y[p])) {
                    case 'undefined':
                        if (typeof(x[p]) !== 'undefined') {
                            return false;
                        }
                        break;
                    case 'object':
                        if (y[p] !== null && x[p] !== null && (y[p].constructor.toString() !== x[p].constructor.toString() || !notes.util.equal(y[p], x[p]))) {
                            return false;
                        }
                        break;
                    case 'function':
                        if (p !== 'equals' && y[p].toString() !== x[p].toString()) {
                            return false;
                        }
                        break;
                    default:
                        if (y[p] !== x[p]) {
                            return false;
                        }
                }
            }
            return true;
        },

        formatDate: function (date) {

            var millis = date.getTime();
            var now = new Date().getTime();

            var offsetDay = 1000 * 60 * 60 * 24;

            var aDayAgoOrLess = millis + offsetDay > now;
            if (aDayAgoOrLess) {
                return $.timeago(date);
            }

            var offsetWeek = offsetDay * 7;
            var aWeekAgoOrLess = millis + offsetWeek > now;
            var hours = date.getHours() + ':' + (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
            if (aWeekAgoOrLess) {
                return $.timeago(date) + ', ' + hours;
            }

            return $.datepicker.formatDate('DD d.m.yy', date) + ' ' + hours;
        },

        formatBytesNum: function (bytes) {
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
        },

        toTimestamp: function (str) {
            var d = str.match(/\d+/g); // extract date parts
            return new Date(d[0], d[1] - 1, d[2], d[3], d[4], d[5]); // build Date object
        },

        getThumbnailByType: function (kind) {
            switch (kind.toLowerCase()) {
                case 'pdf':
                    return 'images/pdf.png';
                case 'bookmark':
                    return 'images/url.png';
                default:
                case 'text':
                    return 'images/text.png';
            }
        }
    };
})(notes);