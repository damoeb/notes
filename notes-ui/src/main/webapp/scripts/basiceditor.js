'use strict';

$.widget('notes.basiceditor', {

    fnClose: function () {
        this.syncModel();

        $('#document-view').hide();
        $('#folder-view').show();
    },

    fnSave: function () {
        console.log('save');
        this.syncModel();
    },

    fnTags: function () {
        notes.dialog.tags.overview(this.getModel());
    },

    fnDelete: function () {
        var $this = this;
        $('#document-list')
            .documentList('refresh', $this.getModel().get('folderId'));

        $('#document-view').hide();
        $('#folder-view').show();

        $this.getModel().destroy();
        $this._destroy();
    },

    fnStar: function () {
        var isStar = $(this).attr('star') === 'true';
        var newStarState = !isStar;
        $(this).attr('star', newStarState);

        if (newStarState) {
            $(this).find('.pane-with-star').removeClass('hidden');
            $(this).find('.pane-without-star').addClass('hidden');
            console.log('star document');
        } else {
            $(this).find('.pane-with-star').addClass('hidden');
            $(this).find('.pane-without-star').removeClass('hidden');
            console.log('de-star document');
        }
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

        if ($.isFunction($this.fnUpdateModel)) {
            $this.fnUpdateModel();
        }

        if (!notes.util.equal(originalModel, $this.options.model.attributes)) {
            $this.getModel().save(null, {success: function () {
                console.log('saved');
                $('#document-list').documentList('refresh', $this.getModel().get('folderId'));
            }});
        }
    }

});
