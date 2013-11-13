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
            fnLoad: '_loadTextEditor'
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

        $this[kind.fnLoad](model);
    },

    createDocument: function () {
        var $this = this;
        var kindString = 'text';

        var kind = $this.kinds[kindString.toLowerCase()];
        $this.loadDocument(kind, new kind.model({
            folderId: $('#directory').directory('selectedFolder')
        }));
    },

    // -- TEXT EDITOR --------------------------------------------------------------------------------------------------

    // todo: models to models.js
    // listener for changes

    _loadTextEditor: function (model) {
        var $this = this;

        var fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('title')});
        var fieldText = $('<textarea/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('text')});

        var target = $this.element.empty().show().addClass('container text-editor');


        // todo fix slider, does not appear as intendet
        var value = model.has('progress') ? model.get('progress') : 0;
        var slider = $('<div/>').slider({
            range: "min",
            value: value,
            min: 0,
            max: 100,
            slide: function (event, ui) {
                console.log(ui.value);
                model.set('progress', ui.value);
            }
        });

        var progress = $('<div/>', {class: 'row'}).append(
                $('<label/>', {text: 'Progress'})
            ).append(
                slider
            );
        if (value <= 0) {
            //progress.hide();
        }


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

                            $('#document-list-view').documentList('updateDocument', model);

                        }});

                        $this._unloadTextEditor();
                    }
                )
            ).append(
                $('<button/>').button({
                    label: 'Delete',
                    icons: {
                        primary: 'ui-icon-trash'
                    }
                }).click(function () {
                        $('#document-list-view').documentList('deleteDocument', model);
                        // todo destory does not work
                        model.destroy();
                        $this._unloadTextEditor();
                    })
            ).append(
                $('<button/>').button({
                    label: 'Reminder',
                    icons: {
                        primary: 'ui-icon-clock'
                    }
                }).click(function () {
                        progress.slideToggle();
                    })
            ).append(
                $('<button/>').button({
                    label: 'Progress',
                    icons: {
                        primary: 'ui-icon-signal'
                    }
                }).click(function () {
                        // todo implement
                    })
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
                progress
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

    _unloadTextEditor: function () {
        // todo implement
        this.element.hide();
    }
});
