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
                    { 'sTitle': 'Id'},
                    { 'sTitle': 'Name' },
                    { 'sTitle': 'Date' },
                    { 'sTitle': 'Kind' },
                    { 'sTitle': 'Size' }
                ]
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
                    $this._createTitleText(doc.title, doc.description),
                    notes.util.formatDate(doc.modified),
                    doc.kind,
                    notes.util.formatBytesNum(doc.size)
                ]);
            }

            $this.table.find('td').unbind('click');

            var dataTable = $this.table.dataTable();
            dataTable.fnClearTable();
            dataTable.fnAddData(data);

            dataTable.find('td').click(function () {
                var aPos = dataTable.fnGetPosition(this);

                // Get the data array for this row
                var aData = dataTable.fnGetData(aPos[0]);
                var documentId = aData[0];
                var kind = aData[3];

                $('#editor').editor('edit', documentId, kind);

            });

        });
    },

    _createTitleText: function (title, description) {
        return title + ' ' + description;
    }

});
