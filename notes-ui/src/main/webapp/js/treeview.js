$.widget("notes.treeItem", {
    options: {
        model: null
    },
    _create: function () {
        this._reset();
    },
    _reset: function () {
        this.element.empty();
        this.container = {};
    },
    _init: function () {
        var $this = this;
        $this._reset();

        var target = $this.element.addClass('item');

        // -- Render

        var model = $this.options.model;

        var children = $('<div/>', {class: 'children'});
        ;

        $this.container.children = children;

        $this._createToggleButton()
            .appendTo(target);

        var icon = $('<div/>', {class: 'icon'})
            .appendTo(target);
        var item = $('<div/>', {class: 'name', text: model.name})
            .appendTo(target);
        children
            .appendTo(target);

        if (!model.leaf) {

            $.each(model.children, function (index, folderData) {

                $('<div/>')
                    .appendTo(children)
                    .treeItem({model: folderData});
            });
        }

        // -- Events

        item.click(function () {
            $this.loadDocuments()
        });
    },

    _createToggleButton: function () {
        var $this = this;

        var toggle = $('<div/>', {class: 'toggle'});

        var fApplyExpanded = function () {
            if ($this.options.model.expanded) {
                toggle.text('+');
                $this.showChildren();
            } else {
                toggle.text('-');
                $this.hideChildren();
            }
        };
        fApplyExpanded();

        return toggle.click(function () {
            $this.options.model.expanded = !$this.options.model.expanded;
            // todo sync model
            fApplyExpanded();
        });
    },
    hideChildren: function () {
        this.container.children.removeClass('hidden');
    },
    showChildren: function () {
        this.container.children.addClass('hidden');
    },
    loadDocuments: function () {
        var $this = this;
        // todo sync model: database current folder
        $('#document-list-view').documentListView({
            folderId: $this.options.model.id
        });
    }
});

$.widget("notes.treeView", {

    options: {
        databaseId: 0
    },

    _init: function () {
        this.container = {};
    },

    _create: function () {

        var $this = this;

        var Node = Backbone.Model.extend({
            defaults: {
                name: 'blank'
            },
            url: '/notes/rest/folder'
        });

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (database) {

            $.each(database.folders, function (index, folderData) {

                $('<div/>')
                    .appendTo($this.element)
                    .treeItem({
                        model: folderData
                    });
            });

        });
    }

});
