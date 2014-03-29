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

//            window.addEventListener("mouseup", function (event) {
//                console.log('mouseup');
//                var selection = notes.proxy.getSelection();
//                var hasSelection = selection !== false && selection.toString().trim().length > 0;
//                if (hasSelection) {
//                    console.log('show snippet menu');
//                    var $targetElement = $(notes.proxy.getSelection().anchorNode.parentElement);
//
//                    $('#notes-snippet-menu').show().position({
//                        of: $targetElement,
//                        my: 'left bottom',
//                        at: 'left top'
//                    });
//
//                } else {
//                    $('#notes-snippet-menu').hide();
//                }
//
//            });

            $notesTicker.find('.notes-finalize-clipping').click(function () {
                $this.finalizeClipping();
            });

//            var $saveSnippet = $('<button class="btn btn-primary"><i class="fa fa-save fa-fw"></i> Save snippet</button>').click(function () {
//
//                var $snippetMarker = $('#notes-snippet-menu').hide().clone()
//                    .show()
//                    .removeClass('notes-snippet-menu')
//                    .removeClass('notes-snippet-saved')
//                    .removeAttr('id')
//                    .attr('data-selection', $this.getSelectionHtml());
//
//                $snippetMarker.find('button')
//                    .html('<i class="fa fa-trash-o fa-fw"></i> Trash')
//                    .attr('title', $this.getSelection())
//                    .click(function () {
//                        $snippetMarker.remove();
//                    }
//                );
//                $('body').append($snippetMarker);
//            });
//
//            var $snippetMenu = $('<div id="notes-snippet-menu" class="notes-snippet"></div>').append($saveSnippet);
//            $('body').append($snippetMenu);

        },
        getURLParameter: function (name) {
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [, ""])[1].replace(/\+/g, '%20')) || null;
        },
        finalizeClipping: function () {

            var $this = this;

            var title = $('title').text();
            var url = this.getURLParameter('url');

//            $('.notes-snippet-saved').each(function(index, snippet) {
//
//                var text = $(snippet).attr('data-selection') + '<p>Seen on <a href="'+url+'">'+url+'</a></p>';
            var text = this.getSelectionHtml() + '<p>--</p><p>Seen on <a href="' + url + '">' + url + '</a></p>';

            var document = {
                title: title,
                text: text,
                kind: 'BOOKMARK'
            };

            console.log(document);

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


})({});