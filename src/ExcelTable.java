import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;



public class ExcelTable {

	private volatile static ExcelTable table;
	static int rownum = 1;
	POIFSFileSystem fs = null;
	HSSFWorkbook wb = null;
	HSSFSheet sheet = null;
	HSSFRow row;
	HSSFCell cell;
	List<Map<String, Double[]>> mapList =new ArrayList<Map<String, Double[]>>();
	
	private ExcelTable(){
		try {
			fs = new POIFSFileSystem(new FileInputStream("totalreport.xls"));
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = wb.getSheetAt(0);
	}
	
	public static ExcelTable getInstance(){
		if (table == null) {
			synchronized (ExcelTable.class){
				if (table == null) {
					table = new ExcelTable();
				}
			}
		}
		return table;
	}
	
	public void write(String name, double[] result) {
		Map<String, Double[]> map = new HashMap<String, Double[]>();
		Double[] D = new Double[11];
		for(int i = 0; i < result.length; i++) {
			D[i] = result[i];
		}
			
		map.put(name, D);
		mapList.add(map);
	}
	
	public void close(String filename) {
		for(Map<String, Double[]> map : mapList) {
			String name = map.keySet().toArray()[0].toString();
			Double[] D = (Double[]) map.values().toArray()[0];
			row = sheet.createRow(rownum);
			cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString(name));
			cell = row.createCell(1);
			cell.setCellValue(D[0]);
			cell = row.createCell(2);
			cell.setCellValue(D[1]);
			cell = row.createCell(3);
			cell.setCellValue(D[2]);
			cell = row.createCell(4);
			cell.setCellValue(D[3]);
			cell = row.createCell(5);
			cell.setCellValue(D[4]);
			cell = row.createCell(6);
			cell.setCellValue(D[5]);
			cell = row.createCell(7);
			cell.setCellValue(D[6]);
			cell = row.createCell(8);
			cell.setCellValue(D[7]);
			cell = row.createCell(9);
			cell.setCellValue(D[8]);
			cell = row.createCell(10);
			cell.setCellValue(D[9]);
			cell = row.createCell(11);
			cell.setCellValue(D[10]);
			rownum++;
		}
		/**/
		try {
			wb.write(new FileOutputStream("C:\\Dropbox\\" + filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ExcelTable t = ExcelTable.getInstance();
		double[] d = {1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0};
		t.write("test", d);
		t.close("test.xls");
	}

}
