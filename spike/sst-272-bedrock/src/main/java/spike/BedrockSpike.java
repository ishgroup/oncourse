/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package spike;

import com.anthropic.bedrock.backends.BedrockBackend;
import com.anthropic.bedrock.backends.BedrockMantleBackend;
import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.http.StreamResponse;
import com.anthropic.helpers.MessageAccumulator;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.MessageParam;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.RawMessageStreamEvent;
import com.anthropic.models.messages.TextBlockParam;
import com.anthropic.core.JsonValue;
import com.anthropic.models.messages.ThinkingConfigAdaptive;
import com.anthropic.models.messages.ThinkingConfigBetweenTools;
import com.anthropic.models.messages.Tool;
import com.anthropic.models.messages.ToolResultBlockParam;
import com.anthropic.models.messages.ToolUseBlock;
import com.anthropic.models.messages.Usage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SST-272 spike: Claude Sonnet 5.5 / Haiku 5.5 on Amazon Bedrock through the Australia-only {@code au.} profiles,
 * called from Java 11 with the Anthropic Java SDK.
 *
 * <pre>
 * ../../gradlew -p . run --args="--mode ttft|loop|schedule|probe --model sonnet|haiku --effort low|medium
 *                                --backend runtime|mantle --runs 3"
 * </pre>
 *
 * Every call appends a JSON line to results/calls.jsonl with timings, token usage and cost at AU list prices.
 * Before each call the spike totals the cost so far and stops at the budget agreed for the spike (US$10).
 */
public final class BedrockSpike {

    private static final double BUDGET_USD = 10.0;
    private static final String REGION = "ap-southeast-2";
    private static final Path RESULTS = Paths.get("results");
    private static final Path CALLS = RESULTS.resolve("calls.jsonl");

    /** AU-profile list prices, US$ per 1M tokens: input, output, 5-minute cache write, cache read (global + 10%). */
    private static final Map<String, double[]> PRICES = Map.of(
            "sonnet", new double[]{2.20, 11.00, 2.75, 0.11},
            "haiku", new double[]{0.11, 0.55, 0.1375, 0.011});

    private static final Map<String, String> MODEL_IDS = Map.of(
            "sonnet", "au.anthropic.claude-sonnet-5-5",
            "haiku", "au.anthropic.claude-haiku-5-5");

    private static final Map<String, String> HOSTS = Map.of(
            "runtime", "bedrock-runtime." + REGION + ".amazonaws.com",
            "mantle", "bedrock-mantle." + REGION + ".api.aws");

    public static void main(String[] args) throws Exception {
        Map<String, String> opt = parse(args);
        String mode = opt.getOrDefault("mode", "ttft");
        String model = opt.getOrDefault("model", "sonnet");
        String effort = opt.getOrDefault("effort", "low");
        String backend = opt.getOrDefault("backend", "runtime");
        String modelId = opt.getOrDefault("model-id", MODEL_IDS.get(model));
        int runs = Integer.parseInt(opt.getOrDefault("runs", "3"));
        String thinking = opt.getOrDefault("thinking", "adaptive");
        Files.createDirectories(RESULTS);

        double[] rtt = probe(HOSTS.get(backend));
        System.out.printf(Locale.ROOT, "TCP connect to %s: min %.0f ms, avg %.0f ms%n", HOSTS.get(backend), rtt[0], rtt[1]);
        System.out.printf(Locale.ROOT, "Spent so far: $%.4f of $%.2f%n", spent(), BUDGET_USD);
        if ("probe".equals(mode)) {
            return;
        }

        AnthropicClient client = AnthropicOkHttpClient.builder()
                .backend("mantle".equals(backend) ? BedrockMantleBackend.fromEnv() : BedrockBackend.fromEnv())
                .build();

        Ctx ctx = new Ctx(client, mode, model, modelId, effort, backend, thinking, rtt[0]);
        for (int run = 1; run <= runs; run++) {
            ctx.run = run;
            try {
                switch (mode) {
                    case "ttft":
                        reply(ctx, Prompts.TTFT_QUESTION, 1);
                        break;
                    case "loop":
                        reply(ctx, Prompts.LOOP_QUESTION, 5);
                        break;
                    case "schedule":
                        reply(ctx, Prompts.SCHEDULE_REQUEST, 3);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown mode " + mode);
                }
            } catch (BudgetExceeded e) {
                System.out.println(e.getMessage());
                break;
            } catch (Exception e) {
                System.out.printf("run %d failed: %s: %s%n", run, e.getClass().getSimpleName(), e.getMessage());
                record(ctx, Map.of("call", 0, "error", e.getClass().getSimpleName() + ": " + e.getMessage(), "cost", 0.0));
            }
        }
        System.out.printf(Locale.ROOT, "Spent so far: $%.4f of $%.2f%n", spent(), BUDGET_USD);
    }

