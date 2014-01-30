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

        $target.addClass('folder folder-' + model.get('id'));

        var $label = $('<a/>', {
            href: '#',
            text: model.get('name')
        });
        var $toggle = $('<i class="fa fa-caret-right fa-fw fa-lg"></i>');

        // -- Structure ------------------------------------------------------------------------------------------------

        if (!model.get('leaf')) {
            $target.append($('<a/>', {href: '#', class: 'toggle'}).append($toggle));
        }
        $target.append($label);
        $target.append($('<span/>', {text: model.get('documentCount'), class: 'pull-right label label-default', style: 'font-size:90%; margin-top:2px;'}));


        var $childrenLayer = $('<ol/>', {class: 'children'});

        $target.append($childrenLayer);


        $self.$childrenLayer = $childrenLayer;
        $self.$toggle = $toggle;

        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        $toggle.click(function () {
            //notes.router.navigate('folder/' + model.get('id'));
            $self.setExpanded(!model.get('expanded'));
        });

        $label.click(function () {
            $self.loadDocuments();
        });

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

        if (model.get('expanded')) {
            $self.setExpanded(true);
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
        $('#document-list').documentList({
            folderId: folderId
        });

        $('#document-view').hide();
        $('#folder-view').show();
    }
});