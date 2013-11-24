$.widget("notes.editor", {

    options: {
        syncInterval: 15000 // msec
    },

    kinds: {
        text: {
            url: '/notes/rest/document/${documentId}',
            fnLoad: '_loadTextEditor'
        },
        pdf: {
            url: '/notes/rest/document/${documentId}',
            fnLoad: '_loadPdfEditor'
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

            $this.loadDocument(kind, new notes.model.Document(document), onUnload);
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


    // -----------------------------------------------------------------------------------------------------------------


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

//    _newReminderSettings: function (model) {
//
//        var __fnUpdateModel = function (date, repeatString) {
//            if (date) {
//                model.set('reminder', {
//                    referenceDate: date.toISOString(),
//                    repetition: repeatString
//                });
//            }
//        };
//
//        // todo reminder on/off button
//
//        var repetitionSelect = $('<select/>', {class: 'ui-corner-all'});
//
//        var _defaultDate;
//        if (model.has('reminder')) {
//            _defaultDate = new Date(model.get('reminder').referenceDate);
//        } else {
//            _defaultDate = new Date();
//        }
//
//        var datePicker = $('<input/>', {type: 'text', class: 'ui-corner-all date-field'}).
//            datepicker({
//                format: 'yy-mm-dd',
//                autoSize: true,
//                onSelect: function () {
//                    var _date = $(this).datepicker('getDate');
//                    var _val = repetitionSelect.val();
//                    __fnUpdateModel(_date, _val);
//                }
//            }).datepicker('setDate', _defaultDate);
//
//        repetitionSelect.change(function () {
//            var _date = datePicker.datepicker('getDate');
//            var _val = $(this).val();
//            __fnUpdateModel(_date, _val);
//        });
//
//        var repeatModes = ['never', 'weekly', 'monthly', 'yearly'];
//        for (var i = 0; i < repeatModes.length; i++) {
//            var mode = repeatModes[i];
//            var _option = $('<option/>', {value: mode.toUpperCase(), text: mode});
//            if (model.has('reminder') && model.get('reminder').repetition.toLowerCase() == mode.toLowerCase()) {
//                _option.attr('selected', true);
//            }
//
//            repetitionSelect.append(
//                _option
//            )
//        }
//
//        var __fnRemoveFromModel = function () {
//            model.unset('reminder');
//            model.unset('reminderId');
//        };
//
//        var _onOffLabel = model.has('reminder') ? 'active' : 'inactive';
//        var _onOffButton = $('<div/>').button({
//            label: _onOffLabel
//        }).click(function () {
//                var label = $(this).button('option', 'label');
//                if (label == 'active') {
//                    $(this).button('option', 'label', 'inactive');
//                    __fnRemoveFromModel();
//
//                } else {
//                    $(this).button('option', 'label', 'active');
//                    __fnUpdateModel(datePicker.datepicker('getDate'), repetitionSelect.val());
//                }
//            });
//
//        var _col1 = 'col-lg-2';
//        var _col2 = 'col-lg-10';
//
//        var reminder = $('<div/>', {class: 'row settings'}).append(
//                $('<div/>', {class: 'col-lg-1'}).append(
//                    $('<label/>', {text: 'Reminder '})
//                )
//            ).append(
//                $('<div/>', {class: 'col-lg-11'}).append(
//                        $('<div/>', {class: 'row'}).append(
//                                $('<div/>', {class: _col1, text: 'Status'})
//                            ).append(
//                                $('<div/>', {class: _col2}).append(_onOffButton)
//                            )
//                    ).append(
//                        $('<div/>', {class: 'row'}).append(
//                                $('<div/>', {class: _col1, text: 'Next'})
//                            ).append(
//                                $('<div/>', {class: _col2}).append(datePicker)
//                            )
//                    ).append(
//                        $('<div/>', {class: 'row'}).append(
//                                $('<div/>', {class: _col1, text: 'Repeat'})
//                            ).append(
//                                $('<div/>', {class: _col2}).append(repetitionSelect)
//                            )
//                    )
//            );
//
//
//        if (model.has('reminder')) {
//            reminder.show();
//        } else {
//            reminder.hide();
//        }
//
//        return reminder;
//    },


    _getToolbar: function (model, onUpdateModel, progressSettings, onUnload) {
        var $this = this;

        var target = $this.element;

        return $('<div/>', {class: 'row'}).append(
                $('<button/>').button({
                    label: 'Close',
                    icons: {
                        primary: 'ui-icon-arrowreturn-1-w'
                    }
                }).click(
                    function () {

                        onUpdateModel();

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
//            ).append(
//                $('<button/>').button({
//                    label: 'Reminder',
//                    icons: {
//                        primary: 'ui-icon-clock'
//                    }
//                }).click(function () {
//                        reminderSettings.slideToggle();
//                    })
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
    },


    // -- TEXT EDITOR --------------------------------------------------------------------------------------------------


    _loadTextEditor: function (model, onUnload) {
        var $this = this;

        var fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all title', type: 'text', value: model.get('title')});
        var fieldText = $('<textarea/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('text')});

        var target = $this.element.empty().show().
            addClass('container text-editor').
            // resets from maximized mode
            removeClass('maximized').
            addClass('row');

        // todo both should be dialogs?
        var progressSettings = $this._newProgressSettings(model);
        //var reminderSettings = $this._newReminderSettings(model);

        // todo implement star/pin functionality

        var onUpdateModel = function () {
            model.set('title', fieldTitle.val());
            model.set('text', fieldText.val());
        };

        var toolbar = $this._getToolbar(model, onUpdateModel, progressSettings, onUnload);

        target.append(
                toolbar
//            ).append(
//                reminderSettings
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

    _unloadTextEditor: function (onUnload) {

        if ($.isFunction(onUnload)) {
            onUnload();
        }

        // todo implement
        this.element.hide();
    },


    // -- PDF EDITOR .--------------------------------------------------------------------------------------------------

    _loadPdfEditor: function (model, onUnload) {

        var $this = this;

        var target = $this.element.empty().show().
            addClass('container text-editor').
            // resets from maximized mode
            removeClass('maximized').
            addClass('row');

        var onUpdateModel = function () {
            // todo implement annotation stuff
        }

        var progressSettings = $this._newProgressSettings(model);

        var toolbar = $this._getToolbar(model, onUpdateModel, progressSettings, onUnload);

        target.append(
                toolbar
            ).append(
                progressSettings
            ).append(
                $('<div/>', {class: 'pdf-container', id: 'pdfContainer'})
            );

        pdfloader.loadPdf(model.get('fileReferenceId'), 2);

    },

    _destroy: function () {
        // todo implement widget method
    }
})
