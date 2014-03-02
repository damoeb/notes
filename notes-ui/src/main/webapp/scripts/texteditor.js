/*global notes:false */
/*global SUMMERNOTE_CFG:false */

'use strict';

$.widget('notes.texteditor', $.notes.basiceditor, {

    options: {
        editMode: false,
        template: '#text-editor'
    },

    _create: function () {

        console.log('create text editor');

        var $this = this;

        var $target = $this.element.empty();

        var model = $this.getModel();

        var template = _.template($($this.options.template).html());

        var $rendered = $(template(model.attributes).trim());
        var $title = $rendered.find('.field-title');
        var $text = $rendered.find('.field-text');
        var $star = $rendered.find('.action-star');

        $target.append($rendered);

        $this.fnUpdateModel = function () {
            console.log('update model');
            model.set('title', $title.code());
            model.set('text', $text.code());
            model.set('star', $star.attr('star') === 'true');
        };

        $this._createParent();

    }
});
