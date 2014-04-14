/*global notes:false */
/*global _:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.documentList', {

    options: {
        folderId: null,
        templates: {
            trash: $('#document-in-folder-view'),
            default: $('#document-in-trash-view')
        },
        menu: {
            trash: $('#navbar-trash'),
            default: $('#navbar-default')
        }
    },

    _init: function () {
        this._fetch(this.options.folderId, 0, 30);
    },

    _fetch: function (folderId, start, rows) {
        var $this = this;

        if (typeof folderId === 'undefined' || folderId === null) {
            throw 'folderId is null';
        }

        if (!(folderId && parseInt(folderId) > 0)) {
            return;
        }

        // choose template for folder trash/default
        var template;
        if (notes.folders.trashFolderId() == folderId) {
            template = _.template(this.options.templates.trash.html());
            this.options.menu.trash.show();
            this.options.menu.default.hide();
        } else {
            template = _.template(this.options.templates.default.html());
            this.options.menu.default.show();
            this.options.menu.trash.hide();
        }

        // todo support sort field
        var url = REST_SERVICE + '/folder/${folderId}/documents/{start}/{rows}';
        var params = {
            '${folderId}': folderId,
            '{start}': start,
            '{rows}': rows
        };

        var $target = $this.element.empty().text('Folder is empty');

        $('#breadcrumbs').breadcrumbs('generate', folderId);

        notes.util.jsonCall('GET', url, params, null, function (documents) {

            if (documents.length > 0) {
                $target.empty();
            }

            // sort by date
            // todo get sort field
            notes.util.sortJSONArrayDESC(documents, 'modified');

            $.each(documents, function (id, doc) {
                var $rendered = $(template(doc).trim()).appendTo($target);
                $rendered.find('.thumb').draggable({
                    cursor: 'move',
                    cursorAt: { top: 5, left: -5 },
                    helper: function () {
                        return $('<i class="fa fa-file-o fa-lg"></i>');
                    },
                    opacity: 0.6
                }).data('document', doc);
                // todo this data stuff is never read, right?

            });

//            $rendered.append('<div>Page 1</div>');
            // todo if(documents.length > rows) add show-more link

        });
    }

});
