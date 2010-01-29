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
package org.gradle.util;

import org.gradle.api.Action;
import org.gradle.listener.ListenerBroadcast;

import java.net.URL;
import java.net.URLClassLoader;

public class ObservableUrlClassLoader extends URLClassLoader {
    private final ListenerBroadcast<Action> broadcast = new ListenerBroadcast<Action>(Action.class);

    public ObservableUrlClassLoader(ClassLoader parent, URL... urls) {
        super(urls, parent);
    }

    public void whenUrlAdded(Action<? super ObservableUrlClassLoader> action) {
        broadcast.add(action);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
        broadcast.getSource().execute(this);
    }

    public void addURLs(Iterable<URL> urls) {
        for (URL url : urls) {
            addURL(url);
        }
    }
}
