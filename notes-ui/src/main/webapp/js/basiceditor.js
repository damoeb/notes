$.widget("notes.basiceditor", {

    options: {
        model: null
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


    _createButton: function (label, icons, onClick, showText) {

        var $this = this;

        showText = typeof showText !== 'undefined' ? showText : true;

        return $('<button/>').button({
            label: label,
            text: showText,
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
//            .append(
//                $this._createButton('Close', {primary: 'ui-icon-arrowreturn-1-w'}, $this.fnClose)
//            )
            .append(
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
        var $this = this;
        if ($this.$progressLayer == null) {
            $this.$progressLayer = $this._newProgressSettings($this.getModel());
        }
        return $this.$progressLayer;
    },

    fnProgress: function () {
        this.getProgressLayer().slideToggle()
    },

    fnDelete: function () {
        var $this = this;
        $('#document-list').documentList('deleteDocument', $this.getModel());
        // todo destory does not work
        $this.getModel().destroy();
        $this._destroy();
    },

    fnMaximize: function (el) {

        var $editor = this.element;
        if ($editor.hasClass('maximized')) {
            $editor.
                removeClass('maximized')
            $(el).
                button('option', 'label', 'Maximize').
                button('option', 'icons', { primary: 'ui-icon-arrow-4-diag'});

            if ($.isFunction(this.fnPostEmbed)) {
                this.fnPostEmbed.call(this)
            }

        } else {
            $editor.
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

    },

    getModel: function () {
        return this.options.model;
    },

    syncModel: function () {
        var $this = this;

        if ($.isFunction($this.fnPreSyncModel)) {
            $this.fnPreSyncModel();
        }

        $this.getModel().save(null, {success: function () {
            $('#document-list').documentList('updateDocument', $this.getModel());
        }});

        console.info('sync');
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
    }

})
