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

//
//    // -- PDF EDITOR .--------------------------------------------------------------------------------------------------
//
//    _loadPdfEditor: function () {
//
//        var $this = this;
//
//        var $target = $this.element.empty().show().
//            addClass('container pdf-editor').
//            // resets from maximized mode
//            removeClass('maximized').
//            addClass('row');
//
//        $this.fnPreSyncModel = function () {
//            console.log('pre sync')
//        };
//        $this.fnPreDestroy = function () {
//            console.log('pre destory')
//        };
//
//        $this.currentPage = 1;
//
//        var $numberOfPages = $('<input/>', {type: 'text', class: 'ui-widget-content ui-corner-all pages', value: '1'});
//        var $pdfLayer = $('<div/>', {class: 'pdf-container'});
//
//        var embedPos = {
//            top: 385,
//            left: 30
//        };
//
//        var maxPos = {
//            top: 93,
//            left: 20
//        };
//
//        var pdfConfig = {
//            fileId: $this.getModel().get('fileReferenceId'),
//            page: $this.currentPage,
//            position: embedPos,
//            layer: $pdfLayer
//        };
//
//        var loadPdf = function () {
//            pdfConfig.page = $this.currentPage;
//            pdfloader.loadPdf(pdfConfig);
//        };
//
//        $this.fnPostMaximize = function () {
//            pdfConfig.position = maxPos;
//            loadPdf();
//        };
//
//        $this.fnPostEmbed = function () {
//            pdfConfig.position = embedPos;
//            loadPdf();
//        };
//
//        // todo navigation does not work
//        var fnPrevious = function () {
//            if ($this.currentPage > 1) {
//                $this.currentPage--;
//                $numberOfPages.val($this.currentPage);
//
//                loadPdf();
//            }
//        };
//        var numberOfPages = $this.getModel().get('numberOfPages');
//
//        var fnNext = function () {
//            if ($this.currentPage < numberOfPages) {
//                $this.currentPage++;
//                $numberOfPages.val($this.currentPage);
//
//                pdfConfig.page = $this.currentPage;
//                pdfloader.loadPdf(pdfConfig);
//            }
//        };
//
//        var config = {
//            left: [
//                $('<span/>', {style: 'margin-left:15px'}),
//                $this._createButton('Previous', {primary: 'ui-icon-triangle-1-w'}, fnPrevious),
//                $numberOfPages,
//                $('<span/>', {text: 'of ' + numberOfPages, style: 'padding-left:5px; padding-right:5px'}),
//                $this._createButton('Next', {secondary: 'ui-icon-triangle-1-e'}, fnNext)
//            ]
//        }
//
//        var $fieldTitle = $('<input/>', {class: 'ui-widget-content ui-corner-all title', type: 'text', value: $this.getModel().get('title')});
//
//        $target.append(
//                $this._getToolbar(config)
//            ).append(
//                $this.getProgressLayer()
//            ).append(
//                $('<div/>', {class: 'row', style: 'margin-top:5px'}).append(
//                    $fieldTitle
//                )
//            ).append(
//                $('<div/>', {class: 'row', style: 'margin-top:5px', text: 'modified ' + notes.util.formatDate(new Date($this.getModel().get('modified'))) + ' by ' + $this.getModel().get('ownerId')})
//            ).append(
//                $pdfLayer
//            );
//
//        pdfloader.loadPdf(pdfConfig);
//    }
})
