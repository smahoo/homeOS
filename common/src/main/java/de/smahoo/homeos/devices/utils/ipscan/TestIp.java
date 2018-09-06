package de.smahoo.homeos.utils.ipscan;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TestIp {


        /**
         * @param args
         */
        public static void main(String[] args) {
                String ip;
                final ExecutorService es = Executors.newFixedThreadPool(15);                        
                  final int timeout = 500;
                  final List<Future<IpScanResult>> futures = new ArrayList<Future<IpScanResult>>();
                  for (int i =1; i <255; i++) {
                          //ip = "192.168.178."+i;
                	  ip = "192.168.2."+i;
                    futures.add(portIsOpen(es, ip,2020, timeout));
                  }
                  es.shutdown();
                  System.out.println("Scan Results:");
                  for (final Future<IpScanResult> f : futures) {
                          try {                                  
                                  if (f.get().isAvailable()) {
                                          System.out.println(" - "+f.get().getIp()+":"+f.get().getPort());
                                  } else {
                                        //  System.out.println(".");
                                  }
                          } catch (Exception exc){
                                  //
                          }
                  }
        }
        
        public static Future<IpScanResult> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
                  return es.submit(new Callable<IpScanResult>() {
                      @Override public IpScanResult call() {
                        try {
                          Socket socket = new Socket();
                          socket.connect(new InetSocketAddress(ip, port), timeout);
                          socket.close();
                        
                          return new IpScanResult(ip, port, true);
                        } catch (Exception ex) {
                                return new IpScanResult(ip, port, false);
                        }
                      }
                   });
                }
        
}
