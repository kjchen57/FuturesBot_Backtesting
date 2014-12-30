import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import excel.GenExcel;

public class NewDdeClient_ThreadMem extends Thread{
	int current = 0;
	Queue<Integer> queue = new LinkedList<Integer>();
	List<Integer> ls = new LinkedList<Integer>();
	int size = 400;
	int lsize = 25;
	int[] price = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0 };
	int sval = 30;
	int svalA = sval;
	int wval = 30;
	int wvalA = wval;
	int win = 0;
	int lost = 0;
	int total = 0;
	int range = 18; // 變動範圍值
	//int flag = 0;
	int high = 0;
	int low = 0;
	int higho = 0;
	int lowo = 0;
	//int posarrive = 300;
	int negarrive = -50;
	//int count = 0;
	int preSettle;
	double HLpercent;
	double nowpercent;
	double percent = 0.0; //指標機率
	int multiple = 2;
	String version = "";
	int inputt;
	boolean runflag = true;
	//LogFile txt;
	int hlflag = 0;
	int tsize = price.length;
	int abscurrent = 0;
	static String futuressignals = "小台指,MXF,";
	static final String futuressignal = "台指期,TXF,";
	int tsize1 = 2;
	int currentmulti = 1;
	String YYMMDD;
	int vol;
	int totalvol;
	double TSLD;
	//int opweek;  //下緣
	//int opweek1; //上緣
	boolean close = false;
	boolean SGXclose = false;
	//int week = 0;
	boolean isOpen = false;
	boolean isLowM = true;
	double kspercent = 0;
	double ksPreSettle = 0;
	Futures fu;
	int Gapvolin = 15;
	int GapvoloutL = 13;
	int GapvoloutH = 18;
	int fuindex;
	double volpro;
	int lowMFlag = 0;
	String nowtime;
	SGXindex sgx;
	double SGXGap = 0.0020;
	double SGXGapA = 0.0010;
	double SGXGapB = 0.0022;
	double SGXGapC = 0.0022;
	double SGXGapL = 0.0012;
	double SGXGapLin = 0.0010;
	double SGXGap34 = 0.0014;
	double SGXGap56 = 0.005;
	double SGXpercent = 0;
	double SGXPreSettle = 0;
	double SGXindex = 0;
	static int lowmcountT = 9;
	int lowmcountB = 0;
	int lowmcountS = 0;
	int HighLowtmp = 0;
	double lowoutgap = 0.0025;
	boolean intoflag = true;
	int countSize = 100;
	int counter = 0;
	int counterPos = 0;
	int svalT = 40;
	int lowmcount = 0;
	double Offset;
	int offset_i;
	DataMap datamap = DataMap.getInstance();
	int daycount = 0;
	GenExcel gendata;
	ExcelTable table = ExcelTable.getInstance();
	Date startdate;
	Date enddate;
	int SGXGapA_i;
	int SGXGap34_i;
	int SGXGapB_i;
	int SGXGapL_i;
	int SGXGapLin_i;
	int OIGap_i;
	double SGXPreGap = 0;
	boolean dangflag = false;
	
	/*
	 * 先利用Regression.SGXTickTransfrom or SGXTickTransfrom1來轉換成複合台指期跟摩台期的TXT檔，再執行這回測。
	 */
	public void run() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(startdate);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd"); 
		String name = "FuturesSGXGapA" + this.getSGXGapA() + "SGXGap34" + this.getSGXGap34() + "SGXGapB" + this.getSGXGapB() + "SGXGapL" + this.getSGXGapL() + "SGXGapLin" + this.getSGXGapLin() + "Range" + this.getRange() + "Offset" + this.getOffset() + "OIGap" + this.getOIGap(); 
		//new File("C:\\Runtime\\" + name).mkdir();
		gendata = new GenExcel();
		while (!enddate.before(startdate)){
			
		
		YYMMDD = dateFormat.format(startdate);
		//YYMMDD = GetWednesday.gettoday();
		List<String> data = datamap.getTickData(YYMMDD);
		if (!data.isEmpty()) {
			double OIpercent = datamap.getPercentData(YYMMDD);
			if (SGXPreGap > 0.002) {
				Offset = 0.001;
			} else if (SGXPreGap < -0.002) { 
				Offset = -0.001;
			} else 
				Offset = 0;
			Offset = Offset + (OIpercent * OIGap_i * 0.0001);
			preSettle = inputt;
			SGXPreSettle = SGXindex;
			this.init(YYMMDD);
			//this.setFileName(name);
			daycount++;
			for (String s1 : data) {
				this.doit(s1);
			}
			this.closetxt();
		}
		
		cal.add(GregorianCalendar.DATE, 1);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			cal.add(GregorianCalendar.DATE, 1);
		/*else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			cal.add(GregorianCalendar.DATE, 2);*/
		startdate = cal.getTime();
		}
		System.out.println(this.getName() + ": Before write Excel!");
		gendata.setDaycount(daycount);
		double[] result = gendata.writeData();
		table.write(name, result);
		System.out.println(this.getName() + ": After write Excel!");
	}
		
	public NewDdeClient_ThreadMem(int SGXGapA, int SGXGap34, int SGXGapB, int SGXGapL, int SGXGapLin, int SGXGapVersion, int offset, int OIGap, Date start, Date end){
		this.setSGXGapA(SGXGapA);
		this.setSGXGap34(SGXGap34);
		this.setSGXGapB(SGXGapB);
		this.setSGXGapL(SGXGapL);
		this.setSGXGapLin(SGXGapLin);
		this.setRange(SGXGapVersion);
		this.setOffset(offset);
		this.setOIGap(OIGap);
		this.startdate = start;
		this.enddate = end;
	}
	
	public void init(String input) {
		current = 0;
		queue = new LinkedList<Integer>();
		ls = new LinkedList<Integer>();
		int[] price_new = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0 };
		price = price_new;
		sval = 30;
		svalA = sval;
		wval = 30;
		wvalA = wval;
		win = 0;
		lost = 0;
		total = 0;
		high = 0;
		low = 0;
		higho = 0;
		lowo = 0;
		negarrive = -50;
		HLpercent = 0;
		nowpercent = 0;
		percent = 0.0; //指標機率
		inputt = 0;
		runflag = true;
		hlflag = 0;
		abscurrent = 0;
		tsize1 = 2;
		currentmulti = 1;
		TSLD = 0;
		close = false;
		SGXclose = false;
		isOpen = false;
		isLowM = true;
		kspercent = 0;
		ksPreSettle = 0;
		Gapvolin = 15;
		GapvoloutL = 13;
		GapvoloutH = 18;
		lowMFlag = 0;
		nowtime = "";
		SGXpercent = 0;
		SGXindex = 0;
		lowmcountB = 0;
		lowmcountS = 0;
		HighLowtmp = 0;
		lowoutgap = 0.0025;
		intoflag = true;
		counter = 0;
		counterPos = 0;
		svalT = 40;
		lowmcount = 0;
		totalvol = 0;
		dangflag = false;
		
		YYMMDD = input;		
		futuressignals = futuressignals + GetWednesday.compareWed1() + ",A";
		
		//opday = GetWednesday.getOpday(YYMMDD);
		
		//YYMMDD = GetWednesday.gettoday();
		if (YYMMDD.equals(GetWednesday.compareWed2(YYMMDD)))
			close = true;
		if (YYMMDD.equals(GetWednesday.getSGXClose(YYMMDD)))
			SGXclose = true;
	}
	
	void doit(String input) {
		String[] temp = input.split(" ");
		nowtime = temp[0];
		if (temp[1].equals("TX00")) {
			int a = Integer.parseInt(temp[2]);
			totalvol = Integer.parseInt(temp[4]);
			if (a != 0 && totalvol > 0) {
				vol = Integer.parseInt(temp[3]);
				detect(a);
				if (runflag) {
					add(a);
					if (isOpen && intoflag) {
						check(a);
						if (counterPos != 0)
							counter++;
					}
					checkneg(a);
					checkTotal(a);
					check_runtime();
				}
				inputt = a;
			}
			// txt.setOutput(getNowTime() + " " + a);
		} else if (temp[1].equals("TSLD") && totalvol > 0) {
			TSLD = Double.parseDouble(temp[2]);
		} else if (temp[1].equals("KOSPI")) {
			double ksindex = Double.parseDouble(temp[2]);
			if (ksindex != 0)
				kspercent = (ksindex / ksPreSettle) - 1;
		} else if (temp[1].equals("TWN")) {
			SGXindex = Double.parseDouble(temp[2]);
			if (SGXindex != 0)
				SGXpercent = (SGXindex / SGXPreSettle) - 1;
			else
				SGXpercent = 0;
		}
	}

	public void writetxt(int input) {
		gendata.addTimes();
		/*boolean result = OMSignAPI.INSTANCE.GoOrder("MTX002", futuressignals,
				getNowTime(), current, input);
		if (!result) {
			System.out.println("OMSignAPI GoOrder Error!!");
			c.alert(botname, Email, "OMSignAPI GoOrder Error!!");
			c.disconnect();
		}
		txt.setOutput(getNowTime() + " current:" + current + ", price:" + input);
		System.out.println("current:" + current + ", price:" + input);*/
	}

	public void check(int input) {
		double percent = Math.abs(nowpercent);
		if (0.01 > percent) {
			multiple = 2;
			// range = 11;
			//sval = 30;
		}
		if ((0.02 > percent) && (percent > 0.01)) {
			multiple = 3;
			dangflag = true;
			// range = 12;
			//sval = 25;
		}
		if ((0.03 > percent) && (percent > 0.02)) {
			multiple = 4;
			dangflag = true;
			// range = 13;
			//sval = 30;
		}
		if ((0.04 > percent) && (percent > 0.03)) {
			multiple = 5;
			dangflag = true;
			intoflag = false;
			// range = 14;
			//sval = 35;
		}
		if ((0.05 > percent) && (percent > 0.04)) {
			multiple = 6;
			dangflag = true;
			intoflag = false;
			// range = 15;
			//sval = 40;
		}
		if ((0.06 > percent) && (percent > 0.05)) {
			multiple = 7;
			dangflag = true;
			intoflag = false;
			// range = 16;
			//sval = 45;
		}
		if (percent > 0.06) {
			multiple = 8;
			dangflag = true;
			intoflag = false;
			// range = 17;
			//sval = 50;
		}

		if (current == 0) {
			if (lowMFlag == 0)
				checkin(input);
			else
				lowMcheckin(input);
		} else {
			checkout(input);
		}
	}

	public void add(int input) {
		queue.add(input);
		if (queue.size() >= size) {
			Object[] I = queue.toArray();
			Integer[] w = dwt(I);
			ls = Arrays.asList(w);
			queue.remove();
		}
	}
	
	private void detect(int input) {
		if (high == 0)
			high = input;
		if (low == 0)
			low = input;
		if (input > high)
			high = input;
		if (input < low)
			low = input;
		if (higho == 0) {
			if (isLowM) {
				higho = input + (10 * multiple);
			} else {
				higho = input + (15 * multiple);
			}
		}
		if (lowo == 0) {
			if (isLowM) {
				lowo = input - (10 * multiple);
			} else {
				lowo = input - (15 * multiple);
			}
		}
		if (input > higho) {
			higho = input;
			if ((hlflag == 0) && ((current > 0) || lowMFlag == -1)) {
				hlflag = 1;
			}
		}
		if (input < lowo) {
			lowo = input;
			if ((hlflag == 0) && ((current < 0) || lowMFlag == 1)) {
				hlflag = -1;
			}
		}
		double p1 = new Double(input).doubleValue();
		double p2 = new Double(preSettle).doubleValue();
		nowpercent = (p1 / p2) - 1;
		double p3 = new Double(high).doubleValue();
		double p4 = new Double(low).doubleValue();
		HLpercent = (p3 / p4) - 1;
		double p5 = new Double(higho).doubleValue();
		double p6 = new Double(lowo).doubleValue();
		double HLOpercent = (p5 / p6) - 1;
		if (HLOpercent > 0.009) {
			lowoutgap = 0.0025;
		} else {
			lowoutgap = 0.0050;
		}
		//percent = ProCalculator();
	}

