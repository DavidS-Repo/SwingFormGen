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
import com.formdev.flatlaf.*;



public class FormGenerator implements ActionListener, DocumentListener {
    private JFrame frame;
    private JTextField titleField;
    private JPanel elementsPanel;
    private List<JTextField> dynamicFields;
    private List<JTextArea> staticHTMLAreas;
    private JComboBox<String> themeComboBox;

    public FormGenerator() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Form Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.setContentPane(contentPanel);
        JPanel titlePanel = new JPanel(new BorderLayout());
        JPanel titleFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField(45);
        titleField.getDocument().addDocumentListener(this);
        titleFieldPanel.add(titleLabel);
        titleFieldPanel.add(titleField);
        titlePanel.add(titleFieldPanel, BorderLayout.CENTER);
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel themeLabel = new JLabel("Themes:");
        String[] themes = {"FlatLaf Light", "FlatLaf Dark", "FlatLaf IntelliJ", "FlatLaf Darcula"};
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.addActionListener(this);
        themePanel.add(themeLabel);
        themePanel.add(themeComboBox);
        titlePanel.add(themePanel, BorderLayout.EAST);
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
		JTextField textField = new JTextField(43);
		textField.getDocument().addDocumentListener(this);
		dynamicFieldPanel.add(new JLabel("Dynamic Field:"));
		dynamicFieldPanel.add(textField);
		addMoveUpButton(dynamicFieldPanel);
		addMoveDownButton(dynamicFieldPanel);
		addRemoveButton(dynamicFieldPanel);
		dynamicFields.add(textField);
		return dynamicFieldPanel;
	}

	private void addMoveUpButton(JPanel panel) {
		JButton moveUpButton = new JButton("▲");
		moveUpButton.addActionListener(e -> {
			int index = elementsPanel.getComponentZOrder(panel);
			if (index > 0) {
				elementsPanel.remove(panel);
				elementsPanel.add(panel, index - 1);
				elementsPanel.revalidate();
				elementsPanel.repaint();
				if (panel.getComponentCount() > 1 && panel.getComponent(1) instanceof JTextField) {
					JTextField textField = (JTextField) panel.getComponent(1);
					int fieldIndex = dynamicFields.indexOf(textField);
					if (fieldIndex > 0) {
						dynamicFields.remove(fieldIndex);
						dynamicFields.add(fieldIndex - 1, textField);
					}
				} else if (panel.getComponentCount() > 1 && panel.getComponent(1) instanceof JScrollPane) {
					JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
					JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
					int fieldIndex = staticHTMLAreas.indexOf(textArea);
					if (fieldIndex > 0) {
						staticHTMLAreas.remove(fieldIndex);
						staticHTMLAreas.add(fieldIndex - 1, textArea);
					}
				}
			}
		});
		panel.add(moveUpButton);
	}

	private void addMoveDownButton(JPanel panel) {
		JButton moveDownButton = new JButton("▼");
		moveDownButton.addActionListener(e -> {
			int index = elementsPanel.getComponentZOrder(panel);
			if (index < elementsPanel.getComponentCount() - 1) {
				elementsPanel.remove(panel);
				elementsPanel.add(panel, index + 1);
				elementsPanel.revalidate();
				elementsPanel.repaint();
				if (panel.getComponentCount() > 1 && panel.getComponent(1) instanceof JTextField) {
					JTextField textField = (JTextField) panel.getComponent(1);
					int fieldIndex = dynamicFields.indexOf(textField);
					if (fieldIndex < dynamicFields.size() - 1) {
						dynamicFields.remove(fieldIndex);
						dynamicFields.add(fieldIndex + 1, textField);
					}
				} else if (panel.getComponentCount() > 1 && panel.getComponent(1) instanceof JScrollPane) {
					JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
					JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
					int fieldIndex = staticHTMLAreas.indexOf(textArea);
					if (fieldIndex < staticHTMLAreas.size() - 1) {
						staticHTMLAreas.remove(fieldIndex);
						staticHTMLAreas.add(fieldIndex + 1, textArea);
					}
				}
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

	private void loadTemplateFromFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Load Template");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Template Files", "txt"));
		int userSelection = fileChooser.showOpenDialog(frame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToLoad = fileChooser.getSelectedFile();
			try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
				clearAllFields();
				String line;
				boolean isFirstLine = true;
				while ((line = reader.readLine()) != null) {
					if (isFirstLine) {
						titleField.setText(line);
						isFirstLine = false;
					} else {
						String[] parts = line.split("\\|");
						if (parts.length >= 3) {
							if (parts[0].equalsIgnoreCase("DynamicTextField")) {
								String fieldValue = parts[2];
								addDynamicField("Dynamic Field:", fieldValue, userSelection);
							} else if (parts[0].equalsIgnoreCase("StaticTextField")) {
								String html = parts[2];
								addStaticHTML(html, userSelection);
							}
						}
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Error loading template: " + e.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JPanel createStaticHTMLPanel() {
	    JPanel staticHTMLPanel = new JPanel(new BorderLayout());
	    JTextArea textArea = new JTextArea(5, 30);
	    textArea.getDocument().addDocumentListener(this);
	    textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	    JScrollPane scrollPane = new JScrollPane(textArea);
	    staticHTMLPanel.add(new JLabel("Static HTML:"), BorderLayout.NORTH);
	    staticHTMLPanel.add(scrollPane, BorderLayout.CENTER);
	    staticHTMLAreas.add(textArea);
	    staticHTMLPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	    return staticHTMLPanel;
	}


	private String generateHTML() {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html><head><title>").append(titleField.getText()).append("</title></head><body>");
		List<String> dynamicFieldValues = new ArrayList<>();
		for (JTextField textField : dynamicFields) {
			String dynamicField = textField.getText().trim();
			if (!dynamicField.isEmpty()) {
				dynamicFieldValues.add(dynamicField);
			}
		}
		for (Component component : elementsPanel.getComponents()) {
			if (component instanceof JPanel) {
				JPanel panel = (JPanel) component;
				Component[] panelComponents = panel.getComponents();
				if (panelComponents.length > 1 && panelComponents[1] instanceof JTextField) {
					JTextField textField = (JTextField) panelComponents[1];
					String dynamicField = textField.getText().trim();
					if (!dynamicField.isEmpty()) {
						htmlBuilder.append(dynamicField);
						dynamicFieldValues.remove(dynamicField);
					}
				} else if (panelComponents.length > 1 && panelComponents[1] instanceof JScrollPane) {
					JScrollPane scrollPane = (JScrollPane) panelComponents[1];
					JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
					String staticHTML = textArea.getText().trim();
					if (!staticHTML.isEmpty()) {
						htmlBuilder.append(staticHTML);
					}
				}
			}
		}
		for (String dynamicFieldValue : dynamicFieldValues) {
			htmlBuilder.append(dynamicFieldValue);
		}
		htmlBuilder.append("</body></html>");
		return htmlBuilder.toString();
	}

	private void saveHTMLToFile(String html) {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Save HTML");
	    fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    fileChooser.setSelectedFile(new File("template.html"));
	    int userSelection = fileChooser.showSaveDialog(frame);
	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	        File fileToSave = fileChooser.getSelectedFile();
	        try (PrintWriter writer = new PrintWriter(fileToSave)) {
	            writer.println(html);
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(frame, "Error saving HTML file: " + e.getMessage(), "Error",
	                    JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	private void saveTemplateToFile() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Save Template");
	    fileChooser.setFileFilter(new FileNameExtensionFilter("Template Files", "txt"));
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    fileChooser.setSelectedFile(new File("template.txt"));
	    int userSelection = fileChooser.showSaveDialog(frame);
	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	        File fileToSave = fileChooser.getSelectedFile();
	        try (PrintWriter writer = new PrintWriter(fileToSave)) {
	            writer.println(titleField.getText());
	            int index = 0;
	            for (Component component : elementsPanel.getComponents()) {
	                if (component instanceof JPanel) {
	                    JPanel panel = (JPanel) component;
	                    Component[] panelComponents = panel.getComponents();
	                    if (panelComponents.length > 1 && panelComponents[1] instanceof JTextField) {
	                        JTextField textField = (JTextField) panelComponents[1];
	                        String dynamicField = textField.getText().trim();
	                        if (!dynamicField.isEmpty()) {
	                            writer.println("DynamicTextField|" + index + "|" + dynamicField);
	                            index++;
	                        }
	                    } else if (panelComponents.length > 1 && panelComponents[1] instanceof JScrollPane) {
	                        JScrollPane scrollPane = (JScrollPane) panelComponents[1];
	                        JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
	                        String staticHTML = textArea.getText().trim();
	                        if (!staticHTML.isEmpty()) {
	                            writer.println("StaticTextField|" + index + "|" + staticHTML);
	                            index++;
	                        }
	                    }
	                }
	            }
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(frame, "Error saving template: " + e.getMessage(), "Error",
	                    JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	private void clearAllFields() {
		titleField.setText("");
		dynamicFields.clear();
		staticHTMLAreas.clear();
		elementsPanel.removeAll();
	}

	private void addDynamicField(String label, String initialValue, int userSelection) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel fieldLabel = new JLabel(label);
		panel.add(fieldLabel);
		JTextField textField = new JTextField(initialValue, 43);
		panel.add(textField);
		addMoveUpButton(panel);
		addMoveDownButton(panel);
		addRemoveButton(panel);
		elementsPanel.add(panel);
		elementsPanel.revalidate();
		elementsPanel.repaint();
		dynamicFields.add(textField);
	}

	private void addStaticHTML(String htmlContent, int index) {
		JPanel staticHTMLPanel = createStaticHTMLPanel();
		JScrollPane scrollPane = (JScrollPane) staticHTMLPanel.getComponent(1);
		JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
		textArea.setText(htmlContent);
		staticHTMLAreas.add(textArea);
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
		} else if (e.getSource() == themeComboBox) {
            changeTheme();
        }
		elementsPanel.revalidate();
		elementsPanel.repaint();
	}
	
	private void changeTheme() {
        String theme = (String) themeComboBox.getSelectedItem();
        try {
            switch (theme) {
                case "FlatLaf Light":
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    break;
                case "FlatLaf Dark":
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    break;
                case "FlatLaf IntelliJ":
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    break;
                case "FlatLaf Darcula":
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                    break;
                default:
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
            }
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
