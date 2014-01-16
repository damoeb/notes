/*global notes:false */
/*global noty:false */

'use strict';

$.widget('notes.breadcrumbs', {

    _init: function () {
        this.generate();
    },

    generate: function (folderId) {
        var $this = this;

        var $breadcrumbs = $('<ol/>', {class: 'breadcrumb'});

        $this.element.empty().append($breadcrumbs);

        if (typeof folderId !== 'undefined') {
            var $folder = notes.app.get$Folder(folderId);

            var templateFolder = _.template($('#breadcrumb-folder').html());

            while ($folder) {

                $breadcrumbs.prepend(
                    $this._getBreadcrumbItem($folder, templateFolder)
                );

                $folder = $folder.getParent();
            }
        }

        // database crumb
        $breadcrumbs.prepend(
            _.template($('#breadcrumb-database').html())().trim()
        );


        return $breadcrumbs;
    },

    _getBreadcrumbItem: function ($folder, template) {
        var $breadcrumb = $(template($folder.getModel().attributes).trim());
        $breadcrumb.find('.action-create').click(function () {
            notes.dialog.folder.newFolder($folder.getModel());
        });
        $breadcrumb.find('.action-delete').click(function () {
            // todo: implement
        });
        $breadcrumb.find('.action-rename').click(function () {
            notes.dialog.folder.rename($folder.getModel());
        });

        return $breadcrumb;
    }

});