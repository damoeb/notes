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
        var $dialog = $('<div/>', {class: 'dialog'});

        var submitFolder = function () {

            $dialog.dialog('close');

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
        };

        $input.keypress(function (e) {
            if (e.which == 13) {
                submitFolder();
            }
        });

        var parentId = parentModel !== null ? parentModel.get('id') : null;

        $dialog.append(
                $('<div/>').append(
                    $input
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Create Folder',
                buttons: [
                    {
                        text: 'Create',
                        click: function () {
                            submitFolder();
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
                                $('#document-list').documentList('refresh', bookmark.get('folderId'));
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
