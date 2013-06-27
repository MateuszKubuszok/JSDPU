/**
 * Copyright 2012-2013 Mateusz Kubuszok
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at</p> 
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.</p>
 */
package net.jsdpu.process.executors;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

public class TestAbstractProcessExecutor {
    @Test
    public void testExecute() throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        // given
        AbstractProcessExecutor executor = MockExecutors.abstractProcessExecutor();
        List<String[]> commands = MockExecutors.mockCommands();

        // when
        ExecutionQueueReader reader = executor.execute(commands);
        List<ProcessBuilder> processBuilders = getProcessBuilders(reader);

        // then
        assertThat(reader)
                .as("execute(List<String[]>) should create ExecutionQueueReader properly")
                .isNotNull();
        assertThat(processBuilders)
                .as("execute(List<String[]>) should initiate ExecutionQueueReader properly")
                .isNotNull().hasSize(1);
    }

    @Test
    public void testExecuteRoot() throws IOException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        // given
        AbstractProcessExecutor executor = MockExecutors.abstractProcessExecutor();
        List<String[]> commands = MockExecutors.mockCommands();

        // when
        ExecutionQueueReader reader = executor.executeRoot(commands);
        List<ProcessBuilder> processBuilders = getProcessBuilders(reader);

        // then
        assertThat(reader).as(
                "execute(String,List<String[]>) should create ExecutionQueueReader properly")
                .isNotNull();
        assertThat(processBuilders)
                .as("execute(String,List<String[]>) should initiate ExecutionQueueReader properly")
                .isNotNull().hasSize(1);
    }

    @Test
    public void testExecuteChoice() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, IOException {
        // given
        AbstractProcessExecutor executor = MockExecutors.abstractProcessExecutor();
        List<String[]> commands = MockExecutors.mockCommands();

        // when
        ExecutionQueueReader reader = executor.execute(commands, true);
        List<ProcessBuilder> processBuilders = getProcessBuilders(reader);

        // then
        assertThat(reader)
                .as("execute(String,List<String[]>,boolean) should create ExecutionQueueReader properly")
                .isNotNull();
        assertThat(processBuilders)
                .as("execute(String,List<String[]>,boolean) should initiate ExecutionQueueReader properly")
                .isNotNull().hasSize(1);
    }

    @Test
    public void testExecuteCommands() throws NoSuchMethodException, SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        // given
        AbstractProcessExecutor executor = MockExecutors.abstractProcessExecutor();
        List<String[]> commands = MockExecutors.mockCommands();
        Method executeCommands = AbstractProcessExecutor.class.getDeclaredMethod("executeCommands",
                List.class);
        executeCommands.setAccessible(true);

        // when
        ExecutionQueueReader reader = (ExecutionQueueReader) executeCommands.invoke(executor,
                commands);
        List<ProcessBuilder> processBuilders = getProcessBuilders(reader);

        // then
        assertThat(reader)
                .as("execute(String,List<String[]>,boolean) should create ExecutionQueueReader properly")
                .isNotNull();
        assertThat(processBuilders)
                .as("execute(String,List<String[]>,boolean) should initiate ExecutionQueueReader properly")
                .isNotNull().hasSize(1);
    }

    private List<ProcessBuilder> getProcessBuilders(ExecutionQueueReader reader)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        Field processQueue = ExecutionQueueReader.class.getDeclaredField("processQueue");
        processQueue.setAccessible(true);
        ProcessQueue queue = (ProcessQueue) processQueue.get(reader);
        Field processBuilders = ProcessQueue.class.getDeclaredField("processBuilders");
        processBuilders.setAccessible(true);
        return (List<ProcessBuilder>) processBuilders.get(queue);
    }
}
