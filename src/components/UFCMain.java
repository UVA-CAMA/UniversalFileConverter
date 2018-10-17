package components;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.commons.io.FilenameUtils;


public class UFCMain {
	
	String myfilename;
    String outputfiletype = "hdf5";
    String breakornobreak = ""; // This breaks up the file by day
    String destfolder;
    InputStream is;
    File convLog; //Conversion log file
    FileWriter fwconv; //Conversion log file writer
    BufferedWriter bwconv; //Conversion log buffered writer
    JFrame frame; 
    String monitoringsystem = " ";
    String savewaveformstoggle = " ";
    String justonexml = " ";
    Process proc1;
    Process proc2;
    Boolean process1inprogress = false;
    Boolean process2inprogress = false;
    String ext;
    
    
	public UFCMain() {
		// TODO Auto-generated constructor stub
	}
	
	public void grabfilenameandconvert(File[] filesandfolders) throws IOException {
		File[] files = checkforfolders(filesandfolders);
		String[] folderlist = storefolders(filesandfolders);
		int numfiles = folderlist.length;
		String[] destfolders = new String[numfiles];
		for (int f=0; f < numfiles; f++) {
			destfolders[f] = destfolder + "\\" + folderlist[f];
			Path path = Paths.get(destfolders[f]);
			if (!Files.exists(path)) {
				Files.createDirectory(path);
			}
		}
		String currentfile = System.getProperty("user.dir");
		convLog = new File(currentfile + "//ConversionLogFiles//" + "ConversionLog" + System.currentTimeMillis());
		try {
			fwconv = new FileWriter(convLog);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		bwconv = new BufferedWriter(fwconv); 
		frame = new JFrame("File Conversion Progress");
		JLabel label = new JLabel();
		JProgressBar progressBar;
		progressBar = new JProgressBar(0,files.length);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		JPanel panel = new JPanel();
		panel.add(progressBar);
		panel.add(label);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
			{
				@Override
	            public void windowClosing(WindowEvent e)
	            {
	                System.out.println("Closed");
	                e.getWindow().dispose();
	                if (process1inprogress) {proc1.destroy();}
	                if (process2inprogress) {proc2.destroy();}
	                System.out.println("Processes destroyed");
	                System.exit(0);
	            }
			});
		bwconv.write("Using Universal File Converter v1.1.1"); bwconv.newLine();
		bwconv.write("(UFC_v1.1.1)");  bwconv.newLine();
		bwconv.write("Using fmtcnv_v4.0.6");  bwconv.newLine();
		bwconv.write("Using StpToolkit_8.4");  bwconv.newLine();
		bwconv.write("User has chosen to convert " + files.length + " files.");  bwconv.newLine();
		bwconv.write("The file path for the first file is: " + (files[0].getAbsolutePath())); bwconv.newLine();
    	for (int i=0; i < files.length; i++) {
    		ZonedDateTime startconversiontime = ZonedDateTime.now();
    		bwconv.newLine();
    		bwconv.write("Started converting at " + startconversiontime); bwconv.newLine();
	    	myfilename = files[i].getAbsolutePath();
	    	progressBar.setValue(i);
	    	label.setText("Converting file number " + (i+1) + " of " + files.length + ":" + myfilename + ". Please wait...");
	    	bwconv.write("Converting file number " + (i+1) + " of " + files.length + ":" + myfilename + "."); bwconv.newLine();
	    	bwconv.write("The file size is: " + files[i].length()/1024 + " kilobytes"); bwconv.newLine();
			frame.pack();
			frame.setVisible(false);
			frame.setLocationRelativeTo(null);
	    	runconverter(startconversiontime,destfolders[i]);
	    	ZonedDateTime endconversiontime = ZonedDateTime.now();
	    	Duration d = Duration.between(startconversiontime, endconversiontime);
	    	long dur = d.getSeconds();
	    	Double durdouble = (double)dur;
	    	Double d_per_mb = durdouble/(files[i].length()/1048576);
	    	String dstring = Long.toString(dur);
	    	  		
	    	String d_per_mb_string = Double.toString(d_per_mb);
	    	bwconv.write("Done converting at " + endconversiontime); bwconv.newLine();
	    	
	    	bwconv.write("Total conversion time: " + dstring + " seconds"); bwconv.newLine();
	    	Double mb_filesize = (double)(files[i].length())/1048576;
	    	String mb_string = Double.toString(mb_filesize);
	    	bwconv.write("Conversion rate: " + d_per_mb_string +" secs per mb for a " + mb_string + " mb file"); bwconv.newLine();
    	}
    	progressBar.setValue(files.length);
    	label.setText("File conversion complete. Converted files are located in: " + destfolder );
    	bwconv.close();
    }
	
