$.widget("notes.texteditor", $.notes.basiceditor, {

    _create: function () {
        var $this = this;

        var model = $this.getModel();

        var $fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all title', type: 'text', value: model.get('title')});
        var $fieldText = $('<textarea/>', {class: 'ui-widget-content ui-corner-all', type: 'text', value: model.get('text')});

        var $target = $this.element.empty().show().
            addClass('container text-editor').
            // resets from maximized mode
            removeClass('maximized');

        // todo both should be dialogs?

        // todo implement star/pin functionality

        $this.fnPreSyncModel = function () {
            console.log('pre sync')
            model.set('title', $fieldTitle.val());
            model.set('text', $fieldText.val());
        };

        $this.fnPreDestroy = function () {
            console.log('pre destory');
            $this.fnClose();
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
    }
})
