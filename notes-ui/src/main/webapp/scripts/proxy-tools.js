console.log('loading proxy-tools');

'use strict';

var notes = {
    loadJs: function (filename) {
        var fileref = document.createElement('script');
        fileref.setAttribute("type", "text/javascript");
        fileref.setAttribute("src", filename);
    },
    loadCss: function (filename) {
        var fileref = document.createElement("link");
        fileref.setAttribute("rel", "stylesheet");
        fileref.setAttribute("type", "text/css");
        fileref.setAttribute("href", filename);
    }
};

// load query
if (typeof window.jQuery === 'undefined') {
    console.log('load jquery');
//    window.jQuery = function() {return "jQuery"};
    notes.loadJs("/ui/bower_components/jquery/jquery.js");
}

(function (notes, $) {

    notes.proxy = {
        init: function () {
            console.log('init proxy');

            $('body').append('<div class="notes-snippet-menu" style="position: absolute; width: 200px; background-color: black; color:white; padding: 10px; z-index: 300;"><a href="#" onclick="notes.proxy.saveQuote()" style="color: white;">Quote this!</a></div>');

            setInterval(function () {
                var selection = notes.proxy.getSelection();
                if (selection !== false && selection.toString().trim().length > 0) {
//                    var selectedString = selection.toString().trim();

                    var refPosition = $(notes.proxy.getSelection().anchorNode.parentElement).position();
                    $('.notes-snippet-menu').css({'top': refPosition.top + 20, left: refPosition.left})
                }
            }, 300);
        },
        saveQuote: function () {
            // todo add site url to text
            var document = {
                title: $('title').text(),
                text: this.getSelectionHtml(),
                kind: 'BOOKMARK'
            };

            // save doc
            $.ajax({
                type: 'POST',
                url: '/notes/rest/document/text/',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(document),
                cache: false,
                processData: false,
                success: function (data) {
                    alert(data.statusCode);
                    // redirect
                    console.log(data);
                    location.href = '/ui/#doc:' + data.result.id;
                },
                error: function () {

                }
            });
        },
        getSelectionHtml: function () {
            var html = "";
            if (typeof window.getSelection != "undefined") {
                var sel = window.getSelection();
                if (sel.rangeCount) {
                    var container = document.createElement("div");
                    for (var i = 0, len = sel.rangeCount; i < len; ++i) {
                        container.appendChild(sel.getRangeAt(i).cloneContents());
                    }
                    html = container.innerHTML;
                }
            } else if (typeof document.selection != "undefined") {
                if (document.selection.type == "Text") {
                    html = document.selection.createRange().htmlText;
                }
            }
            return html;
        },
        getSelection: function () {
            var text = "";
            if (window.getSelection
                && window.getSelection().toString()
                && $(window.getSelection()).attr('type') != "Caret") {
                text = window.getSelection();
                return text;
            }
            else if (document.getSelection
                && document.getSelection().toString()
                && $(document.getSelection()).attr('type') != "Caret") {
                text = document.getSelection();
                return text;
            }
            else {
                var selection = document.selection && document.selection.createRange();

                if (!(typeof selection === "undefined")
                    && selection.text
                    && selection.text.toString()) {
                    text = selection.text;
                    return text;
                }
            }

            return false;
        }
    };

    $(function () {
        notes.proxy.init();
    });


})(notes, jQuery);