$.widget("notes.documentList", {

    _init: function () {
        var $this = this;

        this.template = _.template('<div class="doc doc-id-<%=id %> container">' +
            '<div class="col-lg-1">' +
            '<div class="row"><input type="checkbox"></div>' +
            '<div class="row"><i class="fa fa-flag fa-fw"></i></div>' +
            '<div class="row"><i class="fa fa-star-o fa-fw"></i></div>' +
            '</div>' +
            '<div class="col-lg-11"><div class="row"><%=title %> <span style="float:right"><%=kind %></span></div>' +
            '<div class="row"><%=text %></div>' +
            '<div class="row"><%=date %></div></div></div>')

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
                params: {'${folderId}': folderId}
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

        var $target = $this.element.empty();

        $.each(sources, function (index, source) {

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