/*	private int[] getQueryMaxMin(int input) {
		Object[] t = ls.toArray();
		Arrays.sort(t);
		List<Object> list = Arrays.asList(t);
		int min = (Integer) list.get(0);
		int max = (Integer) list.get(lsize - 1);
		int[] pair = { min, max };
		return pair;
	}*/
	
	private int[] getQueryModel() {
		Object[] t = ls.toArray();
		Arrays.sort(t);
		List<Object> list = Arrays.asList(t);
		int min = (Integer) list.get(0);
		int max = (Integer) list.get(lsize - 1);
		int minpos = ls.lastIndexOf(min);
		int maxpos = ls.lastIndexOf(max);
		/*int q1 = ls.get(0);
		int q2 = ls.get(lsize/5);
		int q3 = ls.get(lsize/5*2);
		int q4 = ls.get(lsize/5*3);
		int q5 = ls.get(lsize/5*4);
		int q6 = ls.get(lsize-1);*/
		int vol = max - min;
		int[] pair = { 0, vol };
		if (vol > range) {
			if ((5 >= minpos) && (maxpos >= 20)) {
				pair[0] = 1;
			} else if ((5 >= maxpos) && (minpos >= 20)) {
				pair[0] = 2;
			}
			if ((16 <= minpos) && (minpos <= 20) && (maxpos >= 23)) {
				pair[0] = 3;
			} else if ((16 <= maxpos) && (maxpos <= 20) && (minpos >= 23)) {
				pair[0] = 4;
			}
		}
		return pair;
	}

	private void checkin(int input) {
		if (ls.size() >= lsize) {
			int[] pair = getQueryModel();
			if (pair[0] != 0) {
				if (pair[0] == 1 && !dangflag) {
					if (((SGXTWGap() + Offset) - SGXGapA) > 0 && !SGXclose) {
						boolean into = true;
						if (percent <= -1)
							into = false;
						if (into) {
							current = 1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(input);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							//txt.setOutput(getNowTime() + " max1:" + input + " " + current);
							lowo = input;
							higho = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) + SGXGapLin) < 0) {
						lowMFlag = -1;
						higho = input;
						lowo = 0;
						isLowM = true;
					}
				} else if (pair[0] == 2) {
					if (((SGXTWGap() + Offset) + SGXGapA) < 0 && !SGXclose) {
						boolean into = true;
						if (percent >= 1)
							into = false;
						if (into) {
							current = -1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(input);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							//txt.setOutput(getNowTime() + " min1:" + input + " " + current);
							higho = input;
							lowo = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) - SGXGapLin) > 0) {
						lowMFlag = 1;
						lowo = input;
						higho = 0;
						isLowM = true;
					}
				} else if (pair[0] == 3 && !dangflag) {
					if (((SGXTWGap() + Offset) - SGXGap34) > 0) {
						boolean into = true;
						if (percent <= -1)
							into = false;
						if (into) {
							current = 1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(input);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							//txt.setOutput(getNowTime() + " max2:" + input + " "	+ current);
							lowo = input;
							higho = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) + SGXGapLin) < 0) {
						lowMFlag = -1;
						higho = input;
						lowo = 0;
						isLowM = true;
					}
				} else if (pair[0] == 4) {
					if (((SGXTWGap() + Offset) + SGXGap34) < 0) {
						boolean into = true;
						if (percent >= 1)
							into = false;
						if (into) {
							current = -1 * currentmulti;
							abscurrent = Math.abs(current);
							writetxt(input);
							for (int i = 0; i < abscurrent; i++)
								price[i] = input;
							// range += 2;
							//txt.setOutput(getNowTime() + " min2:" + input + " "	+ current);
							higho = input;
							lowo = 0;
							hlflag = 0;
							isLowM = false;
						}
					} else if (((SGXTWGap() + Offset) - SGXGapLin) > 0) {
						lowMFlag = 1;
						lowo = input;
						higho = 0;
						isLowM = true;
					}
				}
			} else if (pair[0] == 0) {
				checkSGXGapB(input," sgx");
			}
		}
	}
	
	public void checkSGXGapB(int input, String text) {
		if (Math.abs(kspercent) > 0.01 || !close) {
			if (!SGXclose && intoflag) {
				if (((SGXTWGap() + Offset) - SGXGapB) > 0) {
					boolean into = true;
					if (percent <= -1)
						into = false;
					if (into) {
						counterPos++;
						if (counter > countSize) {
							if (counterPos > (countSize * 3 / 4)) {
								if (((SGXTWGap() + Offset) - (SGXGapB * 2)) > 0) {
									current = 2 * currentmulti;
								} else {
									current = 1 * currentmulti;
								}
								abscurrent = Math.abs(current);
								writetxt(input);
								for (int i = 0; i < abscurrent; i++)
									price[i] = input;
								// range += 2;
								//txt.setOutput(getNowTime() + text + "b:" + input + " " + current);
								lowo = input;
								higho = 0;
								hlflag = 0;
								isLowM = false;
								intoflag = false;
								counter = 0;
								counterPos = 0;
							} else {
								counter = 0;
								counterPos = 0;
							}
						}
					}
				} else if (((SGXTWGap() + Offset) + SGXGapB) < 0) {
					boolean into = true;
					if (percent >= 1)
						into = false;
					if (into) {
						counterPos--;
						if (counter > countSize) {
							if (counterPos < (countSize * -3 / 4)) {
								if (((SGXTWGap() + Offset) + (SGXGapB * 2)) < 0) {
									current = -2 * currentmulti;
								} else {
									current = -1 * currentmulti;
								}
								abscurrent = Math.abs(current);
								writetxt(input);
								for (int i = 0; i < abscurrent; i++)
									price[i] = input;
								// range += 2;
								//txt.setOutput(getNowTime() + text+ "s:"	+ input + " " + current);
								higho = input;
								lowo = 0;
								hlflag = 0;
								isLowM = false;
								intoflag = false;
								counter = 0;
								counterPos = 0;
							} else {
								counter = 0;
								counterPos = 0;
							}
						}
					}
				}
			}
		}
	}
	
	private void lowMcheckin(int input) {
		if ((input >= (lowo + Gapvolin)) && hlflag == -1) {
			if (((SGXTWGap() + Offset) + SGXGapC) < 0) { // 此檢核有時反而阻擋進場時機
				lowMFlag = 0;
				isLowM = false;
				HighLowtmp = 0;
				lowmcountB = 0;
				lowmcount = 0;
			} else if (input < (high - 20) && ((SGXTWGap() + Offset) - SGXGapL) > 0) {
				boolean into = true;
				if (percent <= -1)
					into = false;
				if (into) {
					if (lowmcountB > lowmcountT) {
						current = 1 * currentmulti;
						abscurrent = Math.abs(current);
						writetxt(input);
						for (int i = 0; i < abscurrent; i++)
							price[i] = input;
						//txt.setOutput(getNowTime() + " LOWM BUY:" + input + " "	+ current);
						hlflag = 0;
						lowmcountB = 0;
						lowmcount = 0;
					} else {
						lowmcountB++;
						if (lowmcountB == 1) {
							HighLowtmp = lowo;
						} else if (lowo < HighLowtmp) {
							lowMFlag = 0;
							isLowM = true;
							HighLowtmp = 0;
							lowmcountB = 0;
							lowmcount = 0;
						}
					}
				}
			} else if (((SGXTWGap() + Offset) - 0.0004) < 0) {
				lowmcount++;
				if (lowmcount > countSize) {
					lowMFlag = 0;
					isLowM = true;
					HighLowtmp = 0;
					lowmcountB = 0;
					lowmcount = 0;
				}
			}
		} else if ((input <= (higho - Gapvolin)) && hlflag == 1) {
			if (((SGXTWGap() + Offset) - SGXGapC) > 0) {
				lowMFlag = 0;
				isLowM = false;
				HighLowtmp = 0;
				lowmcountS = 0;
				lowmcount = 0;
			} else if (input > (low + 20) && ((SGXTWGap() + Offset) + SGXGapL) < 0) {
				boolean into = true;
				if (percent >= 1)
					into = false;
				if (into) {
					if (lowmcountS > lowmcountT) {
						current = -1 * currentmulti;
						abscurrent = Math.abs(current);
						writetxt(input);
						for (int i = 0; i < abscurrent; i++)
							price[i] = input;
						//txt.setOutput(getNowTime() + " LOWM SELL:" + input + " " + current);
						hlflag = 0;
						lowmcountS = 0;
						lowmcount = 0;
					} else {
						lowmcountS++;
						if (lowmcountS == 1) {
							HighLowtmp = higho;
						} else if (higho < HighLowtmp) {
							lowMFlag = 0;
							isLowM = true;
							HighLowtmp = 0;
							lowmcountS = 0;
							lowmcount = 0;
						}
					}
				}
			} else if (((SGXTWGap() + Offset) + 0.0004) > 0) {
				lowmcount++;
				if (lowmcount > countSize) {
					lowMFlag = 0;
					isLowM = true;
					HighLowtmp = 0;
					lowmcountS = 0;
					lowmcount = 0;
				}
			}
		}
		checkSGXGapB(input," sgx1");
	}

	private void checkout(int input) {
		if (isLowM) {
			LowMcheckout(input);
		} else {
			if (ls.size() >= lsize) {
				int[] pair = getQueryModel();
				if (pair[0] != 0) {
					if (pair[0] == 1) {
						checkout1(input, true);
					} else if (pair[0] == 2) {
						checkout1(input, false);
					} else if (pair[0] == 3) {
						checkout2(input, true);
					} else if (pair[0] == 4) {
						checkout2(input, false);
					}
				}
			}
			HighMcheckout(input);
		}
	}
	
	private void LowMcheckout(int input) {
		if (current > 0) {
			if ((SGXTWGap() - SGXGap) <= 0) {
				if (getHighLowPro(true) > lowoutgap) {
					if ((input <= (higho - GapvoloutL)) && hlflag == 1) { // 判斷是否高點回檔
						// 多方停利
						for (int i = 0; i < abscurrent; i++) {
							if (input > price[i]) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
								gendata.addWincount();
							} else {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
								gendata.addLostcount();
							}
						}
						init();
						current = 0;
						writetxt(input);
						queue.clear();
					}
				} else {
					if ((SGXTWGap() + 0.0016) < 0) {
						if (SGXTWGap() < -0.0013)
							wval = wvalA - (int) (SGXTWGap() / -0.0011) * 5;
						if (input > (price[abscurrent - 1] + wval)) {
							for (int i = 0; i < abscurrent; i++) {
								if (input > price[i]) {
									win = win + (input - price[i]);
									total = total + (input - price[i]);
									gendata.addWincount();
								} else {
									lost = lost + (price[i] - input);
									total = total - (price[i] - input);
									gendata.addLostcount();
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				}
			}
		} else if (current < 0) {
			if ((SGXTWGap() + SGXGap) >= 0) {
				if (getHighLowPro(false) > lowoutgap) {
					if ((input >= (lowo + GapvoloutL)) && hlflag == -1) { // 判斷是否低點回檔
						// 空方停損
						for (int i = 0; i < abscurrent; i++) {
							if (input < price[i]) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
								gendata.addWincount();
							} else {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
								gendata.addLostcount();
							}
						}
						init();
						current = 0;
						writetxt(input);
						queue.clear();
					}
				} else {
					if ((SGXTWGap() - 0.0016) > 0) {
						if (SGXTWGap() > 0.0013)
							wval = wvalA - (int) (SGXTWGap() / 0.0011) * 5;
						if (input < (price[abscurrent - 1] - wval)) {
							for (int i = 0; i < abscurrent; i++) {
								if (input < price[i]) {
									win = win + (price[i] - input);
									total = total + (price[i] - input);
									gendata.addWincount();
								} else {
									lost = lost + (input - price[i]);
									total = total - (input - price[i]);
									gendata.addLostcount();
								}
							}
							init();
							current = 0;
							writetxt(input);
							queue.clear();
						}
					}
				}
			}
		}
	}
	
	private void HighMcheckout(int input) {
		if (current > 0) {
			if ((SGXTWGap() - SGXGap) <= 0 ) {
			if (getHighLowPro(true) > 0.008) {
				if ((input <= (higho - GapvoloutH)) && hlflag == 1) { // 判斷是否高點回檔
					if (input > (price[abscurrent - 1] + sval)) {
						// 多方停利
						for (int i = 0; i < abscurrent; i++) {
							if (input > price[i]) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
								gendata.addWincount();
							} else {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
								gendata.addLostcount();
							}
						}
						init();
						current = 0;
						writetxt(input);
						queue.clear();
					}
				}
			} else {
				if ((SGXTWGap() + 0.0012) < 0) {
				if (input > (price[abscurrent - 1] + sval)) {
					// 多方停利
					for (int i = 0; i < abscurrent; i++) {
						if (input > price[i]) {
							win = win + (input - price[i]);
							total = total + (input - price[i]);
							gendata.addWincount();
						} else {
							lost = lost + (price[i] - input);
							total = total - (price[i] - input);
							gendata.addLostcount();
						}
					}
					init();
					current = 0;
					writetxt(input);
					queue.clear();
				}
				}
			}
			}
		} else if (current < 0) {
			if ((SGXTWGap() + SGXGap) >= 0) {
			if (getHighLowPro(false) > 0.008) {
				if ((input >= (lowo + GapvoloutH)) && hlflag == -1) { // 判斷是否低點回檔
					if (input < (price[abscurrent - 1] - sval)) {
						// 空方停損
						for (int i = 0; i < abscurrent; i++) {
							if (input < price[i]) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
								gendata.addWincount();
							} else {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
								gendata.addLostcount();
							}
						}
						init();
						current = 0;
						writetxt(input);
						queue.clear();
					}
				}
			} else {
				if ((SGXTWGap() - 0.0012) > 0) {
				if (input < (price[abscurrent - 1] - sval)) {
					// 空方停損
					for (int i = 0; i < abscurrent; i++) {
						if (input < price[i]) {
							win = win + (price[i] - input);
							total = total + (price[i] - input);
							gendata.addWincount();
						} else {
							lost = lost + (input - price[i]);
							total = total - (input - price[i]);
							gendata.addLostcount();
						}
					}
					init();
					current = 0;
					writetxt(input);
					queue.clear();
				}
				}
			}
		}
		}
	}
	
	private void checkout1(int input, boolean up) {
		if (current > 0) {
			if ((input <= (higho - GapvoloutH)) && hlflag == 1 && (SGXTWGap() - SGXGap56) < 0) { // 判斷是否高點回檔
				if (input <= price[0]) { // 多方停損
					current = 0;
					writetxt(input);
					gendata.addLostcount();
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (price[i] - input);
						total = total - (price[i] - input);
					}
					init();
				} else {
					current = 0;
					writetxt(input);
					gendata.addWincount();
					for (int i = 0; i < abscurrent; i++) {
						win = win + (input - price[i]);
						total = total + (input - price[i]);
					}
					init();
				}
			} else {
				if (!up) {
					if (!((SGXTWGap() - 0.0012) > 0)) {
						if (input <= price[0]) { // 多方停損
							current = 0;
							writetxt(input);
							gendata.addLostcount();
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
							}
							init();
						} else {
							current = 0;
							writetxt(input);
							gendata.addWincount();
							for (int i = 0; i < abscurrent; i++) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
							}
							init();
						}
					}
				} else {
					if ((SGXTWGap() - 0.0026) > 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(input);
							//txt.setOutput(getNowTime() + " max3:" + input + " " + current);
							hlflag = 0;
						}
					}
				}
			}
		} else if (current < 0) {
			if ((input >= (lowo + GapvoloutH)) && hlflag == -1 && (SGXTWGap() + SGXGap56) > 0) { // 判斷是否低點回檔
				if (input >= price[0]) { // 空方停損
					current = 0;
					writetxt(input);
					gendata.addLostcount();
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (input - price[i]);
						total = total - (input - price[i]);
					}
					init();
				} else {
					current = 0;
					writetxt(input);
					gendata.addWincount();
					for (int i = 0; i < abscurrent; i++) {
						win = win + (price[i] - input);
						total = total + (price[i] - input);
					}
					init();
				}
			} else {
				if (up) {
					if (!((SGXTWGap() + 0.0012) < 0)) {
						if (input >= price[0]) { // 空方停損
							current = 0;
							writetxt(input);
							gendata.addLostcount();
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
							}
							init();
						} else {
							current = 0;
							writetxt(input);
							gendata.addWincount();
							for (int i = 0; i < abscurrent; i++) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
							}
							init();
						}
					}
				} else {
					if ((SGXTWGap() + 0.0026) < 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (-1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(input);
							//txt.setOutput(getNowTime() + " min3:" + input + " " + current);
							hlflag = 0;
						}
					}
				}
			}
		}
	}
	
	private void checkout2(int input, boolean up) {
		if (current > 0) {
			if ((input <= (higho - GapvoloutH)) && hlflag == 1 && (SGXTWGap() - SGXGap56) < 0) { // 判斷是否高點回檔
				if (input <= price[0]) { // 多方停損
					current = 0;
					writetxt(input);
					gendata.addLostcount();
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (price[i] - input);
						total = total - (price[i] - input);
					}
					init();
				} else {
					current = 0;
					writetxt(input);
					gendata.addWincount();
					for (int i = 0; i < abscurrent; i++) {
						win = win + (input - price[i]);
						total = total + (input - price[i]);
					}
					init();
				}
			} else {
				if (!up) {
					if (!((SGXTWGap() - 0.0012) > 0)) {
						if (input <= price[0]) { // 多方停損
							current = 0;
							writetxt(input);
							gendata.addLostcount();
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
							}
							init();
						} else {
							current = 0;
							writetxt(input);
							gendata.addWincount();
							for (int i = 0; i < abscurrent; i++) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
							}
							init();
						}
					}
				} else {
					if ((SGXTWGap() - 0.0014) > 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(input);
							//txt.setOutput(getNowTime() + " max4:" + input + " "	+ current);
							hlflag = 0;
						}
					}
				}
			}
		} else if (current < 0) {
			if ((input >= (lowo + GapvoloutH)) && hlflag == -1 && (SGXTWGap() + SGXGap56) > 0) { // 判斷是否低點回檔
				if (input >= price[0]) { // 空方停損
					current = 0;
					writetxt(input);
					gendata.addLostcount();
					for (int i = 0; i < abscurrent; i++) {
						lost = lost + (input - price[i]);
						total = total - (input - price[i]);
					}
					init();
				} else {
					current = 0;
					writetxt(input);
					gendata.addWincount();
					for (int i = 0; i < abscurrent; i++) {
						win = win + (price[i] - input);
						total = total + (price[i] - input);
					}
					init();
				}
			} else {
				if (up) {
					if (!((SGXTWGap() + 0.0012) < 0)) {
						if (input >= price[0]) { // 空方停損
							current = 0;
							writetxt(input);
							gendata.addLostcount();
							for (int i = 0; i < abscurrent; i++) {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
							}
							init();
						} else {
							current = 0;
							writetxt(input);
							gendata.addWincount();
							for (int i = 0; i < abscurrent; i++) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
							}
							init();
						}
					}
				} else {
					if ((SGXTWGap() + 0.0014) < 0) {
						if (Math.abs(current) < tsize1) {
							int newcurrent = current + (-1 * currentmulti);
							for (int i = Math.abs(current); i < Math
									.abs(newcurrent); i++)
								price[i] = input;
							current = newcurrent;
							abscurrent = Math.abs(current);
							writetxt(input);
							//txt.setOutput(getNowTime() + " min4:" + input + " "	+ current);
							hlflag = 0;
						}
					}
				}
			}
		}
	}

	private void checkneg(int input) {
		if (current != 0) {
			if (current > 0) {
				if (SGXTWGap() > 0)
					sval = svalA + (int)(SGXTWGap() / 0.0012) * 5;
				if (!(SGXTWGap() > 0.00248)) {
					if (input <= (price[abscurrent - 1] - sval)) { // 多方停損
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input > price[i]) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
								gendata.addWincount();
							} else {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
								gendata.addLostcount();
							}
						}
						init();
						//txt.setOutput(getNowTime() + " BUYLN! cost:" + input);
						sval = 30;
					}
				} else if (input <= (price[abscurrent - 1] - svalT)) { // 多方停損
					int wint = 0;
					int lostt = 0;
					int totalt = total;
					for (int i = 0; i < abscurrent; i++) {
						if (input > price[i]) {
							wint = wint + (input - price[i]);
							totalt = totalt + (input - price[i]);
						} else {
							lostt = lostt + (price[i] - input);
							totalt = totalt - (price[i] - input);
						}
					}
					if (totalt <= negarrive) {
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input > price[i]) {
								win = win + (input - price[i]);
								total = total + (input - price[i]);
								gendata.addWincount();
							} else {
								lost = lost + (price[i] - input);
								total = total - (price[i] - input);
								gendata.addLostcount();
							}
						}
						init();
						//txt.setOutput(getNowTime() + " BUYL50! cost:" + input);
						sval = 30;
						runflag = false;
					}	
				}
			} else {
				if (SGXTWGap() < 0)
					sval = svalA + (int)(SGXTWGap() / -0.0012) * 5;
				if (!(SGXTWGap() < -0.00248)) {
					if (input >= (price[abscurrent - 1] + sval)) { // 空方停損
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input < price[i]) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
								gendata.addWincount();
							} else {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
								gendata.addLostcount();
							}
						}
						init();
						//txt.setOutput(getNowTime() + " SELLLN! cost:" + input);
						sval = 30;
					}
				} else if (input >= (price[abscurrent - 1] + svalT)) { // 空方停損
					int wint = 0;
					int lostt = 0;
					int totalt = total;
					for (int i = 0; i < abscurrent; i++) {
						if (input < price[i]) {
							wint = wint + (price[i] - input);
							totalt = totalt + (price[i] - input);
						} else {
							lostt = lostt + (input - price[i]);
							totalt = totalt - (input - price[i]);
						}
					}
					if (totalt <= negarrive) {
						current = 0;
						writetxt(input);
						for (int i = 0; i < abscurrent; i++) {
							if (input < price[i]) {
								win = win + (price[i] - input);
								total = total + (price[i] - input);
								gendata.addWincount();
							} else {
								lost = lost + (input - price[i]);
								total = total - (input - price[i]);
								gendata.addLostcount();
							}
						}
						init();
						//txt.setOutput(getNowTime() + " SELLL50! cost:" + input);
						sval = 30;
						runflag = false;
					}
				}
			}
		}
	}

	private void checkTotal(int input) {
		if (total <= -30)
			tsize1 = 1;
		if (tsize1 > tsize)
			tsize1 = tsize;
		if (total <= negarrive) {
			//txt.setOutput("Win:" + win);
			//txt.setOutput("Lost:" + lost);
			//txt.setOutput("Total:" + total);
			//txt.setOutput("Negarrive have arrived " + negarrive + "!");
			runflag = false;
		}
	}

	void check_runtime() {
		String sGMT = nowtime;
		int hour = Integer.valueOf(sGMT.substring(0, 2)).intValue();
		int min = Integer.valueOf(sGMT.substring(3, 5)).intValue();
		int sec = Integer.valueOf(sGMT.substring(6, 8)).intValue();
		if (current != 0) {
			if (hour > 12 && min > 13) {
				if (close) {
					clear();
					runflag = false;
				}
				intoflag = false;
			}
			if (hour > 12 && min > 28) {
				clear();
				runflag = false;
				intoflag = false;
			}
		} else {
			if (hour > 12 && min > 0 && close) {
				runflag = false;
				intoflag = false;
			}
			if (hour > 12 && min > 15) {
				runflag = false;
				intoflag = false;
			}
		}
		if (hour > 7 && min > 44 && sec > 30 && !isOpen) {
			isOpen = true;
			if (SGXindex != 0)
				runflag = true;
			else
				runflag = false;
		}
	}

	private String getNowTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd ");
		//dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		java.util.Date date = null;
		try {
			date = df.parse(YYMMDD);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		String datetime = dateFormat.format(date);
		return datetime + nowtime;
	}

	private void clear() {
		if (current != 0) {
			if (current >= 1) {
				for (int i = 0; i < abscurrent; i++) {
					if (inputt > price[i]) {
						win = win + (inputt - price[i]);
						total = total + (inputt - price[i]);
						gendata.addWincount();
					} else {
						lost = lost + (price[i] - inputt);
						total = total - (price[i] - inputt);
						gendata.addLostcount();
					}
				}
				current = 0;
				writetxt(inputt);
				queue.clear();
				abscurrent = Math.abs(current);
			} else {
				for (int i = 0; i < abscurrent; i++) {
					if (inputt < price[i]) {
						win = win + (price[i] - inputt);
						total = total + (price[i] - inputt);
						gendata.addWincount();
					} else {
						lost = lost + (inputt - price[i]);
						total = total - (inputt - price[i]);
						gendata.addLostcount();
					}
				}
				current = 0;
				writetxt(inputt);
				queue.clear();
				abscurrent = Math.abs(current);
			}
		}
	}

	private Integer[] fhw(Object in[]) {
		int size = in.length;
		Integer[] tmp = new Integer[size / 2];

		for (int i = 0; i < size; i += 2) {
			tmp[i / 2] = ((Integer) in[i] + (Integer) in[i + 1]) / 2;
		}
		return tmp;
	}

	private Integer[] dwt(Object in[]) {
		Integer[] temp = null;
		switch (size) {
		case 1600:
			temp = fhw(fhw(fhw(fhw(fhw(fhw(in))))));
			break;
		case 800:
			temp = fhw(fhw(fhw(fhw(fhw(in)))));
			break;
		case 400:
			temp = fhw(fhw(fhw(fhw(in))));
			break;
		case 200:
			temp = fhw(fhw(fhw(in)));
			break;
		case 100:
			temp = fhw(fhw(in));
			break;
		}
		return temp;
	}

	private void init() {
		abscurrent = 0;
		higho = 0;
		lowo = 0;
		hlflag = 0;
		lowMFlag = 0;
		sval = 30;
		wval = 30;
	}

	/*private void gapcheck(int input) {
		if (current == 0) {
			gapcheckin(input);
		}
	}

	private void gapcheckin(int input) {
		if (!close && (gaprange < opweek1)) {
			current = 1 * currentmulti;
			abscurrent = Math.abs(current);
			writetxt(input);
			for (int i = 0; i < abscurrent; i++)
				price[i] = input;
			System.out.println(getNowTime() + " GapBroad:" + input);
			c.alert(botname, Email, getNowTime() + " 逆價差過小 買進:" + input + " "
					+ abscurrent + "口");
			p.plurkAdd(getNowTime() + " 買進:" + input + " "
					+ abscurrent + "口");
			c.disconnect();
			gapcount = 1;
		} else if (!close && (gaprange > opweek)) {
			current = -1 * currentmulti;
			abscurrent = Math.abs(current);
			writetxt(input);
			for (int i = 0; i < abscurrent; i++)
				price[i] = input;
			System.out.println(getNowTime() + " GapNarrow:" + input);
			c.alert(botname, Email, getNowTime() + " 逆價差過大 賣出:" + input + " "
					+ abscurrent + "口");
			p.plurkAdd(getNowTime() + " 賣出:" + input + " "
					+ abscurrent + "口");
			c.disconnect();
			gapcount = 1;
		}
	}*/
	
