$.widget("notes.documentList", {
    constants: {
        doc: {
            related: 'doc-origin-related',
            native: 'doc-origin-native'
        }
    },
    _create: function () {

        var $this = this;

        this.$table = $('<table></table>')
            .appendTo(this.element)
            .dataTable({
                'aaData': {},
                'bFilter': false,
                'bInfo': false,
                'bStateSave': true,
                'bPaginate': false,
                'aaSorting': [
                    [ 4, 'desc' ]
                ],
                'aoColumns': [
                    { 'sTitle': 'Id', sClass: 'column-s' },
                    { 'sTitle': 'Origin', sClass: 'column-s', bVisible: false },
                    { 'sTitle': 'Name', sClass: 'column-text'},
                    { 'sTitle': 'Date', sClass: 'column-l' },
                    { 'sTitle': 'Kind', sClass: 'column-s center no-sort' }
                ],
                'aoColumnDefs': [
                    {
                        'bSortable': false,
                        'aTargets': [ 'no-sort' ]
                    }
                ],
                'oLanguage': {
                    'sEmptyTable': "My Custom Message On Empty Table"
                },
                'fnRowCallback': function (nRow, aData, iDisplayIndex) {

                    var $row = $(nRow);

                    var documentId = aData[0];
                    $row.children().first().addClass('document-id-' + documentId);

                    // todo destroy this event too
                    $row.data('documentId', documentId);

                    $row.unbind('click dblclick draggable');

                    $row.draggable({
                        cursor: 'move',
                        cursorAt: { top: 5, left: -5 },
                        helper: function (event) {
                            return $('<div class="ui-widget-header">Document ' + documentId + '</div>');
                        },
                        opacity: 0.6
                    });


                    // adds origin of document
                    $row.addClass(aData[1]);

                    // -- Events --

                    $row.click(function () {

                        var documentId = aData[0];

                        // call editor
                        $('#editors').editors('edit', documentId, function () {
                            $row.removeClass('highlighted');
                        });
                        // highlight
                        $this.$table.find('tr.highlighted').removeClass('highlighted');
                        $row.addClass('highlighted');
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

        $this.$table.dataTable().fnClearTable();

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
                        $this._createKindElement(doc)
                    ]);
                }

                $this.$table.find('td').unbind('dblclick');

                var $dataTable = $this.$table.dataTable();
                $dataTable.fnAddData(data);
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

    _createTitleText: function (document) {
        var text = '<div class="doc-title">' + document.title + '</div><div class="doc-outline">' + document.outline + '</div>';

        if (document.progress) {
            text += '<div class="progress-of-task ui-corner-all" style="width:' + parseInt(document.progress) + '%; height:4px;"></div>';
        }

        return text;
    },

    _createSpecialElement: function (document) {
        return '<span class="ui-icon ui-icon-star"></span>';
    },

    updateDocument: function (model) {
        var $this = this;
        var $element = $this.$table.find('.document-id-' + model.get('id'));

        var $dataTable = $this.$table.dataTable();

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
            })
        ];

        if ($element.length == 0) {
            // add line
            $dataTable.fnAddData(data);
            var item = $('#directory').directory('folder', model.get('folderId'));
            var model = item.model();
            model.set('documentCount', model.get('documentCount') + 1);
            item.refresh();

        } else {
            var aPos = $dataTable.fnGetPosition($element[0]);
            $dataTable.fnUpdate(data, aPos[0])
        }
    },

    deleteDocument: function (model) {
        var $this = this;
        var $element = $this.$table.find('.document-id-' + model.get('id'));

        var $dataTable = $this.$table.dataTable();

        var aPos = $dataTable.fnGetPosition($element[0]);
        $dataTable.fnDeleteRow(aPos[0])
    },

    _destroy: function () {
        // todo implement widget method
    }

});
