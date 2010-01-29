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
package org.gradle.process;

import org.gradle.api.Action;
import org.gradle.api.internal.ClassPathRegistry;
import org.gradle.messaging.MessagingServer;
import org.gradle.messaging.ObjectConnection;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(JMock.class)
public class DefaultWorkerProcessFactoryTest {
    private final JUnit4Mockery context = new JUnit4Mockery();
    private final MessagingServer messagingServer = context.mock(MessagingServer.class);
    private final ClassPathRegistry classPathRegistry = context.mock(ClassPathRegistry.class);
    private final DefaultWorkerProcessFactory factory = new DefaultWorkerProcessFactory(messagingServer,
            classPathRegistry);

    @Test
    public void createsAWorkerProcess() throws Exception {
        final Set<File> processClassPath = Collections.singleton(new File("something.jar"));

        context.checking(new Expectations() {{
            one(classPathRegistry).getClassPathFiles("WORKER_PROCESS");
            will(returnValue(processClassPath));
        }});

        WorkerProcessBuilder builder = factory.newProcess();

        assertThat(builder.getJavaCommand().getClasspath(), equalTo(processClassPath));
        assertThat(builder.getJavaCommand().getMainClass(), equalTo(GradleWorkerMain.class.getName()));

        builder.worker(new TestAction());
        builder.applicationClasspath(Arrays.asList(new File("app.jar")));
        builder.sharedPackages("package1", "package2");

        final ObjectConnection connection = context.mock(ObjectConnection.class);
        final URI serverAddress = new URI("test:something");

        context.checking(new Expectations(){{
            one(messagingServer).createUnicastConnection();
            will(returnValue(connection));
            one(connection).getLocalAddress();
            will(returnValue(serverAddress));
        }});

        WorkerProcess process = builder.build();

        assertThat(process, instanceOf(DefaultWorkerProcess.class));

        ObjectInputStream instr = new ObjectInputStream(builder.getJavaCommand().getCommand().getStandardInput());
        assertThat(instr.readObject(), equalTo((Object) builder.getApplicationClasspath()));
        assertThat(instr.readObject(), equalTo((Object) builder.getSharedPackages()));
        assertThat(instr.readObject(), instanceOf(Collection.class));
        assertThat(instr.readObject(), instanceOf(TestAction.class));
        assertThat(instr.readObject(), equalTo((Object) serverAddress));
    }

    private static class TestAction implements Action<WorkerProcessContext>, Serializable {
        public void execute(WorkerProcessContext workerProcessContext) {
            throw new UnsupportedOperationException();
        }
    }
}
