package com.spiderio;

import java.util.regex.Pattern;

/**
 * @author Pin HU
 *
 * 21 Oct 2011, 16:17:09
 */
public class TrackerDetector implements Runnable
{
	private final Pattern[] blockHints;
	private final String html;
	private final Spider spider;
	
	public TrackerDetector(Pattern[] blockHints, String html, Spider spider)
	{
		this.blockHints = blockHints;
		this.html = html;
		this.spider = spider;
	}

	public String getHTML()
	{
		return html;
	}
	
    @Override
    public void run()
    {
    	int begin = 0;
    	int end = 0;
    	int end1;
    	int end2;

    	java.util.Set<String> set = new java.util.HashSet<String>();
    	while(true)
    	{
    		String scriptStr;
    		begin = html.indexOf("<script", end);
    		if(begin < 0)
    			break;
    		
    		end1 = html.indexOf("/>", begin);
    		end2 = html.indexOf("</script>", begin);
    		
    		if(end1 < 0)
    			end1 = end2;
    		
    		if(end1 < 0)
    		{
    			end = begin + 6;
    			continue;
    		}
    		
    		end = end1 < end2 ? end1 + 2 : end2 + 9;

    		scriptStr = html.substring(begin, end).replaceAll("\\r|\\n", "");
    		for(int i = blockHints.length - 1; i >= 0; i--)
    		{
    			if(blockHints[i].matcher(scriptStr).matches())
    			{
    				set.add(scriptStr);
    				break;
    			}
    		}
    		spider.updateTrackers(set);
    		
    	}
    }
}
