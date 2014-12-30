import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class DataMap {

	static HashMap <String, List<String>> data_Map = new HashMap<String, List<String>>();
	static HashMap <String, Double> percent_Map = new HashMap<String, Double>();
	
private volatile static DataMap dataMap;
	
	private DataMap(){}
	
	public static DataMap getInstance(){
		if (dataMap == null) {
			synchronized (DataMap.class){
				if (dataMap == null) {
					dataMap = new DataMap();
				}
			}
		}
		return dataMap;
	}
	
	public List<String> getTickData(String day) {
		List<String> data = data_Map.get(day);
		if (data == null) {
			data = new ArrayList<String>();
			String s1;
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader("C:\\Dropbox\\" + day + "_APItick.txt"));
				while ((s1 = in.readLine()) != null) {
					data.add(s1);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			data_Map.put(day, data);
		}
		return data;
	}
	
	public Double getPercentData(String day) {
		Double data = percent_Map.get(day);
		if (data == null) {
			String s1;
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader("C:\\Dropbox\\" + day + "Percent.txt"));
				while ((s1 = in.readLine()) != null) {
					data = Double.valueOf(s1);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			percent_Map.put(day, data);
		}
		return data;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
