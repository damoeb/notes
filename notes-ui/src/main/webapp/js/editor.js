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
        var template = $(_.template($(kind.templateId).html(), model.toJSON()));
        $this.element.empty().append(template);

        $this[kind.fnLoad](template, model);
    },

    createDocument: function () {
        var $this = this;
        var folderId = $('#tree-view').treeView('activeFolder');
        console.log('create in ' + folderId);
        var kindString = 'text';

        var kind = $this.kinds[kindString.toLowerCase()];
        $this.loadDocument(kind, new kind.model({}));
    },

    // -- TEXT EDITOR --------------------------------------------------------------------------------------------------

    _loadTextEditor: function (template, model) {
        var $this = this;
        template.find('#close-view').button().click(function () {
            // todo close
            $this._syncTextModel(template, model);
        });
        template.find('#maximize-view').button().click(function () {
            model.destroy();
            $this._unloadTextEditor(template, model);
            $this._refreshListAfterModelUpdate(model);
        });
        template.find('#safe-doc').button().click(function () {
            $this._syncTextModel(template, model)
        });
        template.find('#delete-doc').button().parent().buttonset();
    },

    _syncTextModel: function (template, model) {
        var $this = this;
        model.set('title', template.find('#field-title').val());
        model.set('text', template.find('#field-text').val());
        model.save();
        $this._refreshListAfterModelUpdate(model);
    },

    _refreshListAfterModelUpdate: function (model) {

    },

    _unloadTextEditor: function (template, model) {
        template.find('#close-view').button('destroy');
        template.find('#maximize-view').button('destroy');
        template.find('#safe-doc').button('destroy');
        template.find('#delete-doc').button('destroy');
    }
});
