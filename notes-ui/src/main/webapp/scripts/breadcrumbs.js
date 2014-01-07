$.widget('notes.breadcrumbs', {

    _init: function () {
        this.element.attr('style', 'padding:5px; margin-bottom:5px;');
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
        var $item = $('<a/>', {href: '#'}).append(
                $('<span/>', {text: $folder.getModel().get('name') })
            ).click(function () {
                notes.dialog.folder.settings($folder.getModel());
            });

        if (!isLeaf) {
            $item.append(
                '<i class="fa fa-angle-right" style="margin-left:4px; margin-right:4px"></i>'
            )
        }

        return $item;
    }

});