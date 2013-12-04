$.widget("notes.folder", {
    options: {
        model: null,
        refresh: null,
        opened: []
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

        var $target = $this.element;

        // -- Structure ------------------------------------------------------------------------------------------------

        var $folder = $('<div/>', {class: 'folder level-' + model.get('level')});
        var $childrenLayer = $('<div/>', {class: 'children'});

        $target.append($folder).append($childrenLayer);

        // todo use opened[] and update openend in databasemodel

        //
        // -- Render ---------------------------------------------------------------------------------------------------
        //

        var documentCount = model.get('documentCount');

        var $fieldName = $('<div/>', {class: 'name', text: model.get('name') });
        var $fieldDocCount = $('<div/>', {class: 'doc-count', text: '(' + documentCount + ')'});

        $this.$fieldName = $fieldName;
        $this.$fieldDocCount = $fieldDocCount;

        $folder.append(
                $this._createExpandChildrenButton(model, $childrenLayer))
            .append(
                $('<div/>', {class: 'icon ui-icon ui-icon-folder-collapsed' })
            ).append(
                $fieldName
            ).append(
                $fieldDocCount
            ).append(
                $this._newSettingsMenu(model)
            ).append(
                $('<div/>', {style: 'clear:both'})
            ).data('folderId', model.get('id'));

        $this.children = [];

        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        // model change listener
        model.onChange(function () {
            $this.refresh();
        });

        // load documents
        $folder.dblclick(function () {
            $this.loadDocuments();

            // sync model: selected folder in database
            var folderId = $this.options.model.get('id');
            $('#databases').databases('setActiveFolderId', folderId);
        });

        // highlight
        $folder.click(function () {
            $this._highlight($folder);
        });

        $folder
            .droppable({hoverClass: 'drop-document', drop: function (event, ui) {
                var $draggable = $(ui.draggable);

                if ($draggable.is('tr')) {

                    new notes.model.Document({
                        id: $draggable.data('documentId'),
                        folderId: model.get('id'),
                        event: 'MOVE'
                    }).save(null, {
                            success: function () {
                                // refresh ui
                                $('#databases').databases('reloadTree');
                            }
                        });
                }
            }});

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

        /*
         if (selectedId == model.get('id')) {
         $this.loadDocuments();
         $this._highlight($folder);
         }
         */

        if (model.get('leaf')) {
            $this.documentCount = documentCount;
            $this.refresh();
        }

        $('#databases').databases('put', $this);

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

        $this.$fieldName.text(model.get('name'));

        var documentCount = model.get('documentCount');

        if (documentCount == 0) {
            $this.$fieldName.parent().addClass('empty');
        } else {
            $this.$fieldName.parent().removeClass('empty');
        }

        $this.documentCount = documentCount;

        $this.$fieldDocCount.text('(' + documentCount + ')');

        if ($this.options.onRefresh) {
            $this.options.onRefresh();
        }

    },

    getDocumentCount: function () {
        return this.documentCount;
    },

    _highlight: function (item) {

        $('#databases .active').removeClass('active');
        item.addClass('active');
    },

    _newSettingsMenu: function (model) {
        return $('<div/>', {class: 'edit ui-icon ui-icon-gear'})
            .click(function () {
                notes.dialog.folder.settings(model);
            });
    },

    _createExpandChildrenButton: function (model, $childrenLayer) {
        var $this = this;

        var $button = $('<div/>', {class: 'toggle ui-icon ui-icon-triangle-1-e'});

        var fnShowHideChildren = function () {
            if (model.get('leaf')) {
                $button.addClass('ui-icon-radio-off');
            } else {
                if (model.get('expanded')) {
                    $button.removeClass('ui-icon-triangle-1-e');
                    $button.addClass('ui-icon-triangle-1-s');
                    $childrenLayer.removeClass('hidden');

                    //
                    // -- Children -------------------------------------------------------------------------------------------------
                    //
                    if ($this.children.length == 0) {

                        notes.util.jsonCall('GET', '/notes/rest/folder/${folderId}/children', {'${folderId}': model.get('id')}, null, function (folders) {

                            if (folders) {
                                for (var i = 0; i < folders.length; i++) {

                                    var $childFolder = $('<div/>')
                                        .appendTo($childrenLayer);

                                    $this.children.push(
                                        $childFolder
                                    );

                                    $childFolder.folder({
                                        model: new notes.model.Folder(folders[i]),
                                        onRefresh: function () {
                                            $this.refresh();
                                        }
                                    });
                                }
                            }
                        });
                    }

                } else {
                    $button.addClass('ui-icon-triangle-1-e');
                    $button.removeClass('ui-icon-triangle-1-s');

                    $childrenLayer.addClass('hidden');
                }
            }
        };

        return $button.click(function () {
            $this.options.model.set('expanded', !$this.options.model.get('expanded'));
            // todo sync model
            fnShowHideChildren();
        });
    },
    loadDocuments: function () {
        var $this = this;

        var folderId = $this.options.model.get('id');
        $('#document-list').documentList({
            folderId: folderId
        });
    },
    _destroy: function () {
        // todo implement widget method
    }
});