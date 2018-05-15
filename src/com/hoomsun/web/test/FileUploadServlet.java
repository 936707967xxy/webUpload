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

		System.out.println("进入后台...");

		/**
		 * 创建DiskFileItemFactory工厂
		 */
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

		/**
		 * 创建 ServletFileUpload对象
		 */
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

		/**
		 * 设置文件名称编码
		 */
		servletFileUpload.setHeaderEncoding(Contans.CHARSET);

		/**
		 * 开始解析文件
		 * 文件md5获取的字符串
		 */
		String fileMd5 = null;
		/**
		 * 文件的索引
		 */
		String chunk = null;
		try {
			/**
			 * 使用ServletFileUpload解析器解析上传数据，
			 * 解析结果返回的是一个List<FileItem>集合，
			 * 每一个FileItem对应一个Form表单的输入项
			 */
			
			List<FileItem> items = servletFileUpload.parseRequest(request);
			for (FileItem fileItem : items) {

				if (fileItem.isFormField()) {
					/**
					 * 普通表单提交数据
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
					 * 保存文件到制定目录地址
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
