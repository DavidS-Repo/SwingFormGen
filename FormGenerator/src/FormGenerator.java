import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FormGenerator implements ActionListener, DocumentListener {
    private JFrame frame;
    private JTextField titleField;
    private JPanel elementsPanel;
    private List<JTextField> dynamicFields;
    private List<JComboBox<String>> titleSizeComboBoxes;
    private List<JCheckBox> boldCheckBoxes;
    private List<JTextArea> staticHTMLAreas;

    public FormGenerator() {
        frame = new JFrame("Form Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        frame.setContentPane(contentPanel);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField(20);
        titleField.getDocument().addDocumentListener(this);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton(buttonPanel, "Add Dynamic Field", this);
        addButton(buttonPanel, "Add Static HTML", this);
        addButton(buttonPanel, "Save HTML", this);
        addButton(buttonPanel, "Save Template", this);
        addButton(buttonPanel, "Load Template", this);

        elementsPanel = new JPanel();
        elementsPanel.setLayout(new BoxLayout(elementsPanel, BoxLayout.Y_AXIS));

        JScrollPane elementsScrollPane = new JScrollPane(elementsPanel);
        elementsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        elementsScrollPane.setPreferredSize(new Dimension(400, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, elementsScrollPane, null);
        splitPane.setResizeWeight(0.5);

        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(splitPane, BorderLayout.CENTER);

        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.add(editorPanel, BorderLayout.CENTER);

        dynamicFields = new ArrayList<>();
        titleSizeComboBoxes = new ArrayList<>();
        boldCheckBoxes = new ArrayList<>();
        staticHTMLAreas = new ArrayList<>();

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private JPanel createDynamicFieldPanel() {
        JPanel dynamicFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField textField = new JTextField(20);
        textField.getDocument().addDocumentListener(this);
        JComboBox<String> titleSizeComboBox = new JComboBox<>(new String[]{"", "h1", "h2", "h3", "h4", "h5", "h6"});
        titleSizeComboBox.addActionListener(this);
        JCheckBox boldCheckBox = new JCheckBox("Bold");
        boldCheckBox.addActionListener(this);

        dynamicFieldPanel.add(new JLabel("Dynamic Field:"));
        dynamicFieldPanel.add(textField);
        dynamicFieldPanel.add(titleSizeComboBox);
        dynamicFieldPanel.add(boldCheckBox);

        addMoveUpButton(dynamicFieldPanel);
        addMoveDownButton(dynamicFieldPanel);
        addRemoveButton(dynamicFieldPanel);

        dynamicFields.add(textField);
        titleSizeComboBoxes.add(titleSizeComboBox);
        boldCheckBoxes.add(boldCheckBox);

        return dynamicFieldPanel;
    }

    private void addMoveUpButton(JPanel panel) {
        JButton moveUpButton = new JButton("Up");
        moveUpButton.addActionListener(e -> {
            int index = elementsPanel.getComponentZOrder(panel);
            if (index > 0) {
                elementsPanel.remove(panel);
                elementsPanel.add(panel, index - 1);
                elementsPanel.revalidate();
                elementsPanel.repaint();
            }
        });
        panel.add(moveUpButton);
    }

    private void addMoveDownButton(JPanel panel) {
        JButton moveDownButton = new JButton("Down");
        moveDownButton.addActionListener(e -> {
            int index = elementsPanel.getComponentZOrder(panel);
            if (index < elementsPanel.getComponentCount() - 1) {
                elementsPanel.remove(panel);
                elementsPanel.add(panel, index + 1);
                elementsPanel.revalidate();
                elementsPanel.repaint();
            }
        });
        panel.add(moveDownButton);
    }

    private void addRemoveButton(JPanel panel) {
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            elementsPanel.remove(panel);
            elementsPanel.revalidate();
            elementsPanel.repaint();
        });
        panel.add(removeButton);
    }

    private JPanel createStaticHTMLPanel() {
        JPanel staticHTMLPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(5, 30);
        textArea.getDocument().addDocumentListener(this);
        JScrollPane scrollPane = new JScrollPane(textArea);
        staticHTMLPanel.add(new JLabel("Static HTML:"), BorderLayout.NORTH);
        staticHTMLPanel.add(scrollPane, BorderLayout.CENTER);
        staticHTMLAreas.add(textArea);
        return staticHTMLPanel;
    }

    private String generateHTML() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><title>").append(titleField.getText()).append("</title></head><body>");

        for (int i = 0; i < dynamicFields.size(); i++) {
            String field = dynamicFields.get(i).getText();
            if (!field.isEmpty()) {
                String size = (String) titleSizeComboBoxes.get(i).getSelectedItem();
                boolean isBold = boldCheckBoxes.get(i).isSelected();
                htmlBuilder.append("<").append(size).append(">");
                if (isBold) {
                    htmlBuilder.append("<b>");
                }
                htmlBuilder.append(field);
                if (isBold) {
                    htmlBuilder.append("</b>");
                }
                htmlBuilder.append("</").append(size).append(">");
            }
        }

        for (JTextArea area : staticHTMLAreas) {
            String html = area.getText();
            if (!html.isEmpty()) {
                htmlBuilder.append(html);
            }
        }

        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString();
    }

    private void saveHTMLToFile(String html) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save HTML");
        fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                writer.println(html);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error saving HTML file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTemplateToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Template");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Template Files", "txt"));

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                writer.println(titleField.getText());
                for (JTextField field : dynamicFields) {
                    writer.println("Dynamic Field: " + field.getText());
                }
                for (JTextArea area : staticHTMLAreas) {
                    writer.println("Static HTML:");
                    writer.println(area.getText());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error saving template: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTemplateFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Template");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Template Files", "txt"));

        int userSelection = fileChooser.showOpenDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                clearAllFields();
                titleField.setText(reader.readLine());
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Dynamic Field:")) {
                        String field = line.substring("Dynamic Field: ".length());
                        addDynamicField(field);
                    } else if (line.equals("Static HTML:")) {
                        String html = reader.readLine();
                        addStaticHTML(html);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error loading template: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearAllFields() {
        titleField.setText("");
        dynamicFields.clear();
        titleSizeComboBoxes.clear();
        boldCheckBoxes.clear();
        staticHTMLAreas.clear();
        elementsPanel.removeAll();
    }

    private void addDynamicField(String field) {
        JPanel dynamicFieldPanel = createDynamicFieldPanel();
        JTextField textField = dynamicFields.get(dynamicFields.size() - 1);
        textField.setText(field);
        elementsPanel.add(dynamicFieldPanel);
        elementsPanel.revalidate();
        elementsPanel.repaint();
    }

    private void addStaticHTML(String html) {
        JPanel staticHTMLPanel = createStaticHTMLPanel();
        JTextArea textArea = staticHTMLAreas.get(staticHTMLAreas.size() - 1);
        textArea.setText(html);
        elementsPanel.add(staticHTMLPanel);
        elementsPanel.revalidate();
        elementsPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Add Dynamic Field")) {
            JPanel dynamicFieldPanel = createDynamicFieldPanel();
            elementsPanel.add(dynamicFieldPanel);
        } else if (command.equals("Add Static HTML")) {
            JPanel staticHTMLPanel = createStaticHTMLPanel();
            elementsPanel.add(staticHTMLPanel);
        } else if (command.equals("Save HTML")) {
            String html = generateHTML();
            saveHTMLToFile(html);
        } else if (command.equals("Save Template")) {
            saveTemplateToFile();
        } else if (command.equals("Load Template")) {
            loadTemplateFromFile();
        }
        elementsPanel.revalidate();
        elementsPanel.repaint();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateButtonsState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateButtonsState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateButtonsState();
    }

    private void updateButtonsState() {
        for (JTextField field : dynamicFields) {
            if (!field.getText().isEmpty()) {
                return;
            }
        }
        for (JTextArea area : staticHTMLAreas) {
            if (!area.getText().isEmpty()) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormGenerator::new);
    }
}