	public File[] checkforfolders(File[] files) {
		ArrayList<File> x = new ArrayList<File>();
		for (int i=0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				File [] filesinfolder = files[i].listFiles();
				for (int j=0; j < filesinfolder.length; j++) {
					x.add(filesinfolder[j]);
				}
			} else {
				x.add(files[i]);
			}
		}
		files = x.toArray(new File[x.size()]);
		return files;
	}
	
	public String[] storefolders(File[] files) {
		ArrayList<String> folders = new ArrayList<String>();
		for (int i=0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				File [] filesinfolder = files[i].listFiles();
				for (int j=0; j < filesinfolder.length; j++) {
					folders.add(files[i].getName());
				}
			} else {
				folders.add("");
			}
		}
		String[] folderstrings = folders.toArray(new String[folders.size()]);
		return folderstrings;
	}
	
	public void choosemonitoringsystem() {
	    Object[] options = {"Unity (Direct Connect, GE System)","Carescape (GE System)","Viridia (Philips Classic System)","PIICiX (Philips System)"};
	    int n = JOptionPane.showOptionDialog(frame, 
    			"Select the system you are using", 
    			"File Converter", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.QUESTION_MESSAGE,
    			null,
    			options, 
    			options[0]);
    	
    	if (n==0) {monitoringsystem = " -u";}
    	else if (n==1) {monitoringsystem = "-cs";}
    	else if (n==2) {monitoringsystem = "-p";}
    	else if (n==3) {monitoringsystem = "-pix";}
	}
	
	public void choosefilebyday() {
	    Object[] options = {"Output One File (NOT Recommended)","Output One File Per Day (RECOMMENDED)"};
	    int n = JOptionPane.showOptionDialog(frame, 
    			"How would you like to save the output?", 
    			"File Converter", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.QUESTION_MESSAGE,
    			null,
    			options, 
    			options[0]);
    	
    	if (n==0) {breakornobreak = "--no-break";}
    	else if (n==1) {breakornobreak = "";}
	}
	
	public void vitalsandwaveformschoice() {
	    Object[] options = {"All Data (larger file)","Vital Signs Only (smaller file)","Only a Short Sample File (smallest file)"};
	    int n = JOptionPane.showOptionDialog(frame,
    			"What would you like to save?", 
    			"File Converter", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.QUESTION_MESSAGE,
    			null,
    			options, 
    			options[0]);
    	
    	if (n==0) {savewaveformstoggle = "  ";}
    	else if (n==1) {savewaveformstoggle = " -xw";}
    	else if (n==2) {
    		if(ext.equalsIgnoreCase("Stp")) {savewaveformstoggle = " -s 1 -e 1440";}
    		else {justonexml = " -1";}
		} // this converts waveforms and vital signs for the first day/first 1440 segments (which is one day for GE)
	}
	
	
	public void runconverter(ZonedDateTime startconversiontime,String destfoldernew) {
		// Run Ryan's C executable on the files selected by the user
		try
		{
			String currentfile = System.getProperty("user.dir");
			bwconv.write("Entered runconverter."); bwconv.newLine();
			ext = FilenameUtils.getExtension(myfilename);
			if (ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("xls")) {
				SimpleExcelReaderExample excelreader = new SimpleExcelReaderExample();
				excelreader.testExcelReader(myfilename);
			}
			bwconv.write("File extension of file to convert is: " + ext); bwconv.newLine();
			String plainfilename = FilenameUtils.getBaseName(myfilename);
			bwconv.write("Plain file name is: " + plainfilename); bwconv.newLine();
			// If the file is an .Stp file, convert to xml first as an intermediate step. Save the logfile and the xml file.
			boolean deidentify = true;
			String deidentifyparam = " ";
			if (deidentify) {
				deidentifyparam = " -blnk";
			}
			if (savewaveformstoggle.length()<2) {
				vitalsandwaveformschoice();
				choosefilebyday(); // This gives people the choice to keep everything in one big file - we removed this choice
			}
			String fullxmlfilepath = (destfoldernew + "\\" + plainfilename + ".xml");
			String fullzipfilepath = (destfoldernew + "\\" + plainfilename + ".zip");
			if (ext.equalsIgnoreCase("Stp")) {
				bwconv.write("Entered Stp converter"); bwconv.newLine();
				if (monitoringsystem.length()<2) { // If we haven't already chosen a monitoring system, set it here
					choosemonitoringsystem();
				}
				frame.setVisible(true);
				String errorfilename = (destfoldernew + "\\" + plainfilename + "_toxml_error.log");
				String inputfilename = (destfoldernew + "\\" + plainfilename + "_toxml_input.log");
				
				File e = new File(errorfilename);
				File i = new File(inputfilename);

				String command1 = currentfile + "\\StpToolkit_8.4\\StpToolkit.exe \"" + myfilename + "\" " + monitoringsystem + deidentifyparam + " -o \"" + fullxmlfilepath + "\" -v" + savewaveformstoggle; 

				bwconv.write("Command1 string is: " + command1); bwconv.newLine();
			    
			    Runtime rt = Runtime.getRuntime();
			    ZonedDateTime c1starttime = ZonedDateTime.now();
			    proc1 = rt.exec(command1);
			    process1inprogress = true;
			    bwconv.write("Command1 has been executed at " + c1starttime); bwconv.newLine();
			    Duration d0 = Duration.between(startconversiontime, c1starttime);
		    	long dur0 = d0.getSeconds();
		    	String dstring0 = Long.toString(dur0);
		    	bwconv.write("File load time: " + dstring0 + " seconds"); bwconv.newLine();	
			    
		    	FileOutputStream fes = new FileOutputStream(e);
		    	FileOutputStream fis = new FileOutputStream(i);
		    	
		    	StreamGobbler errorGobbler = new StreamGobbler(proc1.getErrorStream(),"ERROR",fes);
		    	StreamGobbler inputGobbler = new StreamGobbler(proc1.getInputStream(),"INPUT",fis);
		    	
		    	errorGobbler.start();
	            inputGobbler.start();
		    	
//			    InputStream is = proc1.getInputStream();
//				InputStreamReader isr = new InputStreamReader(is);
//				BufferedReader br1 = new BufferedReader(isr);
//				FileWriter fw = new FileWriter(f);
//				BufferedWriter bw = new BufferedWriter(fw);
				System.out.println(command1);
//				String line = null;
//				while( (line = br1.readLine()) !=null) {
//					bw.write(line);
//					bw.newLine();
//				}
				
				proc1.waitFor();
				process1inprogress = false;
				ZonedDateTime c1endtime = ZonedDateTime.now();
				bwconv.write("Command1 waiting has completed at " + c1endtime); bwconv.newLine();
				
				Duration d1 = Duration.between(c1starttime, c1endtime);
		    	long dur1 = d1.getSeconds();
		    	String dstring1 = Long.toString(dur1);
		    	bwconv.write("Command 1 Time: " + dstring1 + " seconds"); bwconv.newLine();
				
				myfilename = (fullxmlfilepath);
				bwconv.write("xml file path is: " + myfilename); bwconv.newLine();
//				bw.close();
//				is.close();
//				isr.close();
//				br1.close();
//				fw.close();
//				bw.close();
			} else {frame.setVisible(true);}
			Runtime rt2 = Runtime.getRuntime();
			String command2 = currentfile + "\\fmtcnv_v4.0.6\\formatconverter --to " + outputfiletype + " \"" + myfilename + "\" " + justonexml + " " + breakornobreak + " --localtime --pattern \"" + destfoldernew + "\\%i_%s.%t\"";
//			String command2 = currentfile + "\\fmtcnv_v4.0.6\\formatconverter --to " + outputfiletype + " \"" + myfilename + "\" " + justonexml + " " + breakornobreak + " --pattern \"" + destfoldernew + "\\%i_%s.%t\"";
			bwconv.write("Command2 string is: " + command2); bwconv.newLine();
			System.out.println(command2);
			proc2 = rt2.exec(command2);
			process2inprogress = true;
			ZonedDateTime c2starttime = ZonedDateTime.now();
			bwconv.write("Command2 has been executed at " + c2starttime); bwconv.newLine();
			
			String errorfilename2 = (destfoldernew + "\\" + plainfilename + "_tohdf5_error.log");
			String inputfilename2 = (destfoldernew + "\\" + plainfilename + "_tohdf5_input.log");
			
			File e2 = new File(errorfilename2);
			File i2 = new File(inputfilename2);
			
	    	FileOutputStream fes2 = new FileOutputStream(e2);
	    	FileOutputStream fis2 = new FileOutputStream(i2);
	    	
	    	StreamGobbler errorGobbler2 = new StreamGobbler(proc2.getErrorStream(),"ERROR",fes2);
	    	StreamGobbler inputGobbler2 = new StreamGobbler(proc2.getInputStream(),"INPUT",fis2);
	    	
	    	errorGobbler2.start();
            inputGobbler2.start();
            
			proc2.waitFor();
			process2inprogress = false;
			ZonedDateTime c2endtime = ZonedDateTime.now();
			bwconv.write("Command2 waiting has completed at " + c2endtime); bwconv.newLine();
			Duration d2 = Duration.between(c2starttime, c2endtime);
	    	long dur2 = d2.getSeconds();
	    	String dstring2 = Long.toString(dur2);
	    	bwconv.write("Command 2 Time: " + dstring2 + " seconds"); bwconv.newLine();
	    	ZonedDateTime zipstarttime = ZonedDateTime.now();
	    	bwconv.write("Zipping xml file at " + zipstarttime); bwconv.newLine();
	    	if (ext.equalsIgnoreCase("Stp")) {
		    	zipmyfile(fullxmlfilepath,fullzipfilepath);
		    	Path xmlfile = Paths.get(fullxmlfilepath);
		    	Files.deleteIfExists(xmlfile);
	    	}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		System.out.println("Done converting: " + myfilename);
		
	}
    
    public void zipmyfile(String sourceFile, String outputfile) throws Throwable{
        FileOutputStream fos = new FileOutputStream(outputfile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        final byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }
	
		       
}
