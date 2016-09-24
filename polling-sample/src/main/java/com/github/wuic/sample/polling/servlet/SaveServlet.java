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

import com.github.wuic.config.bean.PropertyBean;
import com.github.wuic.config.bean.WuicBean;
import com.github.wuic.util.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * Saves the sumbitted script into the file loaded by WUIC.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.4.0
 */
public class SaveServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        InputStream is = null;
        OutputStream os = null;
        File file;

        try {
            // Save script
            final String code = req.getParameter("script");
            file = new File(InitJavascriptFileListener.DIRECTORY_PATH, InitJavascriptFileListener.FILE_NAME);
            is = new ByteArrayInputStream(code.getBytes());
            os = new FileOutputStream(file);
            IOUtils.copyStream(is, os);
        } finally {
            IOUtils.close(is, os);
        }

        try {
            // Save configuration
            file = new File(InitJavascriptFileListener.DIRECTORY_PATH, "wuic.xml");
            final Boolean cache = "on".equals(req.getParameter("cache"));
            final JAXBContext context = JAXBContext.newInstance(WuicBean.class);
            final WuicBean bean = (WuicBean) context.createUnmarshaller().unmarshal(file);
            final PropertyBean prop = bean.getEngineBuilders().get(0).getProperties().remove(0);
            bean.getEngineBuilders().get(0).getProperties().add(new PropertyBean(prop.getKey(), cache.toString()));
            context.createMarshaller().marshal(bean, file);
        } catch (JAXBException je) {
            throw new ServletException(je);
        }

        resp.sendRedirect("/");
    }
}
