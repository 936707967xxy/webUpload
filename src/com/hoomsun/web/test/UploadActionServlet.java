package com.hoomsun.web.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �ϲ��ϴ��ļ�
 */
public class UploadActionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String serverPath = "e:/";

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("��ʼ�ļ��ϴ�......");
		long start=System.currentTimeMillis();
		
		String action = request.getParameter("action");
		if ("mergeChunks".equals(action)) {
			System.err.println("==================�ϲ�=======================");
			/**
			 * �����Ҫ�ϲ���Ŀ¼
			 */
			String fileMd5 = request.getParameter("fileMd5");
			/**
			 * ��ȡĿ¼�����ļ�
			 */
			File f = new File(serverPath + "/" + fileMd5);
			File[] fileArray = f.listFiles(new FileFilter() {
				/**
				 * �ų�Ŀ¼��ֻҪ�ļ�
				 */
				@Override
				public boolean accept(File pathname) {
					if (pathname.isDirectory()) {
						return false;
					}
					return true;
				}
			});

			/**
			 * ת�ɼ��ϣ���������
			 */
			List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
			/**
			 * ��С��������
			 */
			Collections.sort(fileList, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2
							.getName())) {
						return -1;
					}
					return 1;
				}
			});

			/**
			 * �½������ļ�
			 */
			File outputFile = new File(serverPath + "/"
					+ UUID.randomUUID().toString() + ".zip");
			/**
			 * �����ļ�
			 */
			outputFile.createNewFile();
			/**
			 *  �����
			 */
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			FileChannel outChannel = fileOutputStream.getChannel();
			/**
			 * �ϲ�
			 */
			FileChannel inChannel;
			for (File file : fileList) {
				inChannel = new FileInputStream(file).getChannel();
				inChannel.transferTo(0, inChannel.size(), outChannel);
				inChannel.close();
				/**
				 * ɾ����Ƭ
				 */
				file.delete();
			}

			/**
			 * �ر���
			 */
			fileOutputStream.close();
			outChannel.close();

			/**
			 * ����ļ���
			 */
			File tempFile = new File(serverPath + "/" + fileMd5);
			if (tempFile.isDirectory() && tempFile.exists()) {
				tempFile.delete();
			}
			
			long end=System.currentTimeMillis();
			System.out.println("�ϲ��ļ��ɹ�����ʱ��"+(end-start)+"ms");
			

		} else if ("checkChunk".equals(action)) {
			System.err.println("==================�ϵ��ϴ�=======================");
			/**
			 * У���ļ��Ƿ��Ѿ��ϴ������ؽ����ǰ��
			 */
			String fileMd5 = request.getParameter("fileMd5");//�ļ���ʾ��Ψһ��
			String chunk = request.getParameter("chunk");//�п��±�
			String chunkSize = request.getParameter("chunkSize");//��ǰ�п��С
			/**
			 * �ҵ��ֿ��ļ�
			 */
			File checkFile = new File(serverPath + "/" + fileMd5 + "/" + chunk);

			/**
			 * ����ļ��Ƿ���ڣ��Ҵ�Сһ��
			 */
			response.setContentType("text/html;charset=utf-8");
			if (checkFile.exists()
					&& checkFile.length() == Integer.parseInt((chunkSize))) {
				response.getWriter().write("{\"ifExist\":1}");
			} else {
				response.getWriter().write("{\"ifExist\":0}");
			}
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
