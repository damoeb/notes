<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<!--[if lt IE 7]>
<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>
<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>
<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" xmlns="http://www.w3.org/1999/html"> <!--<![endif]-->
<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <link rel="stylesheet" href="css/bootstrap.css">
    <%--<link rel="stylesheet" href="css/normalize.css">--%>
    <link rel="stylesheet" href="css/main.css">
    <link rel="stylesheet" href="css/vendor/jquery-ui-1.9.2.custom.css">
    <link rel="stylesheet" href="css/vendor/jquery.dataTables.css">
    <link href="js/pdf/pdf.css" rel="stylesheet" media="screen"/>

    <script src="js/vendor/modernizr-2.6.2.min.js"></script>

    <script src="js/vendor/jquery-1.8.2.min.js"></script>
    <script src="js/vendor/jquery-ui-1.9.2.custom.js"></script>
    <script src="js/vendor/jquery.dataTables.js"></script>
    <script src="js/vendor/jquery.timeago.js"></script>
    <script src="js/vendor/jquery.paginate.js"></script>

    <!-- notifications -->
    <!-- see http://needim.github.com/noty/-->
    <script src="js/vendor/jquery.noty.js"></script>
    <script src="js/vendor/noty/layouts/top.js"></script>
    <script src="js/vendor/noty/themes/default.js"></script>

    <script src="js/plugins.js"></script>
    <script src="js/ckeditor/ckeditor.js"></script>
    <script src="js/ckeditor/adapters/jquery.js"></script>

    <script src="js/fileupload/jquery.iframe-transport.js"></script>
    <script src="js/fileupload/jquery.fileupload.js"></script>
    <%--<script src="js/fileupload/jquery.fileupload-ui.js"></script>--%>
    <%--<script src="js/fileupload/jquery.fileupload-process.js"></script>--%>
    <link rel="stylesheet" href="js/fileupload/jquery.fileupload-ui.css">


    <script src="js/pdf/pdf.js" type="text/javascript"></script>
    <script src="js/pdf/textlayerbuilder.js" type="text/javascript"></script>

    <script type="text/javascript">
        var notes = {};
    </script>

    <script src="js/util.js"></script>
    <script src="js/editors.js"></script>
    <script src="js/annotate.js"></script>

    <script type="text/javascript">


        $(document).ready(function () {

            $('#n-editor').editor();

            // --------------------

            $('#create-note').button({
                icons: {
                    primary: 'ui-icon-plus'
                },
                label: 'New Note'
            }).click(function () {
                        $('#n-editor').editor('create');
                    });

            $('#action-settings').button({
                icons: {
                    primary: "ui-icon-gear"
                },
                text: false
            });

            $('#run-search').button({
                icons: {
                    primary: 'ui-icon-search'
                },
                text: false
            });

            $('#markieren').button({
                icons: {
                    primary: 'ui-icon-search'
                }
            }).click(function () {
                        // popup
                    });


            $('#n-list').notes();

        });

    </script>

</head>
<body>

<jsp:include page="inc-header.jsp"/>

<!--[if lt IE 7]>
<p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade
    your browser</a></p>
<![endif]-->

<div class="container">

    <div class="row" style="padding-top:10px; padding-bottom:10px">
        <div class="col-lg-2">
        </div>
        <div class="col-lg-9">
            <input id="field-search" class="ui-widget-content ui-corner-all" type="text" name="title"
                   style="width:300px; font-size:14px; padding:3px 4px;"/>
            <button id="run-search">Search</button>
            <button id="create-note">Create Note</button>
        </div>
        <div class="col-lg-1">
            <button id="action-settings" style="float: right">Settings</button>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-2">
        </div>
        <div class="col-lg-10" id="toolbar">
        </div>
    </div>

    <%-- path ----------------------------------------------------------------------------------------------------- --%>
    <div class="row">
        <div class="col-lg-2">
            <dl>
                <dt>Notebooks</dt>
                <dd>Work</dd>
                <dd>Private</dd>
                <dt style="margin-top:10px">Favourites</dt>
                <dd>Books</dd>
                <dd>Personal</dd>
            </dl>
        </div>

        <div class="col-lg-10">

            <div id="pane-editor" class="row" style="display:none;">
                <div id="n-editor" class="col-lg-12">

                    <div class="row">
                        <div style="display: inline-block; width:10px"></div>
                        <button id="action-back">Back</button>
                        <div class="space"></div>

                        <button id="action-favourite">Favourite</button>
                        <button id="action-remove">Remove</button>
                        <div class="space"></div>

                        <button id="action-tag">Tag</button>
                        <button id="action-attach-file">Attachment</button>
                        <%--<button id="action-download">Download</button>--%>

                        <%--<button id="action-attach-file">Attachment</button>--%>
                        <%--<button id="action-close-note" style="float: right">Close</button>--%>
                    </div>

                    <div class="row" style="margin-top:15px; margin-bottom:5px;">
                        <div>
                            <label for="field-title">Title</label>
                            <input id="field-title" class="ui-widget-content ui-corner-all" type="text"
                                   name="title"/>
                        </div>

                        <div>
                            <label for="field-url">URL</label>
                            <input id="field-url" class="ui-widget-content ui-corner-all" type="text" name="url"/><a
                                id="markieren">Markieren</a>
                        </div>

                        <div id="progress">
                            <div class="bar" style="width: 0%;"></div>
                        </div>

                        <%--<div>--%>
                        <%--<label for="field-tags">Tags</label>--%>
                        <%--<input id="field-tags" class="ui-widget-content ui-corner-all" type="text" name="tags"/>--%>
                        <%--</div>--%>

                        <div id="attachments" style="display: none">
                            <label style="float: left">Files</label>

                            <div id="n-attachments">

                            </div>
                        </div>

                    </div>

                    <textarea type="text" name="content" id="field-content"
                              class="text ui-widget-content ui-corner-all"></textarea>

                </div>

            </div>

            <div id="pane-notes" class="row">

                <%-- tree ----------------------------------------------------------------------------------------- --%>
                <%--
                <div class="col-lg-3">
                    <div id="n-tree" style="background: none; border:none;"></div>
                </div>
                --%>
                <%-- files ---------------------------------------------------------------------------------------- --%>
                <div id="n-list" class="col-lg-12" style="border-left: 1px solid #cccccc; min-height: 500px;">

                </div>
            </div>

            <div id="pane-annotate">

            </div>

        </div>

    </div>

</div>

<jsp:include page="inc-footer.jsp"/>

<div style="display: none">

    <div id="dialog-promt-new-name" title="Rename Note">
        <label for="name">Rename to</label>
        <input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all"/>
    </div>

    <jsp:include page="inc-templates.jsp"/>
</div>

</body>
</html>
