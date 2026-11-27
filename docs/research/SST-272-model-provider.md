# SST-272 – AI model provider for the onCourse AI Assistant: research notes

| | |
|---|---|
| Ticket | [SST-272](https://ish-group.atlassian.net/browse/SST-272): "AI-01 Spike: AI model and onCourse API integration" (2-day timebox) |
| Parent / spec | Epic [SST-260](https://ish-group.atlassian.net/browse/SST-260) (see `docs/research/SST-260.md`); implementation spec [SST-277](https://ish-group.atlassian.net/browse/SST-277) |
| Question | Which model provider and hosting option should the assistant use, given N-04 (APP, no training, data stored and processed in Australia, possible IRAP need for a TAFE pilot), N-07 (first response ≤ 3 s, ≤ 20-class schedule ≤ 10 s), reliable strict/parallel tool calling, prompt-injection resistance (L-14), cost, a Java 11 server, help-docs embeddings and the existing cloud relationship? |
| Researched | 2026-11-24, against live vendor docs, price lists and compliance pages (accessed 2026-11-24), and branch `worktree-SST-260_research` @ `58bf579d7` |
| Neutrality note | Written by Claude (Anthropic). Anthropic is a candidate; its docs are cited and judged like every other vendor's. Where a vendor's primary sources say nothing, the cell says "not found". |

Source tags used below:
- `[A#]` Anthropic, `[W#]` AWS, `[G#]` Google Cloud, `[M#]` Microsoft, `[O#]` OpenAI, `[X#]` public benchmarks. Full URLs in §8.
- `path:line`: this repo.
- **(inferred)**: my own deduction, not stated in a source.
- All prices are list prices in **USD**, excluding GST, per 1M tokens unless stated.

---

## 1. Summary and recommendation

### 1.1 What the evidence says

- **Anthropic's own API cannot keep processing in Australia today.** `inference_geo` accepts only `"global"` and `"us"`, and workspace geo (storage) is `"us"` only `[A3]`. The two Anthropic-operated cloud variants (Claude Platform on AWS, Claude on Google Cloud (Anthropic managed)) use the same two geos; their AWS/GCP region does not pin inference `[A4][A5]`. Claude in Microsoft Foundry offers Global or US Data Zone only `[A10]`.
- **Claude on Amazon Bedrock has an Australia-only geographic profile.** `au.anthropic.claude-sonnet-5-5`, `au.anthropic.claude-haiku-5-5` and `au.anthropic.claude-opus-5-5` route between Sydney and Melbourne only `[W2][W3][W4]`. Price is +10 % over global `[W7][A8]`.
- **Claude on Vertex AI has no Australian option.** Regional endpoints exist only for Sonnet 4.6 and earlier, multi-region only `us`/`eu` `[A9]`; no Claude model is listed for `australia-southeast1` `[G1]`.
- **Azure OpenAI "Standard" in Australia East keeps processing in the Australia geography**, but only `gpt-5.6-sol`, `gpt-4.1-mini` and `gpt-4o` (plus embeddings) are offered that way `[M2][M3]`. Data Zone is "APAC" (Australia, Japan, Korea, Singapore, India), not Australia-only `[M1][M2]`.
- **OpenAI's direct API offers Australian regional processing for one model only** (`gpt-6-luna`), via `au.api.openai.com`, with sales-gated eligibility `[O2]`.
- **Gemini on Vertex AI has an Australian ML-processing commitment** for `gemini-3.5-flash`, `gemini-2.5-flash` (128k) and `text-embedding-004` in `australia-southeast1` `[G2]`.
- **Amazon Nova (v1) and many open-weight models run in-region in Sydney on Bedrock** `[W1]`, but their public tool-calling scores are far below frontier models on the only independent leaderboard found (BFCL V4) `[X1]`.
- **The repo already depends on AWS**: `com.amazonaws:aws-java-sdk-s3` is a core dependency used by document storage, plus optional S3, Azure Blob and Google Classroom integrations (§1.4).
- **The server targets Java 11** (§1.4). Anthropic, AWS and OpenAI Java SDKs need Java 8+; the Google Gen AI Java SDK says Java 17+ from v2.0.0 `[G7]`.

### 1.2 Recommendation

| | Option | Deciding reasons |
|---|---|---|
| **Primary** | **Claude Sonnet 5.5 on Amazon Bedrock via the `au.` geographic profile** (bedrock-runtime, called from Sydney), with **Claude Haiku 5.5 (`au.`)** as a cheaper/faster tier for simple turns. Account-level Bedrock data-retention mode `none`. | (1) The only option found that combines current-generation frontier models with **documented AU-only routing** (Sydney↔Melbourne) `[W2][W3]`. (2) **Bedrock is in AWS's IRAP PROTECTED scope** for Sydney and Melbourne `[W13][W14]`. (3) No training, no sharing with the model provider, ZDR-style retention mode available for these models `[W10][W11][W12]`. (4) Strict tool use and parallel tool calls are documented as stable on bedrock-runtime `[A11][A12]`; prompt caching with a 512-token minimum `[W2]`. (5) Fits the **existing AWS relationship** and **Java 11** (`anthropic-java` and AWS SDK v2 need Java 8+) `[A18][W15]`. (6) Pilot cost ≈ **$450–590/month** with caching (§3). |
| **Runner-up** | **Gemini 3.5 Flash on Vertex AI, `australia-southeast1`** | Documented AU ML-processing commitment `[G2]`; "Generative AI on Vertex AI" in Google's IRAP scope `[G5]`; parallel function calls and a schema-enforcing `VALIDATED` mode `[G6]`; similar cost to Sonnet 5.5 on Bedrock (≈ $390–470/month, §3). Against it: Google's Java SDK 2.x requires Java 17 `[G7]`; Sydney only (no second AU region for gen AI) `[G1]`; prompt logging for abuse monitoring unless the account team exempts you `[G4]`; no independent tool-calling score for 3.5 Flash found. |
| **Fallback** | **Azure OpenAI `gpt-5.6-sol`, Standard deployment in Australia East** | Processing and the abuse-monitoring store stay in the Australia geography `[M3]`; Azure OpenAI was in Microsoft's 2024 IRAP PROTECTED assessment `[M7]`; `openai-java` supports Azure on Java 8+ `[O6]`. Against it: **≈ 2.4× the Sonnet 5.5 cost** ($5/$25, +25 % over global) `[M5]`; strict structured outputs **cannot be combined with parallel tool calls** on Azure `[M8]`; only one current-generation model regionally `[M2]`; modified abuse monitoring is being withdrawn for models released from 1 Nov 2026 `[M4]`. Use if ish or a TAFE mandates Microsoft. |

Within AWS, if a single-region (not just single-country) commitment is ever required: Opus 5.5/Opus 5/Sonnet 5/Haiku 4.5 are listed as **in-region in Melbourne on bedrock-mantle** `[W1]`, and Nova/open-weight models are in-region in Sydney `[W1]`. **(inferred)**

### 1.3 What the 2-day spike must measure to confirm

1. **Latency from the real onCourse host location** to Bedrock `au.` (Sydney source region), streaming: time to first token and full 3-call tool loop, for Sonnet 5.5 and Haiku 5.5 at effort `low`/`medium`, and with `thinking: between_tools`. Sonnet 5.5 defaults to adaptive thinking at effort `high` and does not accept `thinking.type: "disabled"` `[A2][W2]`, which is the main risk to N-07's 3 s target. No vendor publishes AU-region latency, so none is quoted here.
2. **Schedule proposal of 20 classes in ≤ 10 s** end to end, same setup.
3. **Prompt-cache behaviour**: `cache_read_input_tokens` across turns and across users of one instance, with explicit breakpoints (automatic top-level `cache_control` is not supported on bedrock-runtime `[A8]`).
4. **Tool-calling accuracy** on 20–30 onCourse tasks (e.g. "How many classes next week?") with `strict: true` and parallel calls; repeat the same set on the runner-up (Gemini 3.5 Flash AU) for a like-for-like comparison. Note bedrock-runtime allows `tool_choice` `auto`/`none` only `[W2]`.
5. **Prompt-injection set** (L-14): instructions hidden in record fields returned as tool results.
6. **Residency verification**: `aws bedrock get-inference-profile --region ap-southeast-2` for each `au.` profile, confirming destinations are only `ap-southeast-2`/`ap-southeast-4` (AWS says to verify this, not trust the prefix `[W6]`); confirm data-retention mode `none` is accepted for Sonnet/Haiku 5.5 `[W10]`.
7. **Java 11 integration path**: `anthropic-java` + `anthropic-java-bedrock` (`BedrockBackend`, bedrock-runtime) vs AWS SDK v2 `BedrockRuntimeClient` (InvokeModel/Converse) with an `au.` profile ID. Also: model-access approval for Sonnet 5.5 on Bedrock (its access is "See Access", not "Open" `[A7]`).
8. **Actual cost per reply** from response `usage`, to replace the estimates in §3.

### 1.4 Repo facts relevant to the choice

- **Java target is 11 everywhere**:
  - `build.gradle:67` (`sourceCompatibility = 11`);
  - `server/build.gradle:157` (`JavaVersion.VERSION_11`);
  - `types/build.gradle:49`, `server-api/build.gradle:26`, `buildSrc/build.gradle:22`, `buildSrc/apidoc/build.gradle:24-25` (source and target 11);
  - no Gradle toolchain is configured;
  - CI builds with Temurin 11 (`.github/workflows/release.yml:40`, `api-server.yml:16`).
- **Existing cloud SDKs** (`server/build.gradle`):
  - `com.amazonaws:aws-java-sdk-s3:1.11.939` (`:105`), AWS SDK **v1**. Used by core document storage: `server/src/main/java/ish/s3/AmazonS3Service.java:35`, configured through `DocumentService` (`server/src/main/groovy/ish/oncourse/server/document/DocumentService.groovy:74-100`: bucket, access key, secret, region, endpoint, limit). Also used by the optional S3 integration plugin (`server/src/main/groovy/ish/oncourse/server/integration/s3/S3Integration.groovy:28-29`).
  - `com.azure:azure-storage-blob:12.9.0` (`:106`). Used only by the optional Azure Storage integration plugin (`server/src/main/groovy/ish/oncourse/server/integration/azureStorage/AzureStorageIntegration.groovy:21-22`).
  - Google API client / Classroom (`:133-135`). Used by the optional Google Classroom integration (`server/src/main/groovy/ish/oncourse/server/integration/google/classroom/ClassRoomIntegration.groovy:30-31`).
  - No SES, SNS, SQS, Bedrock, Vertex or OpenAI usage was found.
- **What that favours.** AWS is the only cloud on the core path; document storage needs an S3 bucket. A Javadoc example points at `bucket-ish-oncourse.s3.amazonaws.com` (`DocumentService.groovy:52`). So ish most likely already has an AWS account and billing relationship **(inferred)**. Bedrock usage would bill to the same AWS account, with Claude charged through AWS Marketplace `[W2]`.
- Adding AWS SDK **v2** (`software.amazon.awssdk:*`) for Bedrock should coexist with the v1 S3 client, because the package names differ **(inferred)**.

---

## 2. Comparison table

Legend for **AU processing**:
- **Yes**: a primary source states that inference stays in Australia.
- **Partial**: some models only, or some data (system/abuse) may leave.
- **No**: no AU option.

Prices are the AU option's list price, or "n/a" if there is none.

| Option | AU processing | AU-available models | No-training / retention | IRAP | Tool use / strict schemas | Prompt caching | Price /1M in / out (AU option) | Java SDK (min JDK) | Embeddings in AU | Notes |
|---|---|---|---|---|---|---|---|---|---|---|
| **Claude API (first-party)** | **No**: geo `global`/`us` only; storage `us` only `[A3]` | none | No training without permission; prompts/outputs not retained by default except Covered Models (Fable/Mythos: 30 days); ZDR via sales `[A6]` | Not found `[A16]` | Strict + parallel on all current models `[A11][A12]` | Yes; reads 0.05× (5.5 models); min 512 tokens `[A1][A13]` | n/a (global Sonnet 5.5 $2/$10; `us` ×1.1) `[A1]` | `anthropic-java` 3.13.0, Java 8+ `[A18]` | No own model; points to Voyage AI `[A15]` | Reference feature set; fails N-04 |
| **Claude Platform on AWS** (Anthropic-operated) | **No**: AWS region doesn't pin inference; `global`/`us` `[A4]` | none | As Claude API; ZDR on request `[A4]` | Not found | As Claude API | As Claude API | n/a (same rates, billed as CCUs) `[A1]` | `anthropic-java-aws` 3.13.0, Java 8+ `[A4][A18]` | – | AWS billing, Anthropic is processor |
| **Claude on Google Cloud (Anthropic managed)** | **No**: location `global`; `global`/`us`; some services outside GCP `[A5][A3]` | none | As Claude API; ZDR on request `[A6]` | Not found | As Claude API | As Claude API | n/a | `anthropic-java-google-cloud`, Java 8+ `[A18]` | – | |
| **Claude on Bedrock, `au.` profile** | **Yes**: Sydney↔Melbourne only `[W2][W3][W4]` | Sonnet 5.5, Haiku 5.5, Opus 5.5, Opus 5, Sonnet 5, Opus 4.8/4.7/4.6, Sonnet 4.6, Haiku 4.5. Fable/Mythos global only `[W1]` | No training; not shared with provider `[W12]`; retention mode `none` possible (Fable needs `aws_review`) `[W10][W11]` | AWS PROTECTED, Sydney + Melbourne; "Amazon Bedrock [excl. Bedrock Marketplace]" in scope `[W13][W14]` | Strict + parallel stable on bedrock-runtime `[A11][A12]`; `tool_choice` auto/none only `[W2]` | Yes, explicit breakpoints, 5 min/1 h, min 512 `[W2][A8]` | Sonnet 5.5 **$2.20 / $11**; Haiku 5.5 $0.11 / $0.55; Opus 5.5 $4.40 / $22 (+10 %) `[W7]` | `anthropic-java-bedrock` 3.13.0 or AWS SDK v2; Java 8+ `[A7][W15]` | Titan Text Emb. V2, Cohere Embed v3 (Sydney in-region) `[W1][W8]` | **Primary** |
| **Claude on Vertex AI (Google managed)** | **No**: regional only Sonnet ≤ 4.6; multi-region `us`/`eu` `[A9]`; none in `australia-southeast1` `[G1]` | none | Google: no training without permission `[G4]` | Vertex gen AI in Google scope `[G5]` (moot) | Strict + parallel `[A11][A12]` | Yes `[A13]` | n/a | `anthropic-java-vertex`, Java 8+ | – | Not viable for N-04 |
| **Claude in Microsoft Foundry** | **No**: Global or US Data Zone `[A10]` | none | Anthropic processor `[A10]` | Not found | Strict + parallel `[A11]` | Yes | n/a | `anthropic-java-foundry`, Java 8+ `[A10]` | – | |
| **Azure OpenAI, Standard, Australia East** | **Yes**: Australia geography; abuse store in resource geography `[M1][M3]` | `gpt-5.6-sol`, `gpt-4.1-mini`, `gpt-4o`. None in Australia Southeast `[M2]` | Not used to train; not available to OpenAI `[M3]`; human-review abuse monitoring unless MAM approved; MAM withdrawn for models from 1 Nov 2026 `[M4]` | Azure OpenAI in Microsoft's 2024 PROTECTED assessment `[M7]`; current scope list behind login `[M6]` | Strict function calling, **not with parallel calls** `[M8]` | Cached-input meters; 5.6-sol cache write $6.25 `[M5]` | `gpt-5.6-sol` **$5.00 / $25.00** (+25 %); `gpt-4.1-mini` $0.44 / $1.76 (+10 %) `[M5]` | `openai-java` 4.112.0, Java 8+ `[O6]` | `text-embedding-3-small` $0.022, `-3-large` $0.143 `[M2][M5]` | **Fallback** |
| **Azure OpenAI, Data Zone APAC / Global** | **Partial / No**: APAC = AU, JP, KR, SG, IN; Global = any `[M1][M2]` | DZ: `gpt-6.2-sol/astra`, `gpt-6.1-sol/terra`, `gpt-5.6-sol`, `gpt-5.4(-mini)` … `[M2]` | As above | As above | As above | As above | DZ ≈ +20 % vs Global (e.g. `gpt-5.4` $3.00 vs $2.50 input) `[M5]` | `openai-java`, Java 8+ | DZ: 3-small/3-large `[M2]` | Not AU-only |
| **OpenAI API, `au.api.openai.com`** | **Partial**: regional processing only `gpt-6-luna`; other endpoints storage-only; system data may leave AU `[O2]` | `gpt-6-luna` | Not used for training by default; abuse logs ≤ 30 days; ZDR/MAM by approval `[O1]` | Not found `[O5]` | Function calling (Chat Completions only at effort `none`); structured outputs `[O4]`; hosted tools unavailable in AU `[O2]` | Yes (cached $0.011, write $0.1375 incl. uplift) `[O3][O4]` | **$0.11 / $0.55** (incl. 10 % uplift) `[O3][O4]` | `openai-java`, Java 8+ `[O6]` | No (Embeddings storage-only in AU) `[O2]` | Eligibility via sales `[O2]` |
| **Gemini on Vertex AI, `australia-southeast1`** | **Yes** for listed models (ML-processing commitment) `[G2]` | `gemini-3.5-flash`, `gemini-2.5-flash` (128k) `[G2]`; Sydney only `[G1]` | No training without permission; prompt logging for abuse monitoring; implicit cache ≤ 24 h in region `[G4]` | "Generative AI on Vertex AI" in scope `[G5]` | Parallel function calls; `VALIDATED` mode enforces schema `[G6]` | Implicit (default) + explicit (storage $1/1M tok/h) `[G3][G4]` | 3.5 Flash **$1.65 / $9.90** (+10 %); 2.5 Flash $0.30 / $2.50 `[G3]` | `google-genai` 2.6.0: **Java 17+** from 2.0.0 `[G7]` | `text-embedding-004` (AU commitment) `[G2]` | **Runner-up** |
| **Amazon Nova on Bedrock, Sydney** | **Yes**, in-region Sydney. Geo profile is `apac.` (leaves AU); Nova 2 Lite global only `[W1][W5]` | Nova Micro, Lite, Pro (v1) | As Bedrock | As Bedrock | Tool use; strict not found; BFCL: Nova Pro 24.97 % `[X1]` | Yes (Pro read $0.21) `[W5][W8]` | Pro $0.84 / $3.36; Lite $0.063 / $0.252; Micro $0.037 / $0.148 `[W8]` | AWS SDK v2, Java 8+ `[W15]` | Titan V2 (Sydney) | Weak tool-calling evidence |
| **Open-weight on Bedrock, Sydney** | **Yes**, in-region `[W1]` | Qwen3 (235B, 32B, Next 80B, Coder), gpt-oss-120b/20b, Mistral Large 3, Ministral, DeepSeek V3.2, Kimi K2.5, GLM 5, Nemotron `[W1]` | As Bedrock | As Bedrock | Per-model support not verified; BFCL: Qwen3-235B 47.99–52.15 % `[X1]` | No cache meters found `[W8]` | Qwen3 235B $0.227 / $0.906; gpt-oss-120b $0.155 / $0.618; Mistral Large 3 $0.515 / $1.545 `[W8]` | AWS SDK v2, Java 8+ | Titan V2 | |
| **Self-hosted open-weight, AU GPUs** | **Yes** (you pick the region) | any open-weight | You control | IaaS may be in scope; your system needs its own assessment **(inferred)** | Depends on model and serving stack `[X1]` | Self-managed | Fixed: 1× H100 VM, Australia East, ≈ $10.12/h ≈ $7.4k/month list `[M5]` | any HTTP client | self-hosted | High ops burden (§5.8) |

---

## 3. Cost estimate

### 3.1 Assumptions

- **Model call**: 6,000 input tokens, of which 4,000 are a stable cacheable prefix (system prompt + tool definitions) and 2,000 are uncached; 600 output tokens.
- **Reply**: one assistant reply = 3 model calls in the tool loop.
- **Pilot volume**: 3 instances × 10 admin users × 20 replies/day × 22 days = **13,200 replies/month** = 39,600 calls/month.
- **Three caching cases**:
  - **No cache**: every input token at the base input price.
  - **Warm cache**: on every call the 4k prefix is a cache read and the 2k is base input.
  - **Cold start**: on each reply the first call writes the 4k prefix (at the cache-write price, or the base price where there is no write fee) and the other 2 calls read it. Each instance serves ~200 replies per business day, roughly one every 2–3 minutes, so a 5-minute cache should mostly stay warm. The real figure is probably between warm and cold **(inferred)**.
- Token counts differ by tokenizer: Claude 4.7+ produces ~30 % more tokens for the same text `[A1]`. Treat cross-vendor comparisons as rough; the spike should measure actual `usage`.
- Not included: thinking/reasoning tokens (billed as output), embeddings, GST, AUD conversion, and any negotiated discount.

### 3.2 Arithmetic (worked example: Claude Sonnet 5.5, Bedrock `au.`, $2.20 in / $0.11 cache read / $2.75 cache write / $11.00 out `[W7]`)

- **No cache, per call:** 6,000 × $2.20/1M = $0.01320; 600 × $11/1M = $0.00660. Total **$0.01980**.
- **Warm, per call:** 2,000 × $2.20/1M = $0.00440; 4,000 × $0.11/1M = $0.00044; output $0.00660. Total **$0.01144**.
- **Cold-start reply:** first call 2,000 × $2.20 + 4,000 × $2.75 + 600 × $11 (per 1M) = $0.02200; plus 2 warm calls × $0.01144 = $0.02288. Total **$0.04488**.
- **Per reply:** × 3 calls. **Per month:** × 13,200 replies.

The same formula was applied to every option (script run locally; prices as in §2).

### 3.3 Results

| Option (AU unless marked) | Reply, no cache | Reply, warm | Reply, cold start | Month, no cache | Month, warm | Month, cold start | Per instance / month (warm) |
|---|---|---|---|---|---|---|---|
| Claude Sonnet 5.5 – Bedrock `au.` | $0.0594 | $0.0343 | $0.0449 | $784 | **$453** | $592 | $151 |
| Claude Haiku 5.5 – Bedrock `au.` | $0.0030 | $0.0018 | $0.0023 | $39 | **$24** | $30 | $8 |
| Claude Opus 5.5 – Bedrock `au.` | $0.1188 | $0.0686 | $0.0898 | $1,568 | $906 | $1,185 | $302 |
| *Claude Sonnet 5.5 – Claude API, global (not AU, reference)* | $0.0540 | $0.0312 | $0.0408 | $713 | $412 | $539 | $137 |
| GPT-5.6-sol – Azure Standard, Australia East | $0.1350 | $0.0810 | $0.1040 | $1,782 | $1,069 | $1,373 | $356 |
| gpt-4.1-mini – Azure Standard, Australia East | $0.0111 | $0.0071 | $0.0084 | $146 | $94 | $112 | $31 |
| gpt-6-luna – OpenAI `au.api` (+10 %) | $0.0030 | $0.0018 | $0.0023 | $39 | $24 | $30 | $8 |
| Gemini 3.5 Flash – Vertex `australia-southeast1` | $0.0475 | $0.0297 | $0.0356 | $627 | **$392** | $470 | $131 |
| Gemini 2.5 Flash – Vertex `australia-southeast1` | $0.0099 | $0.0067 | $0.0077 | $131 | $88 | $102 | $29 |
| Amazon Nova Pro – Bedrock Sydney in-region | $0.0212 | $0.0136 | $0.0161 | $279 | $180 | $213 | $60 |
| Amazon Nova Lite – Bedrock Sydney in-region | $0.0016 | $0.0010 | $0.0012 | $21 | $13 | $16 | $4 |
| Qwen3 235B – Bedrock Sydney (no caching found) | $0.0057 | n/a | n/a | $75 | n/a | n/a | $25 (no cache) |
| gpt-oss-120b – Bedrock Sydney (no caching found) | $0.0039 | n/a | n/a | $51 | n/a | n/a | $17 (no cache) |
| Self-hosted, 1× H100 VM (Azure Australia East list) | – | – | – | ≈ $7,388 fixed (730 h × $10.121) | | | + ops; ×2 for HA **(inferred)** |

Notes:
- Gemini caching assumes implicit cache hits, which the docs present as a latency optimisation rather than a guarantee `[G4]` **(inferred)**.
- Azure GPT caching mechanics were not verified beyond the existence of cached-input and cache-write meters `[M5]`.
- A Sonnet-for-planning / Haiku-for-simple-turns split would land between those two rows **(inferred)**.

---

## 4. Leads from the bundled reference: verification

| Lead (cached 2026-11-03) | Live status, 2026-11-24 |
|---|---|
| Opus 5.5 $4 / $20, cache read $0.20 | **Confirmed** `[A1]` |
| Sonnet 5.5 $2 / $10, cache read $0.10 | **Confirmed** `[A1]` |
| Haiku 5.5 $0.10 / $0.50 for prompts ≤ 100K | **Confirmed**; > 100K is $0.50 / $2.50 `[A1]` |
| Bedrock/Vertex prices partner-set | **Confirmed** `[A1]`; Bedrock AU geo = +10 % `[W7]` |
| `inference_geo` on first-party, Claude Platform on AWS, Claude on Google Cloud (Anthropic managed); not Bedrock/Vertex/Foundry | **Confirmed** on the English page `[A3]` (one search snippet said otherwise; the live page is authoritative) |
| Values `global` (default) and `us` (1.1×) | **Confirmed**. "Only `"us"` and `"global"` are available"; no AU geo `[A3]` |
| `com.anthropic:anthropic-java` 3.3.0 with Bedrock/Vertex/Foundry backends | **Updated**: install snippets now show **3.13.0** `[A7][A18]`; backends `-bedrock`, `-vertex`, `-foundry`, `-aws`, `-google-cloud`; README: "Java 8+" `[A18]` |
| Fable 5.2 requires 30-day retention, not under ZDR | **Confirmed** (Covered Models) `[A6]`. On Bedrock, Fable requires `aws_review` (AWS human review) `[W10]` and is global-only from AU `[W1]` |
| Message threads not ZDR-eligible | **Confirmed** `[A6]` |

---

## 5. Per-provider details

### 5.1 Anthropic first-party (Claude API) and Anthropic-operated variants

- **Models and prices.**
  - Opus 5.5 $4/$20, Sonnet 5.5 $2/$10, Haiku 5.5 $0.10/$0.50 (≤ 100K prompt).
  - Cache reads: 0.05× on Opus/Sonnet 5.5, 0.1× on Haiku 5.5.
  - 5-minute cache writes: 1.25×.
  - Tool-use system prompt: 286 tokens on the 5.5 models `[A1]`.
  - All current models have a 1M-token context.
  - Comparative latency: Haiku 5.5 "Fastest", Sonnet 5.5 "Fast", Opus 5.5 "Moderate".
  - Default effort: Sonnet 5.5 `high`, Haiku 5.5 `medium` `[A2]`.
- **Residency.**
  - `inference_geo` is `global` (default) or `us`. `us` costs 1.1× on Claude 4.6+ models.
  - Workspace geo (data at rest, endpoint processing) is `us` only and fixed at creation `[A3]`.
  - **No Australian geo exists.**
- **Claude Platform on AWS.**
  - Anthropic operates it; AWS provides IAM, SigV4 and Marketplace billing.
  - Workspaces are bound to an AWS region, but "it does not pin where model inference runs". `inference_geo` is the only control `[A4]`.
  - ZDR is available on request. HIPAA readiness is not available `[A4][A6]`.
- **Claude on Google Cloud (Anthropic managed).**
  - Location stays `global`. Anthropic is the processor, and "some supporting services, such as Anthropic's trust and safety systems, process data outside Google Cloud" `[A5]`.
- **Training and retention.**
  - "Retained data is never used for model training without your express permission."
  - Prompts and outputs are not retained by default, except on Covered Models (Fable/Mythos, 30 days) `[A6]`. The privacy centre describes deletion "within 30 days" as the commercial default `[A17]`; the docs page is the more specific, newer statement **(inferred)**.
  - ZDR via sales. Flagged content may be kept up to 2 years `[A6]`.
- **IRAP.**
  - Anthropic lists HIPAA-ready, ISO 27001:2022, ISO/IEC 42001:2023 and SOC 2 Type I & II. **IRAP: not found in primary sources** `[A16]`.
- **Tool use.**
  - `strict: true` guarantees schema-valid inputs and valid tool names, via grammar-constrained sampling.
  - Schemas are cached up to 24 h `[A11]`.
  - Parallel tool calls are on by default (`disable_parallel_tool_use` to turn off) `[A12]`.
- **Prompt injection.** Anthropic's guidance:
  - put untrusted content in `tool_result` blocks;
  - optionally screen tool output with a small classifier call;
  - return a stripped result if injection is suspected `[A14]`.
- **Java.** `com.anthropic:anthropic-java` 3.13.0, "Java 8+" `[A18]`. The doc examples use newer Java syntax (`void main()`, `IO.println`), but that is just the example style **(inferred)**.
- **Embeddings.** "Anthropic does not offer its own embedding model"; it points to Voyage AI (MongoDB) `[A15]`. Voyage AU residency: not found.
- **Verdict.** Best feature coverage and lowest Claude price, but **fails N-04's "processed in Australia"**.

### 5.2 Claude on Amazon Bedrock

- **Endpoints.**
  - `bedrock-runtime` (InvokeModel/Converse) supports `au.` geo profiles for the 5.5 models.
  - `bedrock-mantle` (native Messages API at `/anthropic/v1/messages`) lists "In-Region" only in Melbourne, and only for some models `[W1][W2]`.
  - Anthropic's mantle page lists `ap-southeast-2` as "Global, AU" and `ap-southeast-4` as "Global, AU, In-region only" `[A7]`. But AWS's model card shows "Geo inference ID: N/A" for mantle `[W2]`. **These two sources conflict on whether `au.` routing works on mantle; plan on bedrock-runtime and verify in the spike.**
- **AU availability (from Sydney and Melbourne)** `[W1]`:
  - **Geo (`au.`)**: Sonnet 5.5, Haiku 5.5, Opus 5.5, Opus 5, Sonnet 5, Opus 4.8, Opus 4.7, Opus 4.6, Sonnet 4.6, Haiku 4.5. Each of these AWS model cards lists an `au.anthropic.…` inference-profile ID (e.g. `au.anthropic.claude-sonnet-4-6`, `au.anthropic.claude-haiku-4-5-20251001-v1:0`).
  - **In-region Melbourne (bedrock-mantle)**: Opus 5.5, Opus 5, Sonnet 5, Opus 4.8, Opus 4.7, Haiku 4.5.
  - **Global only**: Fable 5.2/5.1/5, Mythos 5.2/5.1.
  - Sonnet 4.5 and Opus 4.5 are in legacy (EOL 2027).
- **`au.` destinations.** For `au.anthropic.claude-sonnet-5-5`, `…haiku-5-5` and `…opus-5-5`, source Sydney → Sydney + Melbourne, and source Melbourne → Sydney + Melbourne `[W2][W3][W4]`.
  - AWS warns: "Don't rely on the geographic prefix … alone"; call `GetInferenceProfile` from each source region `[W6]`.
  - AWS also notes that adding Auckland as a source did not change the Sydney/Melbourne destinations (seen only in an AWS blog via search, not verified on a docs page).
- **Pricing.** From the AWS Price List API, identical for Sydney and Melbourne `[W7]`:

  | Model | Input | Cache write 5m | Cache read | Output |
  |---|---|---|---|---|
  | Sonnet 5.5 | $2.20 | $2.75 | $0.11 | $11.00 |
  | Haiku 5.5 | $0.11 | $0.1375 | $0.011 | $0.55 |
  | Opus 5.5 | $4.40 | $5.50 | $0.22 | $22.00 |

  - These are the "Standard" (non-global) meters; "Standard, Global" is $2.00/$10.00 etc.
  - Geo profiles are "priced at source Region rates" `[W1]`, and Anthropic/AWS state a 10 % regional premium `[A8][A1]`.
- **Data and training.**
  - "AWS and the third-party model providers will not use any inputs to or outputs from Amazon Bedrock to train …"; "Users' inputs and model outputs are not shared with any model providers" `[W12]`.
  - Model providers have no access to Bedrock's model deployment accounts `[W9]`.
  - Retention modes: `none` / `default` / `aws_review`. Under `none`, "No request or response data is written to durable storage by AWS".
  - Fable models require `aws_review` (retained up to 30 days, possible AWS human review) `[W10][W11]`.
  - The abuse-detection page names only OpenAI (non-oss) models and Fable models as storing traffic `[W11]`. So Sonnet/Haiku 5.5 should accept `none` **(inferred; verify in spike)**.
  - Cross-region note: prompts and outputs "may be stored in the opt-in Regions for abuse detection purposes" `[W6]`. Melbourne is within AU **(inferred)**.
- **IRAP.**
  - The AWS IRAP assessment "covers in-scope services in the AWS Sydney and Melbourne Regions" at PROTECTED `[W13]`.
  - The services-in-scope list (updated 2 July 2026) includes "Amazon Bedrock [excludes Amazon Bedrock Marketplace]" `[W14]`.
  - Serverless Claude is invoiced through AWS Marketplace `[W2]`. "Bedrock Marketplace" is a separate model-hosting feature **(inferred)**. Confirm with the IRAP report in AWS Artifact.
- **Tool use.**
  - Strict tool use and parallel tool use are "stable" on "Amazon Bedrock (bedrock-runtime)" `[A11][A12]`.
  - The model card lists client-side tool calling with "`tool_choice` types `auto` and `none` only", plus Structured outputs, Guardrails and Explicit Prompt Caching on bedrock-runtime `[W2]`.
- **Caching.** Explicit; minimum 512 tokens per checkpoint; up to 4 checkpoints; TTL 5 min or 1 h; on system, messages and tools `[W2]`. Automatic top-level `cache_control` returns 400 on bedrock-runtime `[A8]`.
- **Latency.**
  - No published AU latency.
  - Service tiers: Standard, Priority ("fastest response times for a price premium"), Flex, Reserved `[W2]`. Whether Priority applies to `au.` profiles was not found.
  - Sonnet 5.5: adaptive thinking on by default; `thinking.type: "disabled"` not supported; use `"between_tools"` `[W2]`.
- **Prompt injection.** Bedrock Guardrails has a prompt-attack filter. It evaluates only content wrapped in guard-content tags, so tool results must be tagged explicitly `[W16]`.
- **Quotas.** Default 2M input TPM, raisable to 5M input / 500k output TPM without Anthropic approval `[A7]`. That is ample for the pilot **(inferred)**.
- **Java.**
  - `anthropic-java` + `anthropic-java-bedrock` 3.13.0 `[A7]`. The bedrock-runtime page shows `BedrockBackend` with the 2.x line for pre-4.7 models because of API-version issues `[A8]`; whether 3.x `BedrockBackend` takes `au.` IDs for 5.5 models is a spike item.
  - Alternative: AWS SDK for Java 2.x (`bedrockruntime`), "Java 8 or later" `[W15]`.
- **Access.** Sonnet 5.5, Haiku 5.5 and Opus 5.5 are "See Access" (criteria checked in the console), not "Open" `[A7]`.

### 5.3 Claude on Google Vertex AI (Google managed)

- **Endpoints.** Global (recommended), multi-region `us`/`eu`, and regional. "Regional endpoints serve Claude Sonnet 4.6 and earlier models only." Multi-region and regional cost +10 % `[A9]`.
- **No Australia option.**
  - The Vertex locations table shows no Claude model supported in `australia-southeast1` `[G1]`.
  - The partner-model ML-processing table has no Australia column `[G2]`.
  - Google's Claude page lists models without regions `[G8]`.
- **Data.** Google won't use data to train without permission `[G4]`. Google bills; prices on Google's page `[G3]`.
- **Verdict.** Fails N-04.

### 5.4 OpenAI models via Microsoft Azure (Azure OpenAI in Microsoft Foundry)

- **Deployment types and where data is processed** `[M1][M3]`:
  - **Global**: "May be processed in any Azure region".
  - **Data Zone**: "only within the Microsoft-specified data zone (US, EU, or Asia Pacific (APAC))". Microsoft "can add regions to either data zone without prior notice".
  - **Standard and Regional Provisioned**: "processed within the customer-specified Azure geography and might be processed between regions within that geography".
  - Data at rest, including the abuse-monitoring store for Global/Data Zone deployments, stays in the customer-designated geography.
- **AU models** `[M2]`:
  - **Standard/Regional, `australiaeast`**: `gpt-4.1-mini` (2025-04-14), `gpt-4o` (2024-11-20), **`gpt-5.6-sol` (2026-07-09)**, `text-embedding-3-large`, `text-embedding-3-small`, `text-embedding-ada-002`.
  - **Data Zone APAC** (columns `australiaeast`, `japaneast`, `koreacentral`, `southeastasia`, `southindia`): `gpt-5.2`, `gpt-5.3-codex`, `gpt-5.4`, `gpt-5.4-mini`, `gpt-5.6-sol`, `gpt-6.1-sol`, `gpt-6.1-terra`, `gpt-6.2-astra`, `gpt-6.2-sol`, `gpt-chat-latest`, `model-router`, embeddings.
  - **Australia Southeast** does not appear in these tables.
- **Prices, Australia East** (Azure Retail Prices API, `armRegionName eq 'australiaeast'`) `[M5]`:
  - `gpt-5.6-sol` Standard Regional, short context: $5.00 in, $0.50 cached in, $6.25 cache write, $25.00 out. Effective 2026-11-01. Global is $4/$0.40/$5/$20, so regional is +25 %.
  - `gpt-4.1-mini` regional: $0.44 / cached $0.11 / $1.76 (+10 % vs global).
  - `gpt-4o` regional: $2.75 / $1.375 / $11.00.
  - Data Zone meters are ≈ +20 % vs Global (e.g. `gpt-5.4` input $3.00 vs $2.50).
- **Training and data.** Prompts and completions "are NOT available to OpenAI", "are NOT used to train any generative AI foundation models without your permission", and the service does not interact with OpenAI-operated services `[M3]`.
- **Abuse monitoring.**
  - Flagged content may get automated review and, if needed, human review by authorized Microsoft employees `[M4]`.
  - The data store is in the resource's geography `[M3]`. Reviewer location is specified only for the EEA; for Australia it was not found.
  - Retention period: not stated on the current pages.
  - Modified Abuse Monitoring (opt-out of storage and human review) requires a Limited Access application. It "will no longer be available" for models made available on or after 1 Nov 2026, with ≥ 30 days' notice `[M4]`. `gpt-5.6-sol` predates that cut-off.
- **IRAP.**
  - Azure in-scope services are IRAP-assessed "up to and including the PROTECTED level" for Australian regions. The service list is in the Service Trust Portal (login required) `[M6]`.
  - Microsoft's own AU news (1 Mar 2024): the 2024 IRAP assessments "include … Azure OpenAI Service", assessed to Protected `[M7]`. The current round and region scope were not verified.
- **Tool use.** Structured outputs with `strict: true` for function calling; "Structured outputs are not supported with parallel function calls … set `parallel_tool_calls` to `false`". The supported-models list stops at gpt-5.1 and does not name `gpt-5.6-sol` `[M8]`; spike to verify.
- **Prompt injection.** Azure Prompt Shields scans "document attacks" at user input and **tool response** intervention points (learn.microsoft.com content-filter Prompt Shields page, seen via search summary only).
- **Java.** `com.openai:openai-java` 4.112.0, "Java 8 or later"; Azure via the same builder with Azure configuration / Entra ID `[O6]`.
- **Latency.** None published for AU. Provisioned types give "lower latency variance" `[M1]`.

### 5.5 OpenAI direct API

- **Data residency** `[O2]`:
  - Table row: "Australia* | `au.api.openai.com` | Yes | Yes" (regional storage, regional processing).
  - Regional processing is only for `/v1/chat/completions` and `/v1/responses` on **`gpt-6-luna`**.
  - Embeddings, Batches, Files, Structured Outputs (excluding schema) and others are storage-only.
  - "Hosted tools are unavailable for regional processing" in Australia, India and Japan. Standard pay-as-you-go only.
  - Residency "doesn't apply to system data", which "may be stored and processed outside the selected region".
  - Eligibility: contact sales; region fixed per project.
- **Training and retention.** API data "is not used to train or improve OpenAI models" unless you opt in. Abuse logs are "retained for up to 30 days". ZDR / Modified Abuse Monitoring "subject to prior approval" `[O1]`.
- **Prices.**
  - `gpt-6-luna` $0.10 in / $0.01 cached / $0.125 cache write / $0.50 out `[O4]`; +10 % on regional-processing endpoints for models released on or after 5 Mar 2026 `[O3]`. AU effective $0.11 / $0.55.
  - For reference (not AU-processed): `gpt-6.2-sol` $2/$10, `gpt-5.6-sol` $4/$20 `[O3]`.
- **Model.** "Our most efficient model for focused, high-volume tasks". Function calling in Responses, and in Chat Completions only at `reasoning.effort` `none`. Structured outputs and prompt caching supported `[O4]`.
- **IRAP.** The OpenAI trust portal lists SOC 2/3, ISO 27001/27017/27018/27701/42001, CSA STAR, FedRAMP 20x and others. **IRAP not listed** `[O5]`.
- **Java.** `openai-java`, Java 8+ `[O6]`.
- **Verdict.** AU-processed, but limited to a small model, gated by sales, no IRAP, and system data may leave AU.

### 5.6 Google Gemini on Vertex AI

- **Residency.**
  - "Endpoints don't guarantee data residency or in-region ML processing" `[G1]`.
  - The data-residency page lists per-model ML-processing commitments. For **Australia (`australia-southeast1`)**: `gemini-3.5-flash`, `gemini-2.5-flash` (128k) and `text-embedding-004`. Not 3.5 Flash-Lite, 3.x Pro, `gemini-embedding-001` or `text-embedding-005` `[G2]`.
  - Locations also list `gemini-embedding-001` and "Embeddings for Multimodal" as regional endpoints in Sydney, without an AU processing commitment `[G1][G2]`.
  - No gen-AI models are listed for `australia-southeast2` (Melbourne) `[G1]`.
  - The global endpoint gives "no data residency guarantees" `[G2]`.
- **Prices** `[G3]`:
  - Gemini 3.5 Flash, non-global, ≤ 200K: $1.65 in, $0.165 cached in, $9.90 out (global $1.50/$0.15/$9.00).
  - Gemini 2.5 Flash: $0.30 / $0.03 / $2.50.
  - Explicit cache storage: $0.000001 per token-hour for Flash models.
  - Non-global pricing for Gemini 3+ took effect 1 Jul 2026.
- **Training and retention** `[G4]`:
  - "Google won't use your data to train or fine-tune any AI/ML models without your prior permission".
  - "Google may log prompts to detect potential abuse" (Google models, under the GCP Terms). Zero data retention requires customer actions and, for some features, the account team.
  - Implicit caching is on by default: in memory ≤ 24 h, "processed in the region that serves the request". Durable cache ≤ 24 h "stays within the selected location" on regional endpoints. Both can be disabled.
- **IRAP.** Google Cloud was assessed against OFFICIAL and PROTECTED ISM controls. "Generative AI on Vertex AI" and "Vertex AI {Online and Batch Prediction}" are listed in scope; no assessment date on the page `[G5]`.
- **Tool use.** Parallel function calls. Modes `AUTO`, `ANY`, `NONE`, and `VALIDATED` ("ensures function schema adherence"; from Gemini 3 it also enforces required parameters) `[G6]`.
- **Prompt injection.** Model Armor added `australia-southeast2`. Its release notes conflict on which filters work there with residency enforced `[G9]` (seen via search summary).
- **Java.** `com.google.genai:google-genai` 2.6.0. README: "Starting from SDK version 2.0.0, Java version 17 or later is required … pin the SDK version to `< 2.0.0`" `[G7]`. The repo's pom still sets compiler source/target 1.8, which is ambiguous. For onCourse (Java 11) plan on **1.x or REST** **(inferred)**.
- **Latency.** None published for AU.

### 5.7 Amazon Nova and open-weight models on Bedrock (Sydney)

- **Nova.**
  - Nova Micro, Lite and Pro (v1) are **in-region in Sydney** `[W1]`.
  - Their geo profile is `apac.` and routes to Tokyo, Seoul, Osaka, Mumbai, Singapore and Sydney (from Sydney), so it leaves AU `[W5]`.
  - Nova 2 Lite is **global-only** from AU `[W1]`.
  - Prompt caching: explicit, min 1K tokens, 5-min TTL `[W5]`.
  - Sydney prices: Micro $0.037/$0.148, Lite $0.063/$0.252, Pro $0.84/$3.36, Pro cache read $0.21 `[W8]`.
- **Open-weight, in-region Sydney on bedrock-runtime and bedrock-mantle** `[W1]`:
  - Qwen3 235B A22B 2507, Qwen3 32B, Qwen3 Next 80B, Qwen3 Coder 480B/30B/Next;
  - gpt-oss-120b/20b (+ Safeguard);
  - Mistral Large 3, Ministral 3B/8B/14B, Magistral Small, Devstral 2;
  - DeepSeek V3.1/V3.2, Kimi K2.5, GLM 4.7/5, NVIDIA Nemotron;
  - Gemma 3.
  - **No Meta Llama model is listed for Sydney** `[W1]`.
- **Open-weight Sydney prices** `[W8]`: Qwen3 235B $0.2266/$0.9064; Qwen3 32B $0.1545/$0.618; gpt-oss-120b $0.1545/$0.618; Mistral Large 3 $0.515/$1.545; DeepSeek V3.2 $0.64/$1.91. No cache meters were found for these.
- **Tool calling (public benchmark).** BFCL V4, last updated 2026-04-12, overall accuracy `[X1]`:
  - Nova Pro (FC) 24.97 %, Nova 2 Lite (FC) 27.10 %, Nova Micro (FC) 22.29 %;
  - Qwen3-235B-A22B-2507 52.15 % (prompt) / 47.99 % (FC); Qwen3-32B (FC) 48.71 %; mistral-large-2411 (FC) 38.37 %;
  - for comparison: Claude Haiku 4.5 (FC) 68.70 %, Gemini 2.5 Flash (FC) 56.24 %, GPT-4.1-mini (FC) 50.45 %;
  - multi-turn accuracy for Nova models is 1–2 %.
  - Current-generation models (Claude 5.5, GPT-6.x, Gemini 3.5) are **not** on BFCL V4.
- **Verdict.** Strongest residency (single region) and cheapest, but the weakest published tool-calling evidence for a 20–40-tool agent.

### 5.8 Self-hosted open-weight models on Australian GPUs (brief)

- **Effort:**
  - provision GPU VMs;
  - run a serving stack (e.g. vLLM; prefix caching is a serving-engine feature);
  - write the tool-call parsing and JSON-schema enforcement yourself;
  - patching, autoscaling, monitoring and capacity planning;
  - your own eval suite;
  - your own IRAP-style assessment of the system **(inferred)**.
- **Rough cost** (Azure Retail Prices, Australia East, Linux pay-as-you-go) `[M5]`:
  - `Standard_NC40ads_H100_v5` (1× H100) $10.121/h ≈ $7,388/month;
  - `Standard_NC24ads_A100_v4` (1× A100) $5.326/h ≈ $3,888/month;
  - at least 2 for availability **(inferred)**.
  - That is 10–300× the API cost at pilot scale (§3).
- **Quality.** The open models with public BFCL scores trail the frontier models on tool calling `[X1]`.
- **Verdict.** Not justified for the pilot. Bedrock's in-region open-weight models (§5.7) cover the same "open model, AU-only" need without the ops burden.

### 5.9 Public tool-calling benchmarks (labelled as such)

- **BFCL V4** (Berkeley Function Calling Leaderboard), last updated 2026-04-12, overall accuracy `[X1]`. Top entries: Claude Opus 4.5 (FC) 77.47 %, Claude Sonnet 4.5 (FC) 73.24 %, Gemini 3 Pro Preview (Prompt) 72.51 %, GLM-4.6 72.38 %, Grok-4.1-fast 69.57 %, Claude Haiku 4.5 (FC) 68.70 %. GPT-5.2 (FC) is 55.87 %, GPT-4.1 (FC) 53.96 %. **It predates the 5.5 / GPT-6 / Gemini 3.5 generation.**
- **τ-bench (Sierra).** τ²-bench combined Pass^1 (retail, airline, telecom): Qwen3.5-397B-A17B 87.9 %, Gemini 3.0 Pro 85.4 %, Claude Opus 4.5 85.3 %. The site shows no update date, and no current-generation entries for the candidates `[X2]`.
- **Conclusion.** Neither leaderboard covers the models being chosen. **The spike's own onCourse tool-calling eval (§1.3, item 4) is the deciding evidence.**

---

## 6. Embeddings options for the help-docs index

- **Two kinds of embedding traffic.**
  - **Index build** embeds the public help docs once per release. Residency matters little here.
  - **Query-time embedding** embeds the user's question, which can contain student names and other personal data, so it should be AU-processed like the chat itself **(inferred)**.
- **Options:**

| Option | AU processing | Price | Notes |
|---|---|---|---|
| Amazon Titan Text Embeddings V2 – Bedrock Sydney, in-region | Yes `[W1]` | $0.026 / 1M tokens (`$0.000026004` per 1K) `[W8]` | Same AWS account and IAM as the primary LLM option **(inferred)** |
| Cohere Embed English / Multilingual (v3) – Bedrock Sydney, in-region | Yes `[W1]` | $0.10 / 1M tokens `[W7]` | Cohere Embed v4 is global-only from AU `[W1]` |
| Azure `text-embedding-3-small` / `-3-large`, Standard, Australia East | Yes `[M2]` | $0.022 / $0.143 per 1M (regional) `[M5]` | |
| Vertex `text-embedding-004`, `australia-southeast1` | Yes (AU ML-processing commitment) `[G2]` | "Embeddings for Text" $0.000025 per 1,000 count `[G3]` | `gemini-embedding-001` is in Sydney but has no AU commitment `[G1][G2]` |
| OpenAI direct `text-embedding-3-small` | No (Embeddings storage-only in AU) `[O2]` | $0.02 / 1M `[O3]` | |
| Anthropic | – | – | No embedding model; Voyage AI suggested; AU residency not found `[A15]` |

- **Cost.** At these prices the index cost is negligible: a few million tokens of help docs costs cents per release **(inferred)**.
- **Recommendation.** If the primary option is Bedrock, use **Titan Text Embeddings V2 in Sydney** for both index and queries, and verify retrieval quality in the spike **(inferred)**.

---

## 7. Risks and open questions

### 7.1 Risks

1. **Latency (N-07).** No vendor publishes AU-region latency. Adaptive thinking defaults (Sonnet 5.5 at `high`, no `disabled` mode) could push first response past 3 s `[A2][W2]`. Mitigations to test: effort `low`/`medium`, `between_tools`, streaming, and Haiku 5.5 for simple turns.
2. **AU profiles lag new launches.** Fable 5.x and Nova 2 Lite are global-only from AU today `[W1]`. Future upgrades may also arrive global-first.
3. **Documentation conflicts.**
   - Bedrock mantle `au.` support: Anthropic's region table vs AWS's model card `[A7][W2]`.
   - Model Armor filter availability in Melbourne `[G9]`.
   - Azure's structured-outputs model list omits `gpt-5.6-sol` `[M8]`.
4. **IRAP is an assessment, not a certification.** ASD's CCSL ended in 2020 `[M6]`. Bedrock's IRAP entry excludes "Bedrock Marketplace" `[W14]`, and whether every Bedrock feature (e.g. bedrock-mantle, Claude via Marketplace billing) is in the assessed boundary needs the AWS Artifact report.
5. **Residency fine print.**
   - Bedrock cross-region abuse-detection storage in opt-in regions `[W6]`.
   - OpenAI "system data" may leave the region `[O2]`.
   - Anthropic-managed variants run some trust-and-safety services outside the cloud `[A5]`.
   - Azure reviewer location for AU: not found `[M3]`.
6. **Retention rules can change per model.** Bedrock now enforces per-model retention modes, e.g. Fable needs `aws_review` `[W10]`. Azure is withdrawing Modified Abuse Monitoring for new models `[M4]`.
7. **Java 11 constraint.** It rules out the current Google Gen AI SDK line `[G7]` and may limit future SDK versions. Anthropic, AWS and OpenAI SDKs are Java 8+ today `[A18][W15][O6]`.
8. **Prompt injection (L-14).** No provider guarantees resistance. Defence must be in the app: untrusted data only in tool results, read-only tools in pilot, the confirm click (N-01), and optional guard models (Bedrock Guardrails, Prompt Shields, Anthropic's classifier pattern) `[A14][W16]`.
9. **Price volatility.** All figures are USD list prices on 2026-11-24. Azure's `gpt-5.6-sol` regional meter only appeared on 2026-11-01 `[M5]`.

### 7.2 Questions for ish / PO

1. **Existing cloud contracts.**
   - Which AWS account owns the onCourse document S3 buckets?
   - Is there an AWS Enterprise agreement, private pricing, or Marketplace commitment that Bedrock/Claude spend could count towards?
   - Any Azure or Google Cloud agreements?
2. **Where do the college instances run** (AWS Sydney? other hosting? on-prem)? This decides network latency to Sydney/Melbourne, and whether IAM roles can be used instead of long-lived keys.
3. **One central ish cloud account or a per-college account?** This affects who the processor is under APP 8, how billing per college works, and quota sharing.
4. **TAFE requirements.**
   - Does the pilot TAFE require IRAP-assessed services, and at what classification (OFFICIAL / OFFICIAL: Sensitive / PROTECTED)?
   - Which state's policy applies? IRAP is a Commonwealth framework; state rules may add or differ **(inferred)**.
   - Is Sydney↔Melbourne routing acceptable, or must processing stay in one region?
5. **Budget.** Is ~US$450–600/month for the 3-instance pilot (Sonnet 5.5, with caching) acceptable? What is the ceiling per college at GA? Is a Haiku-first design preferred on cost grounds (~US$25/month pilot)?
6. **Model choice authority.** Can the PO accept "Claude via AWS" given N-04's wording "stored in Australia", when the model provider (Anthropic) never receives the data on Bedrock `[W9][W12]`? Or is a specific vendor preferred or excluded?

---

## 8. Sources

All accessed 2026-11-24.

**Anthropic**
- `[A1]` Pricing – https://platform.claude.com/docs/en/about-claude/pricing
- `[A2]` Models overview – https://platform.claude.com/docs/en/about-claude/models/overview
- `[A3]` Data residency – https://platform.claude.com/docs/en/manage-claude/data-residency
- `[A4]` Claude Platform on AWS – https://platform.claude.com/docs/en/build-with-claude/claude-platform-on-aws
- `[A5]` Claude Models on Google Cloud (Anthropic managed) – https://platform.claude.com/docs/en/build-with-claude/claude-models-on-google-cloud/anthropic-managed
- `[A6]` API and data retention – https://platform.claude.com/docs/en/manage-claude/api-and-data-retention
- `[A7]` Claude in Amazon Bedrock (bedrock-mantle) – https://platform.claude.com/docs/en/build-with-claude/claude-in-amazon-bedrock
- `[A8]` Claude on Amazon Bedrock (bedrock-runtime) – https://platform.claude.com/docs/en/build-with-claude/claude-on-amazon-bedrock-legacy
- `[A9]` Claude Models on Google Cloud (Google managed) – https://platform.claude.com/docs/en/build-with-claude/claude-models-on-google-cloud/google-managed
- `[A10]` Claude in Microsoft Foundry – https://platform.claude.com/docs/en/build-with-claude/claude-in-microsoft-foundry
- `[A11]` Strict tool use – https://platform.claude.com/docs/en/agents-and-tools/tool-use/strict-tool-use
- `[A12]` Parallel tool use – https://platform.claude.com/docs/en/agents-and-tools/tool-use/parallel-tool-use
- `[A13]` Prompt caching – https://platform.claude.com/docs/en/build-with-claude/prompt-caching
- `[A14]` Mitigate jailbreaks and prompt injections – https://platform.claude.com/docs/en/test-and-evaluate/strengthen-guardrails/mitigate-jailbreaks
- `[A15]` Embeddings – https://platform.claude.com/docs/en/build-with-claude/embeddings
- `[A16]` What certifications has Anthropic obtained – https://privacy.claude.com/en/articles/10015870-what-certifications-has-anthropic-obtained
- `[A17]` How long do you store my organization's data – https://privacy.claude.com/en/articles/7996866-how-long-do-you-store-my-organization-s-data
- `[A18]` anthropic-sdk-java README – https://github.com/anthropics/anthropic-sdk-java (raw: https://raw.githubusercontent.com/anthropics/anthropic-sdk-java/main/README.md)

**AWS**
- `[W1]` Regional availability by models – https://docs.aws.amazon.com/bedrock/latest/userguide/models-region-compatibility.html (parsed from the `.md` version)
- `[W2]` Model card, Claude Sonnet 5.5 – https://docs.aws.amazon.com/bedrock/latest/userguide/model-card-anthropic-claude-sonnet-5-5.html
- `[W3]` Model card, Claude Haiku 5.5 – https://docs.aws.amazon.com/bedrock/latest/userguide/model-card-anthropic-claude-haiku-5-5.html
- `[W4]` Model card, Claude Opus 5.5 – https://docs.aws.amazon.com/bedrock/latest/userguide/model-card-anthropic-claude-opus-5-5.html
- `[W5]` Model card, Amazon Nova Pro – https://docs.aws.amazon.com/bedrock/latest/userguide/model-card-amazon-nova-pro.html (also Nova 2 Lite card)
- `[W6]` Supported Regions and models for inference profiles – https://docs.aws.amazon.com/bedrock/latest/userguide/inference-profiles-support.html
- `[W7]` AWS Price List API, Bedrock foundation models, Sydney and Melbourne – https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonBedrockFoundationModels/current/ap-southeast-2/index.json and …/ap-southeast-4/index.json
- `[W8]` AWS Price List API, Amazon Bedrock, Sydney – https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonBedrock/current/ap-southeast-2/index.json
- `[W9]` Data protection – https://docs.aws.amazon.com/bedrock/latest/userguide/data-protection.html
- `[W10]` Data retention – https://docs.aws.amazon.com/bedrock/latest/userguide/data-retention.html
- `[W11]` Abuse detection – https://docs.aws.amazon.com/bedrock/latest/userguide/abuse-detection.html
- `[W12]` Amazon Bedrock FAQs – https://aws.amazon.com/bedrock/faqs/
- `[W13]` AWS IRAP – https://aws.amazon.com/compliance/irap/
- `[W14]` AWS services in scope, IRAP (last updated 2 July 2026) – https://aws.amazon.com/compliance/services-in-scope/IRAP/
- `[W15]` Setting up the AWS SDK for Java 2.x – https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html
- `[W16]` Detect prompt attacks with Bedrock Guardrails – https://docs.aws.amazon.com/bedrock/latest/userguide/guardrails-prompt-attack.html (seen via search summary)
- (Context only) Amazon Bedrock pricing page – https://aws.amazon.com/bedrock/pricing/. Its tables load dynamically, so prices were taken from `[W7][W8]`.

**Google Cloud**
- `[G1]` Deployments and endpoints (locations) – https://docs.cloud.google.com/vertex-ai/generative-ai/docs/learn/locations
- `[G2]` Data residency – https://docs.cloud.google.com/vertex-ai/generative-ai/docs/learn/data-residency
- `[G3]` Generative AI pricing – https://cloud.google.com/vertex-ai/generative-ai/pricing
- `[G4]` Data governance and zero data retention – https://docs.cloud.google.com/vertex-ai/generative-ai/docs/data-governance
- `[G5]` IRAP compliance – https://cloud.google.com/security/compliance/irap
- `[G6]` Function calling – https://docs.cloud.google.com/vertex-ai/generative-ai/docs/multimodal/function-calling
- `[G7]` Google Gen AI Java SDK README and pom – https://github.com/googleapis/java-genai (raw README and pom.xml on `main`)
- `[G8]` Anthropic Claude models on Agent Platform – https://docs.cloud.google.com/gemini-enterprise-agent-platform/models/partner-models/claude
- `[G9]` Model Armor release notes – https://docs.cloud.google.com/model-armor/release-notes (seen via search summary)

**Microsoft**
- `[M1]` Deployment types – https://learn.microsoft.com/en-us/azure/foundry/foundry-models/concepts/deployment-types
- `[M2]` Region availability for Foundry Models sold by Azure – https://learn.microsoft.com/en-us/azure/foundry/foundry-models/concepts/models-sold-directly-by-azure-region-availability (also https://learn.microsoft.com/en-us/azure/foundry/foundry-models/concepts/models-sold-directly-by-azure)
- `[M3]` Data, privacy, and security for Foundry Models sold by Azure – https://learn.microsoft.com/en-us/azure/ai-foundry/responsible-ai/openai/data-privacy
- `[M4]` Abuse monitoring – https://learn.microsoft.com/en-us/azure/foundry/openai/concepts/abuse-monitoring
- `[M5]` Azure Retail Prices API – https://prices.azure.com/api/retail/prices (filters `armRegionName eq 'australiaeast'` with `contains(productName,'OpenAI')`, and GPU VM SKUs)
- `[M6]` Australia IRAP, Azure compliance – https://learn.microsoft.com/en-us/azure/compliance/offerings/offering-australia-irap
- `[M7]` Microsoft Australia News Centre, 2024 IRAP assessments (1 Mar 2024) – https://news.microsoft.com/en-au/features/microsofts-commitment-to-trust-in-australia-introducing-2024-azure-dynamics-365-and-microsoft-365-irap-assessments/
- `[M8]` Structured outputs – https://learn.microsoft.com/en-us/azure/foundry/openai/how-to/structured-outputs
- (Search summary only) Prompt Shields – https://learn.microsoft.com/en-us/azure/ai-services/content-safety/concepts/jailbreak-detection

**OpenAI**
- `[O1]` Data controls (your data) – https://developers.openai.com/api/docs/guides/your-data
- `[O2]` Data residency – https://developers.openai.com/api/docs/guides/data-residency
- `[O3]` Pricing – https://developers.openai.com/api/docs/pricing
- `[O4]` gpt-6-luna model page – https://developers.openai.com/api/docs/models/gpt-6-luna
- `[O5]` OpenAI Trust Portal – https://trust.openai.com/
- `[O6]` openai-java README – https://github.com/openai/openai-java

**Benchmarks (public leaderboards)**
- `[X1]` Berkeley Function Calling Leaderboard V4 – https://gorilla.cs.berkeley.edu/leaderboard.html (data: https://gorilla.cs.berkeley.edu/data_overall.csv)
- `[X2]` τ-bench leaderboard (Sierra) – https://taubench.com/

**Repo** (branch `worktree-SST-260_research` @ `58bf579d7`)
- `build.gradle:67`; `server/build.gradle:105-106,133-135,157`; `types/build.gradle:49`; `server-api/build.gradle:26`; `buildSrc/build.gradle:22`; `buildSrc/apidoc/build.gradle:24-25`; `.github/workflows/release.yml:40`; `.github/workflows/api-server.yml:16`
- `server/src/main/java/ish/s3/AmazonS3Service.java:35,49`; `server/src/main/groovy/ish/oncourse/server/document/DocumentService.groovy:52,74-100`
- `server/src/main/groovy/ish/oncourse/server/integration/s3/S3Integration.groovy:28-29`; `server/src/main/groovy/ish/oncourse/server/integration/azureStorage/AzureStorageIntegration.groovy:21-22`; `server/src/main/groovy/ish/oncourse/server/integration/google/classroom/ClassRoomIntegration.groovy:30-31`
