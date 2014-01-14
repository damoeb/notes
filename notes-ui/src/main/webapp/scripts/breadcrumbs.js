/*global notes:false */
/*global noty:false */

'use strict';

$.widget('notes.breadcrumbs', {

    _init: function () {
        this.generate();
    },

    generate: function (folderId) {
        var $this = this;

        var $crumbs = $('<div/>');

        $this.element.empty().append($crumbs);

        if (typeof folderId !== 'undefined') {
            var $folder = notes.app.get$Folder(folderId);

            var isLeaf = true;

            while ($folder) {

                $crumbs.prepend(
                    $this._getBreadcrumbItem($folder, isLeaf)
                );

                isLeaf = false;
                $folder = $folder.getParent();
            }
        }

        // database crumb
        $crumbs.prepend(
            $('<a/>', {href: '#'}).append(
                    $('<span/>', {text: 'Database' })
                ).append(
                    '<i class="fa fa-angle-right" style="margin-left:4px; margin-right:4px"></i>'
                ).click(function () {
                    notes.dialog.database.settings();
                })
        );

        return $crumbs;
    },

    _getBreadcrumbItem: function ($folder, isLeaf) {
        var $menuLayer = $('<div/>', {class: 'menu', id: 'menu-' + $folder.getModel().get('id')}).hide();
        var $menu = $('<ul/>').append(
                $('<li/>', {text: 'New folder'}).click(function () {
                    notes.dialog.folder.newFolder($folder.getModel());
                })
            ).append(
                $('<li/>', {text: 'Rename'}).click(function () {
                    notes.dialog.folder.rename($folder.getModel());
                })
            ).append(
                $('<li/>', {text: 'Delete'}).click(function () {
                    noty('Not implemented');
                })
            );
        $menuLayer.append($menu);

        var $link = $('<a/>', {href: '#', text: $folder.getModel().get('name')}).click(function () {
            notes.dialog.folder.settings($folder.getModel());
        });

        var $item = $('<div/>');

        $item.append($link);

        if (!isLeaf) {
            $item.append(
                '<i class="fa fa-angle-right" style="margin-left:4px; margin-right:4px"></i>'
            );
        }

        $item.append($menuLayer);

        return $item;
    }

});