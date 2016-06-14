/*
 * "Copyright (c) 2016   Capgemini Technology Services (hereinafter "Capgemini")
 *
 * License/Terms of Use
 * Permission is hereby granted, free of charge and for the term of intellectual
 * property rights on the Software, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to use, copy, modify and
 * propagate free of charge, anywhere in the world, all or part of the Software
 * subject to the following mandatory conditions:
 *
 * -   The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Any failure to comply with the above shall automatically terminate the license
 * and be construed as a breach of these Terms of Use causing significant harm to
 * Capgemini.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, PEACEFUL ENJOYMENT,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Capgemini shall not be used in
 * advertising or otherwise to promote the use or other dealings in this Software
 * without prior written authorization from Capgemini.
 *
 * These Terms of Use are subject to French law.
 *
 * IMPORTANT NOTICE: The WUIC software implements software components governed by
 * open source software licenses (BSD and Apache) of which CAPGEMINI is not the
 * author or the editor. The rights granted on the said software components are
 * governed by the specific terms and conditions specified by Apache 2.0 and BSD
 * licenses."
 */


package com.github.wuic.spring;

import com.github.wuic.WuicFacade;
import com.github.wuic.exception.WuicException;
import com.github.wuic.thymeleaf.WuicDialect;
import com.github.wuic.util.UrlProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collections;

/**
 * <p>
 * This class demonstrates how to configure spring and mix resolvers and transformers from several projects.
 * </p>
 *
 * @author Guillaume DROUET
 */
@Configuration
@EnableWebMvc
@PropertySource("classpath:application.properties")
public class WuicWebConfig extends WebMvcConfigurerAdapter {

    /**
     * Context path where resources resolved by WUIC are served.
     */
    public static final String RESOURCES_CONTEXT_PATH = "/resources/";

    /**
     * The application context that retrieves the WUIC facade.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * <p>
     * Creates a new {@link WuicFacadeBuilderFactory}.
     * </p>
     *
     * @return the factory
     */
    @Bean
    public WuicFacadeBuilderFactory wuicFacadeBuilderFactory() {
        return new WuicFacadeBuilderFactory();
    }

    /**
     * <p>
     * Creates the facade that WUIC will use.
     * </p>
     *
     * @param facadeBuilderFactory the factory
     * @return the facade
     * @throws WuicException if WUIC can't be initialize
     */
    @Bean
    public WuicFacade wuicFacade(final WuicFacadeBuilderFactory facadeBuilderFactory) throws WuicException {
        return facadeBuilderFactory
                .create()
                .contextPath(RESOURCES_CONTEXT_PATH)
                .build();
    }

    /**
     * <p>
     * Handles javascript and css files related to the application. It resolves and transforms the resources
     * wuic.xml file (here aggregated and compressed with YUICompressor). The version strategy is based on
     * {@link WuicVersionStrategy} that use the version number generated by WUIC regarding the configuration
     * in wuic.xml file (content based version number).
     * </p>
     *
     */
    @Bean
    public SimpleUrlHandlerMapping handleWuicResources(final ServletContext servletContext,
                                                       final ResourceUrlProvider resourceUrlProvider,
                                                       final WuicFacade wuicFacade) {
        final ResourceResolver versionResourceResolver = new VersionResourceResolver().addVersionStrategy(new WuicVersionStrategy(), "/**/*");
        final ResourceResolver pathResourceResolver = new WuicPathResourceResolver(applicationContext.getBean(WuicFacade.class));
        return new WuicHandlerMapping(applicationContext,
                servletContext,
                resourceUrlProvider,
                wuicFacade,
                Arrays.asList(versionResourceResolver, pathResourceResolver),
                Collections.<ResourceTransformer>emptyList());
    }

    /**
     * <p>
     * Creates the thymeleaf view resolver. The WUIC dialect is configured here to let the use the attribute processor
     * in templates.
     * </p>
     *
     * @return the view resolver for thymeleaf
     */
    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        final ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new WuicDialect(applicationContext.getBean(UrlProviderFactory.class), applicationContext.getBean(WuicFacade.class)));

        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setOrder(1);
        return viewResolver;
    }

    /**
     * <p>
     * Creates a bean that produces {@link UrlProviderFactory}.
     * </p>
     *
     * @param resourceUrlProvider the spring url provider
     * @return the factory
     */
    @Bean
    public UrlProviderFactory resourceUrlProviderHelper(final ResourceUrlProvider resourceUrlProvider) {
        return new ResourceUrlProviderHelperFactory(resourceUrlProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        final String resourceLocations = "classpath:/statics/";

        // Uncomment this line if you want to see 'resourceUrlProviderHelper' bean managing CSS without WUIC
        // Also don't forget to comment the import in wuic.json line and uncomment the import in index.html
        //handleCss(registry, resourceLocations);
        handleLib(registry, resourceLocations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    /**
     * <p>
     * Handles css directory content and resolve/transform it only with spring. Here we configure a version strategy
     * based on content.
     * </p>
     *
     * @param registry the registry
     * @param resourceLocations the statics base path
     */
    public void handleCss(final ResourceHandlerRegistry registry, final String resourceLocations) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations(resourceLocations + "/css/")
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }

    /**
     * <p>
     * Handles javascript libraries. It contains angular framework which is resolved/transformed only with spring.
     * Here we configure a fixed version strategy defined by the framework version.
     * </p>
     *
     * @param registry the registry
     * @param resourceLocations the statics base path
     */
    public void handleLib(final ResourceHandlerRegistry registry, final String resourceLocations) {
        registry.addResourceHandler("/lib/**")
                .addResourceLocations(resourceLocations + "/lib/")
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addFixedVersionStrategy("0.9.13", "/**/*"));
    }
}
