$.widget("notes.treeItem", {
    options: {
        model: null,
        selectedId: null
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

        var model = $this.options.model;
        var selectedId = $this.options.selectedId;

        // -- Render

        var target = $this.element;

        var item = $('<div/>', {class: 'item level-' + model.level})
            .appendTo(target);
        if (model.documentCount == 0) {
            item.addClass('empty');
        }
        item.draggable({containment: '#tree-view', helper: "clone", opacity: 0.5, scope: 'folder'})
            .droppable({hoverClass: 'ui-state-active', scope: 'folder', drop: function (event, ui) {
                var draggable = $(ui.draggable);
                if (draggable.hasClass('item')) {
                    console.log('dropped folder');
                }
                if (draggable.is('tr')) {
                    console.log('dropped document');
                }
                //$(ui.draggable).remove();
            }});


        var childrenWrapper = $('<div/>', {class: 'children'}).appendTo(target);

        $this.container.children = childrenWrapper;

        $this._createExpandChildrenButton(model)
            .appendTo(item);

        var icon = $('<div/>', {class: 'icon ui-icon ui-icon-folder-collapsed' })
            .appendTo(item);

        var label = $('<div/>', {class: 'name', text: model.name })
            .appendTo(item);
        var docCount = $('<div/>', {class: 'doc-count', text: '(' + model.documentCount + ')'})
            .appendTo(item);

        var Folder = Backbone.Model.extend({
            url: '/notes/rest/folder'
        });

        $this._newSettingsMenu(item, model, Folder);

        item.append($('<div/>', {style: 'clear:both'}));

        if (!model.leaf) {

            $.each(model.children, function (index, folderData) {

                $('<div/>')
                    .appendTo(childrenWrapper)
                    .treeItem({
                        model: folderData,
                        selectedId: selectedId
                    });
            });
        }
        $this.item = item;

        // -- Events
        item.click(function () {
            $this.highlight();
        });

        if (selectedId == model.id) {
            $this.highlight(false);
        }
    },

    highlight: function (sync) {
        var $this = this;

        sync = typeof sync !== 'undefined' ? sync : true;

        var folderId = $this.options.model.id;

        $('#tree-view .active').removeClass('active');
        $this.item.addClass('active');
        $this.loadDocuments();

        if (sync) {
            // sync model: selected folder in database
            $('#tree-view').treeView('selectedFolder', folderId);
        }
    },

    _newSettingsMenu: function (target, modelData, modelClass) {

        var $this = this;

        var button = $('<div/>', {class: 'edit ui-icon ui-icon-gear'})
            .appendTo(target);

        // todo should be a dialog
        /*
         var menuwrapper = $('<div/>', {class: 'tree-menu'})
         .hide()
         .appendTo(target);

         var menu = $('<ul/>')
         .append($this._newAddFolderButton(modelData, modelClass))
         .append($this._newRenameFolderButton(modelData, modelClass))
         .append($this._newShareFolderButton(modelData, modelClass))
         .append($this._newDelFolderButton(modelData, modelClass))
         .appendTo(menuwrapper)
         .menu();
         */

        button.click(function () {

            var blockUi = $('#block-ui').show();

            var menuwrapper = $(_.template($('#tmpl-folder-menu').html(), {}))
                .appendTo(target)
                .center();

            blockUi.one("click", function () {
                menuwrapper.remove();
                $('#block-ui').hide();
            });

            return false;
        });

    },

    _newDelFolderButton: function (model, modelClass) {
        var link = $('<a/>', {text: 'Delete'});
        link.click(function () {
            new modelClass(model).destroy();
        })
        return $('<li/>').append(link);
    },
    _newRenameFolderButton: function (modelData, modelClass) {
        var $this = this;
        var link = $('<a/>', {text: 'Rename'});
        link.click(function () {
            var newName = prompt('new name');

            var folder = new modelClass(modelData);
            folder.set('name', newName);
            folder.save(null, {
                success: function () {
                    $this.item.find('.name').text(newName);
                }
            });

        })
        return $('<li/>').append(link);
    },
    _newShareFolderButton: function (modelData, modelClass) {
        var link = $('<a/>', {text: 'Share'});
        link.click(function () {
            // todo share
        })
        return $('<li/>').append(link);
    },

    _newAddFolderButton: function (modelData, modelClass) {
        var link = $('<a/>', {text: 'Add'});
        link.click(function () {

            var name = prompt("Gimme a name");

            var folder = new modelClass({
                name: name,
                parentId: modelData.id,
                databaseId: modelData.databaseId
            });
            folder.save(null, {
                success: function () {
                    $('#tree-view').treeView('reload')
                }
            });

        })
        return $('<li/>').append(link);
    },

    _createExpandChildrenButton: function (model) {
        var $this = this;

        var toggle = $('<div/>', {class: 'toggle ui-icon'});

        var fApplyExpanded = function () {
            if (model.leaf) {
                toggle.addClass('ui-icon-radio-off');
            } else {
                if (model.expanded) {
                    toggle.addClass('ui-icon-triangle-1-e');
                    toggle.removeClass('ui-icon-triangle-1-s');
                    $this.showChildren();
                } else {
                    toggle.removeClass('ui-icon-triangle-1-e');
                    toggle.addClass('ui-icon-triangle-1-s');
                    $this.hideChildren();
                }
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
    }
});

$.widget("notes.treeView", {

    options: {
        databaseId: 0
    },

    _init: function () {
        var $this = this;
        $this.container = {};
        $this.selectedFolderId = null;
    },

    _create: function () {

        var $this = this;

        var databaseId = $this.options.databaseId;
        if (typeof(databaseId) == 'undefined') {
            throw 'databaseId is null'
        }

        $this.reload();
    },

    reload: function () {

        console.info('reload');

        var $this = this;

        $this.element.empty();

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (database) {

            // -- create database model --------------------------------------------------------------------------------

            var Database = Backbone.Model.extend({
                url: '/notes/rest/database'
            });

            // remove folder information
            var minDatabaseData = {
                id: database.id,
                ownerId: database.ownerId,
                documentCount: database.documentCount,
                name: database.name,
                selectedFolderId: database.selectedFolderId
            };
            $this.model = new Database(minDatabaseData);


            // -- render children --------------------------------------------------------------------------------------

            if (database.folders && database.folders.length > 0) {

                // database has no folder yet
                var selectedFolderId = database.selectedFolderId;
                if (typeof(selectedFolderId) === 'undefined') {
                    selectedFolderId = database.folders[0].id;
                    $this.model.set('selectedFolderId', selectedFolderId);
                }
                $.each(database.folders, function (index, folderData) {

                    var item = $('<div/>')
                        .appendTo($this.element)
                        .treeItem({
                            model: folderData,
                            selectedId: selectedFolderId
                        });

                    if (folderData.id == selectedFolderId) {
                        item.treeItem('highlight');
                    }

                });
            }
        });
    },

    selectedFolder: function (folderId) {
        var $this = this;
        if (folderId) {
            $this.model.set('selectedFolderId', folderId);
            console.log('selectedFolderId ' + $this.model.get('selectedFolderId'));
            $this.model.save();

        } else {
            //return this.selectedFolderId;
            return $this.model.get('selectedFolderId');
        }
    }


});
