$.widget("notes.editor", {

    options: {
        syncInterval: 15000 // msec
    },

    kinds: {
        text: {
            model: Backbone.Model.extend({
                defaults: {
                    title: '',
                    text: ''
                },
                url: '/notes/rest/document/text/'
            }),
            url: '/notes/rest/document/text/${documentId}',
            fnLoad: '_loadTextEditor'
        }
    },

    _create: function () {
        var $this = this;

        // -- events
        setInterval(function () {

            // todo $this._syncDocument();

        }, $this.options.syncInterval);

    },

    edit: function (documentId, kindString, onUnload) {

        var $this = this;

        if (!documentId) {
            throw 'document id is null.'
        }

        var kind = $this.kinds[kindString.toLowerCase()];
        if (!kind) {
            throw 'kind is null.'
        }
        if (!kind) {
            throw 'kind ' + kindString + ' is supported.'
        }

        var url = kind.url;

        notes.util.jsonCall('GET', url, {'${documentId}': documentId}, null, function (document) {
            $this.documentId = document.id;

            $this.loadDocument(kind, new kind.model(document), onUnload);
        });
    },

    loadDocument: function (kind, model, onUnload) {
        var $this = this;

        $this[kind.fnLoad](model, onUnload);
    },

    createDocument: function () {
        var $this = this;
        var kindString = 'text';

        var kind = $this.kinds[kindString.toLowerCase()];
        $this.loadDocument(kind, new kind.model({
            folderId: $('#directory').directory('selectedFolder')
        }));
    },

    // -- TEXT EDITOR --------------------------------------------------------------------------------------------------

    // todo: models to models.js
    // listener for changes

    _loadTextEditor: function (model, onUnload) {
        var $this = this;

        var fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all title', type: 'text', value: model.get('title')});
        var fieldText = $('<textarea/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('text')});

        var target = $this.element.empty().show().
            addClass('container text-editor').
            // resets from maximized mode
            removeClass('maximized').
            addClass('row');

        var progressSettings = $this._newProgressSettings(model);
        var reminderSettings = $this._newReminderSettings(model);

        var header = $('<div/>', {class: 'row'}).append(
                $('<button/>').button({
                    label: 'Close',
                    icons: {
                        primary: 'ui-icon-arrowreturn-1-w'
                    }
                }).click(
                    function () {
                        model.set('title', fieldTitle.val());
                        model.set('text', fieldText.val());

                        model.save(null, {success: function () {
                            $('#document-list-view').documentList('updateDocument', model);
                        }});

                        $this._unloadTextEditor(onUnload);
                    }
                )
            ).append(
                $('<button/>').button({
                    label: 'Delete',
                    icons: {
                        primary: 'ui-icon-trash'
                    }
                }).click(function () {
                        $('#document-list-view').documentList('deleteDocument', model);
                        // todo destory does not work
                        model.destroy();
                        $this._unloadTextEditor(onUnload);
                    })
            ).append(
                $('<button/>').button({
                    label: 'Reminder',
                    icons: {
                        primary: 'ui-icon-clock'
                    }
                }).click(function () {
                        reminderSettings.slideToggle();
                    })
            ).append(
                $('<button/>').button({
                    label: 'Progress',
                    icons: {
                        primary: 'ui-icon-signal'
                    }
                }).click(function () {
                        progressSettings.slideToggle();
                    })
            ).append(
                $('<button/>', {style: 'float:right'}).button({
                    label: 'Maximize',
                    icons: {
                        primary: 'ui-icon-arrow-4-diag'
                    }
                }).click(
                    function () {

                        if (target.hasClass('maximized')) {
                            target.
                                removeClass('maximized').
                                addClass('row');
                            $(this).
                                button('option', 'label', 'Maximize').
                                button('option', 'icons', { primary: 'ui-icon-arrow-4-diag'});

                        } else {
                            target.
                                removeClass('row').
                                addClass('maximized');
                            $(this).
                                button('option', 'label', 'Unmaximize').
                                button('option', 'icons', { primary: 'ui-icon-arrow-1-se'});
                        }
                    }
                )
            );
        target.append(
                header
            ).append(
                reminderSettings
            ).append(
                progressSettings
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
                    fieldTitle
                )
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
                    fieldText
                )
            );
    },

    _newProgressSettings: function (model) {

        var precentageLabel = $('<span/>', {style: 'font-weight:bold; line-height:25px; margin: 7px'});

        var __setPercentageLabel = function (value) {
            if (value == 0) {
                precentageLabel.text('OFF');
            } else {
                precentageLabel.text(value + ' %');
            }
        };

        var value = model.has('progress') ? model.get('progress') : 0;
        __setPercentageLabel(value);

        var slider = $('<div/>', {style: 'margin:7px 0 7px 0;'}).slider({
            range: "min",
            value: value,
            min: 0,
            max: 100,
            slide: function (event, ui) {
                __setPercentageLabel(ui.value);
                model.set('progress', ui.value);
            }
        });

        var progress = $('<div/>', {class: 'row settings'}).append(
                $('<div/>', {style: 'float:left'}).append(
                        $('<label/>', {text: 'Progress'})
                    ).append(
                        precentageLabel
                    )
            ).append(
                $('<div/>', {class: 'col-lg-5'}).append(
                    slider
                )
            );

        if (value <= 0) {
            progress.hide();
        }

        return progress;
    },

    _newReminderSettings: function (model) {

        var __updateModel = function (date, repeatString) {
            if (date) {
                model.set('reminder', {
                    referenceDate: date.toISOString(),
                    repetition: repeatString
                });
                console.log(model.get('reminder'))
            }
        }

        // todo fix time format, cannot be parsed currently
        // todo delete reminder button

        var repetitionSelect = $('<select/>', {class: 'ui-corner-all'});

        var datePicker = $('<input/>', {type: 'text', class: 'ui-corner-all date-field'}).
            datepicker({
                format: 'yy-mm-dd',
                autoSize: true,
                onSelect: function () {
                    var _date = $(this).datepicker('getDate');
                    var _val = repetitionSelect.val();
                    __updateModel(_date, _val);
                }
            });

        repetitionSelect.change(function () {
            var _date = datePicker.datepicker('getDate');
            var _val = $(this).val();
            __updateModel(_date, _val);
        })

        var repeatModes = ['never', 'weekly', 'monthly', 'yearly'];
        for (var i = 0; i < repeatModes.length; i++) {
            var mode = repeatModes[i];
            repetitionSelect.append(
                $('<option/>', {value: mode.toUpperCase(), text: mode})
            )
        }

        var reminder = $('<div/>', {class: 'row settings'}).append(
                $('<div/>', {style: 'float:left'}).append(
                    $('<span/>', {text: 'Remind me, starting on '})
                )
            ).append(
                $('<div/>', {class: 'col-lg-5'}).append(
                        datePicker
                    ).append(
                        $('<span/>', {text: 'repeat', style: 'margin-left:5px; margin-right:5px;'})
                    ).append(
                        repetitionSelect
                    )
            ).hide();

        return reminder;
    },

    _unloadTextEditor: function (onUnload) {

        if ($.isFunction(onUnload)) {
            onUnload();
        }

        // todo implement
        this.element.hide();
    }
});
