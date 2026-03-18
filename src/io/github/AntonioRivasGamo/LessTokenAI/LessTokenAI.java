package io.github.AntonioRivasGamo.LessTokenAI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/**
 * @author AntonioRivasGamo
 * @version 0.1
 */
public class LessTokenAI extends JFrame {

    private final transient AppPreferences prefs = new AppPreferences();

    private static final Color BG_DARK = new Color(13, 15, 20);
    private static final Color BG_PANEL = new Color(20, 24, 32);
    private static final Color BG_FIELD = new Color(26, 30, 42);
    private static final Color BG_SECTION = new Color(16, 20, 30);
    private static final Color ACCENT = new Color(99, 179, 237);
    private static final Color ACCENT2 = new Color(159, 122, 234);
    private static final Color SUCCESS = new Color(72, 199, 142);
    private static final Color DANGER = new Color(252, 92, 101);
    private static final Color WARNING = new Color(253, 196, 61);
    private static final Color TEXT_PRIMARY = new Color(226, 232, 240);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(40, 48, 68);

    private static final String DIALOG = "Dialog";
    private static final Font MONO = new Font(DIALOG, Font.PLAIN, 13);
    private static final Font UI_B = new Font(DIALOG, Font.BOLD, 14);
    private static final Font UI_SB = new Font(DIALOG, Font.BOLD, 11);
    private static final Font SMALL = new Font(DIALOG, Font.PLAIN, 11);

    private static final Map<String, Properties> BUNDLES = new LinkedHashMap<>();
    private static final Map<String, String> LANG_CODES = new LinkedHashMap<>();

    static {
        LANG_CODES.put("English", "en");
        LANG_CODES.put("Español", "es");
        LANG_CODES.put("Français", "fr");
        LANG_CODES.put("Deutsch", "de");
        LANG_CODES.put("Italiano", "it");
        LANG_CODES.put("Português", "pt");
        LANG_CODES.put("日本語", "ja");
        LANG_CODES.put("한국어", "ko");
        LANG_CODES.put("Русский", "ru");
        LANG_CODES.put("العربية", "ar");

        for (Map.Entry<String, String> entry : LANG_CODES.entrySet()) {
            BUNDLES.put(entry.getKey(), loadBundle(entry.getValue()));
        }
    }

    private static Properties loadBundle(String code) {
        String filename = "messages_" + code + ".properties";
        File[] candidates = {
                new File("i18n", filename),
                new File(System.getProperty("user.dir"), "i18n/" + filename)
        };
        for (File f : candidates) {
            if (f.exists()) {
                try (InputStreamReader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
                    Properties p = new Properties();
                    p.load(r);
                    return p;
                } catch (IOException e) {
                    // TODO: replace with logger
                }
            }
        }
        try (InputStream is = LessTokenAI.class.getResourceAsStream("/i18n/" + filename)) {
            if (is != null) {
                Properties p = new Properties();
                p.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                return p;
            }
        } catch (IOException e) {
            // TODO: replace with logger
        }
        // TODO: replace with custom LessTokenAIException
        throw new RuntimeException("Bundle not found for language code: " + code);
    }

    private String currentLang = "English";

    private String t(String key) {
        return BUNDLES.get(currentLang).getProperty(key);
    }

    private JLabel subtitleLabel;
    private JLabel translationStatusLabel;
    private JLabel processingStatusLabel;

    private JLabel inputLabel;
    private JLabel chineseLabel;
    private JLabel aiLabel;
    private JLabel outputLabel;
    private JTextArea inputArea;
    private JTextArea chineseArea;
    private JTextArea aiResponseArea;
    private JTextArea finalOutputArea;

    private JLabel sectionTranslationLabel;
    private JLabel translationUrlLabel;
    private JLabel translationModelLabel;
    private JLabel translationKeyLabel;
    private JTextField translationUrlField;
    private JTextField translationModelField;
    private JPasswordField translationKeyField;
    private JButton saveTranslationBtn;
    private JButton testTranslationBtn;
    private JLabel translationSavedIndicator;

    private JLabel sectionProcessingLabel;
    private JLabel processingUrlLabel;
    private JLabel processingModelLabel;
    private JLabel processingKeyLabel;
    private JTextField processingUrlField;
    private JTextField processingModelField;
    private JPasswordField processingKeyField;
    private JButton saveProcessingBtn;
    private JButton testProcessingBtn;
    private JLabel processingSavedIndicator;

    private JLabel targetLangLabel;
    private JComboBox<String> targetLangCombo;
    private JButton sendBtn;
    private JLabel hintLabel;

