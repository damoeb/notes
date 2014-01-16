/*global PDFJS:false */
/*global TextLayerBuilder:false */
/*global CustomStyle:false */

'use strict';

var pdfloader = {};

(function (pdfloader) {

    pdfloader.loadPdf = function (config) {

        var $this = this;

        $this.page = config.page;
        $this.position = config.position;
        $this.layer = config.layer;

        PDFJS.disableWorker = true; //Not using web workers. Not disabling results in an error. This line is
        //missing in the example code for rendering a pdf.

        var pdf = PDFJS.getDocument(REST_SERVICE + '/file/' + config.fileId);
        pdf.then(function (pdf) {
            $this.renderPdf(pdf);
        });
    };

    pdfloader.renderPdf = function (pdf) {
        var $this = this;
        pdf.getPage($this.page).then(function (page) {
            $this.renderPage(page);
        });
    };

    pdfloader.renderPage = function (page) {

        var $this = this;

        //Append the canvas to the pdf container div
        var $pdfContainer = $this.layer.empty();

        var width = $pdfContainer.width();

        var viewport = page.getViewport(width / page.getViewport(1.0).width);
        var $canvas = jQuery('<canvas></canvas>');

        //Set the canvas height and width to the height and width of the viewport
        var canvas = $canvas.get(0);
        var context = canvas.getContext('2d');
        canvas.height = viewport.height;
        canvas.width = viewport.width;

        $pdfContainer.css('height', canvas.height + 'px').css('width', canvas.width + 'px');
        $pdfContainer.append($canvas);

        var $textLayerDiv = jQuery('<div />')
            .addClass('textLayer')
            .css('height', viewport.height + 'px')
            .css('width', viewport.width + 'px')
            .offset({
                top: $this.position.top,
                left: $this.position.left
            });

        $pdfContainer.append($textLayerDiv);

        //The following few lines of code set up scaling on the context if we are on a HiDPI display
        var outputScale = {};
        if (outputScale.scaled) {
            var cssScale = 'scale(' + (1 / outputScale.sx) + ', ' +
                (1 / outputScale.sy) + ')';
            CustomStyle.setProp('transform', canvas, cssScale);
            CustomStyle.setProp('transformOrigin', canvas, '0% 0%');

            if ($textLayerDiv.get(0)) {
                CustomStyle.setProp('transform', $textLayerDiv.get(0), cssScale);
                CustomStyle.setProp('transformOrigin', $textLayerDiv.get(0), '0% 0%');
            }
        }

        context._scaleX = outputScale.sx;
        context._scaleY = outputScale.sy;
        if (outputScale.scaled) {
            context.scale(outputScale.sx, outputScale.sy);
        }

        page.getTextContent().then(function (textContent) {
            var textLayer = new TextLayerBuilder($textLayerDiv.get(0), 0); //The second zero is an index identifying
            //the page. It is set to page.number - 1.
            textLayer.setTextContent(textContent);

            var renderContext = {
                canvasContext: context,
                viewport: viewport,
                textLayer: textLayer
            };

            page.render(renderContext);
        });
    };

}(pdfloader));