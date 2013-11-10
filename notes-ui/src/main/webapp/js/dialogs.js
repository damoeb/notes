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
                    $('<a/>', {text: 'Share'})
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
                            model.save();
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
                                    console.log('success');
                                    $('#tree-view').treeView('reload');
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
}