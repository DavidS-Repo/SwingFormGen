import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HtmlHandler {
	private List<JTextField> dynamicFields;
	private List<JTextArea> staticHTMLAreas;

	public HtmlHandler() {
		dynamicFields = new ArrayList<>();
		staticHTMLAreas = new ArrayList<>();
	}

	public String generateHTML(String title, JPanel elementsPanel) {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html><head><title>").append(title).append("</title></head><body>");
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

	public void saveHTMLToFile(String html, JFrame frame) {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Save HTML");
	    fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    fileChooser.setSelectedFile(new File("template.html"));
	    int userSelection = fileChooser.showSaveDialog(frame);
	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	        File fileToSave = fileChooser.getSelectedFile();
	        try (PrintWriter writer = new PrintWriter(fileToSave)) {
	            html = html.replace("\\n", "\n");
	            writer.println(html);
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(frame, "Error saving HTML file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}


	public void addDynamicField(JPanel panel, JTextField textField) {
		dynamicFields.add(textField);
	}

	public void addStaticHTML(JTextArea textArea) {
		staticHTMLAreas.add(textArea);
	}
}