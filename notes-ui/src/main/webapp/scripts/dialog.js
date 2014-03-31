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
                databaseId: notes.folders.databaseId()
            });
            model.save(null, {
                success: function () {
                    $('#databases').database('reload');
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