/*global notes:false */
/*global Backbone:false */
/*global noty:false */

'use strict';

(function (notes) {

    notes.login = function () {

        var onSuccess = function () {
            console.log('logged in');
            noty({text: 'logged in'});
            notes.setup();
        };

        var ontLoginFailed = function () {
            console.log('login failed');
            noty({type: 'error', text: 'login failed'});
        };

        var payload = {
            username: $('#field-username').val(),
            password: $('#field-password').val()
        };

        notes.util.jsonCall('POST', '/notes/rest/auth/login', null, JSON.stringify(payload), onSuccess, ontLoginFailed);
    };

    notes.logout = function () {

        var onSuccess = function () {
            console.log('logged out');
            noty({text: 'logged out'});
        };

        var ontLogoutFailed = function () {
            console.log('logout failed');
            noty({type: 'error', text: 'Logout failed'});
        };

        notes.util.jsonCall('GET', '/notes/rest/auth/logout', null, null, onSuccess, ontLogoutFailed);
    };

    notes.register = function () {

        var onSuccess = function () {

            console.log('registered');
        };

        var ontRegFailed = function () {
            console.log('registration failed');
        };

        var payload = {
            username: $('#field-username').val(),
            password: $('#field-password').val(),
            email: $('#field-email').val()
        };

        notes.util.jsonCall('POST', '/notes/rest/auth/register', null, JSON.stringify(payload), onSuccess, ontRegFailed);
    };

    notes.setup = function () {

        $('.view').hide();

        var onSuccess = function (settings) {

            console.log('Hello ' + settings.user.username);

            notes.app.databaseId(settings.databases[0].id);

            notes.setup.ui();
            notes.setup.router();

        };

        var onNotLoggedIn = function () {
            console.log('not logged in');

            $('#login-view').show();
        };

        notes.util.jsonCall('GET', '/notes/rest/auth/settings', null, null, onSuccess, onNotLoggedIn);
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

        notes.router = new Router();

        // start routing
        // todo: wait until databases are loaded
        Backbone.history.start();

    };

    notes.setup.ui = function () {

        $('#content-view').show();

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
