/*global notes:false */
/*global SUMMERNOTE_CFG:false */

'use strict';

$.widget('notes.bookmark', $.notes.basiceditor, {

    options: {
        editMode: false,
        template: '#bookmark-editor'
    },

    _create: function () {

        console.log('create bookmark editor');

        var $this = this;

        var $target = $this.element.empty();

        var model = $this.getModel();

        var template = _.template($($this.options.template).html());

        var $rendered = $(template(model.attributes).trim());
        var $title = $rendered.find('.field-title');
        var $star = $rendered.find('.action-star');

        $target.append($rendered);

        $this.fnUpdateModel = function () {
            console.log('update model');
            model.set('title', $title.code());
            model.set('star', $star.attr('star') === 'true');
        };

        $this._createParent($rendered);

    }
});
