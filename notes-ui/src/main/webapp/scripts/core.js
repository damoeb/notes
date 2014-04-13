/*global notes:false */
/*global Backbone:false */
/*global noty:false */
/*global REST_SERVICE:false */

'use strict';

(function (notes) {

    notes.login = function () {

        var onSuccess = function () {
            console.log('logged in');
            notes.setup();
        };

        var ontLoginFailed = function () {
            console.log('login failed');
            notes.register();
        };

        var payload = {
            username: 'guest', //$('#field-username').val(),
            password: 'password' //$('#field-password').val()
        };

        notes.util.jsonCall('POST', REST_SERVICE + '/auth/login', null, JSON.stringify(payload), onSuccess, ontLoginFailed);
    };

    notes.logout = function () {

        var onSuccess = function () {
            console.log('logged out');
            notes.messages.information('logged out');
        };

        var ontLogoutFailed = function () {
            console.log('logout failed');
            notes.messages.error('Logout failed');
        };

        notes.util.jsonCall('GET', REST_SERVICE + '/auth/logout', null, null, onSuccess, ontLogoutFailed);
    };

    notes.register = function () {

        var onSuccess = function () {
            console.log('registered');
            notes.login();
        };

        var ontRegFailed = function () {
            console.log('registration failed');
            notes.messages.error('Cannot register');
        };

        var payload = {
            username: 'guest', //$('#field-username').val(),
            password: 'password', //$('#field-password').val(),
            email: 'some@exmaple.com' //$('#field-email').val()
        };

        notes.util.jsonCall('POST', REST_SERVICE + '/auth/register', null, JSON.stringify(payload), onSuccess, ontRegFailed);
    };

    notes.setup = function () {

        console.log('running setup');

        var onSuccess = function (settings) {

            console.log('Hello ' + settings.user.username);

            var database = settings.databases[0];
            notes.folders.databaseId(database.id);
            notes.folders.defaultFolderId(database.defaultFolderId);
            notes.folders.trashFolderId(database.trashFolderId);

            notes.setup.ui();
            notes.setup.router();
            notes.queries.init();

        };

        var onNotLoggedIn = function () {
            console.log('not logged in');

            notes.login();

        };

        notes.util.jsonCall('GET', REST_SERVICE + '/auth/settings', null, null, onSuccess, onNotLoggedIn);
    };

    // -- Routing ------------------------------------------------------------------------------------------------------

    notes.setup.router = function () {

        var Router = Backbone.Router.extend({

            routes: {
//                todo fix
                '': 'dashboard',
                'doc\::id': 'document',
                'folder\::id': 'folder',
                'search\::query': 'search'   // #q:kiwis
            },

            search: function (query) {
                console.log('search ' + query);
                notes.queries.find(query);

            },

            document: function (docId) {
                console.log('open document #' + docId);
                notes.editors.edit(docId);
            },

            folder: function (folderId) {
                console.log('open folder ' + folderId);
                notes.folders.open(folderId);
            }
        });

        notes.router = new Router();

        // start routing
        // todo: wait until databases are loaded
        Backbone.history.start();

    };

    notes.setup.ui = function () {

        console.log('setup ui');

        $('#init-view').remove();
        $('#content-view').show();

        // -- Side Navigation ----------------------------------------------------------------------------------

        $('#databases').database();

        // -- Menu -- ------------------------------------------------------------------------------------------

        // todo if file is dropped to document and upload started -> block screen or other indicator
//            var overallProgress = $('#file-upload').fileupload('progress');
//            var activeUploads = $('#file-upload').fileupload('active');

        $('#file-upload').
            fileupload({
                url: REST_SERVICE + '/file/upload',
                dataType: 'json'
            })
            .bind('fileuploadsubmit', function (e, data) {

                $.blockUI();

                data.formData = {
                    folderId: notes.folders.activeFolderId(),
                    documentId: notes.documents.currentDocumentId()
                };
            })
            .bind('fileuploaddone', function (e, xhr) {
                console.log('done');

                var model = new notes.model.BasicDocument(xhr.result.result);

                $('#document-list').documentList(
                    {
                        folderId: model.get('folderId')
                    }
                );

                $.unblockUI();
            });


        $('#search-input').keypress(function (e) {
            if (e.which === 13) {
                notes.router.navigate('search:' + $(this).val());
            }
        });

        $('#breadcrumbs').breadcrumbs();
    };

})(notes);