/*	private double ProCalculator() {
		double index;
		int ksindex = 0;
		if (kspercent >= 0.015)
			ksindex = 2;
		else if (kspercent >= 0.0075)
			ksindex = 1;
		else if (kspercent <= -0.015)
			ksindex = -2;
		else if (kspercent <= -0.0075)
			ksindex = -1;
		int todayindex = 0;
		if (nowpercent >= 0.01)
			todayindex = 2;
		else if (nowpercent >= 0.005)
			todayindex = 1;
		else if (nowpercent <= -0.01)
			todayindex = -2;
		else if (nowpercent <= -0.005)
			todayindex = -1;
		index = ksindex * 0.1 + OIindex * 0.2 + fuindex * 0.1 + todayindex * 0.1;
		return index;
	}*/
	
	private double SGXTWGap(){
		return SGXpercent - nowpercent;
	}
	
	public void closetxt(){
		gendata.setWin(win);
		gendata.setLost(lost);
		gendata.setTotal(total);
		SGXPreGap = SGXTWGap();
	}
	
	private double getHighLowPro(boolean hlflag){
		double p1;
		double p2 = new Double(price[abscurrent - 1]).doubleValue();
		if (hlflag) {
			p1 = new Double(higho).doubleValue();
			p1 = p1 - p2;
		}
		else {
			p1 = new Double(lowo).doubleValue();
			p1 = p2 - p1;
		}
		return (p1 / p2);
	}
	
	/*private boolean getTSLDGap(boolean bsflag){
		if (bsflag) {
			if (gaprange < TSLDHG)
				return false;
			else
				return true;
		} else {
			if (gaprange > TSLDLG)
				return false;
			else
				return true;
		}
	}*/
	
	/*public void setFileName(String version){
		this.txt = new LogFile("C:\\Runtime\\" + version + "\\" + YYMMDD + "_tickAPI_now" + ".txt");
	}*/
	
	public void setSGXGapA(int sval1) {
		this.SGXGapA = sval1 * 0.0001;
		this.SGXGapA_i = sval1;
	}
	
	public int getSGXGapA() {
		return this.SGXGapA_i;
	}
	
	public void setSGXGapB(int sval1) {
		this.SGXGapB = sval1 * 0.0001;
		this.SGXGapB_i = sval1;
	}
	
	public int getSGXGapB() {
		return this.SGXGapB_i;
	}
	
	public void setSGXGapL(int sval1) {
		this.SGXGapL = sval1 * 0.0001;
		this.SGXGapL_i = sval1;
	}
	
	public int getSGXGapL() {
		return this.SGXGapL_i;
	}
	
	public void setSGXGapLin(int sval1) {
		this.SGXGapLin = sval1 * 0.0001;
		this.SGXGapLin_i = sval1;
	}
	
	public int getSGXGapLin() {
		return this.SGXGapLin_i;
	}
	
	public void setSGXGap34(int sval1) {
		this.SGXGap34 =  sval1 * 0.0001;
		this.SGXGap34_i = sval1;
	}
	
	public int getSGXGap34() {
		return this.SGXGap34_i;
	}

	public void setNegarrive(int negarrive) {
		this.negarrive = negarrive * -1;
	}

	public void setCountSize(int countSize) {
		this.countSize = countSize;
	}

	public void setRange(int range) {
		this.range = range;
	}
	
	public int getRange() {
		return range;
	}

	public int getWval() {
		return wval;
	}

	public void setWval(int wval) {
		this.wval = wval;
	}

	public int getGapvoloutL() {
		return GapvoloutL;
	}

	public void setGapvoloutL(int gapvoloutL) {
		this.GapvoloutL = gapvoloutL;
	}

	public int getLowmcountT() {
		return lowmcountT;
	}

	public void setLowmcountT(int lowmcountT) {
		this.lowmcountT = lowmcountT;
	}

	public int getGapvolin() {
		return Gapvolin;
	}

	public void setGapvolin(int gapvolin) {
		this.Gapvolin = gapvolin;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public void setSGXGap(int sval1) {
		this.SGXGap =  sval1 * 0.0001;
	}
	
	public void setOffset(int sOffset) {
		this.Offset =  sOffset * 0.0001;
		offset_i = sOffset;
	}
	
	public int getOffset() {
		return offset_i;
	}
	
	public void setinputt(int input) {
		this.inputt = input;
	}
	
	public void setSGXindex(double input) {
		this.SGXindex = input;
	}
	
	public int getOIGap() {
		return OIGap_i;
	}
	
	public void setOIGap(int input) {
		this.OIGap_i = input;
	}
	
	public void setPreGap(double input) {
		this.SGXPreGap = input;
	}
}