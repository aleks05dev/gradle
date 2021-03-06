/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.nativeplatform

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.util.TextUtil

class TestSuiteDefinitionIntegrationSpec extends AbstractIntegrationSpec {
    def "plugin can define custom test suite type"() {
        buildFile << """
interface CustomTestSuite extends TestSuiteSpec {
}

class DefaultCustomTestSuite extends BaseComponentSpec implements CustomTestSuite {
    ComponentSpec testedComponent
}

interface CustomTestBinary extends TestSuiteBinarySpec {
    String getData()
}

class DefaultCustomTestBinary extends BaseBinarySpec implements CustomTestBinary {
    TestSuiteSpec testSuite
    String data
}

class TestSuitePlugin extends RuleSource {
    @ComponentType
    void registerTestSuiteType(ComponentTypeBuilder<CustomTestSuite> builder) {
        builder.defaultImplementation(DefaultCustomTestSuite)
    }

    @BinaryType
    void registerBinaryType(BinaryTypeBuilder<CustomTestBinary> builder) {
        builder.defaultImplementation(DefaultCustomTestBinary)
    }
}

apply plugin: NativeBinariesTestPlugin
apply plugin: TestSuitePlugin

model {
    testSuites {
        unitTests(CustomTestSuite)
    }
}
"""

        when:
        run "model", "--detail", "BARE"

        then:
        output.contains(TextUtil.toPlatformLineSeparators("""
    testSuites
        unitTests
            binaries
            sources"""))
    }
}
