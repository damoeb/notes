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

        var dialog = $('<div/>', {class: 'dialog'});

        dialog.append(
                $('<div/>').append(
                    $('<a/>', {text: 'New Folder'}).click(
                        function () {
                            dialog.dialog('close');
                            $this.newFolder(model);
                        })
                )
            ).append(
                $('<div/>').append(
                    $('<a/>', {text: 'Rename'}).click(
                        function () {
                            dialog.dialog('close');
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

        var input = $('<input/>', {name: 'name', type: 'text', value: model.get('name')});

        $('<div/>', {class: 'dialog'}).append(
                $('<div/>').append(
                    input
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Rename ' + model.get('name'),
                buttons: [
                    {
                        text: 'Rename',
                        click: function () {
                            model.set('name', input.val());
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

        var input = $('<input/>', {name: 'name', type: 'text'});

        $('<div/>', {class: 'dialog'}).append(
                $('<div/>').append(
                    input
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'New Folder in ' + parentModel.get('name'),
                buttons: [
                    {
                        text: 'Create',
                        click: function () {
                            var model = new notes.model.folder({
                                name: input.val(),
                                parentId: parentModel.get('id'),
                                databaseId: parentModel.get('databaseId')
                            });
                            model.save(null, {
                                success: function () {
                                    $('#directory').directory('reload');
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

        var dialog = $('<div/>', {class: 'dialog'});

        dialog.append(
                $('<div/>').append(
                    $('<a/>', {text: 'New Database'}).click(
                        function () {
                            dialog.dialog('close');

                        })
                )
            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Settings'
            }));
    }
};

notes.dialog.document = {

    import: function () {

        var dialog = $('<div/>', {class: 'dialog'});

        dialog.append(
                $('<div/>').append(
                    $('<a/>', {text: 'From File'}).click(
                        function () {

                            var upload = $('#file-upload');

                            upload.fileupload({
                                url: '/notes/rest/document/upload',
                                dataType: 'json'
                            });

                            upload
                                .bind('fileuploadsubmit', function (e, data) {
                                    var _folderId = $('#directory').directory('selectedFolder');
                                    data.formData = {folderId: _folderId};
                                })
                                .bind('fileuploaddone', function (e, data) {
                                    console.log('done');

                                    // todo add to table
                                });

                            upload.click();

                            dialog.dialog('close');

                        })
                )
            ).append(
                $('<div/>').append(
                    $('<a/>', {text: 'From Web'}).click(
                        function () {
                            dialog.dialog('close');

                        })
                )

            ).dialog($.extend({}, notes.dialog.defaults, {
                title: 'Settings'
            }));
    }
}