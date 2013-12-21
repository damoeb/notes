$.widget("notes.breadcrumbs", {

    _init: function () {
        this.element.attr('style', 'padding:10px; margin-bottom:10px; background-color:#efefef');
        this.generate();
    },

    generate: function (folderId) {
        var $this = this;

        var $crumbs = $('<div/>');

        $this.element.empty().append($crumbs);

        if (typeof folderId !== 'undefined') {
            var $folder = notes.app.get$Folder(folderId);
            while ($folder) {

                $crumbs.prepend(
                    $this._getBreadcrumbItem($folder)
                );

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

    _getBreadcrumbItem: function ($folder) {
        return $('<a/>', {href: '#'}).append(
                $('<span/>', {text: $folder.getModel().get('name') })
            ).append(
                '<i class="fa fa-angle-right" style="margin-left:4px; margin-right:4px"></i>'
            ).click(function () {
                notes.dialog.folder.settings($folder.getModel());
            })
    }

});