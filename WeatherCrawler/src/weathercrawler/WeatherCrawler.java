package weathercrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WeatherCrawler {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String dir="d:/lomolith/documents/workspace/180221_crawler";
        String file="list.input.org";
        if (args!=null && args.length>0) {
            dir = args[0];
            if (args.length>1) file=args[1];
        }
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date();
        String time=dateFormat.format(date);
        FileReader fr = new FileReader(dir+"/"+file);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(dir+"/"+time+"_"+file+".table");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("time_crawl\taddress\tzone\tx\ty\ttime_annouced\ttemp\tsky\train\ttime_target\tprob\twind_speed\twind\thumidityb\n");
        String line="";
        int count=0;
        Map weatherList = new HashMap();
        while((line=br.readLine())!=null) {
            URLConnection connection = new URL("http://www.weather.go.kr/wid/queryDFSRSS.jsp?zone="+line).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            String content = scanner.next();
            String prediction=getValue(content, "data seq=\"0\"","data");
            Weather w = new Weather();
            w.name_kor=getValue(content, "category");
            w.timestamp=Long.parseLong(getValue(content, "tm"));
            w.x=Integer.parseInt(getValue(content, "x"));
            w.y=Integer.parseInt(getValue(content, "y"));
            w.hour=Integer.parseInt(getValue(prediction, "hour"));
            w.temp=Double.parseDouble(getValue(prediction, "temp"));
            w.sky=Integer.parseInt(getValue(prediction, "sky"));
            w.rain=Integer.parseInt(getValue(prediction, "pty"));
            w.desc_kor=getValue(prediction, "wfKor");
            w.desc_eng=getValue(prediction, "wfEn");

            w.rain_prob=Integer.parseInt(getValue(prediction, "pop"));
            w.wind_speed=Double.parseDouble(getValue(prediction, "ws"));
            w.wind=Integer.parseInt(getValue(prediction, "wd"));
            w.wind_kor=getValue(prediction, "wdKor");
            w.wind_eng=getValue(prediction, "wdEn");
            w.humid=Integer.parseInt(getValue(prediction, "reh"));
            weatherList.put(line, w);
            System.out.println("Reading "+(++count)+" :"+w.name_kor);
            bw.write(time+"\t"+w.name_kor+"\t"+line+"\t"+w.x+"\t"+w.y+"\t"+w.timestamp+"\t"+w.temp+"\t"+w.desc_kor+"\t"+w.rain+"\t"+w.hour+"\t"+w.rain_prob+"\t"+w.wind_speed+"\t"+w.wind_kor+"\t"+w.humid+"\n");
            bw.flush();
        }
        bw.close();
        fw.close();
        br.close();
        fr.close();
    }

    static String getValue(String content, String key) {
        return getValue(content, key, key);
    }
    static String getValue(String content, String start, String end) {
        return content.substring(content.indexOf("<"+start+">")+start.length()+2, content.indexOf("</"+end+">"));
    }
    
    static class Weather {
        final int CLEAN = 1;
        final int CLOUD_LITTLE = 2;
        final int CLOUD_HEAVY = 3;
        final int CLOUD_MOST = 4;
    
        final int NO_RAIN = 0;
        final int RAIN = 1;
        final int RAIN_SNOW = 2;
        final int SNOW_RAIN = 3;
        final int SNOW = 4;
        
        String name_kor;
        long timestamp;
        int x;
        int y;

        int hour;
        double temp;
        int sky;
        int rain;
        String desc_kor;
        String desc_eng;
        int rain_prob;
        double wind_speed;
        int wind;
        String wind_kor;
        String wind_eng;
        int humid;
    }
}


