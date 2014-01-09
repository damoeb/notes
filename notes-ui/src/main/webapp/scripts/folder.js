/*global notes:false */

'use strict';

$.widget('notes.folder', {
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
        $self.$childrenLayer = $('<ul/>', {class: 'children'});

        $target.append($self.$folderLayer).append($self.$childrenLayer);

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

        if ($self._isExpanded()) {
            $self.setExpanded(true, false);
        }

        // model change listener
        model.onChange(function () {
            $self.refresh();
        });

        $self.$openClosedIcon.click(function () {
            $self.setExpanded(!$self.expanded, true);
        });

        $self.$fieldName.click(function () {
            notes.router.navigate('folder/' + model.get('id'));
            $self.setExpanded(true, true);
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

        notes.app.add$Folder($self);
    },

    _isExpanded: function () {
        var $self = this;
        var opened = $self.options.opened;
        var id = $self.options.model.get('id');
        for (var i = 0; i < opened.length; i++) {
            if (opened[i].id === id) {
                return true;
            }
        }
        return false;
    },

    setExpanded: function (expand, callTriggers) {

        var $self = this;

        $self.expanded = expand;
        var folderId = $self.options.model.get('id');

        if (expand) {

            $self.$openClosedIcon.removeClass('fa-caret-right').addClass('fa-caret-down');

            $self.$childrenLayer.show();
            $self._highlight();

            notes.app.activeFolderId(folderId);

            if (callTriggers) {

                $('#databases')
                    .databases('addOpenFolder', folderId);

                $self.loadDocuments();
            }


            // -- Children --
            if ($self.children.length === 0) {

                notes.util.jsonCall('GET', '/notes/rest/folder/${folderId}/children', {'${folderId}': $self.getModel().get('id')}, null, function (folders) {

                    if (folders) {
                        for (var i = 0; i < folders.length; i++) {

                            var $childFolder = $('<li/>')
                                .appendTo($self.$childrenLayer);

                            $self.children.push(
                                $childFolder
                            );

                            $childFolder.folder($self._getFolderConfig(folders[i]));
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

    _getFolderConfig: function (data) {
        var $self = this;
        return {
            parent: $self,
            model: new notes.model.Folder(data),
            opened: $self.options.opened,
            onRefresh: function () {
                $self.refresh();
            }
        };
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
    }
});