$.widget("notes.annotate", {

    options:{
        url: '/notes/rest/note/attachment/'
    },

    // table

    _init:function () {
        var $this = this;

        if(!$this.options.url) {
            console.error('url is not set');
            throw 'url is not set';
        }

        $this.element.empty();

        PDFJS.disableWorker = true;

        $this.pdf = PDFJS.getDocument('217.pdf');
        $this.pdf.then(function(pdf) {
            var $this = this;
            console.log('renderPdf');
            pdf.getPage(1).then($this._renderPage);
        });

        /*
        var _url = '/notes/rest/note/attachment/{ATTACHMENT_ID}';
        var _urlReplacements = {'{ATTACHMENT_ID}':3};

        $.ajax({
            type:'GET',
            url:notes.util.replaceInUrl(_url, _urlReplacements),
            cache:false,
            processData:false,
            success:function (raw) {

                var uint8Array = new Uint8Array(new ArrayBuffer(raw.length));
                for (var i = 0; i < raw.length; i++) {
                    uint8Array[i] = raw.charCodeAt(i);
                }

                //$this.pdf = PDFJS.getDocument(uint8Array);
                $this.pdf = PDFJS.getDocument('217.pdf');
                $this.pdf.then(function(pdf) {
                    var $this = this;
                    console.log('renderPdf');
                    pdf.getPage(1).then($this._renderPage);
                });
            }
        });
        */
    },

    _renderPage : function (page) {
        var $this = this;
        var viewport = page.getViewport(scale);
        var $canvas = jQuery("<canvas></canvas>");

        //Set the canvas height and width to the height and width of the viewport
        var canvas = $canvas.get(0);
        var context = canvas.getContext("2d");
        canvas.height = viewport.height;
        canvas.width = viewport.width;

        //Append the canvas to the pdf container div
        //var $pdfContainer = jQuery("#pdfContainer");
        var $pdfContainer = $this.element;
        $pdfContainer.css("height", canvas.height + "px").css("width", canvas.width + "px");
        $pdfContainer.append($canvas);

        //The following few lines of code set up scaling on the context if we are on a HiDPI display
        var outputScale = getOutputScale();
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

        var canvasOffset = $canvas.offset();
        var $textLayerDiv = jQuery("<div />")
            .addClass("textLayer")
            .css("height", viewport.height + "px")
            .css("width", viewport.width + "px")
            .offset({
                top: canvasOffset.top,
                left: canvasOffset.left
            });

        $pdfContainer.append($textLayerDiv);

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
    },

    _create:function () {

    }

});
