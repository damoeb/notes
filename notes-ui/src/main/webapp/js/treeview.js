$.widget("notes.treeItem", {
    options: {
        model: null,
        selectedId: null,
        refresh: null
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

        var target = $this.element;

        // -- Structure ------------------------------------------------------------------------------------------------

        var item = $('<div/>', {class: 'item level-' + model.get('level')});
        var childrenWrapper = $('<div/>', {class: 'children'});

        target.append(item).append(childrenWrapper);

        // -- Render Item ----------------------------------------------------------------------------------------------

        // my change
        model.change(function () {
            $this.refresh();
        });

        $this.models = [];

        var documentCount = model.get('documentCount');

        if (!model.get('leaf')) {

            var children = model.get('children');
            for (var i = 0; i < children.length; i++) {

                var childModel = new notes.model.folder(children[i]);
                $this.models.push(childModel);

                documentCount += childModel.get('documentCount');

                $('<div/>')
                    .appendTo(childrenWrapper)
                    .treeItem({
                        model: childModel,
                        selectedId: selectedId,
                        refresh: function () {
                            $this.refresh();
                        }
                    });
            }
        }

        var elName = $('<div/>', {class: 'name', text: model.get('name') });
        var elDocCount = $('<div/>', {class: 'doc-count', text: '(' + documentCount + ')'});

        $this.elName = elName;
        $this.elDocCount = elDocCount;

        item.append(
                $this._createExpandChildrenButton(model, childrenWrapper))
            .append(
                $('<div/>', {class: 'icon ui-icon ui-icon-folder-collapsed' })
            ).append(
                elName
            ).append(
                elDocCount
            ).append(
                $this._newSettingsMenu(model)
            ).append(
                $('<div/>', {style: 'clear:both'})
            );

        if (documentCount == 0) {
            item.addClass('empty');
        }

        $('#tree-view').treeView('addDescendant', model.get('id'), $this);

        // -- Events ---------------------------------------------------------------------------------------------------

        item.click(function () {
            $this._highlight(item, true);
        });

        if (selectedId == model.get('id')) {
            $this._highlight(item, false);
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

    },

    model: function () {
        return this.options.model;
    },

    /**
     * render again
     */
    refresh: function () {
        var $this = this;

        var model = $this.options.model;

        $this.elName.text(model.get('name'));

        var docCount = model.get('documentCount');
        for (var i = 0; i < $this.models.length; i++) {
            docCount += $this.models[i].get('documentCount');
        }
        $this.elDocCount.text('(' + docCount + ')');

        if ($this.options.refresh) {
            $this.options.refresh();
        }

    },

    _highlight: function (item, sync) {
        var $this = this;

        $('#tree-view .active').removeClass('active');
        item.addClass('active');
        $this.loadDocuments();

        if (sync) {
            // sync model: selected folder in database
            var folderId = $this.options.model.get('id');
            $('#tree-view').treeView('selectedFolder', folderId);
        }
    },

    _newSettingsMenu: function (model) {
        return $('<div/>', {class: 'edit ui-icon ui-icon-gear'})
            .click(function () {
                notes.dialog.folder.settings(model);
            });

    },

    _createExpandChildrenButton: function (model, children) {
        var $this = this;

        var button = $('<div/>', {class: 'toggle ui-icon'});

        var showHideChildren = function () {
            if (model.get('leaf')) {
                button.addClass('ui-icon-radio-off');
            } else {
                if (model.get('expanded')) {
                    button.addClass('ui-icon-triangle-1-e');
                    button.removeClass('ui-icon-triangle-1-s');
                    children.addClass('hidden')
                } else {
                    button.removeClass('ui-icon-triangle-1-e');
                    button.addClass('ui-icon-triangle-1-s');
                    children.removeClass('hidden')
                }
            }
        };
        showHideChildren();

        return button.click(function () {
            $this.options.model.set('expanded', !$this.options.model.get('expanded'));
            // todo sync model
            showHideChildren();
        });
    },
    loadDocuments: function () {
        var $this = this;

        var folderId = $this.options.model.get('id');
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
        $this.descendants = {};
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

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (databaseJson) {

            // remove folder information
            var minDatabaseData = {
                id: databaseJson.id,
                ownerId: databaseJson.ownerId,
                documentCount: databaseJson.documentCount,
                name: databaseJson.name,
                selectedFolderId: databaseJson.selectedFolderId
            };
            $this.model = new notes.model.database(minDatabaseData);


            // -- render children --------------------------------------------------------------------------------------

            if (databaseJson.folders && databaseJson.folders.length > 0) {

                $.each(databaseJson.folders, function (index, folderJson) {

                    var item = $('<div/>')
                        .appendTo($this.element)
                        .treeItem({
                            model: new notes.model.folder(folderJson),
                            selectedId: databaseJson.selectedFolderId
                        });
                });
            }
        });
    },

    selectedFolder: function (folderId) {
        var $this = this;
        if (folderId) {
            $this.model.set('selectedFolderId', folderId);
            $this.model.save();

        } else {
            return $this.model.get('selectedFolderId');
        }
    },

    addDescendant: function (folderId, descendant) {
        var $this = this;

        console.log('add descedant ' + folderId);
        $this.descendants[folderId] = descendant;
    },

    getItem: function (folderId) {
        var $this = this;
        return $this.descendants[folderId];
    }

});
