/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.MessageParam
import com.anthropic.models.messages.OutputConfig
import com.anthropic.models.messages.TextBlockParam
import com.google.inject.Binder
import com.google.inject.Module
import groovy.transform.CompileStatic
import io.bootique.BQRuntime
import io.bootique.Bootique
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.assistant.model.AssistantModelException
import ish.oncourse.server.assistant.model.AssistantModelProvider
import ish.oncourse.server.assistant.model.BedrockModelProvider
import ish.oncourse.server.assistant.model.ModelMessage
import ish.oncourse.server.assistant.model.ModelRequest
import ish.oncourse.server.assistant.model.ScriptedModelProvider
import ish.oncourse.server.assistant.model.UnconfiguredModelProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import java.nio.file.Files
import java.nio.file.Path

import static org.mockito.Mockito.mock

@CompileStatic
class AssistantConfigTest {

    @Test
    void defaultsMatchBedrockSpike() {
        AssistantConfig config = new AssistantConfig()

        Assertions.assertEquals('none', config.provider)
        Assertions.assertEquals('ap-southeast-2', config.region)
        Assertions.assertEquals('au.anthropic.claude-sonnet-5-5', config.modelId)
        Assertions.assertEquals(60L, config.readTimeoutSeconds)
        Assertions.assertEquals(120L, config.requestTimeoutSeconds)
        Assertions.assertEquals(1, config.maxRetries)
        Assertions.assertEquals(10, config.maxMessagesPerMinute)
    }

    @Test
    void providerIsChosenByConfiguration() {
        Assertions.assertTrue(provider('bedrock') instanceof BedrockModelProvider)
        Assertions.assertTrue(provider(' Bedrock ') instanceof BedrockModelProvider)
        Assertions.assertTrue(provider('fake') instanceof ScriptedModelProvider)
        Assertions.assertTrue(provider('none') instanceof UnconfiguredModelProvider)
        Assertions.assertTrue(provider(null) instanceof UnconfiguredModelProvider)
        Assertions.assertTrue(provider('') instanceof UnconfiguredModelProvider)
        Assertions.assertTrue(provider('openai') instanceof UnconfiguredModelProvider)
    }

    @Test
    void fakeUsesConfiguredReplies() {
        AssistantConfig config = new AssistantConfig(provider: 'fake', fakeReplies: ['Hi there'])

        Assertions.assertEquals('Hi there', config.createProvider().reply(request()).text)
    }

    @Test
    void unconfiguredProviderAlwaysFails() {
        AssistantModelProvider provider = new UnconfiguredModelProvider()

        AssistantModelException e = Assertions.assertThrows(AssistantModelException, { provider.reply(request()) } as Executable)
        Assertions.assertEquals(UnconfiguredModelProvider.NOT_CONFIGURED, e.message)
    }

    @Test
    void bedrockFailureIsAssistantModelException() {
        // No real call can be made: the profile doesn't exist, so the request can't be signed and is never sent,
        // and the region doesn't exist either. Creating the provider doesn't touch AWS, only the message does.
        String profile = 'oncourse-test-missing-' + UUID.randomUUID()
        AssistantConfig config = new AssistantConfig(provider: 'bedrock', region: 'xx-nowhere-1', profile: profile,
                connectTimeoutSeconds: 1, readTimeoutSeconds: 1, requestTimeoutSeconds: 2, maxRetries: 0)
        BedrockModelProvider provider = config.createProvider() as BedrockModelProvider
        try {
            AssistantModelException e = Assertions.assertThrows(AssistantModelException, { provider.reply(request()) } as Executable)

            Assertions.assertTrue(e.message.startsWith('Bedrock call failed: '), e.message)
            // it failed on the credentials, before a request was sent
            Throwable cause = e.cause
            while (cause.cause != null && !cause.message?.contains(profile)) {
                cause = cause.cause
            }
            Assertions.assertTrue(cause.message?.contains(profile), String.valueOf(e.cause))
        } finally {
            provider.close()
        }
    }

    @Test
    void bedrockRequestFollowsSpikeDecisions() {
        AssistantConfig config = new AssistantConfig()
        ModelRequest request = new ModelRequest('You are the onCourse Assistant', [
                ModelMessage.user('first'), ModelMessage.assistant('reply'), ModelMessage.user('second')])

        MessageCreateParams params = BedrockModelProvider.buildParams(config, request)

        Assertions.assertEquals('au.anthropic.claude-sonnet-5-5', params.model().asString())
        Assertions.assertEquals(4000L, params.maxTokens())
        Assertions.assertEquals(OutputConfig.Effort.LOW, params.outputConfig().get().effort().get())
        // adaptive thinking is the default, no tools in this version
        Assertions.assertFalse(params.thinking().isPresent())
        Assertions.assertFalse(params.tools().isPresent())

        List<TextBlockParam> system = params.system().get().asTextBlockParams()
        Assertions.assertEquals(1, system.size())
        Assertions.assertEquals('You are the onCourse Assistant', system[0].text())
        Assertions.assertTrue(system[0].cacheControl().isPresent(), 'the system prompt is a prompt cache breakpoint')

        List<MessageParam> messages = params.messages()
        Assertions.assertEquals([MessageParam.Role.USER, MessageParam.Role.ASSISTANT, MessageParam.Role.USER], messages*.role())
        Assertions.assertEquals(['first', 'reply', 'second'], messages.collect { MessageParam it -> it.content().asString() })
    }

    @Test
    void missingAssistantSectionGivesUnconfiguredProvider() {
        Assertions.assertTrue(providerFromYaml('db:\n  uri: jdbc:mariadb://localhost/x\n') instanceof UnconfiguredModelProvider)
    }

    @Test
    void brokenAssistantSectionGivesUnconfiguredProvider() {
        // the server starts, without a model
        Assertions.assertTrue(providerFromYaml('assistant:\n  provider: fake\n  maxTokens: lots\n') instanceof UnconfiguredModelProvider)
    }

    @Test
    void assistantSectionIsRead() {
        AssistantModelProvider provider = providerFromYaml('assistant:\n  provider: fake\n  fakeReplies:\n    - Hello from yml\n')

        Assertions.assertTrue(provider instanceof ScriptedModelProvider)
        Assertions.assertEquals('Hello from yml', provider.reply(request()).text)
    }

    @Test
    void rateLimitIsRead() {
        AssistantRateLimiter limiter = fromYaml('assistant:\n  provider: fake\n  maxMessagesPerMinute: 1\n', AssistantRateLimiter)

        Assertions.assertTrue(limiter.tryAcquire(1L))
        Assertions.assertFalse(limiter.tryAcquire(1L))
    }

    private static AssistantModelProvider provider(String name) {
        return new AssistantConfig(provider: name).createProvider()
    }

    private static ModelRequest request() {
        return new ModelRequest('system', [ModelMessage.user('hello')])
    }

    private static AssistantModelProvider providerFromYaml(String yaml) {
        return fromYaml(yaml, AssistantModelProvider)
    }

    private static <T> T fromYaml(String yaml, Class<T> type) {
        Path file = Files.createTempFile('onCourse', '.yml')
        try {
            Files.write(file, yaml.bytes)
            BQRuntime runtime = Bootique.app('--config=' + file.toAbsolutePath())
                    .module(AssistantModule)
                    // the settings listener reads the Admin toggles from the database
                    .module({ Binder binder -> binder.bind(ICayenneService).toInstance(mock(ICayenneService)) } as Module)
                    .createRuntime()
            try {
                return runtime.getInstance(type)
            } finally {
                runtime.shutdown()
            }
        } finally {
            Files.delete(file)
        }
    }
}
