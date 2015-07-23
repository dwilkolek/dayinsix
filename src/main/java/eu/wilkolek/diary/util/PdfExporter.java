//package eu.wilkolek.diary.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.nio.charset.Charset;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.common.base.Charsets;
//
//import eu.wilkolek.diary.model.Day;
//
//public class PdfExporter {
//
//    public PdfExporter() {
//        // TODO Auto-generated constructor stub
//    }
//    
//    public File createFile(String username){
//        
//        File file = File.createTempFile(username+DateTimeUtils.getCurrentUTCTime().getTime(), ".txt");
//        
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write();
//        return null;
//    }
//    
//    private byte[] dayToByte(Day day){
//        
//        String string = new String();
//        
//        String date = DateTimeUtils.format(day.getCreationDate());
//        
//        string += date;
//        
//        if (!StringUtils.isEmpty(day.getNote())){
//            ""
//        }
//        
//        return string.getBytes(org.apache.commons.io.Charsets.UTF_8);
//        
//    }
//    
//}
