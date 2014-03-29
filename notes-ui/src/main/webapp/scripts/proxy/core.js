'use strict';

console.log('loading proxy-tools');

(function (notes) {

    notes.proxy = {
        init: function () {
            console.log('init proxy');

            var $this = this;

            $('body')
                .prepend('<div style="height: 48px"></div>')
                .prepend('<div class="notes-ticker" style="position: absolute;"><ul class="list-inline"><li style="padding:7px"><i class="fa fa-gear fa-fw"></i> Web Clipping</li><li><a href="/ui/" class="btn btn-primary">Done</a></li><li class="pull-right"><a class="btn btn-default" href="/some/static/info.htm">More Information</a></li></ul></div>');

            var elementPosition = $('.notes-ticker').offset();

            $(window).scroll(function () {
                if ($(window).scrollTop() > elementPosition.top) {
                    $('.notes-ticker').css('position', 'fixed').css('top', '0');
                } else {
                    $('.notes-ticker').css('position', 'absolute');
                }
            });

            window.addEventListener("mouseup", function (event) {
                console.log('mouseup');
                var selection = notes.proxy.getSelection();
                var hasSelection = selection !== false && selection.toString().trim().length > 0;
                if (hasSelection) {
                    console.log('show snippet menu');
                    var $targetElement = $(notes.proxy.getSelection().anchorNode.parentElement);

                    $('#notes-snippet-menu').show().position({
                        of: $targetElement,
                        my: 'left bottom',
                        at: 'left top'
                    });

                } else {
                    $('#notes-snippet-menu').hide();
                }

            });

            var $saveSnippet = $('<button class="btn btn-primary"><i class="fa fa-save fa-fw"></i> Save snippet</button>').click(function () {
                $this.saveSnippet();
            });
            var $snippetMenu = $('<div id="notes-snippet-menu" class="notes-snippet"></div>').append($saveSnippet);
            $('body').append($snippetMenu);

            $('img').click(function () {
                $(this).toggleClass('notes-taken');
            }).parent('a').attr('href', '#');
        },
        saveSnippet: function () {
            var snippetMarker = $('#notes-snippet-menu').hide().clone().show().removeClass('notes-snippet-menu').removeAttr('id');

            snippetMarker.find('button').html('<i class="fa fa-trash fa-fw"></i> Trash snippet').attr('title', this.getSelection()).click(function () {
                snippetMarker.remove();
            });

//            snippetMarker.append(this.getSelection());

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