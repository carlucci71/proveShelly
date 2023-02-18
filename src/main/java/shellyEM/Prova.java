package shellyEM;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Prova {
	public static void main(String[] args) throws Exception {
		Prova p = new Prova();
		//		p.go();
		p.scanPlug();
	}


	private void scanPlug() throws Exception {
		String baseIP="192.168.0.152";
		String[] ipS = baseIP.split("\\.");
		byte[] ipPlug = { (byte)Integer.parseInt(ipS[0]), (byte)Integer.parseInt(ipS[1]), (byte)Integer.parseInt(ipS[2]), (byte)Integer.parseInt(ipS[3]) };
		InetAddress address = InetAddress.getByAddress(ipPlug);
		while (true) {
			try {
				HttpURLConnection con = (HttpURLConnection)(new URL("http://" + address.getHostAddress() + "/status")).openConnection();
				con.setConnectTimeout(15000);
				JsonNode shellyNode = (new ObjectMapper()).readTree(con.getInputStream());
				int resp = con.getResponseCode();
				if (resp == 200 && shellyNode.has("mac")) {
					double d = Double.valueOf(shellyNode.get("meters").findValuesAsText("power").get(0));
					System.out.println(d);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//	            	System.out.println(String.format("timeout %s", addr));
			}
			Thread.currentThread().sleep(500);
		}

	}


	private void go() throws Exception {
		String baseIP="192.168.0.100";
		String[] ipS = baseIP.split("\\.");
		byte[] ip = { (byte)Integer.parseInt(ipS[0]), (byte)Integer.parseInt(ipS[1]), (byte)Integer.parseInt(ipS[2]), 0 };
		int first=2;//2
		int last=255;//255
		scannerInit(ip, first, last);
	}

	private int lowerIP;
	private int higherIP;
	private byte[] baseScanIP;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(256);

	private  boolean isShelly(InetAddress address) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection)(new URL("http://" + address.getHostAddress() + "/status")).openConnection();
			con.setConnectTimeout(15000);
			JsonNode shellyNode = (new ObjectMapper()).readTree(con.getInputStream());
			int resp = con.getResponseCode();
			if (resp == 200 && shellyNode.has("mac")) {
				System.err.println(shellyNode.get("meters"));
				return true;
			}
			//				      System.out.println(String.format("Not Shelly %s, resp %s, node ()", new Object[] { address, Integer.valueOf(resp), shellyNode }));
			return false;
		} catch (Exception e) {
			//				    	System.out.println(String.format("Not Shelly %s - %s", address, e.toString()));
			return false;
		} finally {
			con.disconnect();
		} 
	}

	public void scannerInit(byte[] ip, int first, int last) throws Exception {
		this.baseScanIP = ip;
		this.lowerIP = first;
		this.higherIP = last;
		//		    System.out.println(String.format("ip scan: %s %s %s", new Object[] { ip, Integer.valueOf(first), Integer.valueOf(last) }));
		scanByIP();
		System.exit(0);
	}	

	private void scanByIP() throws Exception {
		List<ScheduledFuture<?>> lista = new ArrayList<>();
		for (int delay = 0, ip4 = this.lowerIP; ip4 <= this.higherIP; delay += 4, ip4++) {
			this.baseScanIP[3] = (byte)ip4;
			InetAddress addr = InetAddress.getByAddress(this.baseScanIP);
			ScheduledFuture<?> schedule = this.executor.schedule(() -> {
				try {
					//					System.out.println("Cerco: " + addr);
					if (addr.isReachable(4500)) {
						Thread.sleep(50L);
						if (isShelly(addr))
							System.out.println(addr.getHostAddress()); 
					} else {
						//							            	  System.out.println(String.format("no ping %s", addr));
					} 
				} catch (SocketTimeoutException e) {
					//	            	System.out.println(String.format("timeout %s", addr));
				} catch (IOException|InterruptedException e) {
					//	            	System.out.println(String.format("ip scan error %s %s", addr, e.toString()));
				} 
			},delay, TimeUnit.MILLISECONDS);
			lista.add(schedule);
		}
		lista.forEach(schedule -> {
			while (!schedule.isDone()) {
				//				System.out.println("....");
			}
		}); 
		return;
	}


}
