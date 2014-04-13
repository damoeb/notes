'use strict';

console.log('loading proxy-tools');

(function (notes) {

    notes.proxy = {

        init: function () {
            console.log('init proxy');

            var $this = this;

            // --

            var $notesTicker = $('<div class="notes-ticker" style="position: absolute;"><ul class="list-inline" style="margin-bottom: 0"><li style="padding:7px"><i class="fa fa-gear fa-fw"></i> Web Clipping</li><li><a href="/ui/" class="btn btn-default">Cancel</a></li><li><a href="#" class="btn btn-primary notes-finalize-clipping" style="color: #ffffff">Save Selection</a></li><li class="pull-right"><a class="btn btn-default" href="/some/static/info.htm">More Information</a></li></ul></div>');

            $('body')
                .prepend('<div style="height: 48px"></div>')
                .prepend($notesTicker);

            var elementPosition = $notesTicker.offset();

            $(window).scroll(function () {
                if ($(window).scrollTop() > elementPosition.top) {
                    $('.notes-ticker').css('position', 'fixed').css('top', '0');
                } else {
                    $('.notes-ticker').css('position', 'absolute');
                }
            });

            $notesTicker.find('.notes-finalize-clipping').click(function () {
                $this.finalizeClipping();
            });
        },
        getURLParameter: function (name) {
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || null;
        },
        finalizeClipping: function () {

            var $this = this;

            var title = $('title').text();
            var url = this.getURLParameter('url');

            var text = this.getSelectionHtml() + '<p>--</p><p>Seen on <a href="' + url + '">' + url + '</a></p>';

            var document = {
                title: title,
                text: text,
                kind: 'BOOKMARK',
                source: url
            };

            console.log('Finalize selection');

            var onError = function () {
                console.error('Cannot save document');
            };

            // save doc
            $.ajax({
                type: 'POST',
                url: '/notes/rest/document/',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(document),
                cache: false,
                processData: false,
                success: function (data) {
                    if (data.statusCode == 0) {
                        location.href = '/ui/#doc:' + data.result.id;
                    } else {
                        console.log(data);
                        onError();
                    }
                },
                error: function () {
                    onError();
                }
            });
//            });
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
        }
    };

    $(function () {
        notes.proxy.init();
    });


})({});