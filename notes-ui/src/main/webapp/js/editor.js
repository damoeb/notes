$.widget("notes.editor", {

    options: {
        syncInterval: 15000 // msec
    },

    kinds: {
        text: {
            model: Backbone.Model.extend({
                defaults: {
                    title: '',
                    text: ''
                },
                url: '/notes/rest/document/text/'
            }),
            url: '/notes/rest/document/text/${documentId}',
            templateId: '#tmpl-text-editor',
            fnLoad: '_loadTextEditor',
            fnUnload: '_unloadTextEditor'
        }
    },

    _create: function () {
        var $this = this;

        // -- events
        setInterval(function () {

            // todo $this._syncDocument();

        }, $this.options.syncInterval);

    },

    edit: function (documentId, kindString) {

        var $this = this;

        if (!documentId) {
            throw 'document id is null.'
        }

        var kind = $this.kinds[kindString.toLowerCase()];
        if (!kind) {
            throw 'kind is null.'
        }
        if (!kind) {
            throw 'kind ' + kindString + ' is supported.'
        }

        var url = kind.url;

        notes.util.jsonCall('GET', url, {'${documentId}': documentId}, null, function (document) {
            $this.documentId = document.id;

            $this.loadDocument(kind, new kind.model(document));
        });
    },

    loadDocument: function (kind, model) {
        var $this = this;

        console.log(model.get('folderId'));
        $this[kind.fnLoad](model);
    },

    createDocument: function () {
        var $this = this;
        var kindString = 'text';

        var kind = $this.kinds[kindString.toLowerCase()];
        $this.loadDocument(kind, new kind.model({
            folderId: $('#tree-view').treeView('selectedFolder')
        }));
    },

    // -- TEXT EDITOR --------------------------------------------------------------------------------------------------

    // todo: models to models.js
    // listener for changes

    _loadTextEditor: function (model) {
        var $this = this;

        var fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('title')});
        var fieldText = $('<textarea/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('text')});

        var target = $this.element.empty().show().addClass('container');

        var header = $('<div/>', {class: 'row'}).append(
                $('<button/>').button({
                    label: 'Close',
                    icons: {
                        primary: 'ui-icon-arrowreturn-1-w'
                    }
                }).click(
                    function () {
                        model.set('title', fieldTitle.val());
                        model.set('text', fieldText.val());
                        model.save(null, {success: function () {

                            $('#document-list-view').documentListView('updateDocument', model);

                        }});

                        $this._unloadTextEditor(model);
                    }
                )
            ).append(
                $('<button/>', {style: 'float:right'}).button({
                    label: 'Maximize',
                    icons: {
                        primary: 'ui-icon-arrow-4-diag'
                    }
                }).click(
                    function () {
                        // todo implement
                    }
                )
            );
        target.append(
                header
            ).append(
                $('<div/>', {class: 'row'}).append(
                    fieldTitle
                )
            ).append(
                $('<div/>', {class: 'row'}).append(
                    fieldText
                )
            );
    },

    _unloadTextEditor: function (model) {
        // todo implement
        this.element.hide();
    }
});
