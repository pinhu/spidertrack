package com.spiderio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pin HU
 *
 * 21 Oct 2011, 14:07:29
 */
public class Main
{

    public static void main(String[] args)
    {
    	File blockingSiteStore = new File("E:/SpiderTest/blockingSite.txt");
        String controllerLoc = "E:/SpiderTest/control";

        String[] addresses = new String[]{
        		"http://www.avc.com"
        };
        
        int depth = 2;
        int numThreads = 10;
        Map<String, String[]> trackersMap = new HashMap<String, String[]>();
        for(int i = 0; i < addresses.length; i++)
        {
        	Spider spider = new Spider(addresses[i], blockingSiteStore, controllerLoc, depth, numThreads);
        	trackersMap.put(addresses[i], spider.execute());
        }

        try
        {
        	PrintStream ps = new PrintStream(new FileOutputStream("E:/SpiderTest/trackers.xml"));
        	Spider.printTrackersInXML(ps, trackersMap);
        }
        catch(FileNotFoundException fnfe)
        {
        	fnfe.printStackTrace();
        }
    }
}
