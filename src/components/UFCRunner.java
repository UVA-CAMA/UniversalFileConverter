package components;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class UFCRunner {

	public static void main(String[] args) throws IOException {

		// Instructions on how to select files
		JFrame folderframe = new JFrame("FileConverter");
		//folderframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		folderframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		folderframe.addWindowListener(new WindowAdapter(){
			@Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed");
                e.getWindow().dispose();
                System.out.println("Processes destroyed");
                System.exit(0);
            }
		});
		JOptionPane.showMessageDialog(folderframe, "Select the file(s) you want to convert on the next screen. \n To select multiple files, use the SHIFT or CTRL keys.");
		// Choose the files you want to convert
		File[] files=null;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);
		int fcdone = fc.showOpenDialog(null);
		if(fcdone == JFileChooser.APPROVE_OPTION) {
			files = fc.getSelectedFiles();
		} 
		else if(fcdone == JFileChooser.CANCEL_OPTION) {
			System.exit(0);
		}
			
		// Select the destination file location
		JOptionPane.showMessageDialog(folderframe, "Select the destination folder for the converted files \n on the next screen.");
		JFileChooser destfolderfc = new JFileChooser();
		destfolderfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnfolder2 = destfolderfc.showOpenDialog(null);
		if(returnfolder2 == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to save to this destination folder: " + destfolderfc.getSelectedFile().getAbsolutePath());
			UFCMain UFC = new UFCMain();
			UFC.destfolder = destfolderfc.getSelectedFile().getAbsolutePath();
			UFC.grabfilenameandconvert(files);
		}   
		else if(returnfolder2 == JFileChooser.CANCEL_OPTION){
			System.exit(0);
		}
	
	}

}
