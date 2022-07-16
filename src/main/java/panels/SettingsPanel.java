package panels;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.GuiLogic;
import xGui.XBorderPanel;
import xGui.XButton;
import xGui.XLabel;
import xGui.XPagesPanel;
import xGui.XScrollPane;

public class SettingsPanel extends XPagesPanel {

	private static final long serialVersionUID = 1L;

	private XBorderPanel auth = new XBorderPanel();
	private XScrollPane authScroll = new XScrollPane(auth);
	
	private XBorderPanel other = new XBorderPanel();
	private XScrollPane otherScroll = new XScrollPane(other);
	
	private GuiLogic guiLogig = GuiLogic.getInstance();
	
	private XLabel clientSecretsLabel = new XLabel();
	
	public SettingsPanel() {
		super();
		putPage("Youtube Authorisation", authScroll);
		putPage("other", otherScroll);
		auth.north.add(new XLabel("Youtube Authorisation"));
		auth.center.add(new XButton("Choose client_secret.json", e -> chooseClienSecrets()));
		auth.center.add(clientSecretsLabel);
		auth.center.add(new XButton("Authorize now", e -> guiLogig.authorizeYoutube()));
		
		other.north.add(new XLabel("Other"));
		other.center.add(new XButton("Rotate", e -> rotateLayout()));
		updateClientSecretsLabel();
	}
	
	void chooseClienSecrets() {
		JFileChooser fileChooser = new JFileChooser(guiLogig.getClientSecrets());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Json filter", "json"));
		int returnVal = fileChooser.showOpenDialog(this);
		if(returnVal == 0) {
			guiLogig.setClientSecrets(fileChooser.getSelectedFile());
		}
		updateClientSecretsLabel();
	}
	
	void updateClientSecretsLabel() {
		clientSecretsLabel.setText(guiLogig.getClientSecrets().getName());
	}
}