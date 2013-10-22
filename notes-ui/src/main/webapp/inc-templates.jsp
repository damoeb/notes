<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- TEMPLATES ----------------------------------------------------------------------------------------------------- --%>

<div id="dialog-publish-template" class="dialog-publish" title="Publish">
    <!--<div class="section">-->
    <!--<h3>Special</h3>-->
    <!--<label>Publish as Special <input type="text"/></label> <span class="button add-special">Add Special</span>-->
    <!--</div>-->
    <div class="section original-data">

        <h4>Link</h4>

        <div class="org-link"></div>

        <h4>Title</h4>
        <!--suppress HtmlFormInputWithoutLabel -->
        <div class="org-title"></div>

        <h4>Description</h4>

        <div class="org-text" style="overflow: auto; max-height: 150px"></div>

    </div>

    <div class="section custom-data">
        <h4>Custom Title</h4>
        <!--suppress HtmlFormInputWithoutLabel -->
        <input class="custom-title" type="text" style="width: 100%; "/>

        <h4>Custom Description</h4>
        <!--suppress HtmlFormInputWithoutLabel -->
        <textarea class="custom-text" style="width: 100%; height: 50px"></textarea>
    </div>
</div>

<div id="dialog-add-article-template" class="dialog-add-article" title="Add Article">

    <div class="section custom-data">
        <h4>Title</h4>
        <!--suppress HtmlFormInputWithoutLabel -->
        <input class="custom-title" type="text" style="width: 100%; "/>

        <h4>Link</h4>
        <!--suppress HtmlFormInputWithoutLabel -->
        <input class="custom-link" type="text" style="width: 100%; "/>

        <h4>Description</h4>
        <!--suppress HtmlFormInputWithoutLabel -->
        <textarea class="custom-text" style="width: 100%; height: 50px"></textarea>
    </div>
</div>

<div id="dialog-add-feed-template" class="dialog-add-index" title="Add Feed">

    <div class="section custom-data">
        <h4>Url</h4>
        <!--suppress HtmlFormInputWithoutLabel -->
        <input class="url" type="text" style="width: 100%; "/>
    </div>
</div>

