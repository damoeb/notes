/*global notes:false */
/*global REST_SERVICE:false */

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

        var $target = $self.element.empty();

        //$target.text(model.get('name')).append('<span class="badge">' + model.get('documentCount') + '</span>');

        var folderId = 'folder-' + model.get('id');
        var $label = $('<label/>', {for: folderId, text: model.get('name')});
        var $checkbox = $('<input/>', {type: 'checkbox', checked: 'checked', disabled: 'disabled', id: folderId});

        // -- Structure ------------------------------------------------------------------------------------------------

        $target.append($label);
        $target.append($checkbox);


        $self.$childrenLayer = $('<ol/>', {class: 'children'});

        $target.append($self.$childrenLayer);

        //
        // -- Render ---------------------------------------------------------------------------------------------------
        //

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

        $target.click(function () {
            notes.router.navigate('folder/' + model.get('id'));
            $self.setExpanded(true, true);
        });

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

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

                notes.util.jsonCall('GET', REST_SERVICE + '/folder/${folderId}/children', {'${folderId}': $self.getModel().get('id')}, null, function (folders) {

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

        //var model = $self.options.model;

        // todo rename

        if ($self.options.onRefresh) {
            $self.options.onRefresh();
        }

    },

    getDocumentCount: function () {
        return this.documentCount;
    },

    _highlight: function () {

        $('#databases .active').removeClass('active');
    },

    loadDocuments: function () {
        var $self = this;

        var folderId = $self.options.model.get('id');
        $('#document-list').documentList({
            folderId: folderId
        });
    }
});