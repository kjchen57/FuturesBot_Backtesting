import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import api.addEvent;



public class Run_thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String today = GetWednesday.gettoday();
		if (args.length != 0) {
			for (String input : args) {
				if (input.length() > 2) {
					String option = input.substring(0, 2);
					if (option.equals("-E") || option.equals("-e")) {
						today = input.substring(2);
					}
				}
			}
		}
		Properties prop = new Properties();
		//load a properties file
		try {
			prop.load(new FileInputStream("Tconfig.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //get the property value and print it out
		int rangeS = Integer.parseInt(prop.getProperty("rangeS"));
		int rangeE = Integer.parseInt(prop.getProperty("rangeE"));
		int SGXGapAS = Integer.parseInt(prop.getProperty("SGXGapAS"));
		int SGXGapAE = Integer.parseInt(prop.getProperty("SGXGapAE"));
		int SGXGap34S = Integer.parseInt(prop.getProperty("SGXGap34S"));
		int SGXGap34E = Integer.parseInt(prop.getProperty("SGXGap34E"));
		int SGXGapBS = Integer.parseInt(prop.getProperty("SGXGapBS"));
		int SGXGapBE = Integer.parseInt(prop.getProperty("SGXGapBE"));
		int SGXGapLS = Integer.parseInt(prop.getProperty("SGXGapLS"));
		int SGXGapLE = Integer.parseInt(prop.getProperty("SGXGapLE"));
		int SGXGapLinS = Integer.parseInt(prop.getProperty("SGXGapLinS"));
		int SGXGapLinE = Integer.parseInt(prop.getProperty("SGXGapLinE"));
		int OffsetS = Integer.parseInt(prop.getProperty("OffsetS"));
		int OffsetE = Integer.parseInt(prop.getProperty("OffsetE"));
		int OIGapS = Integer.parseInt(prop.getProperty("OIGapS"));
		int OIGapE = Integer.parseInt(prop.getProperty("OIGapE"));
		
		int initpreSettle = 0;
		double initSGXPreSettle = 0;
		double initSGXPreGap = 0;
		
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd"); 
		Date nowdate = null;
		Date startdate = null;
		Date enddate = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		try {
			nowdate=dateFormat.parse(today);
			cal.setTime(nowdate);
			cal.add(GregorianCalendar.MONTH, -1);
			double[] init_para = InitRegression.initNew(dateFormat.format(GetWednesday.getYesterday(cal.getTime())));
			initpreSettle = (int)init_para[0];
			initSGXPreSettle = init_para[1];
			initSGXPreGap = init_para[2];
			nowdate = cal.getTime();
			startdate = nowdate;
			enddate = dateFormat.parse(today);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date predate = startdate;
		while (!enddate.before(predate)){
			String YYMMDD = dateFormat.format(predate);
			//YYMMDD = GetWednesday.gettoday();
			DataMap.getInstance().getTickData(YYMMDD);
			DataMap.getInstance().getPercentData(YYMMDD);
			cal.add(GregorianCalendar.DATE, 1);
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				cal.add(GregorianCalendar.DATE, 1);
			/*else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				cal.add(GregorianCalendar.DATE, 2);*/
			predate = cal.getTime();
		}
		
		int count = 0;
		List<Thread> threadList = new ArrayList<Thread>();
		
		Timer timer = new Timer();
		DateTask dt = new DateTask(threadList);
		timer.scheduleAtFixedRate(dt, 60000, 60000);
		
		for (int x = SGXGapAS; x <= SGXGapAE; x += 2) { // 12~22
			for (int y = SGXGap34S; y <= SGXGap34E; y += 2) { // 12~22
				for (int z = SGXGapBS; z <= SGXGapBE; z += 2) { // 22~34
					for (int m = SGXGapLS; m <= SGXGapLE; m += 2) { // 10~18
						for (int n = SGXGapLinS; n <= SGXGapLinE; n += 2) { // 8~16
							for (int r = rangeS; r <= rangeE; r += 2) {
								for (int s = OffsetS; s <= OffsetE; s += 2) {
									for (int t = OIGapS; t <= OIGapE; t += 5) {
									NewDdeClient_ThreadMem mem = new NewDdeClient_ThreadMem(
											x, y, z, m, n, r, s, t, startdate,
											enddate);
									mem.setName("Thread_" + x  + "_"+ y + "_" + z
											+ "_" + m + "_" + n + "_" + r + "_" + s + "_" + t);
									mem.setinputt(initpreSettle);
									mem.setSGXindex(initSGXPreSettle);
									mem.setPreGap(initSGXPreGap);
									// t.setPriority(10);
									mem.start();
									count++;
									if ((count % 16) == 0)
										try {
											mem.join();
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									threadList.add(mem);
									}
								}
							}
						}
					}
				}
			}
		}
		
		String filename = "Run_Thread"+ getTimeStampfile() + ".xls";
    	ExcelTable.getInstance().close(filename);
		addEvent runEvent = new addEvent();
		runEvent.GamilSender("YOUR_EMAIL", filename);
		System.exit(1);
		
		/*List<Thread> threadList = new ArrayList<Thread>();
		for(int r = 1; r <=10 ; r++) {
			HelloThread t1 = new HelloThread();
			t1.setName("T" + r);
			t1.setPriority(10);
			t1.start();
			threadList.add(t1);
		}
	    // 取得目前執行緒數量
	    System.out.println(Thread.activeCount()); */

	}
	
	private static String getTimeStampfile() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String time = sdf.format(date);
		return time;
	}

}
