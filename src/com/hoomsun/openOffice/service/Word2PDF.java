package com.hoomsun.openOffice.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

/***
 *
 * @author create by 沙漠的波浪
 *
 * @date 2017年2月21日---下午2:29:27
 *
 */
public class Word2PDF {
    private static final int wdFormatPDF = 17;
    private static final int wdDoNotSaveChanges = 0;
    private static final int ppSaveAsPDF = 32;
    

    public static void main(String[] args) {
        int time = convert2PDF("D:/工作流文档.docx", "D:/xuxinyuan.pdf");
                
        if (time == -4) {
            System.out.println("转化失败，未知错误...");
        } else if(time == -3) {
            System.out.println("原文件就是PDF文件,无需转化...");
        } else if (time == -2) {
            System.out.println("转化失败，文件不存在...");
        }else if(time == -1){
            System.out.println("转化失败，请重新尝试...");
        }else if (time < -4) {
            System.out.println("转化失败，请重新尝试...");
        }else {
            System.out.println("转化成功，用时：  " + time + "s...");
        }

    }

    /***
     * 判断需要转化文件的类型（Excel、Word、ppt）
     * 
     * @param inputFile
     * @param pdfFile
     */
    private static int convert2PDF(String inputFile, String pdfFile) {
        String kind = getFileSufix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            return -2;//文件不存在
        }
        if (kind.equals("pdf")) {
            return -3;//原文件就是PDF文件
        }
        if (kind.equals("doc")||kind.equals("docx")||kind.equals("txt")) {
            return Word2PDF.word2pdf(inputFile, pdfFile);
        }else if (kind.equals("ppt")||kind.equals("pptx")) {
            return Word2PDF.ppt2pdf(inputFile, pdfFile);
        }else if(kind.equals("xls")||kind.equals("xlsx")){
            return Word2PDF.excel2pdf(inputFile, pdfFile);
        }else  if(kind.equals("png")){
        	try {
				return Word2PDF.imgToPdf(inputFile, pdfFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	return -4;
        }
		return 0;
    }

    /***
     * 判断文件类型
     * 
     * @param fileName
     * @return
     */
    public static String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }

    /***
     * 
     * Word转PDF
     * 
     * @param inputFile
     * @param pdfFile
     * @return
     */

    public static int word2pdf(String source,String target){  
    	  System.out.println("启动Word");  
    	  long start = System.currentTimeMillis();  
    	  ActiveXComponent app = null;  
    	  try {  
    	   app = new ActiveXComponent("Word.Application");  
    	   app.setProperty("Visible", false);  
    	  
    	   Dispatch docs = app.getProperty("Documents").toDispatch();  
    	   System.out.println("打开文档" + source);  
    	   Dispatch doc = Dispatch.call(docs,//  
    	     "Open", //  
    	     source,// FileName  
    	     false,// ConfirmConversions  
    	     true // ReadOnly  
    	     ).toDispatch();  
    	  
    	   System.out.println("转换文档到PDF " + target);  
    	   File tofile = new File(target);  
    	   if (tofile.exists()) {  
    	    tofile.delete();  
    	   }  
    	   Dispatch.call(doc,//  
    	     "SaveAs", //  
    	     target, // FileName  
    	     wdFormatPDF);  
    	  
    	   Dispatch.call(doc, "Close", false);  
    	   long end = System.currentTimeMillis();  
    	   System.out.println("转换完成..用时：" + (end - start) + "ms.");  
    	  } catch (Exception e) {  
    	   System.out.println("========Error:文档转换失败：" + e.getMessage());  
    	  } finally {  
    	   if (app != null)  
    	    app.invoke("Quit", wdDoNotSaveChanges);  
    	  }
		return 0;  
    	 }  

   

