/*global notes:false */

'use strict';

$.widget('notes.texteditor', $.notes.basiceditor, {

    options: {
        editMode: false
    },

    _create: function () {

        console.log('create text editor');

        var $this = this;

        var $target = $this.element.empty();

        var model = $this.getModel();

        var template = _.template($('#editor-template').html());

        var $rendered = $(template(model.attributes).trim());
        var $title = $rendered.find('.field-title');
        var $text = $rendered.find('.field-text');

        $target.append($rendered);

        $this.fnUpdateModel = function () {
            console.log('update model');
            model.set('title', $title.code());
            model.set('text', $text.code());
        };

        $this.fnEditMode = function () {
            console.log('edit mode');

//            $rendered.find('.action-save').show();
            $rendered.find('.action-edit-mode').hide();
            $rendered.find('.action-view-mode').show();
            $text.summernote(SUMMERNOTE_CFG);
        };

        $this.fnViewMode = function () {
            console.log('view mode');
//            $rendered.find('.action-save').hide();
            $rendered.find('.action-edit-mode').show();
            $rendered.find('.action-view-mode').hide();

            $this.fnSave.call($this);

            $text.destroy();
        };

        if ($this.options.editMode) {
            $this.fnEditMode.call($this);
        }

        $rendered.find('.action-edit-mode').click(function () {
            $this.fnEditMode.call($this);
        });

        $rendered.find('.action-view-mode').click(function () {
            $this.fnViewMode.call($this);
        });

//        $rendered.find('.action-save').click(function() {
//            $this.fnSave.call($this);
//        });

//        var $tagsLayer = $('<span/>');
//
//        var fnRenderTags = function () {
//
//            $tagsLayer.empty();
//
//            if (model.has('tags') && model.get('tags').length > 0) {
//                // todo sort tags
//                $.each(model.get('tags'), function (index, tag) {
//                    $tagsLayer.append(
//                        $('<a/>', {
//                            text: tag.name,
//                            href: '#tag:' + tag.name
//                        })
//                    );
//
//                    if (index < model.get('tags').length - 1) {
//                        $tagsLayer.append(', ');
//                    }
//
//                });
//            }
//        };
//
//        fnRenderTags();

    }
});
