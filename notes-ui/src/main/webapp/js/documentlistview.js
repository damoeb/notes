$.widget("notes.documentListView", {
    options: {

    },
    _create: function () {
        this.table = $('<table></table>')
            .appendTo(this.element)
            .dataTable({
                'aaData': {},
                'bFilter': false,
                'bInfo': false,
                'bStateSave': true,
                'bPaginate': false,
                'aoColumns': [
                    { 'sTitle': 'Id', sClass: 'column-s folder-id' },
                    { 'sTitle': 'Name' },
                    { 'sTitle': 'Date', sClass: 'column-l' },
                    { 'sTitle': 'Kind', sClass: 'column-s' },
                    { 'sTitle': 'Size', sClass: 'column-m' }
                ],
                'fnDrawCallback': function () {
                    var $this = this;
                    $this.find('.folder-id').each(function () {
                        $(this).
                            removeClass('folder-id').
                            addClass('folder-id-' + $(this).text());
                    });

                    // todo should only be done once
                    $this.find('td')
                        .dblclick(function () {
                            var aPos = $this.fnGetPosition(this);

                            // Get the data array for this row
                            var aData = $this.fnGetData(aPos[0]);
                            var documentId = aData[0];
                            var kind = aData[3];

                            // call editor
                            $('#editor').editor('edit', documentId, kind);

                        })
                        .click(function () {
                            // highlight
                            $this.find('tr.active').removeClass('active');
                            $(this).parent('tr').addClass('active');
                        });

                }
            });
    },
    _init: function () {
        var $this = this;

        if ($this.options.folderId && $this.options.folderId > 0) {
            $this._fetch($this.options.folderId);
        }
    },
    _fetch: function (folderId) {
        var $this = this;

        notes.util.jsonCall('GET', '/notes/rest/folder/${folderId}/documents', {'${folderId}': folderId}, null, function (documents) {

            var data = [];
            for (var id in documents) {
                var doc = documents[id];

                data.push([
                    doc.id,
                    $this._createTitleText(doc.title, doc.outline),
                    $this._createDateElement(doc.modified),
                    doc.kind,
                    $this._createSizeElement(doc.size)
                ]);
            }

            $this.table.find('td').unbind('dblclick');
            // todo $this.table.find('tr').draggable('destroy');

            var dataTable = $this.table.dataTable();
            dataTable.fnClearTable();
            dataTable.fnAddData(data);
            dataTable.find('tr').draggable({helper: "clone", opacity: 0.5, scope: 'folder'});

        });
    },

    _createDateElement: function (dateString) {
        // todo <span hidden>long</span> datestring
        return notes.util.formatDate(dateString)
    },
    _createSizeElement: function (bytes) {
        // todo <span hidden>long</span> string
        return notes.util.formatBytesNum(bytes)
    },
    _createTitleText: function (title, description) {
        return '<div class="doc-title">' + title + '</div><div class="doc-outline">' + description + '</div>';
    },

    updateDocument: function (model) {
        var $this = this;
        var element = $this.table.find('.folder-id-' + model.get('id'));

        var dataTable = $this.table.dataTable();

        var data = [
            model.get('id'),
            $this._createTitleText(model.get('title'), model.get('outline')),
            $this._createDateElement(model.get('modified')),
            model.get('kind'),
            $this._createSizeElement(model.get('size'))
        ];

        if (element.length == 0) {
            // add line
            dataTable.fnAddData(data);
            var item = $('#tree-view').treeView('getItem', model.get('folderId'));
            var model = item.model();
            model.set('documentCount', model.get('documentCount') + 1);
            item.refresh();

        } else {
            var aPos = dataTable.fnGetPosition(element[0]);
            dataTable.fnUpdate(data, aPos[0])
        }
    }

});
