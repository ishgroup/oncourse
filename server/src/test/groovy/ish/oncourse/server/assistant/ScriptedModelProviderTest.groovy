/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import groovy.transform.CompileStatic
import ish.oncourse.server.assistant.model.AssistantModelException
import ish.oncourse.server.assistant.model.ModelMessage
import ish.oncourse.server.assistant.model.ModelRequest
import ish.oncourse.server.assistant.model.ScriptedModelProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

@CompileStatic
class ScriptedModelProviderTest {

    @Test
    void repliesInTurnAndStartsAgain() {
        ScriptedModelProvider fake = new ScriptedModelProvider(['one', 'two'])

        Assertions.assertEquals('one', fake.reply(request('a')).text)
        Assertions.assertEquals('two', fake.reply(request('b')).text)
        Assertions.assertEquals('one', fake.reply(request('c')).text)
    }

    @Test
    void echoesWithoutReplies() {
        Assertions.assertEquals('You said: hello', new ScriptedModelProvider([]).reply(request('hello')).text)
        Assertions.assertEquals('You said: hello', new ScriptedModelProvider(null).reply(request('hello')).text)
    }

    @Test
    void recordsRequests() {
        ScriptedModelProvider fake = new ScriptedModelProvider(['ok'])
        ModelRequest first = request('a')
        ModelRequest second = request('b')

        fake.reply(first)
        fake.reply(second)

        Assertions.assertEquals([first, second], fake.requests)
    }

    @Test
    void failNextFailsOnce() {
        ScriptedModelProvider fake = new ScriptedModelProvider(['ok'])
        AssistantModelException failure = new AssistantModelException('boom')
        fake.failNext(failure)

        AssistantModelException thrown = Assertions.assertThrows(AssistantModelException, { fake.reply(request('a')) } as Executable)

        Assertions.assertSame(failure, thrown)
        Assertions.assertEquals('ok', fake.reply(request('b')).text)
        Assertions.assertEquals(2, fake.requests.size())
    }

    private static ModelRequest request(String text) {
        return new ModelRequest('system', [ModelMessage.user(text)])
    }
}
