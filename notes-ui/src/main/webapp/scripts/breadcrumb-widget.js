/*global notes:false */
/*global _:false */

'use strict';

$.widget('notes.breadcrumbs', {

    _init: function () {
        this.generate();
    },

    generate: function (folderId) {
        var $this = this;

        var $breadcrumbs = $('<ol/>');

        $this.element.empty().append($breadcrumbs);

        var model = notes.folders.getFolderModel(folderId);
        if (typeof model !== 'undefined') {

            var templateFolder = _.template($('#breadcrumb-folder').html());

            var $folder = $('#databases .folder-' + model.get('id')).parent().data('notes-folder');

            while ($folder) {

//                <i class="fa fa-angle-right"></i>
                var $rendered = $(templateFolder($folder.getModel().attributes).trim());

                $rendered.find('.action-folder-delete').click(function () {
                    // todo: implement
                });
                $rendered.find('.action-folder-rename').click(function () {
                    notes.dialog.folder.rename($folder.getModel());
                });

                $breadcrumbs.prepend(
                    $rendered
                );

                $folder = $folder.getParent();
            }
        }

        // database crumb
        $breadcrumbs.prepend(
            _.template($('#breadcrumb-database').html())().trim()
        );

        return $breadcrumbs;
    }

});