$.widget("notes.editor", {

    options: {
        syncInterval: 15000 // msec
    },

    settings: {
        text: {
            fnLoad: '_loadTextEditor'
        },
        pdf: {
            fnLoad: '_loadPdfEditor'
        }
    },

    _create: function () {
        var $this = this;

        $this.nav = $('<ul/>').appendTo($this.element);

//        // -- events
//        setInterval(function () {
//
//            // todo $this.syncModel();
//
//        }, $this.options.syncInterval);

    },

    edit: function (documentId, unloadCallback) {

        var $this = this;

        $this.unloadCallback = unloadCallback;

        if (!documentId) {
            throw 'document id is null.'
        }

        notes.util.jsonCall('GET', '/notes/rest/document/${documentId}', {'${documentId}': documentId}, null, function (document) {
            $this.documentId = document.id;

            var settings = $this.settings[document.kind.toLowerCase()];

            var model = new notes.model.Document(document);
            model.set('event', 'UPDATE');

            $this.loadDocument(settings, model);
        });
    },

    loadDocument: function (settings, model) {
        var $this = this;

        $this.model = model;
        $this.$progressLayer = $this._newProgressSettings(model);

        $this[settings.fnLoad]();
    },

    createDocument: function () {
        var $this = this;
        var kindString = 'text';

        var setting = $this.settings[kindString.toLowerCase()];
        $this.loadDocument(setting, new notes.model.Document({
            folderId: $('#directory').directory('selectedFolder')
        }));
    },


    // -----------------------------------------------------------------------------------------------------------------


    _newProgressSettings: function (model) {

        var $percentageLabel = $('<span/>', {style: 'font-weight:bold; line-height:25px; margin: 7px'});

        var fnSetPercentageLabel = function (value) {
            if (value == 0) {
                $percentageLabel.text('OFF');
            } else {
                $percentageLabel.text(value + ' %');
            }
        };

        var value = model.has('progress') ? model.get('progress') : 0;
        fnSetPercentageLabel(value);

        var $slider = $('<div/>', {style: 'margin:7px 0 7px 0;'}).slider({
            range: "min",
            value: value,
            min: 0,
            max: 100,
            slide: function (event, ui) {
                fnSetPercentageLabel(ui.value);
                model.set('progress', ui.value);
            }
        });

        var $progress = $('<div/>', {class: 'row settings'}).append(
                $('<div/>', {style: 'float:left'}).append(
                        $('<label/>', {text: 'Progress'})
                    ).append(
                        $percentageLabel
                    )
            ).append(
                $('<div/>', {class: 'col-lg-5'}).append(
                    $slider
                )
            );

        if (value <= 0) {
            $progress.hide();
        }

        return $progress;
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


    _createButton: function (label, icons, onClick) {

        var $this = this;

        return $('<button/>').button({
            label: label,
            icons: {
                primary: icons.primary,
                secondary: icons.secondary
            }
        }).click(function () {
                if ($.isFunction(onClick)) {
                    onClick.call($this, this)
                }
            }
        )
    },

    _getToolbar: function (config) {
        var $this = this;

        var $toolbar = $('<div/>', {class: 'row'});

        var $left = $('<div/>', {style: 'float:left'})
            .append(
                $this._createButton('Close', {primary: 'ui-icon-arrowreturn-1-w'}, $this.fnClose)
            ).append(
                $this._createButton('Delete', {primary: 'ui-icon-trash'}, $this.fnDelete)
            ).append(
                $this._createButton('Progress', {primary: 'ui-icon-signal'}, $this.fnProgress)
            );

        // todo add custom buttons via a config
        if (config && config.left) {
            for (var i = 0; i < config.left.length; i++) {

                $left.append(
                    config.left[i]
                );
            }
        }

        var $right = $('<div/>', {style: 'float:right'}).append(
            $this._createButton('Maximize', {primary: 'ui-icon-arrow-4-diag'}, $this.fnMaximize)
        );

        return $toolbar.append($left).append($right);
    },

    getProgressLayer: function () {
        return this.$progressLayer;
    },

    fnProgress: function () {
        this.getProgressLayer().slideToggle()
    },

    fnDelete: function () {
        var $this = this;
        $('#document-list-view').documentList('deleteDocument', $this.getModel());
        // todo destory does not work
        $this.getModel().destroy();
        $this._destroy();
    },

    fnMaximize: function (el) {

        var $editor = this.element;
        if ($editor.hasClass('maximized')) {
            $editor.
                removeClass('maximized').
                addClass('row');
            $(el).
                button('option', 'label', 'Maximize').
                button('option', 'icons', { primary: 'ui-icon-arrow-4-diag'});

            if ($.isFunction(this.fnPostEmbed)) {
                this.fnPostEmbed.call(this)
            }

        } else {
            $editor.
                removeClass('row').
                addClass('maximized');
            $(el).
                button('option', 'label', 'Unmaximize').
                button('option', 'icons', { primary: 'ui-icon-arrow-1-se'});

            if ($.isFunction(this.fnPostMaximize)) {
                this.fnPostMaximize.call(this)
            }
        }

    },

    fnClose: function () {

        var $this = this;

        $this.syncModel();

        $this._destroy();
    },

    getModel: function () {
        return this.model;
    },

    syncModel: function () {
        var $this = this;

        if ($.isFunction($this.fnPreSyncModel)) {
            $this.fnPreSyncModel();
        }

        $this.getModel().save(null, {success: function () {
            $('#document-list-view').documentList('updateDocument', $this.getModel());
        }});

        console.info('sync');
    },

    // -- TEXT EDITOR --------------------------------------------------------------------------------------------------


    _loadTextEditor: function () {
        var $this = this;

        var model = $this.getModel();

        var $fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all title', type: 'text', value: model.get('title')});
        var $fieldText = $('<textarea/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('text')});

        var $target = $this.element.empty().show().
            addClass('container text-editor').
            // resets from maximized mode
            removeClass('maximized').
            addClass('row');

        // todo both should be dialogs?

        // todo implement star/pin functionality

        $this.fnPreSyncModel = function () {
            console.log('pre sync')
            model.set('title', $fieldTitle.val());
            model.set('text', $fieldText.val());
        };

        $this.fnPreDestroy = function () {
            console.log('pre destory')
        };

        $target.append(
                $this._getToolbar()
            ).append(
                $this.getProgressLayer()
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
                    $fieldTitle
                )
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px', text: 'permalink '}).append(
                    $('<a/>', {href: '#', text: 'http://notes.org/' + model.get('id')})
                )
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px', text: 'modified ' + notes.util.formatDate(new Date(model.get('modified'))) + ' ago by ' + model.get('ownerId')})
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
                    $fieldText
                )
            );
    },

    _destroy: function () {

        var $this = this;

        if ($.isFunction($this.unloadCallback)) {
            $this.unloadCallback();
        }

        if ($.isFunction($this.fnPreDestroy)) {
            $this.fnPreDestroy();
        }

        // todo implement destroy/reuse?
        $this.element.hide();
    },


    // -- PDF EDITOR .--------------------------------------------------------------------------------------------------

    _loadPdfEditor: function () {

        var $this = this;

        var $target = $this.element.empty().show().
            addClass('container pdf-editor').
            // resets from maximized mode
            removeClass('maximized').
            addClass('row');

        $this.fnPreSyncModel = function () {
            console.log('pre sync')
        };
        $this.fnPreDestroy = function () {
            console.log('pre destory')
        };

        $this.currentPage = 1;

        var $numberOfPages = $('<input/>', {type: 'text', class: 'ui-widget-content ui-corner-all pages', value: '1'});
        var $pdfLayer = $('<div/>', {class: 'pdf-container'});

        var embedPos = {
            top: 385,
            left: 30
        };

        var maxPos = {
            top: 93,
            left: 20
        };

        var pdfConfig = {
            fileId: $this.getModel().get('fileReferenceId'),
            page: $this.currentPage,
            position: embedPos,
            layer: $pdfLayer
        };

        var loadPdf = function () {
            pdfConfig.page = $this.currentPage;
            pdfloader.loadPdf(pdfConfig);
        };

        $this.fnPostMaximize = function () {
            pdfConfig.position = maxPos;
            loadPdf();
        };

        $this.fnPostEmbed = function () {
            pdfConfig.position = embedPos;
            loadPdf();
        };

        // todo navigation does not work
        var fnPrevious = function () {
            if ($this.currentPage > 1) {
                $this.currentPage--;
                $numberOfPages.val($this.currentPage);

                loadPdf();
            }
        };
        var numberOfPages = $this.getModel().get('numberOfPages');

        var fnNext = function () {
            if ($this.currentPage < numberOfPages) {
                $this.currentPage++;
                $numberOfPages.val($this.currentPage);

                pdfConfig.page = $this.currentPage;
                pdfloader.loadPdf(pdfConfig);
            }
        };

        var config = {
            left: [
                $('<span/>', {style: 'margin-left:15px'}),
                $this._createButton('Previous', {primary: 'ui-icon-triangle-1-w'}, fnPrevious),
                $numberOfPages,
                $('<span/>', {text: 'of ' + numberOfPages, style: 'padding-left:5px; padding-right:5px'}),
                $this._createButton('Next', {secondary: 'ui-icon-triangle-1-e'}, fnNext)
            ]
        }

        var $fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all title', type: 'text', value: $this.getModel().get('title')});

        $target.append(
                $this._getToolbar(config)
            ).append(
                $this.getProgressLayer()
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
                    $fieldTitle
                )
            ).append(
                $('<div/>', {class: 'row', style: 'margin-top:5px', text: 'modified ' + notes.util.formatDate(new Date($this.getModel().get('modified'))) + ' by ' + $this.getModel().get('ownerId')})
            ).append(
                $pdfLayer
            );

        pdfloader.loadPdf(pdfConfig);
    }
})
