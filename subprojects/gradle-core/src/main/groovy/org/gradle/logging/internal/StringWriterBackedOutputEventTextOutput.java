/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.logging.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringWriterBackedOutputEventTextOutput extends WriterBackedStyledTextOutput implements OutputEventTextOutput {
    public StringWriterBackedOutputEventTextOutput() {
        super(new StringWriter());
    }

    @Override
    public String toString() {
        return getWriter().toString();
    }

    public OutputEventTextOutput exception(Throwable throwable) {
        PrintWriter writer = new PrintWriter(getWriter());
        throwable.printStackTrace(writer);
        writer.flush();
        return this;
    }

    @Override
    public OutputEventTextOutput text(Object text) {
        super.text(text);
        return this;
    }

    @Override
    public OutputEventTextOutput println() {
        super.println();
        return this;
    }
}
