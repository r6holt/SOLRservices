package solrjava;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class Server {
	public Server() {}
	
	public int tryPort () {
		
		/*Runtime rt = Runtime.getRuntime();
		try {
			rt.exec("cmd.exe /c cd (dir solr-6.6.0 /AD /s) && cmd.exe solr start -p 8900");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//cd \""+new_dir+"\" & start cmd.exe /k \"java -flag -flag -cp terminal-based-program.jar\"");*/
		
		
		
		/*ProcessBuilder builder = new ProcessBuilder( "C:/Windows/System32/cmd.exe");
		builder.redirectErrorStream(true); 
		
        Process p=null;
        try {
            p = builder.start();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        //get stdin of shell
        BufferedWriter p_stdin = 
          new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

        // execute the desired command (here: ls) n times
        int n=10;
        //for (int i=0; i<n; i++) {
            try {
                //single execution
            p_stdin.write("find c:\\ -type f -name \"Documents\"");//(dir c:\\ /AD /s /b | find \"Documents\")");
            p_stdin.newLine();
            p_stdin.flush();
            }
            catch (IOException e) {
            System.out.println(e);
            }
        //}

        // finally close the shell by execution exit command
        try {
            p_stdin.write("exit");
            p_stdin.newLine();
            p_stdin.flush();
        }
        catch (IOException e) {
            System.out.println(e);
        }

	    // write stdout of shell (=output of all commands)
	    Scanner s = new Scanner( p.getInputStream() );
	    while (s.hasNext())
	    {
	        System.out.println( s.next() );
	    }
	    s.close();*/
	    
	        
			
		try {	
	        HttpSolrClient solr = new HttpSolrClient.Builder(GUI.urlString).build();
	        
	        solr.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
}
