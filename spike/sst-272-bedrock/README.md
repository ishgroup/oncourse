# SST-272 spike: Claude on Amazon Bedrock, Australia-only

Throwaway prototype for ticket #01 of [SST-277](https://ish-group.atlassian.net/browse/SST-277), on story
[SST-272](https://ish-group.atlassian.net/browse/SST-272). It answers one question: can onCourse (Java 11) call Claude
Sonnet 5.5 and Haiku 5.5 through Bedrock's Australia-only `au.` inference profiles, fast and cheaply enough for the
assistant? It is a standalone Gradle build, not part of the onCourse build, and is not meant to be merged as is.

Run date: 25–26 Nov 2026 (AEDT). Region: `ap-southeast-2` (Sydney), where onCourse instances run.

## How to run

Needs AWS credentials for the ish account in the default chain, with Bedrock access, and JDK 11.

```bash
cd spike/sst-272-bedrock
../../gradlew -q -p . run --args="--mode probe --backend runtime"            # network round trip only, free
../../gradlew -q -p . run --args="--mode ttft --model sonnet --effort low --runs 3"
../../gradlew -q -p . run --args="--mode loop --model sonnet --effort low --runs 3"
../../gradlew -q -p . run --args="--mode schedule --model sonnet --effort low --runs 2"
../../gradlew -q -p . run -Dspike.trace=true --args="--mode loop --runs 1"     # print every stream event
```

Options: `--model sonnet|haiku`, `--effort low|medium|high`, `--backend runtime|mantle`,
`--thinking adaptive|between_tools|adaptive:summarized|adaptive:updates`, `--model-id <override>`.
Every call is appended to `results/calls.jsonl` with timings, token usage and cost at AU list prices. Before each call
the spike adds up `results/calls.jsonl` and stops at US$10.

Modes:
- **ttft**: one short question, no tools needed. Time to first event and first visible token.
- **loop**: "How many classes start next week at Sydney, and how many students are enrolled?", answered through the
  tool loop (16 strict-schema tools with canned answers, see `Tools.java`).
- **schedule**: propose 20 Saturday First Aid classes with known room, tutor and holiday clashes, through
  `propose_classes`, then a one-paragraph summary.

The system prompt (~1.5k tokens) and 16 tools (~2.8k tokens) are cached with one explicit breakpoint: a 4,304-token
prefix, close to what the real assistant will send.

## AWS account changes made for the spike

Approved by the account admin on 25 Nov 2026.

| Setting | Before | After |
|---|---|---|
| Marketplace agreement, `anthropic.claude-sonnet-5-5` | not accepted | accepted (pay per token, no upfront fee) |
| Marketplace agreement, `anthropic.claude-haiku-5-5` | not accepted | accepted |
| Bedrock data retention, `ap-southeast-2` | `inherit` | `none` |
| Bedrock data retention, `ap-southeast-4` | `inherit` | `none` |
| Model invocation logging | off | off (unchanged, so prompts aren't stored in CloudWatch or S3) |

## Findings

### Residency

`aws bedrock get-inference-profile` shows `au.anthropic.claude-sonnet-5-5` and `au.anthropic.claude-haiku-5-5`
routing only to `ap-southeast-2` and `ap-southeast-4`.

### Integration path (Java 11)

**Anthropic Java SDK 3.14.0 with `anthropic-java-bedrock` and `BedrockBackend.fromEnv()`**, which calls
bedrock-runtime. It compiles and runs on JDK 11 with no extra dependencies. Verified on it:
- streaming;
- 16 strict tools (`strict: true`, `additionalProperties: false`);
- parallel tool calls;
- prompt caching with an explicit `cache_control` breakpoint: a 4,304-token prefix, read in full on every warm call;
- effort `low` / `medium`;
- `between_tools` thinking.

`BedrockMantleBackend` (bedrock-mantle) **rejects the `au.` model IDs**: 404, "The model
'au.anthropic.claude-sonnet-5-5' does not exist". So Australia-only routing means bedrock-runtime for now. The AWS SDK v2
path wasn't needed.

### Latency

Measured from a laptop about 330 ms (TCP connect) from Sydney, on warm runs (run 1 of each JVM excluded). Inside
`ap-southeast-2`, each call should be roughly one round trip faster. Medians, small samples (2–4 runs per row):

| Mode | Model, effort | Reply total | Calls | First event, call 1 | First event, final answer |
|---|---|---|---|---|---|
| ttft | Sonnet 5.5, low | 2.5 s | 1 | 1.0 s | 1.0 s |
| ttft | Sonnet 5.5, medium | 3.4 s | 1 | 1.2 s | 1.2 s |
| ttft | Haiku 5.5, low | 1.9 s | 1 | 1.2 s | 1.2 s |
| loop | Sonnet 5.5, low | 11.8 s | 4 | 0.9 s | 4.1 s |
| loop | Sonnet 5.5, medium | 15.3 s | 4 | 1.0 s | 1.0–4.3 s |
| loop | Haiku 5.5, medium | 10.4 s | 3 | 2.4 s | 1.2 s |
| schedule | Sonnet 5.5, low | 17.9 s | 2 | 1.0 s | 3.5 s |
| schedule | Haiku 5.5, low/medium | 16–18 s | 2–3 | 1.2–1.5 s | 1.1–1.7 s |

What this means for the targets:
- **Short answers meet the 3 s target** (first token about 1 s).
- **Multi-step answers don't, unless the panel shows progress.** A question that needs data takes 3–4 sequential
  model calls, about 11–15 s in all. The tool steps start within 1–2 s, so the panel can show "Checking classes…" from
  the tool events straight away.
- **Sonnet's final answer after tool calls isn't streamed on Bedrock.** `message_start` arrives about 3.5–4.5 s into
  the call and then all the text arrives within about 50 ms. Trace output is in `--trace` runs. This happened with
  adaptive thinking, with `between_tools`, and with `display: "updates"` (beta). Haiku's final answers stream normally,
  and so do Sonnet's answers when there's no tool history.
- **The 20-class schedule misses the 10 s target: 16–21 s.** About 4–6 s goes to thinking before the tool call and
  about 9 s to generating ~1.7k tokens of class rows. The follow-up summary call adds about 3.5 s.
- **Cold start: the first call after the request shape changes took 15–35 s.** This happened three times: the first
  call ever, after switching to `between_tools`, and after adding the beta header. The likely cause is compiling the
  strict tool schemas. Calls after that were back to normal.

### Quality (small sample; the real check is the evaluation set, #26)

- **Schedule correctness** (20 Saturdays from 6 Feb 2027, skipping the 24 Apr holiday, so the run ends Sat 26 Jun):
  - Sonnet with adaptive thinking: 6 of 6 correct;
  - Sonnet with `between_tools`: 2 of 3;
  - Haiku at low effort: 2 of 4;
  - Haiku at medium effort: 2 of 2.
- **Tool use**: both models chose sensible tools, used parallel calls, and cited source and "as of". Several answers
  said "I can't verify the enrolment total". That was right, because the canned `list_records` answer only returns 2 of
  the 14 classes. The evaluation set needs consistent fixtures.

### Cost

From response `usage`, at AU list prices: Sonnet $2.20 / $11 per million input/output tokens, cache read $0.11, cache
write $2.75. Medians:

| Reply type | Sonnet 5.5, low | Haiku 5.5 |
|---|---|---|
| Short answer | $0.0025 | $0.0001–0.0002 |
| Tool loop (3–4 calls) | $0.017 (medium effort: $0.023) | $0.001–0.0014 |
| 20-class schedule | $0.028 | $0.002 |

That's below the $0.034 per reply assumed on SST-272. At the pilot volume of 4,400 replies per instance per month, most
of them tool loops, Sonnet at low effort comes to about **$75–80 per instance per month** rather than $150.

The spike itself cost **US$0.58** of the US$10 budget.

### Per-college cost attribution (for #25)

`aws bedrock create-inference-profile --model-source copyFrom=<au. profile ARN> --tags ...` creates an **application
inference profile** that copies the `au.` routing and carries tags. One profile per college, tagged for example
`college=<key>`, with the tag turned on as a cost allocation tag, splits the Bedrock bill per college. Each onCourse
instance then calls its own profile ARN instead of the shared `au.` ID. Nothing was created during the spike. Pair this
with the in-app usage metering in #25.

## Recommendation

1. **Default model: Claude Sonnet 5.5 through `au.anthropic.claude-sonnet-5-5` on bedrock-runtime, effort `low`,
   adaptive thinking.** Medium effort was slower and more expensive in the tool loop, with no visible gain on these
   tasks. `between_tools` saved nothing and got one schedule wrong.
2. **No Haiku for tool-using turns in v1.** It's 10–15× cheaper but got the schedule's date arithmetic wrong at low
   effort. Revisit with the evaluation set (#26), for example for plain how-to answers.
3. **Design for latency in the product:**
   - stream tool progress to the panel as soon as each tool starts;
   - send a warm-up request when the server starts or deploys;
   - keep tool definitions and thinking settings stable, so the 15–35 s cold start doesn't hit users.
4. **Schedule proposals:**
   - render the review card from the `propose_classes` input as it streams (fine-grained tool streaming,
     `eager_input_streaming`);
   - skip the summary call, because the card already shows the proposal;
   - make the class-row schema compact, with defaults for time, site and places.

   Re-measure against the 10 s target, or relax it to "first row on screen in 10 s".
5. **Re-run this spike from inside `ap-southeast-2`** (EC2 or CloudShell, same jar) before go-live, to replace the
   laptop numbers.
