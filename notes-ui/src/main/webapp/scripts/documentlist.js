/*global notes:false */
/*global _:false */
/*global REST_SERVICE:false */

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

        var source =
        {
            url: REST_SERVICE + '/folder/${folderId}/documents',
            params: {'${folderId}': folderId}
        };

        var $target = $this.element.empty().text('Folder is empty');

        $('#breadcrumbs').breadcrumbs('generate', folderId);

        notes.util.jsonCall('GET', source.url, source.params, null, function (documents) {

            if (documents.length > 0) {
                $target.empty();
            }

            $.each(documents, function (id, doc) {
                $this._render(new notes.model.Document(doc)).appendTo($target);
            });
        });
    },

    _render: function (model) {

        return $(this.template(model.attributes)).click(function () {

            var $tmpl = $(this);

            // highlight
            $tmpl.addClass('active');

            // call editor
            $('#editors').editors('edit', model.get('id'), function () {
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
