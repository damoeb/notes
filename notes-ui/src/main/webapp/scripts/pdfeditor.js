/*global notes:false */
/*global pdfloader:false */

'use strict';

$.widget('notes.pdfeditor', $.notes.basiceditor, {

    options: {
        template: '#pdf-editor'
    },

    _create: function () {

        console.log('create pdf editor');

        var $this = this;

        var $target = $this.element.empty();

        var model = $this.getModel();

        var template = _.template($($this.options.template).html());

        var $rendered = $(template(model.attributes).trim());
        var $title = $rendered.find('.field-title');
        var $page = $rendered.find('.field-current-page');

        var $pdfLayer = $rendered.find('.pdf-container');
        var $numberOfPages = $rendered.find('.field-page');


        $target.append($rendered);

        $this.fnUpdateModel = function () {
            console.log('update model');
            model.set('title', $title.code());
        };

        var currentPage = 1;
        var numberOfPages = $this.getModel().get('numberOfPages');


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
            pdfConfig.page = currentPage;
            pdfloader.loadPdf(pdfConfig);
        };

        $rendered.find('.action-previous-page').click(function () {
            if (currentPage > 1) {
                currentPage--;
                $page.val(currentPage);

                loadPdf();
            }
        });

        $rendered.find('.action-next-page').click(function () {
            if (currentPage < numberOfPages) {
                currentPage++;
                $page.val(currentPage);

                pdfConfig.page = currentPage;
                pdfloader.loadPdf(pdfConfig);
            }
        });

        pdfloader.loadPdf(pdfConfig);

        $rendered.find('.action-close').click(function () {
            $this.fnSave.call($this);

            $this._destroy();

            $('#document-view').hide();
            $('#folder-view').show();
        });
    }
});
