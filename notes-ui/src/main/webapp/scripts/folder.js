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

        var folderId = 'folder-' + model.get('id');
        var $label = $('<a/>', {href: '#', text: model.get('name')});
        var $toggleButton = $('<img/>', {src: '/images/tree/toggle-small.png', id: folderId});
        var $toggle = $('<a/>', {href: '#'}).append($toggleButton);

        // -- Structure ------------------------------------------------------------------------------------------------

        $target.append($toggle);
        $target.append($label);

        var $childrenLayer = $('<ol/>', {class: 'children'});

        $target.append($childrenLayer);

        //
        // -- Fields ---------------------------------------------------------------------------------------------------
        //

        $self.children = [];
        $self.$childrenLayer = $childrenLayer;
        $self.$toggleButton = $toggleButton;


        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        // model change listener
        model.onChange(function () {
            $self.refresh();
        });

        $label.click(function () {
            //notes.router.navigate('folder/' + model.get('id'));
            $self.loadDocuments();
        });
        $toggle.click(function () {
            //notes.router.navigate('folder/' + model.get('id'));
            $self.setChildLayerVisibility(!$self.expanded);
        });

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

        var opened = $self.options.opened;
        var folderId = $self.options.model.get('id');
        for (var i = 0; i < opened.length; i++) {
            if (opened[i].id === folderId) {
                $self.setChildLayerVisibility(true);
                break;
            }
        }

        notes.app.add$Folder($self);
    },

    setChildLayerVisibility: function (visible) {

        var $self = this;

        $self.expanded = visible;

        if (visible) {

            // -- Children --
            if ($self.children.length === 0) {

                $self._fetchChildFolders();
            }

            $self.$toggleButton.attr('src', '/images/tree/toggle-small-expand.png');

            $('#databases').databases('addOpenFolder', $self.getModel().get('id'));
            $self.$childrenLayer.show();

        } else {

            $self.$toggleButton.attr('src', '/images/tree/toggle-small.png');

            $('#databases').databases('removeOpenFolder', $self.getModel().get('id'));

            $self.$childrenLayer.hide();
        }
    },

    getModel: function () {
        return this.options.model;
    },

    _fetchChildFolders: function () {
        var $self = this;

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

    loadDocuments: function () {
        var $self = this;

        var folderId = $self.options.model.get('id');

        // todo highlight folder
        notes.app.activeFolderId(folderId);

        $('#document-list').documentList({
            folderId: folderId
        });
    }
});