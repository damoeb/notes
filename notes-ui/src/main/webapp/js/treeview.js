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
        this.documentCount = 0;
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

        //
        // -- Render ---------------------------------------------------------------------------------------------------
        //

        var documentCount = model.get('documentCount');

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

        //
        // -- Children -------------------------------------------------------------------------------------------------
        //

        $this.children = [];

        if (!model.get('leaf')) {

            var children = model.get('children');
            if (children) {
                for (var i = 0; i < children.length; i++) {

                    $this.children.push(
                        $('<div/>')
                            .appendTo(childrenWrapper)
                            .treeItem({
                                model: new notes.model.folder(children[i]),
                                selectedId: selectedId,
                                onRefresh: function () {
                                    $this.refresh();
                                }
                            })
                    );
                }
            }
        }

        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        // model change listener
        model.onChange(function () {
            $this.refresh();
        });

        // load documents
        item.dblclick(function () {
            $this.loadDocuments();

            // sync model: selected folder in database
            var folderId = $this.options.model.get('id');
            $('#tree-view').treeView('selectedFolder', folderId);
        });

        // highlight
        item.click(function () {
            $this._highlight(item);
        });

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

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

        if (selectedId == model.get('id')) {
            $this.loadDocuments();
            $this._highlight(item);
        }

        if (model.get('leaf')) {
            $this.documentCount = documentCount;
            $this.refresh();
        }

        $('#tree-view').treeView('pushFolder', $this);

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

        var documentCount = model.get('documentCount');
        for (var i = 0; i < $this.children.length; i++) {
            documentCount += $this.children[i].treeItem('getDocumentCount');
        }

        if (documentCount == 0) {
            $this.elName.parent().addClass('empty');
        } else {
            $this.elName.parent().removeClass('empty');
        }

        $this.documentCount = documentCount;

        $this.elDocCount.text('(' + documentCount + ')');

        if ($this.options.onRefresh) {
            $this.options.onRefresh();
        }

    },

    getDocumentCount: function () {
        return this.documentCount;
    },

    _highlight: function (item) {

        $('#tree-view .active').removeClass('active');
        item.addClass('active');
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
                    button.removeClass('ui-icon-triangle-1-e');
                    button.addClass('ui-icon-triangle-1-s');
                    children.removeClass('hidden');
                } else {
                    button.addClass('ui-icon-triangle-1-e');
                    button.removeClass('ui-icon-triangle-1-s');
                    children.addClass('hidden');
                }
                model.save();
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
        $('#document-list-view').documentList({
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

        var $this = this;

        $this.element.empty();

        notes.util.jsonCall('GET', '/notes/rest/database/${dbId}', {'${dbId}': $this.options.databaseId}, null, function (databaseJson) {

            // remove folder information
            var minDatabaseData = {
                id: databaseJson.id,
                ownerId: databaseJson.ownerId,
                name: databaseJson.name,
                selectedFolderId: databaseJson.selectedFolderId
            };
            $this.model = new notes.model.database(minDatabaseData);


            // -- render root ------------------------------------------------------------------------------------------
            var root = $('<div/>').appendTo($this.element);


            var rootSettings = $('<div/>', {class: 'ui-icon ui-icon-gear', style: 'float:right'}).click(function () {
                notes.dialog.folder.settings(new notes.model.folder({
                    databaseId: databaseJson.id
                }));
            });

            $('<div/>', {class: 'group-item active'}).append(
                    $('<div/>', {class: 'group-icon ui-icon ui-icon-clipboard'})
                ).append(
                    $('<div/>', {text: databaseJson.name, class: 'group-label'})
                ).append(
                    rootSettings
                ).append(
                    $('<div/>', {class: 'clear'})
                ).appendTo(
                    root
                );


            // -- render children --------------------------------------------------------------------------------------

            if (databaseJson.folders && databaseJson.folders.length > 0) {

                $.each(databaseJson.folders, function (index, folderJson) {
                    $('<div/>')
                        .appendTo(root)
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

    pushFolder: function (treeItem) {
        var $this = this;

        $this.descendants[treeItem.model().get('id')] = treeItem;
    },

    folder: function (folderId) {
        var $this = this;
        return $this.descendants[folderId];
    }

});
