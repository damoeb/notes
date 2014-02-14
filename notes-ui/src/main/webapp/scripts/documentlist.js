/*global notes:false */
/*global _:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.documentList', {

    _init: function () {
        this.template = _.template($('#document-in-folder-view').html());
    },

    refresh: function (folderId) {

        if (typeof folderId === 'undefined' || folderId === null) {
            throw 'folderId is null';
        }
        this._fetch(folderId);
    },

    _fetch: function (folderId) {
        var $this = this;

        if (!(folderId && parseInt(folderId) > 0)) {
            return;
        }

        var url = REST_SERVICE + '/folder/${folderId}/documents';
        var params = {'${folderId}': folderId};

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
                $($this.template(doc).trim()).appendTo($target);
            });
        });
    },

    _render: function (model) {

        return $(this.template(model.attributes).trim()).click(function () {

            var $tmpl = $(this);

            // highlight
            $tmpl.addClass('active');

            // call editor
            $('#editors').editors('editDocument', model.get('id'));

        })
    }

});
