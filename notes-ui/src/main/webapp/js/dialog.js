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

    settings: function (model) {
        var $this = this;

        var $dialog = $('<div/>', {class: 'dialog'});

        $dialog.append(
                $('<div/>').append(
                    $('<a/>', {text: 'New Folder'}).click(
                        function () {
                            $dialog.dialog('close');
                            $this.newFolder(model);
                        })
                )
            ).append(
                $('<div/>').append(
                    $('<a/>', {text: 'Rename'}).click(
                        function () {
                            $dialog.dialog('close');
                            $this.rename(model);
                        })
                )
            ).append(
                $('<div/>').append(
                    $('<a/>', {text: 'Add to favourites'})
                )
            ).append(
                $('<div/>').append(
                    $('<a/>', {text: 'Delete'})
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Settings'
            }));
    },

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
                            model.set('name', $input.val());
                            model.save(null, {
                                success: function () {
                                    model.change();
                                }
                            });
                            $(this).dialog("close");
                        }
                    },
                    {
                        text: 'Cancel',
                        click: function () {
                            $(this).dialog("close");
                        }
                    }
                ]
            }));
    },

    newFolder: function (parentModel) {

        var $input = $('<input/>', {name: 'name', type: 'text'});

        $('<div/>', {class: 'dialog'}).append(
                $('<div/>').append(
                    $input
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'New Folder in ' + parentModel.get('name'),
                buttons: [
                    {
                        text: 'Create',
                        click: function () {
                            var model = new notes.model.Folder({
                                name: $input.val(),
                                parentId: parentModel.get('id'),
                                databaseId: parentModel.get('databaseId')
                            });
                            model.save(null, {
                                success: function () {
                                    $('#databases').databases('reloadTree');
                                }
                            });
                            $(this).dialog("close");
                        }
                    },
                    {
                        text: 'Cancel',
                        click: function () {
                            $(this).dialog("close");
                        }
                    }
                ]
            }));
    }
};

notes.dialog.database = {

    settings: function () {

        var $dialog = $('<div/>', {class: 'dialog'});

        $dialog.append(
                $('<div/>').append(
                    $('<a/>', {text: 'New Database'}).click(
                        function () {
                            $dialog.dialog('close');

                        })
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Settings'
            }));
    }
};

notes.dialog.document = {

    import: function () {

        var $dialog = $('<div/>', {class: 'dialog'});

        $dialog.append(
                $('<div/>').append(
                    $('<a/>', {text: 'From File'}).click(
                        function () {

                            var upload = $('#file-upload');

                            upload.click();

                            $dialog.dialog('close');

                        })
                )
            ).append(
                $('<div/>').append(
                    $('<a/>', {text: 'From Web'}).click(
                        function () {
                            $dialog.dialog('close');

                            notes.dialog.document.bookmark();
                        })
                )

            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Settings'
            }));
    },

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

                            var bookmark = new notes.model.Bookmark({
                                url: $input.val(),
                                folderId: $('#databases').databases('getActiveFolderId')
                            });
                            bookmark.save(null, {success: function (newmodel) {

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

                            $(this).dialog("close");
                        }
                    },
                    {
                        text: 'Cancel',
                        click: function () {
                            $(this).dialog("close");
                        }
                    }
                ]
            }));
    }
};


notes.dialog.settings = {

    general: function () {

        var $dialog = $('<div/>', {class: 'dialog'});

        $dialog.append(
                $('<div/>').append(
                    $('<a/>', {text: 'Export'}).click(
                        function () {
                            $dialog.dialog('close');
                        })
                )
            ).append(
                $('<div/>').append(
                    $('<a/>', {text: 'Import'}).click(
                        function () {
                            $dialog.dialog('close');
                            notes.dialog.document.import();
                        })
                )

            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Settings'
            }));
    }
}