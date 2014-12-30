import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class InitRegression {
	static int inputt;
	int totalvol;
	static double SGXindex;

	public static double[] init(String YYMMDD) {
		String s1;
		boolean notfound = true;
		double[] result = {0,0};
		while (notfound) {
		try {
			BufferedReader in = new BufferedReader(new FileReader("C:\\Dropbox\\" + YYMMDD + "_APItick.txt"));
			notfound = false;
			InitRegression client = new InitRegression();
			while ((s1 = in.readLine()) != null) {
				client.doit(s1);
			}
			//client.closetxt(version);
			result[0] = inputt;
			result[1] = SGXindex;
		} catch (FileNotFoundException e) {
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
			GregorianCalendar cal = new GregorianCalendar();
			Date nowdate;
			try {
				nowdate = dateFormat.parse(YYMMDD);
				cal.setTime(nowdate);
				cal.add(GregorianCalendar.DATE, -1);
				YYMMDD = dateFormat.format(cal.getTime());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return result;
	}
	
	public static double[] initNew(String YYMMDD) {
		double[] result = {0,0};
		double[] resultPre = {0,0};
		double[] Gap = {0,0,0};
		result = init(YYMMDD);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowdate;
		try {
			nowdate = dateFormat.parse(YYMMDD);
			cal.setTime(nowdate);
			cal.add(GregorianCalendar.DATE, -1);
			YYMMDD = dateFormat.format(cal.getTime());
			resultPre = init(YYMMDD);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		double SGXGap = (((result[1] / resultPre[1]) - 1)) - ((result[0] / resultPre[0]) - 1);
		Gap[0] = result[0];
		Gap[1] = result[1];
		Gap[2] = SGXGap;
		return Gap;
	}
	
	public static void init(String YYMMDD, int version) {
		String s1;
		boolean notfound = true;
		while (notfound) {
		try {
			BufferedReader in = new BufferedReader(new FileReader("C:\\Dropbox\\" + YYMMDD + "_APItick.txt"));
			notfound = false;
			InitRegression client = new InitRegression();
			while ((s1 = in.readLine()) != null) {
				client.doit(s1);
			}
			client.closetxt(version);
		} catch (FileNotFoundException e) {
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
			GregorianCalendar cal = new GregorianCalendar();
			Date nowdate;
			try {
				nowdate = dateFormat.parse(YYMMDD);
				cal.setTime(nowdate);
				cal.add(GregorianCalendar.DATE, -1);
				YYMMDD = dateFormat.format(cal.getTime());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
		
	void doit(String input) {
		String[] temp = input.split(" ");
		if (temp[1].equals("TX00")) {
			int a = Integer.parseInt(temp[2]);
			totalvol = Integer.parseInt(temp[4]);
			if (a != 0 && totalvol > 0) {
				inputt = a;
			}
			// txt.setOutput(getNowTime() + " " + a);
		} else if (temp[1].equals("TWN")) {
			SGXindex = Double.parseDouble(temp[2]);
		}
	}
	
	public void closetxt(int version){
		LogFile txtF = new LogFile("FuT" + version + ".txt");
		txtF.setOutput(inputt+"");
		txtF.close();
		LogFile txtS = new LogFile("SGXT" + version + ".txt");
		txtS.setOutput(SGXindex+"");
		txtS.close();
		GoodWindowsExec.runwait("Copy FuT" + version + ".txt FuTemp" + version + ".txt");
		GoodWindowsExec.runwait("Copy SGXT" + version + ".txt SGXTemp" + version + ".txt");
	}
	
}