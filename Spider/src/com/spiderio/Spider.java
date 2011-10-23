package com.spiderio;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.PageFetcher;

public class Spider 
{
	private String address;
	private Set<String> trackersSet;
	private File blockingSiteStore;
	private String controllerLoc;
	private int depth;
	private int numThreads;
	
	public Spider(String address, File blockingSiteStore, String controllerLoc, int depth, int numThreads)
	{
		this.address = address;
		this.blockingSiteStore = blockingSiteStore;
		this.controllerLoc = controllerLoc;
		this.depth = depth;
		this.numThreads = numThreads;
		trackersSet = new HashSet<String>();
	}
	
	public String[] execute()
	{
		retrieveTrackers(crawling());
    	
		return getTrackers();
	}
	
	private void retrieveTrackers(BlockingQueue<Runnable> blockingQueue)
	{
    	
        int availableCPU = Runtime.getRuntime().availableProcessors();
        int poolSize =  availableCPU > 2 ? availableCPU - 1 : 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.SECONDS, blockingQueue);
        
        System.out.println("Start tracker detection, pool size: " + poolSize);
        while(!blockingQueue.isEmpty())
        {
        	TrackerDetector detector = (TrackerDetector)blockingQueue.poll();
        	if(detector != null)
        		executor.execute(detector);
        }

        while(!executor.isTerminated())
        {
        	executor.shutdown();
    		try 
    		{
				executor.awaitTermination(10, TimeUnit.SECONDS);
			} 
    		catch (InterruptedException e) 
    		{
				e.printStackTrace();
			}
        }
        
        System.out.println("Completed");
	}
	
	private BlockingQueue<Runnable> crawling()
	{
        BlockingListParser blParser = new BlockingListParser(blockingSiteStore);
        blParser.parse();
        String[] blockLinks = blParser.getBlocks();
        
        Pattern[] blockHints= new Pattern[blockLinks.length];
        for(int i = blockLinks.length - 1; i >= 0; i--)
        {
            String replacement = blockLinks[i].substring(blockLinks[i].lastIndexOf("/") + 1).replace("_", ".*");
            blockHints[i] = Pattern.compile("^.*?" + replacement + "\\.[a-z].*?");
        }

        String[] urlFilters = new String[]{address};
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>();

        SpiderCrawler.configure(urlFilters, blockHints, blockingQueue, this);
        CrawlController controller;
		try 
		{
			java.util.Random random = new java.util.Random();
			controller = new CrawlController(controllerLoc + "/" + random.nextInt(1 << 16));
	        controller.addSeed(address);
	        controller.setMaximumCrawlDepth(depth);
	        controller.start(SpiderCrawler.class, numThreads); 
	        
	        PageFetcher.stopConnectionMonitorThread();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return blockingQueue;
	}
	
	public void updateTrackers(Set<String> set)
	{
		synchronized(trackersSet)
		{
			trackersSet.addAll(set);
		}
	}
	
	public String[] getTrackers()
	{
		String[] trackers;
		synchronized(trackersSet)
		{
			trackers = new String[trackersSet.size()];
			trackersSet.toArray(trackers);
		}
		
		return trackers;
	}
	
	public static void printTrackersInXML(PrintStream ps, Map<String, String[]> trackersMap)
	{
		ps.println("<entries>");
		for(Iterator<Map.Entry<String, String[]>> iterator = trackersMap.entrySet().iterator(); iterator.hasNext();)
		{
			Map.Entry<String, String[]> entry = iterator.next();
			String address = entry.getKey();
			String[] trackers = entry.getValue();
			
			ps.println("<entry>");
			ps.println("<address>");
			ps.println(address);
			ps.println("</address>");
			ps.println("<trackers>");
			for(int i = trackers.length - 1; i >= 0; i--)
			{
				ps.println("<tracker>");
				ps.println(trackers[i]);
				ps.println("</tracker>");
			}
			ps.println("</trackers>");
			ps.println("</entry>");
		}
		ps.println("<entries>");

	}
}