    /** One assistant reply: call the model, run any tools it asks for, and repeat up to maxCalls times. */
    private static void reply(Ctx ctx, String userText, int maxCalls) {
        List<MessageParam> history = new ArrayList<>();
        history.add(MessageParam.ofUserOfContentBlockParams(Collections.singletonList(
                ContentBlockParam.ofText(TextBlockParam.builder().text(userText).build()))));
        long start = System.nanoTime();
        double replyCost = 0;
        int calls = 0;
        String finalText = "";
        for (int i = 1; i <= maxCalls; i++) {
            Call c = call(ctx, history, i);
            calls = i;
            replyCost += c.cost;
            history.add(c.message.toParam());
            List<ContentBlockParam> results = new ArrayList<>();
            for (ContentBlock block : c.message.content()) {
                block.text().ifPresent(t -> c.text.append(t.text()));
                if (block.toolUse().isPresent()) {
                    ToolUseBlock tu = block.toolUse().get();
                    String input = String.valueOf(tu._input());
                    results.add(ContentBlockParam.ofToolResult(ToolResultBlockParam.builder()
                            .toolUseId(tu.id())
                            .content(Tools.execute(tu.name(), input))
                            .build()));
                }
            }
            finalText = c.text.toString();
            if (results.isEmpty()) {
                break;
            }
            history.add(MessageParam.ofUserOfContentBlockParams(results));
        }
        double totalMs = ms(System.nanoTime() - start);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("call", "reply");
        summary.put("calls", calls);
        summary.put("total_ms", round(totalMs));
        summary.put("cost", 0.0);
        summary.put("reply_cost", replyCost);
        summary.put("final_text", truncate(finalText, 400));
        record(ctx, summary);
        System.out.printf(Locale.ROOT, "run %d REPLY: %d calls, %.0f ms, $%.5f | %s%n",
                ctx.run, calls, totalMs, replyCost, truncate(finalText.replace('\n', ' '), 160));
    }

