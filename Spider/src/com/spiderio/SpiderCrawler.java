package com.spiderio;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Pin HU
 *
 * 21 Oct 2011, 11:23:00
 */
public class SpiderCrawler extends WebCrawler
{
    private static String[] urlFilters;
    private static Pattern[] blockHints;
    private static BlockingQueue<Runnable> queue;
    private static Spider spider;
    
    Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    @Override
    public boolean shouldVisit(WebURL url) {
        if(url == null)
            return false;
        
        String href = url.getURL().toLowerCase();
        if (filters.matcher(href).matches())
            return false;
        
        for(int i = urlFilters.length - 1; i >= 0; i--)
        {
            if (href.startsWith(urlFilters[i]))
                return true;
        }
        
        return false;
    }

    @Override
    public void visit(Page page) 
    {
    	String html = page.getHTML();
        queue.add(new TrackerDetector(blockHints, html, spider));          
    }
    
    public static void configure(String[] urlFilters, Pattern[] blockHints,  BlockingQueue<Runnable> queue, Spider spider)
    {
        SpiderCrawler.urlFilters = urlFilters;
        SpiderCrawler.blockHints = blockHints;
        SpiderCrawler.queue = queue;
        SpiderCrawler.spider = spider;
    }
}
