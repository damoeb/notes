
$.widget("notes.editor", {

    options:{
        updateIntervalMsec: 15000,
        fTitle: '#field-title',
        fUrl: '#field-url',
        fText: '#field-content',
        cList: '#pane-notes',
        cProgress: '#progress',
        fAttachments: '#n-attachments',
        cEditor: '#pane-editor',
        cAttachments: '#attachments'
    },

//    noteId,
//    fields: {
//        text, title, url
//    },

    _init:function () {
        var $this = this;
        var editor = $this.element;

        $this.container = {};
        $this.noteId = null;
        $this.container.title = editor.find($this.options.fTitle);
        $this.container.attachments = editor.find($this.options.fAttachments);

        var config = {
            toolbar: 'Basic',
            linkShowAdvancedTab: false,
            scayt_autoStartup: false,
            enterMode: Number(2),
            toolbar_Full: [
                ['Styles', 'Bold', 'Italic', 'Underline', '-', 'NumberedList', 'BulletedList'],
                ['Link', 'Unlink'],
                ['Undo', 'Redo', '-', 'SelectAll']
            ]
        };

        $this.container.text = editor.find($this.options.fText).ckeditor(function () {}, config);

        $this.container.url = editor.find($this.options.fUrl);

    },

    _create:function () {
        var $this = this;
        var editor = $this.element;

        $this._sync(false);

        $('#action-back').button({
            icons: {
                primary: 'ui-icon-arrowreturn-1-w'
            },
            text: false
        }).click(function() {
            $this.close();
        });

        $('#action-favourite').button({
            icons: {
                primary: 'ui-icon-star'
            },
            text: false
        });

        $('#action-remove').button({
            icons: {
                primary: 'ui-icon-trash'
            },
            text: false
        }).parent().buttonset();

        $('#action-tag').button({
            icons: {
                primary: 'ui-icon-tag'
            },
            label: 'Tag'
        });
//        $('#action-download').button({
//            icons: {
//                primary: 'ui-icon-arrowthickstop-1-s'
//            },
//            text: false
//        });


        $('#action-attach-file').button({
            icons: {
                primary: 'ui-icon-folder-open'
            },
            label: 'Attachment'
        }).click(function() {
            console.log('click');

            var upload = $('<input type="file" name="files[]" multiple>');
            editor.append(upload);

            upload.css('visibility','hidden').fileupload({
                url: '/notes/rest/note/attachment/add',
                dataType: 'json'
            });

            upload
                .bind('fileuploadadd', function (e) {
                    console.log('add file');
                })
                .bind('fileuploadsubmit', function (e, data) {
                    data.formData = {noteId: $this.noteId};
                    $($this.options.cProgress).slideDown();
                })
                .bind('fileuploaddone', function (e, data) {
                    console.log('done');
                    $.each(data.result.result, function (index, file) {
                        $this._addAttachment(file);
                    });

                    editor.find('input[type="file"]').fileupload('destroy').remove();
                });

            upload.click();
        });

//        $('#action-close-note').button({
//            icons: {
//                primary: 'ui-icon-close'
//            },
//            label: 'Close'
//        }).click(function() {
//            $this.close();
//        })
//        .parent().buttonset();

        $this.note_content = {};

        setInterval(function() {

            $this._updateNote();

        }, $this.options.updateIntervalMsec);

    },

    _updateNote:function() {
        var $this = this;
        var new_note_content = {
            title: $this.container.title.val(),
            url: $this.container.url.val(),
            text: $this.container.text.val()
        };

        var hasTitle = new_note_content.title.replace(/[ \t\n]*/g,'').length>0;
        var hasChanged = !notes.util.equal($this.note_content, new_note_content);

        if(hasTitle && hasChanged && $this._isSyncing()) {

            var url;
            if($this.noteId) {
                console.log('update');
                url = '/notes/rest/note/'+$this.noteId;
            } else {
                console.log('create');
                url = '/notes/rest/note/';
            }

            notes.util.jsonCall('POST', url, null, JSON.stringify(new_note_content), function (note) {
                $this.noteId = note.id;
            });

            $this.note_content = new_note_content;

        }
    },

    _reset:function () {
        var $this = this;
        $this._sync(false);
        $this.container.title.val('');
        $this.container.text.val('');
        $this.container.url.val('');
        $this.container.attachments.empty();
        $this.noteId = null;
        $($this.options.cAttachments).hide();
    },

    create: function() {
        this._reset();
        this.show();
        this._sync(true);
    },

    edit: function(noteId) {

        var $this = this;

        if(!noteId) {
            throw 'note id is null.'
        }

        $this._sync(false);

        notes.util.jsonCall('GET', '/notes/rest/note/'+noteId, null, null, function (note) {
            $this.noteId = note.id;
            $this.container.title.val(note.title);
            $this.container.text.val(note.text);
            $this.container.url.val(note.url);
            $this.container.attachments.empty();

            if(note.attachments && note.attachments.length>0) {
                $($this.options.cAttachments).show();
                for(var i=0; i<note.attachments.length; i++) {
                    var attachment = note.attachments[i];
                    $this._addAttachment(attachment);
                }
            }
            $this._sync(true);
            $this.show();
        });
    },

    _getAttachmentLabel:function(attachment) {
        return attachment.name + ' ('+ notes.util.formatBytesNum(attachment.size)+')'
    },

    _addAttachment: function(attachment) {
        var $this = this;

        $($this.options.cAttachments).slideDown();

        // todo get propper icon for note.kind
        var button = $('<div/>').button({
            icons: {
                primary: 'ui-icon-image'
            },
            label: $this._getAttachmentLabel(attachment)
        }).appendTo($this.container.attachments);

        var menuwrapper = $('<div/>', {class: 'attachment-menu'})
            .appendTo($this.container.attachments)
            .hide();

        var menu = $('<ul/>')
            .append($this._newOpenAttachment(attachment));
        if(attachment.contentType && $this._canAnnotate(attachment.contentType)) {
            menu.append($this._newAnnotateAttachment(button, attachment));
        }

        menu
            .append($this._newRenameAttachment(button, attachment))
            .append($this._newRemoveAttachment(button, attachment))
            .appendTo(menuwrapper)
            .menu();

        button.click(function() {
            menuwrapper.show().position({
                my: "left top",
                at: "left bottom",
                of: button
            });
            $( document ).one( "click", function() {
                menuwrapper.hide();
            });

            return false;
        });

        return button;
    },

    _canAnnotate:function(contentType) {
        contentType = contentType.toLowerCase();
        switch(contentType) {
            // fall through
            case 'application/pdf':
                return true;
            default:
                return false;
        }
    },

    _newOpenAttachment:function(attachment) {
        return $('<li><a href="#"><span class="ui-icon ui-icon-disk"></span>Open</a></li>').click(function() {
            window.open("/notes/rest/note/attachment/"+attachment.id);
        });
    },

    _newAnnotateAttachment:function(domreference, attachment) {
        return $('<li><a href="#"><span class="ui-icon ui-icon-disk"></span>Annotate</a></li>').click(function() {
            // todo annotate attachment
        });
    },

    _newRemoveAttachment:function(domreference, attachment) {
        var $this = this;
        return $('<li><a href="#"><span class="ui-icon ui-icon-trash"></span>Delete</a></li></ul>').click(function() {

            notes.util.jsonCall('POST', '/notes/rest/note/attachment/remove/{ATTACHMENT_ID}/{NOTE_ID}', {'{ATTACHMENT_ID}':attachment.id, '{NOTE_ID}': $this.noteId}, null, function () {
                domreference.remove();
                // todo if no attachments left, hide files div
            });

        });
    },
    _newRenameAttachment:function(domreference, attachment) {
        var $this = this;
        return $('<li><a href="#"><span class="ui-icon ui-icon-trash"></span>Rename</a></li></ul>').click(function() {

            $( "#dialog-promt-new-name" ).dialog({
                resizable: false,
                height:140,
                modal: true,
                buttons: {
                    "Rename": function() {
                        var newName = $(this).find('input[type="text"]').val();
                        if(newName.length>0) {
                            notes.util.jsonCall('POST', '/notes/rest/note/attachment/rename/{ATTACHMENT_ID}/?name={NAME}', {'{ATTACHMENT_ID}':attachment.id, '{NAME}':newName}, null, function (newAttachment) {
                                domreference.find('.ui-button-text').text($this._getAttachmentLabel(newAttachment));
                            });
                        }

                        $( this ).dialog( "close" );
                    },
                    Cancel: function() {
                        $( this ).dialog( "close" );
                    }
                },
                open: function( event, ui ) {
                    $(this).find('input[type="text"]').val(attachment.name).select().focus();
                }
            });

        });
    },

    _sync: function(sync) {
        console.log('sync? '+sync);
        this.runSyncs = sync;
    },
    _isSyncing: function() {
        return this.runSyncs;
    },

    show: function() {
        $(this.options.cList).hide();
        $(this.options.cEditor).show();
    },

    hide: function() {
        $(this.options.cList).show();
        $(this.options.cEditor).hide();
    },

    close: function () {
        var $this = this;
        $this.hide();
        $this._updateNote();
        $this._reset();
        $('#n-list').notes('refresh');
    }

});
