$.widget('notes.texteditor', $.notes.basiceditor, {

    _create: function () {
        var $this = this;

        var model = $this.getModel();

        var $fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all title', type: 'text', value: model.get('title')});
        var $fieldText = $('<textarea/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('text')});

        var $target = $this.element.empty().show().
            addClass('container text-editor').
            // resets from maximized mode
            removeClass('maximized');

        // todo both should be dialogs?

        // todo implement star/pin functionality

        $this.fnPreSyncModel = function () {
            console.log('pre sync')
            model.set('title', $fieldTitle.val());
            model.set('text', $fieldText.val());
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
                        $tagsLayer.append(', ')
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
                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
                    $fieldTitle
                )
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'})
                    .append(
                        $dateLayer
                    )
                    .append(
                        $ownerLayer
                    )
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'})
                    .append(
                        $tagsLayer
                    )
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
                    $fieldText
                )
            );
    }
})
