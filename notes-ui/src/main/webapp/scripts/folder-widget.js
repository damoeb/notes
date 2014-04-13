/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

// todo ugliest widget ever

/**
 * Taxononmy http://en.wikipedia.org/wiki/Taxonomy_%28general%29
 */
$.widget('notes.folder', {
    options: {
        parent: null,
        model: null,
        onSelect: null,
        sync: true
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
        var $this = this;
        $this._reset();
        var $target = $this.element.empty();
        var model = $this.options.model;

        var templateFolder = _.template($('#folder-in-tree').html());

        var $rendered = $(templateFolder(model.attributes).trim());
        $target.append($rendered);

        $this.$childrenLayer = $target.find('.children');
        $this.$toggle = $target.find('.toggle');

        if (notes.folders.activeFolderId() === model.get('id')) {
            $rendered.addClass('active');
        }
        if (notes.folders.trashFolderId() === model.get('id')) {
            $rendered.addClass('trash');
        }
        if (notes.folders.defaultFolderId() === model.get('id')) {
            $rendered.addClass('untitled');
        }

        //
        // -- Events ---------------------------------------------------------------------------------------------------
        //

        $this.$toggle.click(function () {
            $this.setExpanded(!model.get('expanded'));
            $this._syncModel();
        });

        if (typeof $this.options.onSelect !== 'undefined' && $this.options.onSelect !== null) {
            $rendered.find('a.name').attr('href', '#').click(function () {
                $this.options.onSelect(model);
            });
        }


        /**
         * Drap and Drop. This section will cause folder to expand if a droppable thing is hovered longer than <code>EXPAND_TIME</code>
         */

        var $folderOnly = $target.find('.folder');

        var EXPAND_TIMEOUT = 1000;

        if (!model.get('leaf')) {

            var hover = false;
            $folderOnly.mouseenter(function () {
                hover = true;
//                console.debug('in '+model.get('name'));
                setTimeout(function () {
                    if (hover && $folderOnly.hasClass('dropping-document') && !model.get('expanded')) {
                        console.log('expand ' + model.get('name'));
                        $this.setExpanded(true);
                        $this._syncModel();
                    }
                }, EXPAND_TIMEOUT);
            });
            $folderOnly.mouseleave(function () {
                hover = false;
//                console.debug('out '+model.get('name'));
            });
        }

        $folderOnly.droppable({hoverClass: 'dropping-document', drop: function (event, ui) {
            var document = $(ui.draggable).data('document');

            console.log('drop in ' + model.get('name'));
            notes.documents.moveTo([document.id], model.get('id'));

        }});

        //
        // -- Triggers -------------------------------------------------------------------------------------------------
        //

        if (model.get('expanded')) {
            $this.setExpanded(true);
        }

        notes.folders.storeFolderModel($this.getModel());
    },

    _syncModel: function () {
        if (this.options.sync) {
            this.options.model.save();
        }
    },
    updateDocCount: function (delta) {
        var model = this.options.model;
        model.set('documentCount', Math.max(model.get('documentCount') + delta, 0));
        this._init();
    },
    setExpanded: function (expand) {

        var $self = this;

        if ($self.options.model.get('leaf')) {
            throw 'cannot expand folder, cause its a leaf';
        }

        $self.options.model.set('expanded', expand);

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

                notes.util.sortJSONArrayASC(folders, 'name');

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
            model: new notes.model.Folder(data),
            onSelect: $self.options.onSelect,
            sync: $self.options.sync
        };
    },

    getParent: function () {
        return this.options.parent;
    }
});