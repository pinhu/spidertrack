package com.spiderio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Pin HU
 *
 * 21 Oct 2011, 13:39:10
 */
public class BlockingListParser
{
    private Set<String> blocks;
    
    private File inputFile;
    
    public BlockingListParser(File inputFile)
    {
        this.inputFile = inputFile;
        blocks = new HashSet<String>();
    }
    
    public void setInputFile(File inputFile)
    {
        this.inputFile = inputFile;
    }
    
    public void parse()
    {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try
        {
            fis = new FileInputStream(inputFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            
            String line = br.readLine();
            String[] parts = line.split("href=\"");
            for(int i = parts.length - 1; i >= 0; i--)
            {
                String link = parts[i].substring(0, parts[i].indexOf("\""));
                blocks.add(link);
            }
        }
        catch(FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            if(br != null)
            {
                try
                {
                    br.close();
                } catch (IOException e){}
            }
            if(isr != null)
            {
                try
                {
                    isr.close();
                } catch (IOException e){}
            }
            if(fis != null)
            {
                try
                {
                    fis.close();
                } catch (IOException e){}
            }
        }
        
    }
    
    public String[] getBlocks()
    {
        String[] ret = new String[blocks.size()];
        blocks.toArray(ret);
        return ret;
    }
    
    
}
