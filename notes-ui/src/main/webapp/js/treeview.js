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

        $this.container.children = children;

        $this._createToggleButton()
            .appendTo(target);

        var icon = $('<div/>', {class: 'icon ui-icon ui-icon-folder-collapsed' })
            .appendTo(target);
        var label = $('<div/>', {class: 'name', text: model.name + '(' + model.documentCount + ')'})
            .appendTo(target);


        $this._newMenu(target, model);

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
        label.click(function () {
            $this.loadDocuments()
        });
    },

    _newMenu: function (target, model) {

        var $this = this;

        var button = $('<div/>', {class: 'edit ui-icon ui-icon-gear'})
            .appendTo(target);

        var menuwrapper = $('<div/>', {class: 'tree-menu'})
            .hide()
            .appendTo(target);

        var menu = $('<ul/>')
            .append($this._newAddFolderButton(model))
            .append($this._newDelFolderButton(model))
            .appendTo(menuwrapper)
            .menu();

        button.click(function () {
            menuwrapper.show().position({
                my: "left top",
                at: "left bottom",
                of: button
            });
            $(document).one("click", function () {
                menuwrapper.hide();
            });

            return false;
        });

    },

    _newDelFolderButton: function (model) {
        var link = $('<a/>', {text: 'Delete'});
        link.click(function () {
            var Folder = Backbone.Model.extend({
                url: '/notes/rest/folder'
            });
            new Folder(model).destroy();
        })
        return $('<li/>').append(link);
    },

    _newAddFolderButton: function (model) {
        var link = $('<a/>', {text: 'Add'});
        link.click(function () {

            var name = prompt("Gimme a name");

            var Folder = Backbone.Model.extend({
                url: '/notes/rest/folder'
            });
            var folder = new Folder({
                name: name,
                parentId: model.id,
                databaseId: model.databaseId
            });
            folder.save({
                success: function () {
                    $('#tree-view').treeView('reload')
                }
            });

        })
        return $('<li/>').append(link);
    },

    _createToggleButton: function () {
        var $this = this;

        var toggle = $('<div/>', {class: 'toggle ui-icon'});

        var fApplyExpanded = function () {
            if ($this.options.model.expanded) {
                toggle.addClass('ui-icon-triangle-1-e');
                toggle.removeClass('ui-icon-triangle-1-s');
                $this.showChildren();
            } else {
                toggle.removeClass('ui-icon-triangle-1-e');
                toggle.addClass('ui-icon-triangle-1-s');
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

        var folderId = $this.options.model.id;
        $('#document-list-view').documentListView({
            folderId: folderId
        });

        // sync model: active folder in database
        $('#tree-view').treeView('activeFolder', folderId);
    }
});

$.widget("notes.treeView", {

    options: {
        databaseId: 0
    },

    _init: function () {
        var $this = this;
        $this.container = {};
        $this.activeFolderId = null;
    },

    _create: function () {

        var $this = this;

        var databaseId = $this.options.databaseId;
        if (typeof(databaseId) == 'undefined') {
            throw 'databaseId is null'
        }

        var Node = Backbone.Model.extend({
            defaults: {
                name: 'blank'
            },
            url: '/notes/rest/folder'
        });

        $this.reload();
    },

    reload: function () {

        var $this = this;

        $this.element.empty();

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (database) {

            $this.activeFolder(database.activeFolderId);

            $.each(database.folders, function (index, folderData) {

                $('<div/>')
                    .appendTo($this.element)
                    .treeItem({
                        model: folderData
                    });
            });

        });
    },

    activeFolder: function (folderId) {
        if (folderId) {
            console.log('activeFolderId ' + folderId);
            this.activeFolderId = folderId;
        } else {
            return this.activeFolderId;
        }
    }


});
