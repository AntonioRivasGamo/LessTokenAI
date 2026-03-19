package io.github.AntonioRivasGamo.LessTokenAI;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 
 */
public class AppPreferences {

    private static final String NODE = "com.translator.app";
    private static final String KEY_TRANSLATION_URL = "translation_url";
    private static final String KEY_TRANSLATION_MODEL = "translation_model";
    private static final String KEY_TRANSLATION_KEY = "translation_key";
    private static final String KEY_PROCESSING_URL = "processing_url";
    private static final String KEY_PROCESSING_MODEL = "processing_model";
    private static final String KEY_PROCESSING_KEY = "processing_key";
    private static final String KEY_LANG = "ui_lang";

    private final Preferences prefs = Preferences.userRoot().node(NODE);

    public String getTranslationUrl() {
        return prefs.get(KEY_TRANSLATION_URL, "");
    }

    public void setTranslationUrl(String url) {
        prefs.put(KEY_TRANSLATION_URL, url);
    }

    public String getTranslationModel() {
        return prefs.get(KEY_TRANSLATION_MODEL, "");
    }

    public void setTranslationModel(String model) {
        prefs.put(KEY_TRANSLATION_MODEL, model);
    }

    public String getTranslationKey() {
        return prefs.get(KEY_TRANSLATION_KEY, "");
    }

    public void setTranslationKey(String key) {
        prefs.put(KEY_TRANSLATION_KEY, key);
    }

    public String getProcessingUrl() {
        return prefs.get(KEY_PROCESSING_URL, "");
    }

    public void setProcessingUrl(String url) {
        prefs.put(KEY_PROCESSING_URL, url);
    }

    public String getProcessingModel() {
        return prefs.get(KEY_PROCESSING_MODEL, "");
    }

    public void setProcessingModel(String model) {
        prefs.put(KEY_PROCESSING_MODEL, model);
    }

    public String getProcessingKey() {
        return prefs.get(KEY_PROCESSING_KEY, "");
    }

    public void setProcessingKey(String key) {
        prefs.put(KEY_PROCESSING_KEY, key);
    }

    public String getLang() {
        return prefs.get(KEY_LANG, "English");
    }

    public void setLang(String lang) {
        prefs.put(KEY_LANG, lang);
    }

    public void flush() throws BackingStoreException {
        prefs.flush();
    }

    public void clear() throws BackingStoreException {
        prefs.clear();
    }
}