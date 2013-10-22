$.widget("notes.notes", {

    options:{
        url: '/notes/rest/note/list'
    },

    // table

    _init:function () {
        var $this = this;

        if(!$this.options.url) {
            console.error('url is not set');
            throw 'url is not set';
        }

        var target = $this.element.empty();

        var table = $('<table></table>');

        target.append(table);

        $this.table = table.dataTable({
            'bPaginate': false,
            'sScrollY': "200px",
            'bFilter': false,
            'bInfo': false,
            'aoColumns':[
                { 'sTitle':'<div class="column-1"><span class="ui-icon ui-icon-star" style="display:inline-block"></span></div>' },
                { 'sTitle':'<div class="column-2">Id</div>', 'bVisible': false },
                { 'sTitle':'<div class="column-3">Name</div>' },
                { 'sTitle':'<div class="column-4">Modified</div>' },
                { 'sTitle':'<div class="column-5">Kind</div>' },
                { 'sTitle':'<div class="column-6">Size</div>' }
            ],
            'aaSorting': [[ 3, 'desc' ]]
        });


        $this.refresh();
    },

    refresh:function() {
        var $this = this;

        $this.table.fnClearTable();

        notes.util.jsonCall('GET', $this.options.url, null, null, function (response) {


            var _currentTs = new Date().getTime();
            var data = [];
            for (var id in response.list) {
                var note = response.list[id];

                var _modifiedTs = notes.util.toTimestamp(note.modified);

                var _lastModified;
                if(_currentTs-_modifiedTs < 24 * 60 * 60 * 1000) {
                    _lastModified = jQuery.timeago(_modifiedTs);
                } else {
                    _lastModified = note.modified;
                }
                var _sizeStr = notes.util.formatBytesNum(note.size);

                data.push([
                    '<div class="column-1"><span class="ui-icon ui-icon-star" style="display:inline-block"></span></div>',
                    note.id,
                    '<div class="column-3"><div class="list-item-title">'+note.title+'</div><div class="list-item-preview" style="white-space: nowrap; width: 450px; overflow:hidden;">'+note.preview+'</div></div>',
                    '<div class="column-4"><span style="display:none">'+_modifiedTs+'</span> <span style="white-space: nowrap">'+ _lastModified+'</span></div>',
                    '<div class="column-5">'+note.kind+'</div>',
                    '<div class="column-6"><span style="white-space: nowrap">'+ _sizeStr + '</span></div>'
                ]);
            }

            $this.table.fnAddData(
                data
            );

            $this.table.find('td').click( function () {
                var aPos = $this.table.fnGetPosition( this );

                // Get the data array for this row
                var aData = $this.table.fnGetData( aPos[0] );
                var noteId = aData[1];

                $('#n-editor').editor('edit', noteId);

            });

        });

//        $('.button').live('click', function () {
//
//            var pos = $this.oTable.dataTable().fnGetPosition($(this).parent()[0]);
//            var feedId = $(this).parent().parent().children().first().text();
//
//            if($(this).hasClass('status')) {
//                var activate = $(this).hasClass('activate');
//                curator.util.jsonCall('POST', '/curator/rest/feed/status/{id}/'+activate, {'{id}':feedId}, null, function (feed) {
//                    $this.oTable.dataTable().fnUpdate('<span class="button status">'+(activate?'Deactivate':'Activate')+'</span>', pos[0], pos[1]);
//                })
//            } else
//
//            if($(this).hasClass('harvest')) {
//                curator.util.jsonCall('POST', '/curator/rest/feed/harvest/{id}/', {'{id}':feedId}, null, function (feed) {
//                    $this.oTable.dataTable().fnUpdate('Scheduled', pos[0], pos[1]);
//                })
//            }
//        });




    },

    _create:function () {

    }

});
