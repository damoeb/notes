'use strict';

$.widget('notes.basiceditor', {

    options: {
        model: null
    },

    _createButton: function (label, tooltip, onClick) {
        var $this = this;

        return $('<button/>', {title: tooltip, class: 'btn btn-default', html: label}).click(function () {
                if ($.isFunction(onClick)) {
                    onClick.call($this, this);
                }
            }
        );
    },

    _getToolbar: function (config) {
        var $this = this;

        var $toolbar = $('<div/>', {class: 'row'});

        var $left = $('<div/>', {style: 'float:left'}).append(
                $this._createButton('<i class="fa fa-reply"></i>', 'Back', $this.fnClose)
            ).append(
                $this._createButton('<i class="fa fa-tags"></i>', 'Tags', $this.fnTags)
            ).append(
                $this._createButton('<i class="fa fa-trash-o"></i>', 'Delete', $this.fnDelete)
            );

        if ($this.options.model.has('modified')) {
            $left.append(
                $('<span/>', {text: 'changed ' + notes.util.formatDate(new Date($this.options.model.get('modified')))})
            );
        }

        if (config && config.left) {
            for (var i = 0; i < config.left.length; i++) {

                $left.append(
                    config.left[i]
                );
            }
        }

        var $right = $('<div/>', {style: 'float:right'}).append(
            $this._createButton('<i class="fa fa-arrows-alt"></i>', 'Maximize', $this.fnMaximize)
        );

        return $toolbar.append($left).append($right);
    },


    fnClose: function () {
        this.syncModel();

        $('#document-view').hide();
        $('#folder-view').show();
    },


    fnSave: function () {
        this.syncModel();
    },

    fnTags: function () {
        notes.dialog.tags.overview(this.getModel());
    },

    fnDelete: function () {
        var $this = this;
        $('#document-list')
            .documentList('deleteDocument', $this.getModel());

        $('#document-view').hide();
        $('#folder-view').show();

        $this.getModel().destroy();
        $this._destroy();
    },

    fnMaximize: function (el) {
        var $this = this;
        var $editor = this.element;
        if ($editor.hasClass('maximized')) {
            $editor.removeClass('maximized');

            $(el).
                button('option', 'label', 'Maximize').
                button('option', 'icons', { primary: 'ui-icon-arrow-4-diag'});

            if ($.isFunction($this.fnPostEmbed)) {
                $this.fnPostEmbed.call($this);
            }

        } else {

            $editor.addClass('maximized');

            $(el).
                button('option', 'label', 'Unmaximize').
                button('option', 'icons', { primary: 'ui-icon-arrow-1-se'});

            if ($.isFunction($this.fnPostMaximize)) {
                $this.fnPostMaximize.call($this);
            }
        }
    },

    getModel: function () {
        return this.options.model;
    },

    syncModel: function () {
        var $this = this;

        var originalModel = $.extend({}, $this.options.model.attributes);

        if ($.isFunction($this.fnPreSyncModel)) {
            $this.fnPreSyncModel();
        }

        if (!notes.util.equal(originalModel, $this.options.model.attributes)) {
            $this.getModel().save(null, {success: function () {
                noty('Saved');
                $('#document-list').documentList('updateDocument', $this.getModel());
            }});
        }
    },

    _destroy: function () {
        var $this = this;

        console.log('unload callback');
        if ($.isFunction($this.unloadCallback)) {
            $this.unloadCallback();
        }

        if ($.isFunction($this.fnPreDestroy)) {
            $this.fnPreDestroy();
        }
    }

});
