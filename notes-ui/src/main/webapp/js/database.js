$.widget("notes.databaseList", {

    url: '/notes/rest/database/list',

    options: {
        databaseId: null
    },

    _create: function () {
        var $this = this;

        $this.reload();
        $this.models = {};
    },

    reload: function () {
        var $this = this;

        var editButton = $('<div/>', {class: 'ui-icon ui-icon-gear', style: 'float:right'}).click(function () {
            notes.dialog.database.settings();
        });
        var target = $this.element.empty()
            .append(
                $('<div/>', {text: 'Databases', class: 'group-header'}).append(
                    editButton
                )
            );

        notes.util.jsonCall('GET', $this.url, null, null, function (list) {

            $.each(list, function (index, json) {

                var model = new notes.model.database(json);

                $this.models[model.get('id')] = model;

                var item = $('<div/>', {class: 'group-item'}).append(
                        $('<div/>', {class: 'group-icon ui-icon ui-icon-clipboard'})
                    ).append(
                        $('<div/>', {text: model.get('name'), class: 'group-label'})
//                    ).append(
//                        $('<div/>', {text: model.get('documentCount'), class: 'group-doc-count ui-corner-all'})
                    ).append(
                        $('<div/>', {class: 'clear'})
                    ).appendTo(
                        target
                    ).click(function () {

                        target.find('.active').removeClass('active');
                        $(this).addClass('active');

                        $('#tree-view').directory({databaseId: model.get('id')});
                    });

                if ($this.options.databaseId == model.get('id')) {
                    item.addClass('active');
                }
            });

        });
    }
});
