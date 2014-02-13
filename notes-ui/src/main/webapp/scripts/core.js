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
            noty({text: 'logged out'});
        };

        var ontLogoutFailed = function () {
            console.log('logout failed');
            noty({type: 'error', text: 'Logout failed'});
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
            noty({type: 'error', text: 'Cannot register'});
        };

        var payload = {
            username: 'guest', //$('#field-username').val(),
            password: 'password', //$('#field-password').val(),
            email: 'some@exmaple.com' //$('#field-email').val()
        };

        notes.util.jsonCall('POST', REST_SERVICE + '/auth/register', null, JSON.stringify(payload), onSuccess, ontRegFailed);
    };

    notes.setup = function () {

        var onSuccess = function (settings) {

            console.log('Hello ' + settings.user.username);

            notes.app.databaseId(settings.databases[0].id);

            notes.setup.ui();
            notes.setup.router();

        };

        var onNotLoggedIn = function () {
            console.log('not logged in');

            notes.login();

        };

        notes.util.jsonCall('GET', REST_SERVICE + '/auth/settings', null, null, onSuccess, onNotLoggedIn);
    };

    // -- Search -------------------------------------------------------------------------------------------------------

    notes.search = function () {

        var query = $('#search-input').val();

        $('#search-view').search('refresh', query);
    };

    $('#search-input').keypress(function (e) {
        if (e.which == 13) {
            notes.search();
        }
    });

    $('#action-search').click(function (e) {
        notes.search();
    });

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

            folder: function (folderId) {
                console.log('open folder ' + folderId);
                $('#document-list').documentList('refresh', folderId);
            }
        });

        notes.router = new Router();

        // start routing
        // todo: wait until databases are loaded
        Backbone.history.start();

    };

    notes.setup.ui = function () {

        $('#init-view').remove();
        $('#content-view').show();

        // -- Side Navigation ----------------------------------------------------------------------------------

        $('#databases').databases({
            databaseId: notes.app.databaseId()
        });

        $('#editors').editors();

        // -- Menu -- ------------------------------------------------------------------------------------------

        $('#import-document')
            .click(notes.dialog.document.import);

        // todo if file is dropped to document and upload started -> block screen or other indicator
//            var overallProgress = $('#fileupload').fileupload('progress');
//            var activeUploads = $('#fileupload').fileupload('active');

        $('#file-upload').
            fileupload({
                url: REST_SERVICE + '/file/upload',
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

                var model = new notes.model.BasicDocument(xhr.result.result);

                $('#document-list').documentList('refresh', model.get('folderId'));
            });


        $('#search-input').keypress(function (e) {
            if (e.which === 13) {
                notes.router.navigate('search/' + $(this).val());
            }
        });

        $('#breadcrumbs').breadcrumbs();
    };

})(notes);
