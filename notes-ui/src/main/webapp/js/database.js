$.widget("notes.databaseList", {

    options: {
        name: 'Group',
        databaseId: null,
        url: null
    },

    _create: function () {
        var $this = this;

        $this.reload();
        $this.models = {};
    },

    reload: function () {
        var $this = this;

        var target = $this.element.empty()
            .append(
                $('<div/>', {text: $this.options.name, class: 'group-header'})
            );

        notes.util.jsonCall('GET', $this.options.url, null, null, function (list) {

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

                        $('#tree-view').treeView({databaseId: model.get('id')});
                    });

                if ($this.options.databaseId == model.get('id')) {
                    item.addClass('active');
                }
            });

        });
    }
});
