/*global notes:false */
/*global Backbone:false */

'use strict';

(function (notes) {

    notes.app = function () {

        var _activeFolderId = 0;
        var _documentId = 0;
        var _databaseId = 0;
        var _descendants = {};

        this.activeFolderId = function (id) {
            if (typeof id !== 'undefined') {
                _activeFolderId = id;
            } else {
                return _activeFolderId;
            }
        };

        this.documentId = function (id) {
            if (typeof id !== 'undefined') {
                _documentId = id;
            } else {
                return _documentId;
            }
        };

        this.databaseId = function (id) {
            if (typeof id !== 'undefined') {
                _databaseId = id;
            } else {
                return _databaseId;
            }
        };

        this.add$Folder = function ($folder) {
            _descendants[$folder.getModel().get('id')] = $folder;
        };

        this.get$Folder = function (folderId) {
            return _descendants[folderId];
        };
    };


    notes.login = function () {

        var onSuccess = function () {

            console.log('logged in');
            notes.setup();
        };

        var ontLoginFailed = function () {
            console.log('login failed');

        };

        notes.util.jsonCall('POST', '/notes/rest/user/login', null, null, onSuccess, ontLoginFailed);
    };

    notes.logout = function () {

        var onSuccess = function () {
            console.log('logged out');
        };

        var ontLogoutFailed = function () {
            console.log('logout failed');

        };

        notes.util.jsonCall('GET', '/notes/rest/user/logout', null, null, onSuccess, ontLogoutFailed);
    };

    notes.register = function () {

        var onSuccess = function () {

            console.log('registered');
            notes.setup();
        };

        var ontLoginFailed = function () {
            console.log('registration failed');

        };

        notes.util.jsonCall('POST', '/notes/rest/user/register', null, null, onSuccess, ontLoginFailed);
    };

    notes.setup = function () {

        $('.view').hide();

        var onSuccess = function (settings) {

            console.log('Hello ' + settings.user.username);

            notes.app.databaseId(settings.database.id);

            notes.setup.ui();

        };

        var onNotLoggedIn = function () {
            console.log('not logged in');

            $('#login-view').show();
        };

        notes.util.jsonCall('GET', '/notes/rest/user/settings', null, null, onSuccess, onNotLoggedIn);
    };

    // -- Routing ------------------------------------------------------------------------------------------------------

    notes.setup.router = function () {

        var Router = Backbone.Router.extend({

            routes: {
                'folder/:id': 'folder',
                'search/:query': 'search'   // #search/kiwis/p7
            },

            search: function (query) {
                console.log('search ' + query);
                notes.search({
                    query: query,
                    databaseId: notes.app.databaseId()
                });

            },

            folder: function (id) {
                console.log('open folder ' + id);
                $('#document-list').documentList({
                    folderId: id
                });
            }
        });

        notes.router.instance = new Router();

        // start routing
        Backbone.history.start();

    };

    notes.setup.ui = function () {
        // -- Side Navigation ----------------------------------------------------------------------------------

        $('#databases').databases({
            databaseId: notes.app.databaseId()
        });

        $('#editors').editors();

        // -- Menu -- ------------------------------------------------------------------------------------------

        $('#settings').button({
            icons: {primary: 'ui-icon-gear'},
            text: false
        }).click(notes.dialog.settings.general);

        $('#create-document')
            .button()
            .click(function () {
                notes.dialog.document.create();
            });

        $('#import-document')
            .button()
            .click(notes.dialog.document.import)
            .parent().buttonset();

        // todo if file is dropped to document and upload started -> block screen or other indicator
//            var overallProgress = $('#fileupload').fileupload('progress');
//            var activeUploads = $('#fileupload').fileupload('active');

        $('#file-upload').
            fileupload({
                url: '/notes/rest/file/upload',
                dataType: 'json'
            })
            .bind('fileuploadsubmit', function (e, data) {

                data.formData = {
                    folderId: notes.app.activeFolderId(),
                    documentId: notes.app.documentId()
                };
            })
            .bind('fileuploaddone', function (e, xhr) {
                console.log('done');

                var model = new notes.model.Document(xhr.result.result);

                $('#document-list').documentList('updateDocument', model);
            });


        $('#search-input').keypress(function (e) {
            if (e.which === 13) {
                notes.router.navigate('search/' + $(this).val());
            }
        });


        $('#breadcrumbs').breadcrumbs();

    };

})(notes);

