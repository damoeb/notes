'use strict';

console.log('loading proxy-tools');

(function (notes) {

    notes.proxy = {
        init: function () {
            console.log('init proxy');

//            $('body').append('<div id="notes-overlay" style="z-index: 1000; border: none; margin: 0px; padding: 0px; width: 100%; height: 100%; top: 0px; left: 0px; background-color: rgb(0, 0, 0); opacity: 0.6; cursor: wait; position: fixed; color: #ffffff">Please wait..</div>');

            $('body').prepend('<div class="notes-ticker" style="position: static">Web Clipping</a> <button class="notes-button">Done</button> This website is manipulated. <a href="/static/info.htm" style="color: white; text-decoration: underline;">More Information</a></div></div>');

            var elementPosition = $('.notes-ticker').offset();

            $(window).scroll(function () {
                if ($(window).scrollTop() > elementPosition.top) {
                    $('.notes-ticker').css('position', 'fixed').css('top', '0');
                } else {
                    $('.notes-ticker').css('position', 'static');
                }
            });


            var $this = this;

            var $saveSnippet = $('<a href="#" style="color: white;">Save snippet...</a>').click(function () {
                $this.saveSnippet()
            });
            var $snippetMenu = $('<div class="notes-snippet-menu notes-snippet"></div>').append($saveSnippet);
            $('body').append($snippetMenu);

            $('img').click(function () {
                $(this).toggleClass('notes-taken');
            }).parent('a').attr('href', '#');

            setInterval(function () {
                var selection = notes.proxy.getSelection();
                var hasSelection = selection !== false && selection.toString().trim().length > 0;
                if (hasSelection) {
                    console.log('show snippet menu');
                    var $targetElement = $(notes.proxy.getSelection().anchorNode.parentElement);
//                    var anchorOffset = $targetElement.offset();
//                    var focusOffset = $(notes.proxy.getSelection().focusNode.parentElement).offset();
//
//                    var left = Math.min(anchorOffset.left, focusOffset.left);
//                    var top = Math.max(0, Math.min(anchorOffset.top, focusOffset.top) - 35);
//
//                    $('.notes-snippet-menu').show().css({'top': top, left: left})
                    $('.notes-snippet-menu').show().position({
                        of: $targetElement,
                        my: 'left bottom',
                        at: 'left top'
                    });

                }
            }, 300);
        },
        saveSnippet: function () {
            var snippetMarker = $('.notes-snippet-menu').clone().removeClass('notes-snippet-menu').empty();

            snippetMarker.append(this.getSelectionHtml());

            $('body').append(snippetMarker);
        },
        saveQuote: function () {

            // todo add site url to text
            // todo look for quotes on page
            // todo show summary, and ask if one document per snippet
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


})({});