    /** One streamed model call, timed from send to first event, first visible output and end. */
    private static Call call(Ctx ctx, List<MessageParam> history, int index) {
        if (spent() >= BUDGET_USD) {
            throw new BudgetExceeded(String.format(Locale.ROOT, "Budget of $%.2f reached, stopping.", BUDGET_USD));
        }
        MessageCreateParams.Builder b = MessageCreateParams.builder()
                .model(ctx.modelId)
                .maxTokens(8000L)
                .systemOfTextBlockParams(Collections.singletonList(TextBlockParam.builder()
                        .text(Prompts.SYSTEM)
                        .cacheControl(CacheControlEphemeral.builder().build())
                        .build()))
                .outputConfig(OutputConfig.builder().effort(effort(ctx.effort)).build())
                .messages(history);
        for (Tool t : Tools.ALL) {
            b.addTool(t);
        }
        if ("between_tools".equals(ctx.thinking)) {
            // Sonnet 5.5 only: no thinking before the final answer, at effort high or below.
            b.thinking(ThinkingConfigBetweenTools.builder().build());
        } else if (ctx.thinking.startsWith("adaptive:")) {
            // adaptive:summarized | adaptive:updates (beta, progress notes between tool calls)
            String display = ctx.thinking.substring("adaptive:".length());
            b.thinking(ThinkingConfigAdaptive.builder().putAdditionalProperty("display", JsonValue.from(display)).build());
            if ("updates".equals(display)) {
                b.putAdditionalHeader("anthropic-beta", "thinking-display-updates-2026-08-18");
            }
        }

        long t0 = System.nanoTime();
        long firstEvent = -1;
        long firstOutput = -1;
        MessageAccumulator acc = MessageAccumulator.create();
        try (StreamResponse<RawMessageStreamEvent> stream = ctx.client.messages().createStreaming(b.build())) {
            Iterator<RawMessageStreamEvent> it = stream.stream().iterator();
            while (it.hasNext()) {
                RawMessageStreamEvent ev = it.next();
                long now = System.nanoTime();
                if (firstEvent < 0) {
                    firstEvent = now;
                }
                if (firstOutput < 0 && ev.contentBlockDelta()
                        .map(d -> d.delta().text().isPresent() || d.delta().inputJson().isPresent())
                        .orElse(false)) {
                    firstOutput = now;
                }
                if (TRACE) {
                    System.out.printf(Locale.ROOT, "    %6.0f ms  %s%n", ms(now - t0), describe(ev));
                }
                acc.accumulate(ev);
            }
        }
        long t1 = System.nanoTime();
        Message msg = acc.message();
        Usage u = msg.usage();
        long in = u.inputTokens();
        long out = u.outputTokens();
        long cw = u.cacheCreationInputTokens().orElse(0L);
        long cr = u.cacheReadInputTokens().orElse(0L);
        double[] p = PRICES.get(ctx.model);
        double cost = (in * p[0] + out * p[1] + cw * p[2] + cr * p[3]) / 1_000_000.0;

        List<String> tools = new ArrayList<>();
        for (ContentBlock block : msg.content()) {
            block.toolUse().ifPresent(tu -> tools.add(tu.name()));
        }
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("call", index);
        row.put("first_event_ms", round(ms(firstEvent - t0)));
        row.put("first_output_ms", firstOutput < 0 ? null : round(ms(firstOutput - t0)));
        row.put("total_ms", round(ms(t1 - t0)));
        row.put("input_tokens", in);
        row.put("cache_write_tokens", cw);
        row.put("cache_read_tokens", cr);
        row.put("output_tokens", out);
        row.put("cost", cost);
        row.put("stop_reason", msg.stopReason().map(String::valueOf).orElse(""));
        row.put("tools", tools);
        record(ctx, row);
        System.out.printf(Locale.ROOT,
                "run %d call %d: first event %.0f ms, first output %s ms, total %.0f ms | in %d, cache w/r %d/%d, out %d | $%.5f | %s %s%n",
                ctx.run, index, ms(firstEvent - t0), firstOutput < 0 ? "-" : String.format(Locale.ROOT, "%.0f", ms(firstOutput - t0)),
                ms(t1 - t0), in, cw, cr, out, cost, row.get("stop_reason"), tools);
        return new Call(msg, cost);
    }

    private static final boolean TRACE = Boolean.getBoolean("spike.trace");

    /** One-line description of a stream event for --trace runs. */
    private static String describe(RawMessageStreamEvent ev) {
        if (ev.contentBlockStart().isPresent()) {
            return "content_block_start " + truncate(String.valueOf(ev.contentBlockStart().get().contentBlock()), 90);
        }
        if (ev.contentBlockDelta().isPresent()) {
            String d = String.valueOf(ev.contentBlockDelta().get().delta());
            return "delta " + truncate(d.replace('\n', ' '), 90);
        }
        if (ev.messageStart().isPresent()) {
            return "message_start";
        }
        if (ev.messageDelta().isPresent()) {
            return "message_delta";
        }
        if (ev.contentBlockStop().isPresent()) {
            return "content_block_stop";
        }
        return "message_stop";
    }

