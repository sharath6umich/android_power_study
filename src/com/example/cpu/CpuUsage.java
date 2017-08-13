package com.example.cpu;

import java.io.IOException;
import android.app.ActivityManager.MemoryInfo;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.view.Menu;
import android.widget.TextView;

public class CpuUsage extends Activity {

	public TextView text;
	public TextView ramtext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cpu_usage);
		Pattern p1 = Pattern.compile("a*b"); // a simple regex
		// slightly more complex regex: an attempt at validating email addresses
		Pattern p2 = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\b");
		text = (TextView) this.findViewById(R.id.usage);
		text.setText(String.valueOf(readUsage()));
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		ramtext = (TextView) this.findViewById(R.id.ram);
		ramtext.setText(String.valueOf(availableMegs));
	}
	
	private float readUsage() {
	    try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        String[] toks = load.split(" ");

	        long idle1 = Long.parseLong(toks[5]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();

	        toks = load.split(" ");

	        long idle2 = Long.parseLong(toks[5]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
	}
	
	class RegexThread extends Thread {
	   RegexThread() {
	      // Create a new, second thread
	      super("Regex Thread");
	      start(); // Start the thread
	   } 

	   // This is the entry point for the second thread.
	   public void run() {
	      while(true) {
	        Pattern p = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\b");
	      }
	   }
	}

	static class CPUStresser {
		public static void main(String args[]) {
			int NUM_THREADS = 100; // run 100 threads for 120s
			int RUNNING_TIME = 120;
			for(int i = 0; i < NUM_THREADS; ++i) {
				new CpuUsage().new RegexThread(); // create a new thread
			}
			try {
				Thread.sleep(1000 * RUNNING_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cpu_usage, menu);
		return true;
	}

}
