$.widget("notes.pdfeditor", $.notes.basiceditor, {

    _create: function () {

        var $this = this;

        var $target = $this.element.empty().show().
            addClass('container pdf-editor').
            // resets from maximized mode
            removeClass('maximized');

        $this.fnPreSyncModel = function () {
            console.log('pre sync')
        };
        $this.fnPreDestroy = function () {
            console.log('pre destory')
        };

        var currentPage = 1;

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
            page: currentPage,
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
            if (currentPage > 1) {
                currentPage--;
                $numberOfPages.val(currentPage);

                loadPdf();
            }
        };
        var numberOfPages = $this.getModel().get('numberOfPages');

        var fnNext = function () {
            if (currentPage < numberOfPages) {
                currentPage++;
                $numberOfPages.val(currentPage);

                pdfConfig.page = currentPage;
                pdfloader.loadPdf(pdfConfig);
            }
        };

        var config = {
            left: [
                $('<span/>', {style: 'margin-left:15px'}),
                $this._createButton('Previous', {primary: 'ui-icon-triangle-1-w'}, fnPrevious, false),
                $numberOfPages,
                $('<span/>', {text: 'of ' + numberOfPages, style: 'padding-left:5px; padding-right:5px'}),
                $this._createButton('Next', {secondary: 'ui-icon-triangle-1-e'}, fnNext, false)
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