    private static OutputConfig.Effort effort(String value) {
        switch (value) {
            case "low":
                return OutputConfig.Effort.LOW;
            case "medium":
                return OutputConfig.Effort.MEDIUM;
            case "high":
                return OutputConfig.Effort.HIGH;
            default:
                throw new IllegalArgumentException("Unknown effort " + value);
        }
    }

    /** TCP connect time to the endpoint, a proxy for network round trip from this machine. Returns {min, avg}. */
    private static double[] probe(String host) {
        double min = Double.MAX_VALUE;
        double sum = 0;
        int n = 5;
        for (int i = 0; i < n; i++) {
            long t0 = System.nanoTime();
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(host, 443), 5000);
            } catch (IOException e) {
                return new double[]{-1, -1};
            }
            double t = ms(System.nanoTime() - t0);
            min = Math.min(min, t);
            sum += t;
        }
        return new double[]{min, sum / n};
    }

    private static final Pattern COST = Pattern.compile("\"cost\":([0-9.eE-]+)");

    private static double spent() {
        if (!Files.exists(CALLS)) {
            return 0;
        }
        double total = 0;
        try {
            for (String line : Files.readAllLines(CALLS, StandardCharsets.UTF_8)) {
                Matcher m = COST.matcher(line);
                if (m.find()) {
                    total += Double.parseDouble(m.group(1));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + CALLS, e);
        }
        return total;
    }

    private static void record(Ctx ctx, Map<String, Object> fields) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("ts", Instant.now().toString());
        row.put("mode", ctx.mode);
        row.put("backend", ctx.backend);
        row.put("model", ctx.modelId);
        row.put("effort", ctx.effort);
        row.put("thinking", ctx.thinking);
        row.put("run", ctx.run);
        row.put("rtt_min_ms", round(ctx.rttMs));
        row.putAll(fields);
        try {
            Files.write(CALLS, Collections.singletonList(json(row)), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write " + CALLS, e);
        }
    }

    private static String json(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof Number || v instanceof Boolean) {
            return String.valueOf(v);
        }
        if (v instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            for (Map.Entry<?, ?> e : ((Map<?, ?>) v).entrySet()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                sb.append(json(String.valueOf(e.getKey()))).append(':').append(json(e.getValue()));
            }
            return sb.append('}').toString();
        }
        if (v instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            for (Object o : (List<?>) v) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                sb.append(json(o));
            }
            return sb.append(']').toString();
        }
        String s = String.valueOf(v).replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
        return '"' + s + '"';
    }

    private static Map<String, String> parse(String[] args) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i + 1 < args.length; i += 2) {
            m.put(args[i].replaceFirst("^--", ""), args[i + 1]);
        }
        return m;
    }

    private static double ms(long nanos) {
        return nanos / 1_000_000.0;
    }

    private static double round(double v) {
        return Math.round(v * 10) / 10.0;
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    private static final class Ctx {
        final AnthropicClient client;
        final String mode;
        final String model;
        final String modelId;
        final String effort;
        final String backend;
        final String thinking;
        final double rttMs;
        int run;

        Ctx(AnthropicClient client, String mode, String model, String modelId, String effort, String backend,
            String thinking, double rttMs) {
            this.client = client;
            this.mode = mode;
            this.model = model;
            this.modelId = modelId;
            this.effort = effort;
            this.backend = backend;
            this.thinking = thinking;
            this.rttMs = rttMs;
        }
    }

    private static final class Call {
        final Message message;
        final double cost;
        final StringBuilder text = new StringBuilder();

        Call(Message message, double cost) {
            this.message = message;
            this.cost = cost;
        }
    }

    private static final class BudgetExceeded extends RuntimeException {
        BudgetExceeded(String message) {
            super(message);
        }
    }
}
