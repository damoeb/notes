$.widget("notes.documentList", {

    _init: function () {
        var $this = this;

        this.template = _.template('<div class="doc doc-id-<%=id %> container ui-draggable">' +
            '<div class="col-lg-2">' +
            '<img src="https://cdn1.iconfinder.com/data/icons/FileTypesIcons/80/readme.png">' +
            '</div>' +
            '<div class="col-lg-9">' +
            '<div class="row" style="font-weight:bold">' +
            '<%=title %>' +
            '<i class="fa fa-star" style="color:red"></i>' +
            '</div>' +
            '<div class="row">' +
            '<%=date %> ago - by <span style="font-weight:bold">damoeb</span> - 1,321 views' +
            '</div>' +
            '<div class="row">' +
            '<%=text %>' +
            '</div>' +
            '</div>' +
            '<div class="col-lg-1">' +
            '<div class="row" title="visibility: public">' +
            '<i class="fa fa-circle" style="color:green"></i>' +
            '</div>' +
            '<div class="row" title="in 82 days">' +
            '<i class="fa fa-bell-o"></i>' +
            '</div></div></div>')

        var folderId = $this.options.folderId;
        if (folderId && parseInt(folderId) > 0) {
            $this._fetch(folderId);
        }
    },
    _fetch: function (folderId) {
        var $this = this;

        var sources = [
            {
                url: '/notes/rest/folder/${folderId}/documents',
                params: {'${folderId}': folderId},
                type: 'native'
            },
            {
                url: '/notes/rest/folder/${folderId}/related-documents?offset=${offset}&count=${count}',
                params: {
                    '${folderId}': folderId,
                    '${offset}': '0',
                    '${count}': 100
                }
            }
        ];

        $this.element.empty();

        var $native = $('<div/>').appendTo($this.element);
        var $descendants = $('<div/>').append('<div>Some descendants</div>').appendTo($this.element);

        $.each(sources, function (index, source) {

            var $target;
            if (source.type == 'native') {
                $target = $native;
            } else {
                $target = $descendants;
            }

            notes.util.jsonCall('GET', source.url, source.params, null, function (documents) {

                $.each(documents, function (id, doc) {
                    $this._render(new notes.model.Document(doc)).appendTo($target);
                });
            });
        });
    },

    _render: function (model) {

        var values = {
            id: model.get('id'),
            title: model.get('title'),
            text: model.get('outline'),
            kind: model.get('kind'),
            date: notes.util.formatDate(new Date(model.get('modified')))
        };

        return $(this.template(values)).click(function () {

            var $tmpl = $(this);

            // highlight
            $tmpl.addClass('active');

            // call editor
            $('#editors').editors('edit', values.id, function () {
                $tmpl.removeClass('active');
            });
        }).draggable({
                cursor: 'move',
                cursorAt: { top: 5, left: -5 },
                helper: function () {
                    return $('<i class="fa fa-file-o fa-lg"></i>');
                },
                opacity: 0.6
            })
    },

    updateDocument: function (model) {
        var $this = this;
        var $element = $this.element.find('.doc-id-' + model.get('id'));

        if ($element.length == 0) {
            // append
            $this._render(model).appendTo($this.element);

        } else {
            // replace
            $element.replaceWith($this._render(model));
        }
    },

    deleteDocument: function (model) {
        var $this = this;
        $this.element.find('.doc-id-' + model.get('id')).remove()
    },

    _destroy: function () {
        // todo implement widget method
    }

});
