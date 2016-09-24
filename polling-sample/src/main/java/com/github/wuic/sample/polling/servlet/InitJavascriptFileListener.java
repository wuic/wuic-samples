/*
 * Copyright (c) 2016   The authors of WUIC
 *
 * License/Terms of Use
 * Permission is hereby granted, free of charge and for the term of intellectual
 * property rights on the Software, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to use, copy, modify and
 * propagate free of charge, anywhere in the world, all or part of the Software
 * subject to the following mandatory conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, PEACEFUL ENJOYMENT,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.github.wuic.sample.polling.servlet;

import com.github.wuic.ApplicationConfig;
import com.github.wuic.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;

/**
 * <p>
 * Creates a file on the disk which will be loaded by WUIC.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.4.0
 */
public class InitJavascriptFileListener implements ServletContextListener {

    /**
     * Directory where the file is stored.
     */
    public static final String DIRECTORY_PATH = IOUtils.mergePath(System.getProperty("java.io.tmpdir"), "wuic-polling");

    /**
     * File name.
     */
    public static final String FILE_NAME = "nut-polling-sample.js";

    /**
     * The logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        InputStream is = null;
        OutputStream os = null;

        final File dir = new File(DIRECTORY_PATH);
        log.info("Creating {}: {}", DIRECTORY_PATH, dir.mkdirs());

        // Prepare nut
        System.setProperty("wuic.dir", DIRECTORY_PATH);

        try {
            is = getClass().getResourceAsStream("/default-apps.js");
            os = new FileOutputStream(new File(dir, FILE_NAME));
            IOUtils.copyStream(is, os);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        } finally {
            IOUtils.close(is, os);
        }

        // Prepare wuic.xml
        try {
            final File file = new File(dir, "wuic.xml");
            System.setProperty(ApplicationConfig.WUIC_SERVLET_XML_PATH_PARAM, file.toURI().toURL().toString());
            is = getClass().getResourceAsStream("/default-wuic.xml");
            os = new FileOutputStream(file);
            IOUtils.copyStream(is, os);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        } finally {
            IOUtils.close(is, os);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
    }
}
