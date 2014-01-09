/*global notes:false */
/*global _:false */

'use strict';

$.widget('notes.documentList', {

    options: {
        folderId: null
    },

    _init: function () {
        var $this = this;

        this.template = _.template($('#document-in-list').html());

        var folderId = $this.options.folderId;
        if (folderId && parseInt(folderId) > 0) {
            $this._fetch(folderId);
        }
    },
    _fetch: function (folderId) {
        var $this = this;

        var sources = [
            {
                url: '/notes/rest/folder/${folderId}/documents',
                params: {'${folderId}': folderId},
                type: 'native'
            },
            {
                url: '/notes/rest/folder/${folderId}/related-documents?offset=${offset}&count=${count}',
                params: {
                    '${folderId}': folderId,
                    '${offset}': '0',
                    '${count}': 100
                }
            }
        ];

        $this.element.empty();

        $('#breadcrumbs').breadcrumbs('generate', folderId);

        var $native = $('<div/>', {text: 'Folder is empty'}).appendTo($this.element);
        var $descendants = $('<div/>', {style: 'border-top: 3px solid #efefef; padding-top:5px'}).appendTo($this.element);

        $.each(sources, function (index, source) {

            var $target;
            if (source.type === 'native') {
                $target = $native;
            } else {
                $target = $descendants;
            }

            notes.util.jsonCall('GET', source.url, source.params, null, function (documents) {

                if (documents.length > 0) {
                    $target.empty();
                }

                $.each(documents, function (id, doc) {
                    $this._render(new notes.model.Document(doc)).appendTo($target);
                });
            });
        });
    },

    _getThumbnail: function (kind) {
        switch (kind.toLowerCase()) {
            case 'pdf':
                return 'images/pdf.png';
            case 'bookmark':
                return 'images/url.png';
            default:
            case 'text':
                return 'images/text.png';
        }
    },

    _render: function (model) {

        var values = {
            id: model.get('id'),
            title: model.get('title'),
            text: model.get('outline'),
            kind: model.get('kind'),
            views: model.get('views'),
            privacy: model.get('privacy'),
            star: model.get('star'),
            reminder: model.get('reminder'),
            thumbnail: this._getThumbnail(model.get('kind')),
            date: notes.util.formatDate(new Date(model.get('modified')))
        };

        return $(this.template(values)).click(function () {

            var $tmpl = $(this);

            // highlight
            $tmpl.addClass('active');

            // call editor
            $('#editors').editors('edit', values.id, function () {
                $tmpl.removeClass('active');
            });
        }).draggable({
                cursor: 'move',
                cursorAt: { top: 5, left: -5 },
                helper: function () {
                    return $('<i class="fa fa-file-o fa-lg"></i>');
                },
                opacity: 0.6
            });
    },

    updateDocument: function (model) {
        var $this = this;
        var $element = $this.element.find('.doc-id-' + model.get('id'));

        $element.replaceWith($this._render(model));
    },

    deleteDocument: function (model) {
        var $this = this;
        $this.element.find('.doc-id-' + model.get('id')).remove();
    }

});