/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.folder', {
    options: {
        parent: null,
        model: null
    },
    _create: function () {
        this._reset();
    },
    _reset: function () {
        var $self = this;
        $self.element.empty();
        $self.childrenLoaded = false;
    },
    _init: function () {
        var $self = this;
        $self._reset();
        var $target = $self.element.empty();
        var model = $self.options.model;

        var templateFolder = _.template($('#folder-in-tree').html());

        $target.append($(templateFolder(model.attributes).trim()));

        $self.$childrenLayer = $target.find('.children');
        $self.$toggle = $target.find('.toggle');

        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        $self.$toggle.click(function () {
            //notes.router.navigate('folder/' + model.get('id'));
            $self.setExpanded(!model.get('expanded'));
        });

        $target.find('.name').click(function () {
            $self.loadDocuments();
        });

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

        if (model.get('expanded')) {
            $self.setExpanded(true);
        }

        // active folder
        if (notes.app.activeFolderId() === model.get('id')) {
            $target.addClass('active');
        }

        notes.app.add$Folder($self);
    },

    setExpanded: function (expand) {

        var $self = this;

        if ($self.options.model.get('leaf')) {
            throw 'cannot expand folder, cause its a leaf';
        }

        $self.options.model.set('expanded', expand).save();

        var folderId = $self.options.model.get('id');

        if (expand) {

            $self.$toggle.removeClass('fa-caret-right').addClass('fa-caret-down');

            // -- Children --
            if (!$self.childrenLoaded) {

                $self._fetchChildren();
            }

            $self.$childrenLayer.show();


        } else {

            $self.$toggle.addClass('fa-caret-right').removeClass('fa-caret-down');

            $self.$childrenLayer.hide();
        }
    },

    _fetchChildren: function () {
        var $self = this;

        notes.util.jsonCall('GET', REST_SERVICE + '/folder/${folderId}/children', {'${folderId}': $self.getModel().get('id')}, null, function (folders) {

            $self.childrenLoaded = true;

            if (folders) {
                for (var i = 0; i < folders.length; i++) {

                    var $childFolder = $('<li/>')
                        .appendTo($self.$childrenLayer);

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
            model: new notes.model.Folder(data)
        };
    },

    getParent: function () {
        return this.options.parent;
    },

    loadDocuments: function () {
        var $self = this;

        var folderId = $self.options.model.get('id');

        notes.app.activeFolderId(folderId);

        $('#databases .folder.active').removeClass('active');
        $('#databases .folder-' + $self.options.model.get('id')).addClass('active');

        // todo save document when closed
        $('#document-list').documentList('refresh', folderId);

        $('#document-view').hide();
        $('#folder-view').show();
        $('#document-and-folder-view').show();
        $('#search-view').hide();
    }
});