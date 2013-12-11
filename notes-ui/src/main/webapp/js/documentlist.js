$.widget("notes.documentList", {

    _init: function () {
        var $this = this;

        this.template = _.template($('#document-in-list').html());

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

        var $header = $('<div/>', {text: 'In ' + $this._resolveFolderName(folderId), style: 'padding:10px; margin-bottom:10px; background-color:#efefef'}).appendTo($this.element);

        var $native = $('<div/>').appendTo($this.element);
        var $descendants = $('<div/>', {style: 'border-top: 3px solid #efefef; padding-top:5px'}).appendTo($this.element);

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

    _resolveFolderName: function (folderId) {
        var $folder = $('#databases').databases('get$Folder', folderId);
        return $folder.model().get('name');

    },

    _getThumbnail: function (kind) {
        switch (kind.toLowerCase()) {
            case 'pdf':
                return 'img/kind/pdf.png';
            case 'bookmark':
                return 'img/kind/url.png';
            default:
            case 'text':
                return 'img/kind/text.png';
        }
    },

    _render: function (model) {

        var values = {
            id: model.get('id'),
            title: model.get('title'),
            text: model.get('outline'),
            kind: model.get('kind'),
            views: model.get('views'),
            privacy: model.get('privacy'),
            star: model.get('star'),
            reminder: model.get('reminder'),
            thumbnail: this._getThumbnail(model.get('kind')),
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
