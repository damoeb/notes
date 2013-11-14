$.widget("notes.documentList", {
    constants: {
        doc: {
            related: 'doc-origin-related',
            native: 'doc-origin-native'
        }
    },
    options: {

    },
    _create: function () {

        var $this = this;

        this.table = $('<table></table>')
            .appendTo(this.element)
            .dataTable({
                'aaData': {},
                'bFilter': false,
                'bInfo': false,
                'bStateSave': true,
                'bPaginate': false,
                'aaSorting': [
                    [ 3, 'desc' ]
                ],
                'aoColumns': [
                    { 'sTitle': 'Id', sClass: 'column-s' },
                    { 'sTitle': 'Origin', sClass: 'column-s', bVisible: false },
                    { 'sTitle': 'Name', sClass: 'column-text'},
                    { 'sTitle': 'Date', sClass: 'column-l' },
                    { 'sTitle': 'Kind', sClass: 'column-s center' },
                    { 'sTitle': 'Size', sClass: 'column-m' }
                ],
                'oLanguage': {
                    'sEmptyTable': "My Custom Message On Empty Table"
                },
                'fnRowCallback': function (nRow, aData, iDisplayIndex) {

                    var row = $(nRow);

                    row.children().first().addClass('folder-id-' + aData[0]);

                    // adds origin of document
                    row.addClass(aData[1]);

                    row.dblclick(function () {

                        var documentId = aData[0];
                        var kind = $(aData[4]).attr('kind');

                        // call editor
                        $('#editor').editor('edit', documentId, kind, function () {

                        });
                    })
                        .click(function () {
                            // highlight
                            $this.table.find('tr.highlighted').removeClass('highlighted');
                            row.addClass('highlighted');
                        });


                    return nRow;
                }
            });
    },
    _init: function () {
        var $this = this;

        var folderId = $this.options.folderId;
        if (folderId && folderId > 0) {
            $this._fetch(folderId);
        }
    },
    _fetch: function (folderId) {
        var $this = this;

        var sources = [
            {
                url: '/notes/rest/folder/${folderId}/documents',
                origin: $this.constants.doc.native,
                params: {'${folderId}': folderId}
            },
            {
                url: '/notes/rest/folder/${folderId}/related-documents?offset=${offset}&count=${count}',
                origin: $this.constants.doc.related,
                params: {
                    '${folderId}': folderId,
                    '${offset}': '0',
                    '${count}': 100
                }
            }
        ];

        $this.table.dataTable().fnClearTable();

        $.each(sources, function (index, source) {

            notes.util.jsonCall('GET', source.url, source.params, null, function (documents) {

                var data = [];
                for (var id in documents) {
                    var doc = documents[id];

                    data.push([
                        doc.id,
                        source.origin,
                        $this._createTitleText(doc),
                        $this._createDateElement(doc.modified),
                        $this._createKindElement(doc),
                        $this._createSizeElement(doc.size)
                    ]);
                }

                $this.table.find('td').unbind('dblclick');
                // todo $this.table.find('tr').draggable('destroy');

                var dataTable = $this.table.dataTable();
                dataTable.fnAddData(data);
                dataTable.find('tr').draggable({helper: "clone", opacity: 0.5, scope: 'folder'});

            });
        });
    },

    _createDateElement: function (isoDateString) {

        var date = new Date(isoDateString);

        return $('<div/>').append(
                $('<span/>', {class: 'hidden', text: date.getTime()})
            ).append(
                $('<span/>', {text: notes.util.formatDate(date)})
            ).html()
    },

    _createKindElement: function (document) {

        var kind = document.kind;

        if (typeof(document.reminderId) != 'undefined') {
            kind += ' + ALARM'
        }

        return $('<div/>').append(
            $('<span/>', {kind: document.kind, text: kind})
        ).html()
    },

    _createSizeElement: function (bytes) {

        return $('<div/>').append(
                $('<span/>', {class: 'hidden', text: bytes})
            ).append(
                $('<span/>', {text: notes.util.formatBytesNum(bytes)})
            ).html()
    },

    _createTitleText: function (document) {
        var text = '<div class="doc-title">' + document.title + '</div><div class="doc-outline">' + document.outline + '</div>';

        if (document.progress) {
            text += '<div class="progress-of-task ui-corner-all" style="width:' + parseInt(document.progress) + '%; height:4px;"></div>';
        }

        return text;
    },

    updateDocument: function (model) {
        var $this = this;
        var element = $this.table.find('.folder-id-' + model.get('id'));

        var dataTable = $this.table.dataTable();

        var data = [
            model.get('id'),
            $this.constants.doc.native,
            $this._createTitleText({
                title: model.get('title'),
                outline: model.get('outline'),
                progress: model.get('progress')
            }),
            $this._createDateElement(model.get('modified')),
            $this._createKindElement({
                kind: model.get('kind'),
                reminderId: model.get('reminderId')
            }),
            $this._createSizeElement(model.get('size'))
        ];

        if (element.length == 0) {
            // add line
            dataTable.fnAddData(data);
            var item = $('#directory').directory('folder', model.get('folderId'));
            var model = item.model();
            model.set('documentCount', model.get('documentCount') + 1);
            item.refresh();

        } else {
            var aPos = dataTable.fnGetPosition(element[0]);
            dataTable.fnUpdate(data, aPos[0])
        }
    },

    deleteDocument: function (model) {
        var $this = this;
        var element = $this.table.find('.folder-id-' + model.get('id'));

        var dataTable = $this.table.dataTable();

        var aPos = dataTable.fnGetPosition(element[0]);
        dataTable.fnDeleteRow(aPos[0])
    }

});
