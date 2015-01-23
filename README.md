The table below describes every samples of the WUIC project. Each sample is a module defined in 'samples'.
Samples are not hosted in maven central. To use them, simply download the project's archive on github, move
into the desired sample module and just run :

```
  mvn jetty:run
```

Some samples are currently available on the 'SNAPSHOT' branche. However, you can use them until they will be released.
All samples apply optimizations provided by core module:
- Aggregation
- Client-side caching
- Server-side caching
- Servlet serving process results
- Rewrite CSS URL
- Rewrite Sourcemap URL

<table width=100% height=100%>
    <tr>
        <td><b>Name</b></td>
        <td><b>Description</b></td>
        <td><b>Additional feature(s)</b></td>
    </tr>
        <td rowspan="4">bootstrap3-sample</td>
        <td rowspan="4">
            This sample embeds the bootstrap3 samples and applies the Servlet filter provided by WUIC.
        </td>
        <td>
             YuiCompressor Javascript & CSS minification
        </td>
    <tr>
         <td>HTML filter</td>
    </tr>
    <tr>
         <td>Server-side caching with EhCache</td>
    </tr>
    </tr>
         <td>HTML compressor support</td>
    </tr>
    <tr>
        <td rowspan="5">js-css-sample</td>
        <td rowspan="5">
            The webapp embeds the <a href="http://jqueryui.com/resources/download/jquery-ui-1.10.2.zip">JQuery UI</a> archive.
            All the demos have been copied into the '/using-wuic' path to show how to integrate them with WUIC.
            This way, you can see how you can embed uncompressed framework resources and configure a 'production' mode
            using WUIC. Think how it would be useful to disable compression to debug when your application raises an
            error in a compressed script!
            <b>
                NOTE : work in progress! Already integrated the different 'accordion', 'addClass', 'animate', 'autocomplete', 'button', 'datepicker' demos.
            </b>

            You can also see how the servlet filter works under the path '/jquery-ui-1.10.2/filter'.
        </td>
        <td>
             YuiCompressor Javascript & CSS minification
        </td>
    <tr>
        <td>HTML filter</td>
    </tr>
    <tr>
        <td>JSP Tag</td>
    </tr>
    <tr>
        <td>Server-side caching with EhCache</td>
    </tr>
    </tr>
         <td>HTML compressor support</td>
    </tr>
    <tr>
        <td rowspan="5">js-sprite-sample</td>
        <td rowspan="5">
            The sample includes the <a href="http://gwennaelbuchet.github.io/cgSceneGraph/">cgSceneGraph</a> framework.
            It demonstrates how to include a set of images aggregated and loaded with sprite in Javascript. The demo
            just displays the different images using sprites.
        </td>
        <td>YuiCompressor Javascript & CSS minification</td>
    </tr>
    <tr>
        <td>HTML filter</td>
    </tr>
    <tr>
        <td>Server-side caching with EhCache</td>
    </tr>
    <tr>
        <td>Image aggregation with javascript sprite generation</td>
    </tr>
    <tr>
        <td>JSP tag</td>
    </tr>
    <tr>
        <td rowspan="5">css-sprite-sample</td>
        <td rowspan="5">
            Demonstrates how to include a set of images aggregated and loaded with sprite in CSS. The demo
            just displays a set of different flags loaded from a single image .
        </td>
        <td>YuiCompressor Javascript & CSS minification</td>
    </tr>
    <tr>
        <td>HTML filter</td>
    </tr>
    <tr>
        <td>Server-side caching with EhCache</td>
    </tr>
    <tr>
        <td>Image aggregation with CSS sprite generation</td>
    </tr>
    <tr>
        <td>JSP tag</td>
    </tr>
    <tr>
        <td>thymeleaf-sample</td>
        <td>
            With a modified demo from <a herf="http://datatables.net/">datatable</a> project, this samples shows how you
            can use the thymeleaf dialect and its import processor.
        </td>
        <td>
             Thymeleaf support
        </td>
    </tr>
    <tr>
        <td rowspan="3">polling-sample</td>
        <td rowspan="3">
            Demonstrates how WUIC can poll both configuration files and nuts and refresh them at runtime.
        </td>
        <td>YuiCompressor Javascript & CSS minification</td>
    </tr>
    <tr>
        <td>Configuration polling</td>
    </tr>
    <tr>
        <td>Nut polling</td>
    </tr>
    <tr>
        <td>build-time-sample</td>
        <td>
            This sample shows how you can process nuts with WUIC when you build your project with maven and not on the fly.
            The application is based on a demo from the famous <a href="https://github.com/madebymany/sir-trevor-js">Sir Trevor</a>
            project.
        </td>
        <td>
             Plugin 'static-helper-maven-plugin' for maven.
        </td>
    </tr>
    <tr>
        <td rowspan="3">spring-sample</td>
        <td rowspan="3">
            Spring 4.1 offers web resources managements and could be integrated with WUIC as demonstrated by this sample.
            The view also relies on thymeleaf support.
        </td>
        <td>YuiCompressor Javascript & CSS minification</td>
    </tr>
    <tr>
        <td>Spring assets pipeline integration</td>
    </tr>
    <tr>
        <td>Thymeleaf integration with spring</td>
    </tr>
    <tr>
        <td rowspan="3">typescript-sample</td>
        <td rowspan="3">
            Typescript must be compiled into javascript to be interpreted by the browser.
            This sample demonstrates how WUIC does it transparently.
        </td>
        <td>WRO4J based extension</td>
    </tr>
    <tr>
        <td>Cross-platform support (rhino)</td>
    </tr>
    <tr>
        <td>Node.JS support</td>
    </tr>
</table>
