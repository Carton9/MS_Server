package org.mike.ms.udp;

import java.util.TimerTask;

public abstract class CounterTask extends TimerTask {
	private int count;
	private int time;
	public CounterTask(int time){
		this.time=time;
	}
	@Override
	public final void run() {
		// TODO Auto-generated method stub
		action();
		count++;
		if(count==time)
			cancel();
	}
	abstract void action();
}
