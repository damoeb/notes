$.widget("notes.editors", {

    options: {
        syncInterval: 15000 // msec
    },

    _create: function () {
        var $this = this;

        $this.doc2Tab = {};
        $this.nav = $('<ul/>', {style: 'border-right:hidden; border-left:hidden; border-top:hidden; background:none; border-radius:0;'}).appendTo($this.element);
        $this.uniqueId = parseInt(Math.random() * 10000);

        $this.element.tabs();
//        // -- events
//        setInterval(function () {
//
//            // todo $this.syncModel();
//
//        }, $this.options.syncInterval);

    },

    edit: function (documentId, unloadCallback) {

        var $this = this;

        if (!documentId) {
            throw 'document id is null.'
        }

        notes.util.jsonCall('GET', '/notes/rest/document/${documentId}', {'${documentId}': documentId}, null, function (document) {

            var settings = {
                kind: document.kind,
                unloadCallback: unloadCallback
            };

            var model = new notes.model.Document(document);
            model.set('event', 'UPDATE');

            $this.loadDocument(settings, model);
        });
    },

    createDocument: function () {
        var $this = this;
        var kindString = 'text';

        var settings = {kind: kindString};
        $this.loadDocument(settings, new notes.model.Document({
            folderId: $('#database').database('selectedFolder')
        }));
    },

    _getUniqueId: function () {
        return this.uniqueId++;
    },

    loadDocument: function (settings, model) {
        var $this = this;

        var tabExists = model.has('id') && !(typeof($this.doc2Tab[model.get('id')]) === 'undefined');

        if (tabExists) {
            // open requested tab
            $this.element.tabs({active: $this.doc2Tab[model.get('id')]});
        } else {

            var maxTitleLen = 25;

            var documentId = model.has('id') ? model.get('id') : $this._getUniqueId();

            var tabTitle = model.has('title') ? model.get('title') : 'New';
            var shortTabTitle = tabTitle;
            if (shortTabTitle.length > maxTitleLen) {
                shortTabTitle = shortTabTitle.substr(0, maxTitleLen / 2) + '..' + shortTabTitle.substr(shortTabTitle.length - maxTitleLen / 2, shortTabTitle.length);
            }

            var uniqueId = 'document-' + documentId;
            var $tabContent = $('<div/>', {id: uniqueId, class: 'container'});

            var $tabHeader = $('<li/>').append(
                    $('<a/>', {href: '#' + uniqueId, text: shortTabTitle, title: tabTitle})
                ).append(
                    $('<span/>', {class: 'ui-icon ui-icon-close', role: 'presentation', text: 'Remove Tab'}).click(function () {
                        $tabContent.remove();
                        delete $this.doc2Tab[documentId];
                        $tabHeader.remove();

                        // todo select other tab if available
//                        var newTabId;
//                        for(newTabId in $this.doc2Tab) break;
//
//                        $this.element.tabs( {active: newTabId} );
                    })
                );
            $this.nav.append($tabHeader);

            switch (settings.kind.toLowerCase().trim()) {
                case 'text':
                    $tabContent.texteditor({model: model});
                    break;
                case 'pdf':
                    $tabContent.pdfeditor({model: model});
                    break;
            }

            var tabId = $.map($this.doc2Tab,function (n, i) {
                return i;
            }).length;
            $this.doc2Tab[documentId] = tabId;
            $this.element.append($tabContent);
            $this.element.tabs("refresh");
            $this.element.tabs({active: tabId});
        }

    }
})
