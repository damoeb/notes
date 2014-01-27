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
        var $target = $self.element.empty();
        var model = $self.options.model;

        $target.addClass('folder folder-' + model.get('id'));

        var $label = $('<a/>', {
            href: '#',
            text: model.get('name')
        }).append($('<span/>', {text: model.get('documentCount'), class: 'pull-right'}));
        var $toggle = $('<i class="fa fa-plus-square-o fa-fw"></i>');

        // -- Structure ------------------------------------------------------------------------------------------------

        if (!model.get('leaf')) {
            $target.append($('<a/>', {href: '#'}).append($toggle));
        }
        $target.append($label);


        var $childrenLayer = $('<ol/>', {class: 'children'});

        $target.append($childrenLayer);


        $self.$childrenLayer = $childrenLayer;
        $self.$toggle = $toggle;

        //
        // -- Render ---------------------------------------------------------------------------------------------------
        //

        $self.children = [];

        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        // model change listener
        model.onChange(function () {
            $self.refresh();
        });

        $toggle.click(function () {
            //notes.router.navigate('folder/' + model.get('id'));
            $self.setExpanded(!$self.expanded);
        });

        $label.click(function () {
            $self.loadDocuments();
        });

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

        var opened = $self.options.opened;
        var id = $self.options.model.get('id');
        for (var i = 0; i < opened.length; i++) {
            if (opened[i].id === id) {
                $self.setExpanded(true);
                break;
            }
        }

        notes.app.add$Folder($self);
    },

    setExpanded: function (expand) {

        var $self = this;

        $self.expanded = expand;
        var folderId = $self.options.model.get('id');

        if (expand) {

            $self.$toggle.removeClass('fa-plus-square-o').addClass('fa-minus-square-o');

            $('#databases').databases('addOpenFolder', folderId);

            // -- Children --
            if ($self.children.length === 0) {

                $self._fetchChildren();
            }

            $self.$childrenLayer.show();


        } else {

            $self.$toggle.addClass('fa-plus-square-o').removeClass('fa-minus-square-o');

            $('#databases').databases('removeOpenFolder', folderId);

            $self.$childrenLayer.hide();
        }
    },

    _fetchChildren: function () {
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

    loadDocuments: function () {
        var $self = this;

        var folderId = $self.options.model.get('id');

        notes.app.activeFolderId(folderId);

        $('#databases .folder.active').removeClass('active');
        $('#databases .folder-' + $self.options.model.get('id')).addClass('active');

        $('#document-list').documentList({
            folderId: folderId
        });
    }
});