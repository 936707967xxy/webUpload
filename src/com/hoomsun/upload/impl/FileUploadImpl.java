package com.hoomsun.upload.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.hoomsun.upload.api.CheckMergePageUploadServer;
import com.hoomsun.web.util.Contans;

public class FileUploadImpl implements Serializable,CheckMergePageUploadServer{
	
	/**
	 * ��֤�汾һ���Ա�ʾ
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * �������洢�ļ���ַ
	 */
	private String serverPath = "e:/";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
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
					if (Contans.FROM_INFO.equals(fieldName)) {
						String info = fileItem.getString(Contans.CHARSET);
						System.out.println("info:" + info);
					}
					if (Contans.FILE_MD5.equals(fieldName)) {
						fileMd5 = fileItem.getString(Contans.CHARSET);
						System.out.println("fileMd5:" + fileMd5);
					}
					if (Contans.FILE_CHUNK.equals(fieldName)) {
						chunk = fileItem.getString(Contans.CHARSET);
						System.out.println("chunk:" + chunk);
					}
				} else { 
					File file = new File(serverPath + File.separator + fileMd5);
					if (!file.exists()) {
						file.mkdirs();
					}
					/**
					 * �����ļ����ƶ�Ŀ¼��ַ
					 */
					File chunkFile = new File(serverPath + File.separator + fileMd5 + File.separator + chunk);
					FileUtils.copyInputStreamToFile(fileItem.getInputStream(), chunkFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}

}
