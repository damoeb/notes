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
<link href="js/pdf/pdf.css" rel="stylesheet" media="screen" />

<script src="js/vendor/modernizr-2.6.2.min.js"></script>

<script src="js/vendor/jquery-1.8.2.min.js"></script>
<script src="js/vendor/jquery.raty.js"></script>
<script src="js/vendor/jquery-ui-1.9.2.custom.js"></script>
<script src="js/vendor/jquery.dataTables.js"></script>
<script src="js/vendor/jquery.timeago.js"></script>
<script src="js/vendor/jquery.paginate.js"></script>
<script src="js/vendor/jquery.jstree.js"></script>

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
<script src="js/editor.js"></script>
<script src="js/notes.js"></script>
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
    }).click(function() {
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
    }).click(function() {
        // popup
    });


    /*
    $("#n-tree")
            .jstree({
                // List of active plugins
                "plugins": [
                    "themeroller", "json_data", "ui", "crrm", "dnd", "search", "types", "contextmenu"
                ],

                // I usually configure the plugin that handles the data first
                // This example uses JSON as it is most common
                "json_data": {
                    // This tree is ajax enabled - as this is most common, and maybe a bit more complex
                    // All the options are almost the same as jQuery's AJAX (read the docs)
                    "ajax": {
                        // the URL to fetch the data
                        "url": "/notes/rest/structure/",
                        // the `data` function is executed in the instance's scope
                        // the parameter is the node being loaded
                        // (may be -1, 0, or undefined when loading the root nodes)
                        "data": function (n) {
                            // the result is fed to the AJAX request `data` option
                            return {
                                "operation": "get_children",
                                "id": n.attr ? n.attr("id") : 1
                            };
                        }
                    }
                },

                // UI & core - the nodes to initially select and open will be overwritten by the cookie plugin

                // the UI plugin - it handles selecting/deselecting/hovering nodes
                "ui": {
                    // this makes the node with ID node_4 selected onload
                    "initially_select": [ "4" ]
                },
                // the core plugin - not many options here
                "core": {
                    // just open those two nodes up
                    // as this is an AJAX enabled tree, both will be downloaded from the server
                    "initially_open": [ "2" , "3" ]
                }
            })
            .bind("create.jstree", function (e, data) {
                $.post(
                        "/notes/rest/structure/",
                        {
                            "operation": "create_node",
                            "id": data.rslt.parent.attr("id"),
                            "position": data.rslt.position,
                            "title": data.rslt.name,
                            "type": data.rslt.obj.attr("rel")
                        },
                        function (r) {
                            if (r.status) {
                                $(data.rslt.obj).attr("id", r.id);
                            }
                            else {
                                $.jstree.rollback(data.rlbk);
                            }
                        }
                );
            })
            .bind("remove.jstree", function (e, data) {
                data.rslt.obj.each(function () {
                    $.ajax({
                        async: false,
                        type: 'POST',
                        url: "/notes/rest/structure/",
                        data: {
                            "operation": "remove_node",
                            "id": this.id
                        },
                        success: function (r) {
                            if (!r.status) {
                                data.inst.refresh();
                            }
                        }
                    });
                });
            })
            .bind("rename.jstree", function (e, data) {
                $.post(
                        "/notes/rest/structure/",
                        {
                            "operation": "rename_node",
                            "id": data.rslt.obj.attr("id"),
                            "title": data.rslt.new_name
                        },
                        function (r) {
                            if (!r.status) {
                                $.jstree.rollback(data.rlbk);
                            }
                        }
                );
            })
            .bind("move_node.jstree", function (e, data) {
                data.rslt.o.each(function (i) {
                    $.ajax({
                        async: false,
                        type: 'POST',
                        url: "/notes/rest/structure/",
                        data: {
                            "operation": "move_node",
                            "id": $(this).attr("id"),
                            "ref": data.rslt.cr === -1 ? 1 : data.rslt.np.attr("id"),
                            "position": data.rslt.cp + i,
                            "title": data.rslt.name,
                            "copy": data.rslt.cy ? 1 : 0
                        },
                        success: function (r) {
                            if (!r.status) {
                                $.jstree.rollback(data.rlbk);
                            }
                            else {
                                $(data.rslt.oc).attr("id", r.id);
                                if (data.rslt.cy && $(data.rslt.oc).children("UL").length) {
                                    data.inst.refresh(data.inst._get_parent(data.rslt.oc));
                                }
                            }
                            $("#analyze").click();
                        }
                    });
                });
            });
      */

    $('#n-list').notes();

    //$('#pane-annotate').annotate();

//    $('#n-table').dataTable({
//        'bStateSave': true,
//        'bPaginate': false,
//        "sScrollY": "200px",
//        "bFilter": false,
//        "bInfo": false,
//        "aaData": [
//            /* Reduced data set */
//            [ "Trident", "Internet Explorer 4.0", "Win 95+", 4, "X" ],
//            [ "Trident", "Internet Explorer 5.0", "Win 95+", 5, "C" ],
//            [ "Trident", "Internet Explorer 5.5", "Win 95+", 5.5, "A" ],
//            [ "Trident", "Internet Explorer 6.0", "Win 98+", 6, "A" ],
//            [ "Trident", "Internet Explorer 7.0", "Win XP SP2+", 7, "A" ],
//            [ "Gecko", "Firefox 1.5", "Win 98+ / OSX.2+", 1.8, "A" ],
//            [ "Gecko", "Firefox 2", "Win 98+ / OSX.2+", 1.8, "A" ],
//            [ "Gecko", "Firefox 3", "Win 2k+ / OSX.3+", 1.9, "A" ],
//            [ "Webkit", "Safari 1.2", "OSX.3", 125.5, "A" ],
//            [ "Webkit", "Safari 1.3", "OSX.3", 312.8, "A" ],
//            [ "Webkit", "Safari 2.0", "OSX.4+", 419.3, "A" ],
//            [ "Webkit", "Safari 3.0", "OSX.4+", 522.1, "A" ]
//        ],
//        "aoColumns": [
//            { "sTitle": "Engine" },
//            { "sTitle": "Browser" },
//            { "sTitle": "Platform" },
//            { "sTitle": "Version", "sClass": "center" },
//            { "sTitle": "Grade", "sClass": "center" }
//        ]
//    });

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
                            <input id="field-url" class="ui-widget-content ui-corner-all" type="text" name="url"/><a id="markieren">Markieren</a>
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
        <input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
    </div>

    <jsp:include page="inc-templates.jsp"/>
</div>

</body>
</html>
