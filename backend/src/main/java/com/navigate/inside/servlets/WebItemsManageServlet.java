package com.navigate.inside.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.language.bm.Lang;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.navigate.inside.database.operations.ConnPool;
import com.navigate.inside.database.operations.NodeResProvider;
import com.navigate.inside.objects.Node;
import com.navigate.inside.utils.Constants;
import com.navigate.inside.utils.FilesUtils;

/**
 * Servlet implementation class WebItemsManageServlet
 */

public class WebItemsManageServlet extends HttpServlet {

	
	private static final long serialVersionUID = 1L;

	private static final String RESOURCE_FAIL_TAG = "{\"result_code\":0}";
	private static final String RESOURCE_SUCCESS_TAG = "{\"result_code\":1}";

	private static final String IS_DELETE = "delete";
	public static final int DB_RETRY_TIMES=5;
	
	
	public void init(ServletConfig config) throws ServletException {

		super.init();

		String tmp = config.getServletContext().getInitParameter("localAppDir");
		if (tmp != null) {
			FilesUtils.appDirName = config.getServletContext().getRealPath(tmp);
			System.out.println(FilesUtils.appDirName);

		}

	}

	
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		// Commons file upload classes are specifically instantiated
        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(5000000);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(5000000);
		ServletOutputStream out = null;
		
		int retry = DB_RETRY_TIMES;
		Connection conn = null;


        String id = null;
		String id2 = null;
        int direction = -1, num = 0, dirMax = -1;
		boolean isDelete = false, isDirct;
		TreeMap<String,byte []> imageparts = new TreeMap<>();		byte [] image= null;
		String respPage = RESOURCE_FAIL_TAG;
		try {
			int ImageSize = 0;
			System.out.println("=======Item Servlet =======");
			// Parse the incoming HTTP request
			// Commons takes over incoming request at this point
			// Get an iterator for all the data that was sent
			List<FileItem> items = upload.parseRequest(req);
			Iterator<FileItem> iter = items.iterator();
            conn = ConnPool.getInstance().getConnection();
			// Set a response content type
			resp.setContentType("text/html");
			// Setup the output stream for the return XML data
			out = resp.getOutputStream();

			// Iterate through the incoming request data
			while (iter.hasNext()) {
				// Get the current item in the iteration
				FileItem item = (FileItem) iter.next();

				// If the current item is an HTML form field
				if (item.isFormField()) {
					// If the current item is file data
					
					// If the current item is file data
					String fieldname = item.getFieldName();
					String fieldvalue = item.getString();

					System.out.println(fieldname + "=" + fieldvalue);


					if (fieldname.equals(Constants.FirstID)) {
						id = fieldvalue;
					}else if (fieldname.equals(Constants.SecondID)) {
						id2 = fieldvalue;
					}else if(fieldname.equals(Constants.DIRECT)){
						isDirct = Boolean.valueOf(fieldvalue);
					}else if(fieldname.equals(Constants.Direction)){
                        direction = Integer.valueOf(fieldvalue);
                    }else if(fieldname.equals(IS_DELETE)){
		
						isDelete = Boolean.valueOf(fieldvalue);
						
					}
					
					
				} else {
					imageparts.put(item.getName(),item.get());
					ImageSize += item.get().length;
				}
			}
			if (ImageSize > 0) {
				Map.Entry<String, byte[]> entry;
				//ByteArrayInputStream bis;
				image = new byte[ImageSize];
				int offset = 0, size,k;
				if (imageparts != null)
					while (!imageparts.isEmpty()) {
						entry = imageparts.pollFirstEntry();
						size = entry.getValue().length;
						k = size + offset;
						System.out.println( offset + " --->  " + k);
						System.arraycopy(entry.getValue(), 0, image, offset, size);

						offset += size;
					}

				System.out.println( offset + " " + image.length);

			}

			while (retry > 0) {

				try {


					NodeResProvider itemResProvider = new NodeResProvider();

					if(isDelete){
						
						
						if(itemResProvider.deleteItem(id, conn)){
							respPage = RESOURCE_SUCCESS_TAG;
						}
						
					}
					else{
						if(itemResProvider.pairNodes(id, id2, direction, image, conn)){
							respPage = RESOURCE_SUCCESS_TAG;
						}
						
					}

					retry = 0;

				} catch (SQLException e) {
					e.printStackTrace();
					retry--;
				} catch (Throwable t) {
					t.printStackTrace();
					retry = 0;
				} finally {
					if (conn != null) {
						ConnPool.getInstance().returnConnection(conn);
					}
				}

			}
			out.println(respPage);
			out.close();

	
		} catch (FileUploadException fue) {
			fue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
