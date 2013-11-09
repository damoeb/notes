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
                    $('<a/>', {text: 'New Folder'})
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
        var $this = this;

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