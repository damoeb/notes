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
                    { 'sTitle': 'Id', sClass: 'column-s' },
                    { 'sTitle': 'Name' },
                    { 'sTitle': 'Date', sClass: 'column-l' },
                    { 'sTitle': 'Kind', sClass: 'column-s' },
                    { 'sTitle': 'Size', sClass: 'column-m' }
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

            $this.table.find('td').unbind('dblclick');
            // todo $this.table.find('tr').draggable('destroy');

            var dataTable = $this.table.dataTable();
            dataTable.fnClearTable();
            dataTable.fnAddData(data);

            dataTable.find('td')
                .dblclick(function () {
                    var aPos = dataTable.fnGetPosition(this);

                    // Get the data array for this row
                    var aData = dataTable.fnGetData(aPos[0]);
                    var documentId = aData[0];
                    var kind = aData[3];

                    // call editor
                    $('#editor').editor('edit', documentId, kind);

                })
                .click(function () {
                    // highlight
                    $this.table.find('tr.active').removeClass('active');
                    $(this).parent('tr').addClass('active');
                });

            dataTable.find('tr').draggable({helper: "clone", opacity: 0.5, scope: 'folder'});

        });
    },

    _createTitleText: function (title, description) {
        return title + ' ' + description;
    }

});
