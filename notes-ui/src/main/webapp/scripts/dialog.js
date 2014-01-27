/*global notes:false */

'use strict';

notes.dialog = {
    defaults: {
        appendTo: '#dialog',
        modal: false,
        resizable: false,
        draggable: false,
        close: function () {
            $(this).dialog('destroy').empty();
        }
    }
};

notes.dialog.folder = {

    rename: function (model) {

        var $input = $('<input/>', {name: 'name', type: 'text', value: model.get('name')});

        $('<div/>', {class: 'dialog'}).append(
                $('<div/>').append(
                    $input
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Rename ' + model.get('name'),
                buttons: [
                    {
                        text: 'Rename',
                        click: function () {
                            $(this).dialog('close');
                            model.set('name', $input.val());
                            model.save(null, {
                                success: function () {
                                    model.change();
                                }
                            });
                        }
                    },
                    {
                        text: 'Cancel',
                        click: function () {
                            $(this).dialog('close');
                        }
                    }
                ]
            }));
    },

    newFolder: function (parentModel) {

        var $input = $('<input/>', {name: 'name', type: 'text'});

        var parentId = parentModel !== null ? parentModel.get('id') : null;

        $('<div/>', {class: 'dialog'}).append(
                $('<div/>').append(
                    $input
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Create Folder',
                buttons: [
                    {
                        text: 'Create',
                        click: function () {
                            $(this).dialog('close');

                            var model = new notes.model.Folder({
                                name: $input.val(),
                                parentId: parentId,
                                databaseId: notes.app.databaseId()
                            });
                            model.save(null, {
                                success: function () {
                                    $('#databases').databases('reloadTree');
                                }
                            });
                        }
                    },
                    {
                        text: 'Cancel',
                        click: function () {
                            $(this).dialog('close');
                        }
                    }
                ]
            }));
    }
};

notes.dialog.document = {

    bookmark: function () {

        var $input = $('<input/>', {name: 'name', type: 'text', value: 'http://' });

        $('<div/>', {class: 'dialog'}).append(
                $('<div/>').append(
                    $input
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'From Web',
                open: function () {
                    $input.focus().select();
                },
                buttons: [
                    {
                        text: 'Bookmark',
                        click: function () {

                            $(this).dialog('close');

                            var bookmark = new notes.model.Bookmark({
                                url: $input.val(),
                                folderId: notes.app.activeFolderId()
                            });
                            bookmark.save(null, {success: function () {
//                            bookmark.save(null, {success: function (newmodel) {
//                                var $pdfLayer = $('#bookmark-preview').show();
//
//                                var fetchModel = function () {
//                                    bookmark.fetch({success: function () {
//                                        if (bookmark.has('siteSnapshotId')) {
//                                            console.info(bookmark.get('siteSnapshotId'));
//
//                                            var maxPos = {
//                                                top: 93,
//                                                left: 20
//                                            };
//
//                                            var pdfConfig = {
//                                                fileId: bookmark.get('siteSnapshotId'),
//                                                page: 1,
//                                                position: maxPos,
//                                                layer: $pdfLayer.find('#bookmark-preview-pdf')
//                                            };
//
//                                            pdfloader.loadPdf(pdfConfig);
//
//                                        } else {
//                                            setTimeout(fetchModel, 2000);
//                                        }
//                                    }})
//                                };
//
//                                setTimeout(fetchModel, 2000);

                                // mark area
                                $('#document-list').documentList('updateDocument', bookmark);
                            }});
                        }
                    },
                    {
                        text: 'Cancel',
                        click: function () {
                            $(this).dialog('close');
                        }
                    }
                ]
            }));
    }
};

notes.dialog.tags = {

    overview: function (documentModel, callback) {

        var $input = $('<input/>', {name: 'name', type: 'text'});

        var $tagsLayer = $('<div/>');
        var fnRender = function () {

            $tagsLayer.empty();

            if (documentModel.has('tags') && documentModel.get('tags').length > 0) {
                $.each(documentModel.get('tags'), function (index, tag) {
                    var $tag = $('<div/>').append(
                            $('<span/>', {text: tag.name})
                        ).append(
                            $('<button/>', {text: 'X'}).button().click(function () {

                                var newTags = [];
                                for (var i = 0; i < documentModel.get('tags').length; i++) {
                                    var t = documentModel.get('tags')[i];
                                    if (t.name !== tag.name) {
                                        newTags.push(t);
                                    }
                                }
                                documentModel.set('tags', newTags);
                                $tag.remove();
                            })
                        );
                    $tagsLayer.append($tag);
                });
            }
        };

        fnRender();

        $('<div/>', {class: 'dialog'}).append(
                $('<div/>').append(
                        $input
                    ).append(
                        $('<button/>', {text: 'Add'}).button().click(function () {
                            var name = $input.val();
                            if (name.length > 0) {
                                documentModel.get('tags').push({name: name});
                                fnRender();
                            }
                        })
                    )
            ).append(
                $tagsLayer
            )
            .dialog($.extend({}, notes.dialog.defaults, {
                title: 'Tags',
                buttons: [
                    {
                        text: 'Close',
                        click: function () {
                            $(this).dialog('close');
                        }
                    }
                ],
                close: function () {
                    if ($.isFunction(callback)) {
                        callback();
                    }
                }
            }));
    }
};