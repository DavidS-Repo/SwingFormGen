import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
	private HtmlHandler htmlHandler;

	public FormGenerator() {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		frame = new JFrame("HTMLGen");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		frame.setContentPane(contentPanel);

		// Title Panel
		JPanel titlePanel = new JPanel(new BorderLayout());

		// Title Field
		JPanel titleFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel titleLabel = new JLabel("Page Title:");
		titleField = new JTextField(45);
		titleField.getDocument().addDocumentListener(this);
		titleFieldPanel.add(titleLabel);
		titleFieldPanel.add(titleField);
		titlePanel.add(titleFieldPanel, BorderLayout.CENTER);

		// Theme Panel
		JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel themeLabel = new JLabel("Themes:");
		String[] themes = {"Dark", "Light"};
		themeComboBox = new JComboBox<>(themes);
		themeComboBox.addActionListener(this);
		themePanel.add(themeLabel);
		themePanel.add(themeComboBox);
		titlePanel.add(themePanel, BorderLayout.EAST);

		// Button Panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addButton(buttonPanel, "Add Dynamic Field", this);
		addButton(buttonPanel, "Add Static HTML", this);
		addButton(buttonPanel, "Save HTML", this);
		addButton(buttonPanel, "Save Template", this);
		addButton(buttonPanel, "Load Template", this);

		// Elements Panel
		elementsPanel = new JPanel();
		elementsPanel.setLayout(new BoxLayout(elementsPanel, BoxLayout.Y_AXIS));
		JScrollPane elementsScrollPane = new JScrollPane(elementsPanel);
		elementsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		elementsScrollPane.setPreferredSize(new Dimension(400, 200));

		// Editor Panel
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, elementsScrollPane, null);
		splitPane.setResizeWeight(0.5);
		JPanel editorPanel = new JPanel(new BorderLayout());
		editorPanel.add(splitPane, BorderLayout.CENTER);

		// Add components to content panel
		contentPanel.add(titlePanel, BorderLayout.NORTH);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		contentPanel.add(editorPanel, BorderLayout.CENTER);

		// Initialize variables
		dynamicFields = new ArrayList<>();
		staticHTMLAreas = new ArrayList<>();
		htmlHandler = new HtmlHandler();

		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private JPanel createDynamicFieldPanel() {
		JPanel dynamicFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField textField = new JTextField(54);
		dynamicFieldPanel.add(new JLabel(""));
		Dimension textFieldSize = textField.getPreferredSize();
		textFieldSize.height = 44;
		textField.setPreferredSize(textFieldSize);
		textField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (textField.getText().equals("Dynamic Field")) {
					textField.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (textField.getText().isEmpty()) {
					textField.setText("Dynamic Field");
				}
			}
		});
		textField.setText("Dynamic Field");
		dynamicFieldPanel.add(textField);
		addMoveUpButton(dynamicFieldPanel);
		addMoveDownButton(dynamicFieldPanel);
		addRemoveButton(dynamicFieldPanel);
		dynamicFields.add(textField);
		return dynamicFieldPanel;
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
		staticHTMLPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		return staticHTMLPanel;
	}

	private void addButton(JPanel panel, String text, ActionListener listener) {
		JButton button = new JButton(text);
		button.addActionListener(listener);
		panel.add(button);
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
		JPanel moveButtonsPanel = new JPanel();
		moveButtonsPanel.setLayout(new BoxLayout(moveButtonsPanel, BoxLayout.Y_AXIS));
		moveButtonsPanel.add(moveUpButton);
		panel.add(moveButtonsPanel);
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
		JPanel moveButtonsPanel = (JPanel) panel.getComponent(2);
		moveButtonsPanel.add(moveDownButton);
	}

	private void addRemoveButton(JPanel panel) {
		JButton removeButton = new JButton("Remove");
		removeButton.setPreferredSize(new Dimension(removeButton.getPreferredSize().width, 44));
		removeButton.addActionListener(e -> {
			elementsPanel.remove(panel);
			elementsPanel.revalidate();
			elementsPanel.repaint();
		});
		panel.add(removeButton);
	}

	private void clearAllFields() {
		titleField.setText("");
		dynamicFields.clear();
		staticHTMLAreas.clear();
		elementsPanel.removeAll();
	}

	private void addDynamicField(String label, String initialValue, int userSelection) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel fieldLabel = new JLabel("");
		panel.add(fieldLabel);
		JTextField textField = new JTextField(initialValue, 54);
		Dimension textFieldSize = textField.getPreferredSize();
		textFieldSize.height = 44;
		textField.setPreferredSize(textFieldSize);
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
				StringBuilder currentFieldText = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					if (isFirstLine) {
						titleField.setText(line);
						isFirstLine = false;
					} else {
						if (line.startsWith("DynamicTextField|") || line.startsWith("StaticTextField|")) {
							if (currentFieldText.length() > 0) {
								processLoadedField(currentFieldText.toString(), userSelection);
								currentFieldText = new StringBuilder();
							}
						}
						currentFieldText.append(line).append("\n");
					}
				}
				if (currentFieldText.length() > 0) {
					processLoadedField(currentFieldText.toString(), userSelection);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Error loading template: " + e.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void processLoadedField(String fieldText, int userSelection) {
		String[] parts = fieldText.split("\\|");
		if (parts.length >= 3) {
			if (parts[0].equalsIgnoreCase("DynamicTextField")) {
				String fieldValue = parts[2].trim();
				addDynamicField("Dynamic Field:", fieldValue, userSelection);
			} else if (parts[0].equalsIgnoreCase("StaticTextField")) {
				String html = parts[2].trim();
				addStaticHTML(html, userSelection);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("Add Dynamic Field")) {
			JPanel dynamicFieldPanel = createDynamicFieldPanel();
			elementsPanel.add(dynamicFieldPanel);
			JTextField dynamicField = (JTextField) dynamicFieldPanel.getComponent(1);
			htmlHandler.addDynamicField(dynamicFieldPanel, dynamicField);
		} else if (command.equals("Add Static HTML")) {
			JPanel staticHTMLPanel = createStaticHTMLPanel();
			elementsPanel.add(staticHTMLPanel);
			JScrollPane scrollPane = (JScrollPane) staticHTMLPanel.getComponent(1);
			JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
			htmlHandler.addStaticHTML(textArea);
		} else if (command.equals("Save HTML")) {
			String html = htmlHandler.generateHTML(titleField.getText(), elementsPanel);
			htmlHandler.saveHTMLToFile(html, frame);
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
	        if ("Light".equals(theme)) {
	            UIManager.setLookAndFeel(new FlatLightLaf());
	        } else if ("Dark".equals(theme)) {
	            UIManager.setLookAndFeel(new FlatDarkLaf());
	        } else {
	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        }
	        SwingUtilities.updateComponentTreeUI(frame);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(FormGenerator::new);
	}
}
