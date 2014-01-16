/*global notes:false */

'use strict';

$.widget('notes.texteditor', $.notes.basiceditor, {

    _create: function () {
        var $this = this;

        var model = $this.getModel();

        var $title = $('<input/>', {class: 'form-control title', type: 'text', value: model.get('title')});
        var $text = $('<textarea/>', {class: 'form-control', rows: 10, text: model.get('text')});

        var $target = $this.element.empty().show().
            addClass('editor text-editor').
            // resets from maximized mode
            removeClass('maximized');

        // todo both should be dialogs?

        // todo implement star/pin functionality

        $this.fnPreSyncModel = function () {
            console.log('pre sync');
            model.set('title', $title.val());
            model.set('text', $text.val());
        };

        $this.fnPreDestroy = function () {
            console.log('pre destory');
        };

        var $tagsLayer = $('<span/>');
        var $addTag = $('<a/>', {href: '#'});

        var fnRenderTags = function () {

            $tagsLayer.empty();

            if (model.has('tags') && model.get('tags').length > 0) {
                // todo sort tags
                $.each(model.get('tags'), function (index, tag) {
                    $tagsLayer.append(
                        $('<a/>', {
                            text: tag.name,
                            href: '#tag:' + tag.name
                        })
                    );

                    if (index < model.get('tags').length - 1) {
                        $tagsLayer.append(', ');
                    }

                    if (index >= model.get('tags').length - 1) {
                        $tagsLayer.append(' ').append(
                            $addTag.text('add')
                        );
                    }
                });
            } else {
                $tagsLayer.append(
                    $addTag.text('add tag')
                );
            }
        };

        fnRenderTags();

        $addTag.click(function () {
            notes.dialog.tags.overview(model, function () {
                fnRenderTags();
            });
        });


        var $ownerLayer = $('<span/>');
        if (model.has('owner')) {
            $ownerLayer.text(' by ' + model.get('owner'));
        }

        var $dateLayer = $('<span/>');

        if (model.has('modified')) {
            $dateLayer.text('changed ' + notes.util.formatDate(new Date(model.get('modified'))));
        }

        $target.append(
                $this._getToolbar()
            ).append(
                $('<div/>', {class: 'row'}).append(
                    $title
                )
            ).append(
                $('<div/>', {class: 'row'})
                    .append(
                        $dateLayer
                    )
                    .append(
                        $ownerLayer
                    )
            ).append(
                $('<div/>', {class: 'row'})
                    .append(
                        $tagsLayer
                    )
            ).append(
                $('<div/>', {class: 'row'}).append(
                    $text
                )
            );
    }
});
