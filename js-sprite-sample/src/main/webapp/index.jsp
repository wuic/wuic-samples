<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sample JS sprites (with cgSceneGraph) - Page generation timestamp: <%=System.currentTimeMillis()%></title>

    <%@ taglib prefix="wuic-conf" uri="http://www.github.com/wuic/xml-conf" %>
    <wuic-conf:xml-configuration>
        <wuic>
            <heaps>
                <heap id="index">
                    <heap-id>img</heap-id>
                    <heap-id>cgsg-js</heap-id>
                </heap>
            </heaps>
        </wuic>
    </wuic-conf:xml-configuration>
</head>
<body>

<canvas id="scene" width="1920" height="1080">
    Your browser does not support the canvas element.
</canvas>

<%@ taglib prefix="wuic" uri="http://www.github.com/wuic" %>
<wuic:html-import workflowId="index"/>
<script type="text/javascript">
    // Get the canvas
    var canvasScene = document.getElementById("scene");

    // Create the scene
    var main = new CGSGView(canvasScene);
    //resize the canvas to fulfill the viewport
    this.viewDimension = cgsgGetRealViewportDimension();
    main.setCanvasDimension(this.viewDimension);
    main.startPlaying();

    var aggregateUrl;
    var offsetY = 5;
    var maxW = 0;
    var root = new CGSGNode(0, 0, 0, 0);
    CGSG.sceneGraph.addNode(root, null);

    //Create the Node Image Factory with his specific workflow ID
    var imageFactory = new WUICCGSGNodeImageFactory("img");

    //get the map between imgUrl and spriteUrl
    var imgMap = imageFactory.getImgMap();

    //init property of the nodes
    var data = {};
    data.x = 0;
    data.y = 0;

    // Add each image to the scene
    // Do not use '&lt;' in the code until this issue is fixed: https://github.com/wuic/wuic/issues/182
    var i = 0;
    while (i !== imgMap.getLength()) {

        //create the CGSGNodeImage with the WUIC factory
        var node = imageFactory.create(imgMap.getAt(i).key, data);

        //create a textNode for the img name
        var text = new CGSGNodeText(10, offsetY + node.getHeight() / 2, node.name + " : ");
        text.setTextBaseline("middle");
        root.addChild(text);

        node.translateTo(text.position.x + text.getWidth() + 10, offsetY);
        root.addChild(node);

        offsetY += node.getHeight() + 5;

        if (node.position.x + node.getWidth() > maxW) {
            maxW = node.position.x + node.getWidth();
        }

        i++;
    }

    var aggregateText = new CGSGNodeText(100 + maxW, 5, "Downloaded image : ");

    // Show the original image
    var imgAggregate = imageFactory.buildNode({x: aggregateText.position.x, y: 40}, imgMap.getAt(0).value);
    root.addChild(aggregateText);
    root.addChild(imgAggregate);
</script>
</body>
</html>