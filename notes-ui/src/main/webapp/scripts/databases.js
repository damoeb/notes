/*global notes:false */
/*global REST_SERVICE:false */

'use strict';

$.widget('notes.databases', {

    url: REST_SERVICE + '/database/list',

    options: {
        databaseId: null
    },

    _create: function () {
        var $self = this;

        $self.reload();
    },

    _init: function () {
        var $self = this;
        $self.$tree = null;
    },

    _getModel: function () {
        return this.$tree.tree('getModel');
    },

    reloadTree: function () {
        this.$tree.tree('reload');
    },

    reload: function () {
        var $self = this;

        var $target = $self.element.empty();

        notes.util.jsonCall('GET', $self.url, null, null, function (list) {

            $.each(list, function (index, json) {

                var model = new notes.model.Database(json);

                var $tree = $('<ol/>', {class: 'tree'}).appendTo(
                    $target
                );

                $self.$tree = $tree.tree({
                    databaseId: model.get('id')
                });
            });

        });
    }
});
