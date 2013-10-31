$.widget("notes.documentListView", {
    options: {
    },
    _init: function () {
        this._reset();
    },
    _reset: function () {
        if (this.oTable) {
            this.oTable.fnDestroy();
        }
        this.element.empty();
    },
    _create: function () {
        var $this = this;
        $this._reset();

        var table = $('<table></table>');

        $this.element.append(table);

        notes.util.jsonCall('GET', '/notes/rest/folder/${folderId}/documents', {'${folderId}': $this.options.folderId}, null, function (documents) {

            var data = [];
            for (var id in documents) {
                var doc = documents[id];

                data.push([
                    doc.id,
                    $this._createTitleText(doc.title, doc.description),
                    notes.utils.formatDate(doc.modified),
                    doc.kind,
                    notes.util.formatBytesNum(doc.size)
                ]);
            }

            $this.oTable = table.dataTable({
                'aaData': data,
                'bFilter': false,
                'bInfo': false,
                'bStateSave': true,
                'bPaginate': false,
                'aoColumns': [
                    { 'sTitle': 'Id', 'bVisible': false },
                    { 'sTitle': 'Name' },
                    { 'sTitle': 'Date' },
                    { 'sTitle': 'Kind' },
                    { 'sTitle': 'Size' }
                ]
            });

            $this.oTable.find('td').click(function () {
                var aPos = $this.oTable.fnGetPosition(this);

                // Get the data array for this row
                var aData = $this.oTable.fnGetData(aPos[0]);
                var documentId = aData[0];
                var kind = aData[4];

                console.log(documentId);
                //$('#n-editor').editor('open', noteId);

                $('#editor').editor(documentId, kind);

            });

        });
    },

    _createTitleText: function (title, description) {
        return title + ' ' + description;
    }

});
