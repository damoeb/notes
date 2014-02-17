'use strict';

$.widget('notes.basiceditor', {

    fnClose: function () {
        this.syncModel();

        $('#document-view').hide();
        $('#folder-view').show();
    },

    _createParent: function () {
        var $this = this;

        console.log('create parent');

        var $rendered = $this.element;
        $rendered.find('.action-close').unbind('click').click(function () {
            $this.fnSave.call($this);

            $this._destroy();

            $('#document-view').hide();
            $('#folder-view').show();
        });

        $rendered.find('.action-star').unbind('click').click($this.fnStar);

        $this.fnRenderTags();
        // todo recommend tags
        $this.fnRenderTagNetwork();

        var addNewTagsFromField = function () {
            var newTagNames = $this.element.find('.new-tag-value').val().split(' ');
            for (var i = 0; i < newTagNames.length; i++) {
                var newTagName = newTagNames[i];
                if (newTagName.length > 1) {
                    $this.fnAddTag(newTagName);
                }
            }
        };
        $this.element.find('.action-new-tag')
            .unbind('click')
            .click(addNewTagsFromField);
        $this.element.find('.new-tag-value')
            .unbind('keypress')
            .keypress(function (e) {
                if (e.which == 13) {
                    addNewTagsFromField();
                }
            }
        );
    },

    fnSave: function () {
        console.log('save');
        this.syncModel();
    },

    fnRemoveTag: function (name) {
        var $this = this;

        console.log('remove tag ' + name);

        var model = $this.getModel();

        var newTags = [];
        for (var i = 0; i < model.get('tags').length; i++) {
            var t = model.get('tags')[i];
            if (t.name !== name) {
                newTags.push(t);
            }
        }
        model.set('tags', newTags);
        $this.tagsChanged = true;

        $this.fnRenderTags();
    },

    fnAddTag: function (name) {
        var $this = this;

        console.log('add tag "' + name + '"');

        var model = $this.getModel();

        model.get('tags').push({name: name});

        $this.tagsChanged = true;

        $this.fnRenderTags();
    },

    fnRenderTagNetwork: function () {
        var $this = this;

        console.log('render tag network');

        var $tagsLayer = $this.element.find('.tags-network');

        var onSuccess = function (tags) {
            $tagsLayer.empty();
            if (tags.length == 0) {
                $tagsLayer.append('none');
            } else {
                for (var i = 0; i < tags.length; i++) {
                    var tag = tags[i];
                    var $tag = $('<a/>', {href: '#', class: 'label label-default', title: 'Add tag', text: tag.name}).click(function () {
                        $this.fnAddTag(tag.name);
                    });
                    $tagsLayer.append(' ');
                    $tagsLayer.append($tag);
                }
            }
        };

        notes.util.jsonCall('GET', REST_SERVICE + '/tag/network', null, null, onSuccess, null);

    },

    fnRenderTags: function () {
        var $this = this;

        console.log('render tags');

        var model = $this.getModel();

        var $tagsLayer = $this.element.find('.document-tags').empty();

        if (model.has('tags') && model.get('tags').length > 0) {
            // todo sort tags
            $.each(model.get('tags'), function (index, tag) {
                var $tag = $('<a/>', {href: '#', class: 'label label-default', title: 'Remove tag', text: tag.name}).click(function () {
                    $this.fnRemoveTag(tag.name);
                });
                $tagsLayer.append($tag);
                $tagsLayer.append(' ');
            });
        }
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

//    fnMaximize: function (el) {
//        var $this = this;
//        var $editor = this.element;
//        if ($editor.hasClass('maximized')) {
//            $editor.removeClass('maximized');
//
//            $(el).
//                button('option', 'label', 'Maximize').
//                button('option', 'icons', { primary: 'ui-icon-arrow-4-diag'});
//
//            if ($.isFunction($this.fnPostEmbed)) {
//                $this.fnPostEmbed.call($this);
//            }
//
//        } else {
//
//            $editor.addClass('maximized');
//
//            $(el).
//                button('option', 'label', 'Unmaximize').
//                button('option', 'icons', { primary: 'ui-icon-arrow-1-se'});
//
//            if ($.isFunction($this.fnPostMaximize)) {
//                $this.fnPostMaximize.call($this);
//            }
//        }
//    },

    getModel: function () {
        return this.options.model;
    },

    syncModel: function () {
        var $this = this;

        var originalModel = $.extend({}, $this.options.model.attributes);

        if ($.isFunction($this.fnUpdateModel)) {
            $this.fnUpdateModel();
        }

        if ($this.tagsChanged || !notes.util.equal(originalModel, $this.options.model.attributes)) {
            $this.getModel().save(null, {success: function () {
                console.log('saved');
                $('#document-list').documentList('refresh', $this.getModel().get('folderId'));
            }});
        }
    }

});
