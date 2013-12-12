$.widget("notes.folder", {
    options: {
        parent: null,
        model: null,
        refresh: null,
        opened: []
    },
    _create: function () {
        this._reset();
    },
    _reset: function () {
        var $self = this;
        $self.element.empty();
        $self.documentCount = 0;
        $self.expanded = false;
    },
    _init: function () {
        var $self = this;
        $self._reset();

        var model = $self.options.model;

        var $target = $self.element;

        // -- Structure ------------------------------------------------------------------------------------------------

        $self.$folderLayer = $('<div/>', {class: 'fldr fldr-lvl-' + model.get('level')});
        $self.$childrenLayer = $('<div/>', {class: 'children'});

        $target.append($self.$folderLayer).append($self.$childrenLayer);

        // todo use opened[] and update opened in databasemodel

        //
        // -- Render ---------------------------------------------------------------------------------------------------
        //

        var documentCount = model.get('documentCount');

        var $fieldName = $('<span/>', {class: 'fldr-name', text: model.get('name') });
        var $fieldDocCount = $('<span/>', {class: 'fldr-dc-cnt', text: documentCount});

        $self.$fieldName = $fieldName;
        $self.$fieldDocCount = $fieldDocCount;

        $self.$openClosedIcon = $('<i/>', {class: 'fa fa-caret-right fa-fw fa-lg'});

        $self.$folderLayer.append(
                $self.$openClosedIcon
            ).append(
                $fieldName
            ).append(
                $fieldDocCount
            ).append(
                $('<div/>', {style: 'clear:both'})
            ).data('folderId', model.get('id'));

        $self.children = [];

        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        // model change listener
        model.onChange(function () {
            $self.refresh();
        });

        $self.$openClosedIcon.click(function () {
            $self.setExpanded(!$self.expanded);
        });

        $self.$fieldName.click(function () {
            $self.setExpanded(true);
        });

        $self.$folderLayer
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

        if (model.get('leaf')) {
            $self.documentCount = documentCount;
            $self.refresh();
        }

        $('#databases').databases('put', $self);

    },

    setExpanded: function (expand) {

        var $self = this;

        $self.expanded = expand;
        var folderId = $self.options.model.get('id');

        if (expand) {

            $self.$openClosedIcon.removeClass('fa-caret-right').addClass('fa-caret-down');

            $self.$childrenLayer.show();
            $self._highlight();

            $('#databases')
                .databases('setActiveFolderId', folderId)
                .databases('addOpenFolder', folderId);

            // -- Documents --

            $self.loadDocuments();

            // -- Children --
            if ($self.children.length == 0) {

                notes.util.jsonCall('GET', '/notes/rest/folder/${folderId}/children', {'${folderId}': $self.getModel().get('id')}, null, function (folders) {

                    if (folders) {
                        for (var i = 0; i < folders.length; i++) {

                            var $childFolder = $('<div/>')
                                .appendTo($self.$childrenLayer);

                            $self.children.push(
                                $childFolder
                            );

                            $childFolder.folder({
                                parent: $self,
                                model: new notes.model.Folder(folders[i]),
                                onRefresh: function () {
                                    $self.refresh();
                                }
                            });
                        }
                    }
                });
            }
        } else {

            $('#databases').databases('removeOpenFolder', folderId);

            $self.$openClosedIcon.addClass('fa-caret-right').removeClass('fa-caret-down');
            $self.$childrenLayer.hide();
        }
    },

    getModel: function () {
        return this.options.model;
    },

    getParent: function () {
        return this.options.parent;
    },

    /**
     * render again
     */
    refresh: function () {
        var $self = this;

        var model = $self.options.model;

        $self.$fieldName.text(model.get('name'));

        if ($self.options.onRefresh) {
            $self.options.onRefresh();
        }

    },

    getDocumentCount: function () {
        return this.documentCount;
    },

    _highlight: function () {

        $('#databases .active').removeClass('active');
        this.$folderLayer.addClass('active');
    },

    loadDocuments: function () {
        var $self = this;

        var folderId = $self.options.model.get('id');
        $('#document-list').documentList({
            folderId: folderId
        });
    },
    _destroy: function () {
        // todo implement widget method
    }
});