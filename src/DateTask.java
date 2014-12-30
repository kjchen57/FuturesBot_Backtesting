import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import api.addEvent;


public class DateTask extends TimerTask {
	static boolean alive = true;
	static int before = 0;
	static int count = 0;
	List<Thread> threadList;
	public DateTask(List<Thread> obj) {
		this.threadList = obj;
	}
    public void run() {
    	int dead = 0;
        for(Thread t : threadList) {
        	if (t.isAlive())
        		alive = false;
        	else
        		dead += 1;
        	if (threadList.size() == dead)
        		alive = true;
        }
        System.out.println("Thread 總數量：" + threadList.size() + " Thread 結束數量：" + dead + " Count：" + count);
        if (alive) {
        	String filename = "Run_Thread"+ getTimeStampfile() + ".xls";
        	ExcelTable.getInstance().close(filename);
    		addEvent runEvent = new addEvent();
    		runEvent.GamilSender("philipzheng@gmail.com", filename);
    		System.exit(1);
        } else {
        	if (before == dead)
        		count++;
        	else
        		count = 0;
        	if (count > 10) {
        		String filename = "Run_Thread"+ getTimeStampfile() + ".xls";
            	ExcelTable.getInstance().close(filename);
        		addEvent runEvent = new addEvent();
        		runEvent.GamilSender("philipzheng@gmail.com", filename);
        		System.exit(1);
        	}
        	before = dead;
        }
    }
    
	private String getTimeStampfile() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sdf.format(date);
		return time;
	}
}