# 〈译〉 LessTokenAI

**Save money on paid AI APIs by compressing your prompts to Simplified Chinese before sending them.**

Simplified Chinese is one of the most token-efficient written languages. The same idea expressed in English, Spanish, or Russian can be represented in significantly fewer tokens when written in Chinese, which means lower costs on APIs that charge per token (OpenAI, Anthropic, Gemini, etc.).

LessTokenAI automates the whole flow locally using Ollama (or any other API):

```
Your prompt -> Ollama compresses to Chinese -> Paid AI API -> Ollama translates response back
```

---

## Token savings by language

These are approximate averages. Actual savings depend on the model, the content, and the tokenizer used. Values are compared against Simplified Chinese as the baseline.

| Language   | Approx. tokens vs Chinese | Savings using Chinese |
|------------|--------------------------|----------------------|
| 简体中文    | baseline                 | —                    |
| English    | ~1.3×                    | ~25%                 |
| Español    | ~1.5×                    | ~33%                 |
| Français   | ~1.5×                    | ~33%                 |
| Deutsch    | ~1.6×                    | ~38%                 |
| Italiano   | ~1.5×                    | ~33%                 |
| Português  | ~1.5×                    | ~33%                 |
| Русский    | ~2.5×                    | ~60%                 |
| العربية    | ~2.0×                    | ~50%                 |
| 日本語      | ~1.1×                    | ~9%                  |
| 한국어      | ~1.2×                    | ~17%                 |

> **Note:** Japanese and Korean are already fairly token-efficient due to their logographic components, so the savings are smaller. The biggest gains come from languages that use Latin or Cyrillic scripts, especially Russian and Arabic.

---

## Requirements

- [Ollama](https://ollama.com) (or another API) running locally (or on a remote machine)
- A translation-capable model pulled (e.g. `llama3`, `qwen2.5`, `mistral`)
- Java 17+
- A paid AI API key (OpenAI, Anthropic, or any OpenAI-compatible endpoint)

---

## Setup

1. Clone the repo and open it in your IDE
2. Run Ollama and pull a model:
   ```bash
   ollama pull qwen2.5
   ```
3. Build and run `LessTokenAI.java`
4. In the app, configure:
   - **Ollama URL** and **model** in the Ollama settings block
   - **API URL** and **API Key** in the paid AI block
5. Select your response language and hit **Send**

---

## Project structure

```
src/io/github/AntonioRivasGamo/LessTokenAI/
├── LessTokenAI.java      # Swing UI
├── AppUtil.java          # Pipeline logic (Ollama + API calls) — implement here
└── AppPreferences.java   # Persistent settings via java.util.prefs

i18n/
├── messages_en.properties
├── messages_es.properties
├── messages_fr.properties
├── messages_de.properties
├── messages_it.properties
├── messages_pt.properties
├── messages_ja.properties
├── messages_ko.properties
├── messages_ru.properties
└── messages_ar.properties
```

---

## Contributing translations

The UI is fully internationalized via `.properties` files in the `i18n/` folder. If you spot a bad translation or want to add a new language, contributions are very welcome.

**To improve an existing translation:** edit the relevant `messages_XX.properties` file and open a pull request. Each key is self-explanatory and the files are plain UTF-8 text.

**To add a new language:**
1. Create `i18n/messages_XX.properties` (where `XX` is the ISO 639-1 code)
2. Add one line to the `LANG_CODES` map in `LessTokenAI.java`:
   ```java
   LANG_CODES.put("Your Language", "xx");
   ```
3. Open a pull request

All translations are welcome, whether it's a full new language or just a correction to an existing one.
