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


package com.github.wuic.sample;

import com.github.wuic.EnumNutType;
import com.github.wuic.config.ObjectBuilderInspector;
import com.github.wuic.engine.EngineType;
import com.github.wuic.engine.core.TextAggregatorEngine;
import com.github.wuic.nut.ConvertibleNut;
import com.github.wuic.util.IOUtils;
import com.github.wuic.util.Input;
import com.github.wuic.util.Output;
import com.github.wuic.util.Pipe;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * This class installs an additional transformer that will wrap any aggregated javascript files in an anonymous function
 * like this {@code (function(){ ... })();}.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.5.2
 */
@ObjectBuilderInspector.InspectedType(TextAggregatorEngine.class)
public class FunctionWrapperObjectBuilderInspector implements ObjectBuilderInspector, Pipe.Transformer<ConvertibleNut> {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T inspect(T object) {
        TextAggregatorEngine.class.cast(object).addTransformer(this);

        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean transform(final Input is, final Output output, final ConvertibleNut convertible) throws IOException {
        if (convertible.getNutType().equals(EnumNutType.JAVASCRIPT)) {
            final Writer os = output.writer();
            os.write("(function(){");
            IOUtils.copyStream(is.reader(), os);
            os.write("})();");

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAggregateTransformedStream() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int order() {
        return EngineType.AGGREGATOR.ordinal();
    }
}
