package com.hoomsun.upload.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hoomsun.upload.api.CheckMergePageUploadServer;
import com.hoomsun.web.util.Contans;

public class CheckMergePageUploadImpl implements Serializable,CheckMergePageUploadServer{

	/**
	 * 验证版本一致性标示
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 服务器存储文件地址
	 */
	private String serverPath = "e:/";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("开始文件上传......");
		long start=System.currentTimeMillis();
		
		String action = request.getParameter(Contans.ACTION);
		if (Contans.CHUNKS_MERGE.equals(action)) {
			System.err.println("==================合并=======================");
			/**
			 * 获得需要合并的目录
			 */
			String fileMd5 = request.getParameter(Contans.FILE_MD5);
			/**
			 * 读取目录所有文件
			 */
			File f = new File(serverPath + File.separator + fileMd5);
			File[] fileArray = f.listFiles(new FileFilter() {
				/**
				 * 排除目录，只要文件
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
			 * 转成集合，便于排序
			 */
			List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
			/**
			 * 从小到大排序
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
			 * 新建保存文件
			 */
			File outputFile = new File(serverPath + File.separator
					+ UUID.randomUUID().toString() + Contans.FILE_SUFFIX);
			/**
			 * 创建文件
			 */
			outputFile.createNewFile();
			/**
			 *  输出流
			 */
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			FileChannel outChannel = fileOutputStream.getChannel();
			/**
			 * 合并
			 */
			FileChannel inChannel;
			for (File file : fileList) {
				inChannel = new FileInputStream(file).getChannel();
				inChannel.transferTo(0, inChannel.size(), outChannel);
				inChannel.close();
				/**
				 * 删除分片
				 */
				file.delete();
			}

			/**
			 * 关闭流
			 */
			fileOutputStream.close();
			outChannel.close();

			/**
			 * 清除文件加
			 */
			File tempFile = new File(serverPath + File.separator + fileMd5);
			if (tempFile.isDirectory() && tempFile.exists()) {
				tempFile.delete();
			}
			
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			long end=System.currentTimeMillis();
			System.out.println("合并文件成功！耗时："+(end-start)+"ms");
			

		} else if (Contans.CHECK_CHUNK.equals(action)) {
			System.err.println("==================断点上传=======================");
			/**
			 * 校验文件是否已经上传并返回结果给前端
			 */
			String fileMd5 = request.getParameter(Contans.FILE_MD5);
			String chunk = request.getParameter(Contans.FILE_CHUNK);
			String chunkSize = request.getParameter(Contans.FILE_CHUNK_SIZE);
			/**
			 * 找到分块文件
			 */
			File checkFile = new File(serverPath + File.separator + fileMd5 + File.separator + chunk);

			/**
			 * 检查文件是否存在，且大小一致
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

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}
	
	
}
