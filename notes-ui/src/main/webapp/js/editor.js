$.widget("notes.editor", {

    options: {
        syncInterval: 15000 // msec
    },

    _create: function () {
        var $this = this;

        setInterval(function () {

            // todo $this._syncDocument();

        }, $this.options.syncInterval);

    },

    _init: function () {
        var $this = this;
        var editor = $this.element;

        // todo make map of editor implementations by document-kind
        var _impl = {}
        _impl['text'] = 'textEditor';
        _impl['rtf'] = 'rtfEditor';
    },

    edit: function (documentId) {

        var $this = this;

        if (!documentId) {
            throw 'note id is null.'
        }

        $this._sync(false);

        notes.util.jsonCall('GET', '/notes/rest/note/' + documentId, null, null, function (note) {
            $this.noteId = note.id;
            $this.container.title.val(note.title);
            $this.container.text.val(note.text);
            $this.container.url.val(note.url);
            $this.container.attachments.empty();

            if (note.attachments && note.attachments.length > 0) {
                $($this.options.cAttachments).show();
                for (var i = 0; i < note.attachments.length; i++) {
                    var attachment = note.attachments[i];
                    $this._addAttachment(attachment);
                }
            }
            $this._sync(true);
            $this.show();
        });
    },

    close: function () {
        var $this = this;
        $this.hide();
        $this._updateNote();
        $this._reset();
        $('#n-list').notes('refresh');
    }

});
