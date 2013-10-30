$.widget("notes.treeItem", {
    options: {
        model: null
    },
    _init: function () {
        this._reset();
    },
    _reset: function () {
        this.element.empty();
        this.container = {};
    },
    _create: function () {
        var $this = this;
        $this._reset();

        var model = $this.options.model;

        var item = $('<div class="icon"></div><div class="name">' + model.name + '</div>');

        $this.element.empty().addClass('item').append(item);

        $this.container.children = $('<div/>', {class: 'children'});

        if (!model.leaf) {
            item.prepend($this._createToggleButton());
        }

        model.children.each(function (index, folderData) {

            var item = $('<div/>').treeItem({
                model: folderData
            });
            $this.container.children.append(item);
        });
        $this.element.html(item);
    },

    _createToggleButton: function () {
        var $this = this;

        var toggle = $('<div class="toggle"></div>');

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
        this.children.removeClass('hidden');
    },
    showChildren: function () {
        this.children.addClass('hidden');
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

//        $this.element.append(this.container.children);

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (database) {

            $.each(database.folders, function (index, folderData) {

                var item = $('<div/>').treeItem({
                    model: folderData
                });
                $this.element.append(item);
            });

        });
    }

});
