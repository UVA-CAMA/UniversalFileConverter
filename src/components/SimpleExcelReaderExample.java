package components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
/**
 * A dirty simple program that reads an Excel file.
 * @author www.codejava.net
 * Altered by Amanda Edwards
 *
 */
public class SimpleExcelReaderExample {
     
    public File[] testExcelReader(String excelfilename) {
		String [] filenames = null;
		
        String excelFilePath = excelfilename;
        FileInputStream inputStream;
        String ext = FilenameUtils.getExtension(excelfilename);
		try {
			inputStream = new FileInputStream(new File(excelFilePath));
			
	        Workbook workbook;
	        
			try {
				if (ext.equalsIgnoreCase("xlsx")) {
					workbook = new XSSFWorkbook(inputStream);
				} else {
					workbook = new HSSFWorkbook(inputStream);
				}
				Sheet firstSheet = workbook.getSheetAt(0);
				int numfiles = firstSheet.getLastRowNum() + 1; // Count starts at 0
				filenames = new String[numfiles];
		         
		        for(int i=0; i<numfiles; i++) {
		            Row row = firstSheet.getRow(i);
		            Cell cell = row.getCell(0);
		            filenames[i] = cell.getStringCellValue();
		        }
		         
		        workbook.close();
		        inputStream.close();
		        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File[] files = new File[filenames.length];
		for (int i = 0; i < filenames.length; i++) {
		   files[i] = new File(filenames[i]);
		} 
		
		return files;
    }
 
}
