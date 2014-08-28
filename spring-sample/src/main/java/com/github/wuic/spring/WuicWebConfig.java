/*
 * "Copyright (c) 2014   Capgemini Technology Services (hereinafter "Capgemini")
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
import com.github.wuic.thymeleaf.SpringWuicDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * <p>
 * This class demonstrates how to configure spring and mix resolvers and transformers from several projects.
 * </p>
 *
 * @author Guillaume DROUET
 */
@Configuration
@EnableWebMvc
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
     * Creates the facade that WUIC will use.
     * </p>
     *
     * @return the facade
     * @throws WuicException if WUIC can't be initialize
     */
    @Bean
    public WuicFacade wuicFacade() throws WuicException {
        return WuicFacade.newInstance(RESOURCES_CONTEXT_PATH, getClass().getResource("/wuic.xml"), Boolean.TRUE);
    }

    /**
     * <p>
     * Creates the thymeleaf view resolver. The WUIC dialect is configured here to let the use the attribute processor
     * in templates.
     * </p>
     *
     * @param resourceUrlProvider the provider that will resolve public URLs
     * @return the view resolver for thymeleaf
     */
    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(final ResourceUrlProvider resourceUrlProvider) {
        final ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new SpringWuicDialect(resourceUrlProvider, applicationContext.getBean(WuicFacade.class), true));

        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setOrder(1);
        return viewResolver;
    }

    /**
     * <p>
     * Creates a bean that will be used in thymeleaf template through SPEL to resolve public URLs when not using WUIC.
     * </p>
     *
     * @param resourceUrlProvider the spring url provider
     * @return the bean exposing method for SPEL
     */
    @Bean
    public ResourceUrlProviderHelper resourceUrlProviderHelper(final ResourceUrlProvider resourceUrlProvider) {
        return new ResourceUrlProviderHelper(resourceUrlProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        final String resourceLocations = "classpath:/statics/";
        handleCss(registry, resourceLocations);
        handleLib(registry, resourceLocations);
        handleJs(registry, resourceLocations);

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
                .addContentVersionStrategy("/**/*");
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
                .addFixedVersionStrategy("0.9.13", "/**/*");
    }

    /**
     * <p>
     * Handles javascript related to the application. It resolves and transforms the resources declared in wuic.xml file
     * (here aggregated and compressed with YUICompressor). The version strategy is based on {@link WuicVersionStrategy}
     * that use the version number generated by WUIC regarding the configuration in wuic.xml file (content based version
     * number).
     * </p>
     *
     * @param registry the registry
     * @param resourceLocations the statics base path
     */
    public void handleJs(final ResourceHandlerRegistry registry, final String resourceLocations) {
        registry.addResourceHandler(RESOURCES_CONTEXT_PATH + "**")
                .addResourceLocations(resourceLocations)
                .addVersionStrategy(new WuicVersionStrategy(), "/**/*")
                .addResolver(new WuicPathResourceResolver(applicationContext.getBean(WuicFacade.class)));
    }
}