    public LessTokenAI() {
        super("〈译〉 LessTokenAI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 800));
        setPreferredSize(new Dimension(1100, 900));
        getContentPane().setBackground(BG_DARK);

        buildUI();
        loadSavedSettings();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTextGrid(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setBackground(BG_DARK);

        JLabel title = new JLabel("〈译〉  LessTokenAI");
        title.setFont(new Font(DIALOG, Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);

        subtitleLabel = new JLabel(t("subtitle"));
        subtitleLabel.setFont(SMALL);
        subtitleLabel.setForeground(TEXT_MUTED);

        titles.add(title);
        titles.add(Box.createVerticalStrut(3));
        titles.add(subtitleLabel);

        JPanel statuses = new JPanel(new GridLayout(2, 1, 0, 5));
        statuses.setBackground(BG_DARK);
        translationStatusLabel = mkStatusLabel(t("section.translation"), t("status.checking"), WARNING);
        processingStatusLabel = mkStatusLabel(t("section.processing"), t("status.checking"), WARNING);
        statuses.add(translationStatusLabel);
        statuses.add(processingStatusLabel);

        p.add(titles, BorderLayout.WEST);
        p.add(statuses, BorderLayout.EAST);
        return p;
    }

    private JLabel mkStatusLabel(String svc, String msg, Color c) {
        JLabel l = new JLabel("● " + svc + ":  " + msg);
        l.setFont(new Font(DIALOG, Font.PLAIN, 12));
        l.setForeground(c);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    private JPanel buildTextGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setBackground(BG_DARK);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        inputArea = textArea(true);
        chineseArea = textArea(false);
        aiResponseArea = textArea(false);
        finalOutputArea = textArea(false);

        for (JTextArea ta : new JTextArea[] { chineseArea, aiResponseArea, finalOutputArea })
            ta.setBackground(new Color(18, 22, 34));
        finalOutputArea.setForeground(new Color(152, 251, 190));

        inputLabel = areaLabel(t("label.input"));
        chineseLabel = areaLabel(t("label.chinese"));
        aiLabel = areaLabel(t("label.ai_response"));
        outputLabel = areaLabel(t("label.output"));

        grid.add(wrapArea(inputLabel, inputArea, ACCENT));
        grid.add(wrapArea(chineseLabel, chineseArea, ACCENT2));
        grid.add(wrapArea(aiLabel, aiResponseArea, WARNING));
        grid.add(wrapArea(outputLabel, finalOutputArea, SUCCESS));
        return grid;
    }

    private JLabel areaLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UI_SB);
        l.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        return l;
    }

    private JTextArea textArea(boolean editable) {
        JTextArea ta = new JTextArea();
        ta.setFont(MONO);
        ta.setBackground(BG_FIELD);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(ACCENT);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(editable);
        ta.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        ta.setSelectionColor(new Color(99, 179, 237, 55));
        return ta;
    }

    private JPanel wrapArea(JLabel lbl, JTextArea ta, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(BG_PANEL);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        lbl.setForeground(accent);
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(new LineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(ta.getBackground());
        styleScrollBar(sp.getVerticalScrollBar());
        p.add(lbl, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottom() {
        JPanel outer = new JPanel(new BorderLayout(0, 8));
        outer.setBackground(BG_DARK);

        JPanel settingsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        settingsRow.setBackground(BG_DARK);
        settingsRow.add(buildTranslationBlock());
        settingsRow.add(buildProcessingBlock());
        settingsRow.add(buildActionBlock());
        outer.add(settingsRow, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        hintLabel = new JLabel(t("hint"));
        hintLabel.setFont(SMALL);
        hintLabel.setForeground(TEXT_MUTED);
        footer.add(hintLabel, BorderLayout.WEST);

        outer.add(footer, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildTranslationBlock() {
        JPanel card = settingsCard();

        sectionTranslationLabel = sectionTitle(t("section.translation"), ACCENT);
        card.add(sectionTranslationLabel, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(BG_SECTION);
        GridBagConstraints g = fieldGbc();

        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        translationUrlLabel = mutedLabel(t("label.translation_url"));
        fields.add(translationUrlLabel, g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        translationUrlField = styledTextField();
        fields.add(translationUrlField, g);

        g.gridx = 0;
        g.gridy = 1;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        translationModelLabel = mutedLabel(t("label.translation_model"));
        fields.add(translationModelLabel, g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        translationModelField = styledTextField();
        fields.add(translationModelField, g);

        g.gridx = 0;
        g.gridy = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        translationKeyLabel = mutedLabel(t("label.api_key"));
        fields.add(translationKeyLabel, g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        translationKeyField = new JPasswordField();
        styleInput(translationKeyField);
        fields.add(translationKeyField, g);

        card.add(fields, BorderLayout.CENTER);

        JPanel south = btnRow();
        saveTranslationBtn = accentButton(t("btn.save_translation"), ACCENT);
        testTranslationBtn = accentButton(t("btn.test_translation"), ACCENT2);
        translationSavedIndicator = indicatorLabel();
        saveTranslationBtn.addActionListener(e -> saveTranslationSettings());
        // TODO: implement testTranslationBtn logic in AppUtil
        south.add(saveTranslationBtn);
        south.add(Box.createHorizontalStrut(6));
        south.add(testTranslationBtn);
        south.add(Box.createHorizontalStrut(10));
        south.add(translationSavedIndicator);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildProcessingBlock() {
        JPanel card = settingsCard();

        sectionProcessingLabel = sectionTitle(t("section.processing"), ACCENT2);
        card.add(sectionProcessingLabel, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(BG_SECTION);
        GridBagConstraints g = fieldGbc();

        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        processingUrlLabel = mutedLabel(t("label.processing_url"));
        fields.add(processingUrlLabel, g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        processingUrlField = styledTextField();
        fields.add(processingUrlField, g);

        g.gridx = 0;
        g.gridy = 1;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        processingModelLabel = mutedLabel(t("label.translation_model"));
        fields.add(processingModelLabel, g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        processingModelField = styledTextField();
        fields.add(processingModelField, g);

        g.gridx = 0;
        g.gridy = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        processingKeyLabel = mutedLabel(t("label.api_key"));
        fields.add(processingKeyLabel, g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        processingKeyField = new JPasswordField();
        styleInput(processingKeyField);
        fields.add(processingKeyField, g);

        card.add(fields, BorderLayout.CENTER);

        JPanel south = btnRow();
        saveProcessingBtn = accentButton(t("btn.save_processing"), ACCENT);
        testProcessingBtn = accentButton(t("btn.test_processing"), ACCENT2);
        processingSavedIndicator = indicatorLabel();
        saveProcessingBtn.addActionListener(e -> saveProcessingSettings());
        // TODO: implement testProcessingBtn logic in AppUtil
        south.add(saveProcessingBtn);
        south.add(Box.createHorizontalStrut(6));
        south.add(testProcessingBtn);
        south.add(Box.createHorizontalStrut(10));
        south.add(processingSavedIndicator);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildActionBlock() {
        JPanel card = settingsCard();

        targetLangLabel = sectionTitle(t("label.target_lang"), SUCCESS);
        card.add(targetLangLabel, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(BG_SECTION);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        String[] langNames = BUNDLES.keySet().toArray(new String[0]);
        targetLangCombo = new JComboBox<>(langNames);
        targetLangCombo.setFont(new Font(DIALOG, Font.PLAIN, 13));
        targetLangCombo.setBackground(BG_FIELD);
        targetLangCombo.setForeground(TEXT_PRIMARY);
        targetLangCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        targetLangCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleCombo(targetLangCombo);
        targetLangCombo.addActionListener(e -> {
            currentLang = (String) targetLangCombo.getSelectedItem();
            prefs.setLang(currentLang);
            applyLanguage();
        });

        center.add(targetLangCombo);
        center.add(Box.createVerticalStrut(12));

        sendBtn = accentButton(t("btn.translate"), SUCCESS);
        sendBtn.setFont(new Font(DIALOG, Font.BOLD, 14));
        sendBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        sendBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sendBtn.addActionListener(e -> onSend());
        center.add(sendBtn);

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private void applyLanguage() {
        subtitleLabel.setText(t("subtitle"));
        retranslateStatus(translationStatusLabel, t("section.translation"));
        retranslateStatus(processingStatusLabel, t("section.processing"));

        inputLabel.setText(t("label.input"));
        chineseLabel.setText(t("label.chinese"));
        aiLabel.setText(t("label.ai_response"));
        outputLabel.setText(t("label.output"));

        sectionTranslationLabel.setText(t("section.translation"));
        translationUrlLabel.setText(t("label.translation_url"));
        translationModelLabel.setText(t("label.translation_model"));
        translationKeyLabel.setText(t("label.api_key"));
        saveTranslationBtn.setText(t("btn.save_translation"));
        testTranslationBtn.setText(t("btn.test_translation"));

        sectionProcessingLabel.setText(t("section.processing"));
        processingUrlLabel.setText(t("label.processing_url"));
        processingModelLabel.setText(t("label.translation_model"));
        processingKeyLabel.setText(t("label.api_key"));
        saveProcessingBtn.setText(t("btn.save_processing"));
        testProcessingBtn.setText(t("btn.test_processing"));

        targetLangLabel.setText(t("label.target_lang"));
        sendBtn.setText(t("btn.translate"));
        hintLabel.setText(t("hint"));

        revalidate();
        repaint();
    }

    private void retranslateStatus(JLabel lbl, String svc) {
        Color c = lbl.getForeground();
        String msg = c.equals(SUCCESS) ? t("status.connected")
                : c.equals(DANGER) ? t("status.disconnected") : t("status.checking");
        lbl.setText("● " + svc + ":  " + msg);
    }

    private void saveTranslationSettings() {
        prefs.setTranslationUrl(translationUrlField.getText().trim());
        prefs.setTranslationModel(translationModelField.getText().trim());
        String key = new String(translationKeyField.getPassword()).trim();
        if (!key.isEmpty())
            prefs.setTranslationKey(key);
        try {
            prefs.flush();
            showIndicator(translationSavedIndicator, t("saved.translation_ok"), SUCCESS);
        } catch (Exception ex) {
            showIndicator(translationSavedIndicator, t("saved.translation_error") + ex.getMessage(), DANGER);
        }
    }

    private void saveProcessingSettings() {
        prefs.setProcessingUrl(processingUrlField.getText().trim());
        prefs.setProcessingModel(processingModelField.getText().trim());
        String key = new String(processingKeyField.getPassword()).trim();
        if (!key.isEmpty())
            prefs.setProcessingKey(key);
        try {
            prefs.flush();
            showIndicator(processingSavedIndicator, t("saved.processing_ok"), SUCCESS);
        } catch (Exception ex) {
            showIndicator(processingSavedIndicator, t("saved.processing_error") + ex.getMessage(), DANGER);
        }
    }

    private void loadSavedSettings() {
        translationUrlField.setText(prefs.getTranslationUrl());
        translationModelField.setText(prefs.getTranslationModel());
        String translationKey = prefs.getTranslationKey();
        if (!translationKey.isEmpty())
            translationKeyField.setText(translationKey);

        processingUrlField.setText(prefs.getProcessingUrl());
        processingModelField.setText(prefs.getProcessingModel());
        String processingKey = prefs.getProcessingKey();
        if (!processingKey.isEmpty())
            processingKeyField.setText(processingKey);

        String savedLang = prefs.getLang();
        for (int i = 0; i < targetLangCombo.getItemCount(); i++) {
            if (targetLangCombo.getItemAt(i).equals(savedLang)) {
                currentLang = savedLang;
                targetLangCombo.setSelectedIndex(i);
                break;
            }
        }
        applyLanguage();
    }

    private void showIndicator(JLabel lbl, String msg, Color color) {
        lbl.setForeground(color);
        lbl.setText(msg);
        Timer t = new Timer(3000, e -> lbl.setText(" "));
        t.setRepeats(false);
        t.start();
    }

    private void onSend() {
        String prompt = inputArea.getText().trim();
        if (prompt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    t("warn.empty_prompt"), t("warn.title"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        sendBtn.setEnabled(false);
        sendBtn.setText(t("processing"));
        chineseArea.setText(t("translating_chinese"));
        aiResponseArea.setText("");
        finalOutputArea.setText("");

        // TODO: implement pipeline

        sendBtn.setEnabled(true);
        sendBtn.setText(t("btn.translate"));
    }

    private JPanel settingsCard() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG_SECTION);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        return p;
    }

    private JLabel sectionTitle(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(DIALOG, Font.BOLD, 12));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    private JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UI_SB);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JLabel indicatorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(SMALL);
        l.setForeground(SUCCESS);
        return l;
    }

    private JPanel btnRow() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBackground(BG_SECTION);
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        return p;
    }

    private GridBagConstraints fieldGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(3, 0, 3, 8);
        return g;
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        styleInput(tf);
        return tf;
    }

    private void styleInput(JTextField tf) {
        tf.setFont(MONO);
        tf.setBackground(BG_FIELD);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 9, 5, 9)));
    }

    private JButton accentButton(String text, Color accent) {
        Color normal = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25);
        Color hovered = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 55);

        JButton b = new JButton(text) {
            private boolean isHovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(java.awt.Graphics g) {
                g.setColor(isHovered ? hovered : normal);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
            }
        };

        b.setFont(UI_B);
        b.setForeground(accent);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(accent, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? new Color(40, 50, 70) : BG_FIELD);
                setForeground(TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }

    private void styleScrollBar(JScrollBar sb) {
        sb.setBackground(BG_FIELD);
        sb.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(60, 75, 100);
                trackColor = BG_FIELD;
            }

            @Override
            protected JButton createDecreaseButton(int o) {
                return emptyBtn();
            }

            @Override
            protected JButton createIncreaseButton(int o) {
                return emptyBtn();
            }

            private JButton emptyBtn() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // TODO: replace with logger
        }
        UIManager.put("OptionPane.background", BG_PANEL);
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        SwingUtilities.invokeLater(() -> new LessTokenAI().setVisible(true));
    }
}