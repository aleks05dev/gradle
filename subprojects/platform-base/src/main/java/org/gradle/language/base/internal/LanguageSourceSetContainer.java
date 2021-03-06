/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.language.base.internal;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.CompositeDomainObjectSet;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.internal.typeconversion.NotationParser;
import org.gradle.language.base.FunctionalSourceSet;
import org.gradle.language.base.LanguageSourceSet;

import java.util.Set;

public class LanguageSourceSetContainer {
    private final NotationParser<Object, Set<LanguageSourceSet>> sourcesNotationParser = SourceSetNotationParser.parser();
    private FunctionalSourceSet mainSources;
    private DefaultDomainObjectSet<LanguageSourceSet> additionalSources = new DefaultDomainObjectSet<LanguageSourceSet>(LanguageSourceSet.class);

    public void setMainSources(FunctionalSourceSet mainSources) {
        this.mainSources = mainSources;
    }

    public void source(Object sources) {
        additionalSources.addAll(sourcesNotationParser.parseNotation(sources));
    }

    public FunctionalSourceSet getMainSources() {
        return mainSources;
    }

    public DomainObjectSet<LanguageSourceSet> getSources() {
        @SuppressWarnings("unchecked")
        DomainObjectSet<LanguageSourceSet> sources = CompositeDomainObjectSet.create(LanguageSourceSet.class, mainSources, additionalSources);
        return sources;
    }
}