    /**
     * EXCEL转成PDF
     * @author xinyuan.xu@hoomsun.com
     * @param source
     * @param target
     * @return
     * @Description:
     * @param 2018年2月27日
     */
    public static int excel2pdf(String source, String target) {  
        System.out.println("启动Excel");  
        long start = System.currentTimeMillis();  
        ActiveXComponent app = new ActiveXComponent("Excel.Application"); // 启动excel(Excel.Application)  
        try {  
        app.setProperty("Visible", false);  
        Dispatch workbooks = app.getProperty("Workbooks").toDispatch();  
        System.out.println("打开文档" + source);  
        Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method, new Object[]{source, new Variant(false),new Variant(false)}, new int[3]).toDispatch();  
        Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] {  
        target, new Variant(57), new Variant(false),  
        new Variant(57), new Variant(57), new Variant(false),  
        new Variant(true), new Variant(57), new Variant(true),  
        new Variant(true), new Variant(true) }, new int[1]);  
        Variant f = new Variant(false);  
        System.out.println("转换文档到PDF " + target);  
        Dispatch.call(workbook, "Close", f);  
        long end = System.currentTimeMillis();  
        System.out.println("转换完成..用时：" + (end - start) + "ms.");  
        } catch (Exception e) {  
         System.out.println("========Error:文档转换失败：" + e.getMessage());  
        }finally {  
         if (app != null){  
          app.invoke("Quit", new Variant[] {});  
         }  
        }
		return 0;  
   }  

    /**
     * PPT转成PDF
     * @author xinyuan.xu@hoomsun.com
     * @param inputFile
     * @param pdfFile
     * @return
     * @Description:
     * @param 2018年2月27日
     */
    public static int ppt2pdf(String inputFile,String pdfFile){  
    	  System.out.println("启动PPT");  
    	  long start = System.currentTimeMillis();  
    	  ActiveXComponent app = null;  
    	  try {  
    	   app = new ActiveXComponent("Powerpoint.Application");  
    	   Dispatch presentations = app.getProperty("Presentations").toDispatch();  
    	   System.out.println("打开文档" + inputFile);  
    	   
    	   /**
    	    *  source FileName  
    	    *  true ReadOnly  
    	    *  true Untitled 指定文件是否有标题
    	    *  false  WithWindow 指定文件是否可见
    	    */
    	   Dispatch presentation = Dispatch.call(presentations, "Open", inputFile,true,true,false).toDispatch();  
    	   System.out.println("转换文档到PDF " + pdfFile);  
    	   File tofile = new File(pdfFile);  
    	   if (tofile.exists()) {  
    	    tofile.delete();  
    	   }  
    	   Dispatch.call(presentation,"SaveAs", pdfFile,ppSaveAsPDF);  
    	   Dispatch.call(presentation, "Close");  
    	   long end = System.currentTimeMillis();  
    	   System.out.println("转换完成..用时：" + (end - start) + "ms.");  
    	  } catch (Exception e) {  
    	   System.out.println("========Error:文档转换失败：" + e.getMessage());  
    	  } finally {  
    	   if (app != null) app.invoke("Quit");  
    	  }
		return 0;  
    	 }  
    
    /**
     * IMAGE转
     * @author xinyuan.xu@hoomsun.com
     * @param imgFilePath
     * @param pdfFilePath
     * @return
     * @throws IOException
     * @Description:
     * @param 2018年2月27日
     */
	public static int imgToPdf(String imgFilePath, String pdfFilePath)
			throws IOException {
		File file = new File(imgFilePath);
		if (file.exists()) {
			Document document = new Document();
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(pdfFilePath);
				PdfWriter.getInstance(document, fos);

				// 添加PDF文档的某些信息，比如作者，主题等等
				document.addAuthor("arui");
				document.addSubject("test pdf.");
				// 设置文档的大小
				document.setPageSize(PageSize.A4);
				// 打开文档
				document.open();
				// 写入一段文字
				// document.add(new Paragraph("JUST TEST ..."));
				// 读取一个图片
				Image image = Image.getInstance(imgFilePath);
				float imageHeight = image.getScaledHeight();
				float imageWidth = image.getScaledWidth();
				int i = 0;
				while (imageHeight > 500 || imageWidth > 500) {
					image.scalePercent(100 - i);
					i++;
					imageHeight = image.getScaledHeight();
					imageWidth = image.getScaledWidth();
					System.out.println("imageHeight->" + imageHeight);
					System.out.println("imageWidth->" + imageWidth);
				}
				image.setAlignment(Image.ALIGN_CENTER);
				document.add(image);
			} catch (DocumentException de) {
				System.out.println(de.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
			document.close();
			fos.flush();
			fos.close();
			return 0;
		} else {
			return -4;
		}
	}
}
