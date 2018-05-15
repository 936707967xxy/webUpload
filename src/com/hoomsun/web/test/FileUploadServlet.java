package com.hoomsun.web.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.hoomsun.web.util.Contans;


public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String serverPath = "e:/";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());

		System.out.println("�����̨...");

		/**
		 * ����DiskFileItemFactory����
		 */
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

		/**
		 * ���� ServletFileUpload����
		 */
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

		/**
		 * �����ļ����Ʊ���
		 */
		servletFileUpload.setHeaderEncoding(Contans.CHARSET);

		/**
		 * ��ʼ�����ļ�
		 * �ļ�md5��ȡ���ַ���
		 */
		String fileMd5 = null;
		/**
		 * �ļ�������
		 */
		String chunk = null;
		try {
			/**
			 * ʹ��ServletFileUpload�����������ϴ����ݣ�
			 * ����������ص���һ��List<FileItem>���ϣ�
			 * ÿһ��FileItem��Ӧһ��Form����������
			 */
			
			List<FileItem> items = servletFileUpload.parseRequest(request);
			for (FileItem fileItem : items) {

				if (fileItem.isFormField()) {
					/**
					 * ��ͨ���ύ����
					 */
					String fieldName = fileItem.getFieldName();
					if ("info".equals(fieldName)) {
						String info = fileItem.getString(Contans.CHARSET);
						System.out.println("info:" + info);
					}
					if ("fileMd5".equals(fieldName)) {
						fileMd5 = fileItem.getString(Contans.CHARSET);
						System.out.println("fileMd5:" + fileMd5);
					}
					if ("chunk".equals(fieldName)) {
						chunk = fileItem.getString(Contans.CHARSET);
						System.out.println("chunk:" + chunk);
					}
				} else { 
					File file = new File(serverPath + "/" + fileMd5);
					if (!file.exists()) {
						file.mkdirs();
					}
					/**
					 * �����ļ����ƶ�Ŀ¼��ַ
					 */
					File chunkFile = new File(serverPath + "/" + fileMd5 + "/" + chunk);
					FileUtils.copyInputStreamToFile(fileItem.getInputStream(), chunkFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
