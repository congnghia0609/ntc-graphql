/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.graphql.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Oct 12, 2015
 */
public class UploadFormUtil extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(UploadFormUtil.class);
    
    public static UploadFormUtil instance = new UploadFormUtil();
    
    private boolean isMultipart = false;
    private String dirUpload;
    private final int MEMORY_THRESHOLD      = 20 * 1024 * 1024; // 20 Mb.
    private final int MAX_FILE_SIZE         = 50 * 1024 * 1024; // 50 Mb.
    private final int MAX_REQUEST_SIZE      = 60 * 1024 * 1024; // 60 Mb.
    private ServletFileUpload upload;
    
    private UploadFormUtil(){
        // Get the file location where it would be stored.
        File wdir = new File("");
        dirUpload = wdir.getAbsolutePath() + File.separator + "public" + File.separator + "uploads";
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory - if more than MEMORY_THRESHOLD which files are stored in disk.
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // Location to save data that is larger than maxMemSize.
        File wdirUpload = new File(dirUpload);
        if(!wdirUpload.exists()){
            wdirUpload.mkdir();
        }
        factory.setRepository(wdirUpload);

        // Create a new file upload handler
        upload = new ServletFileUpload(factory);
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
    }
    
    public static UploadFormUtil getInstance(){
        return instance;
    }
    
    public void getMapFormUpload(HttpServletRequest req, Map<String, FileItem> mapFile, Map<String, String> params){
        try {
            // Check that we have a file upload request
            isMultipart = ServletFileUpload.isMultipartContent(req);
            if(isMultipart ){
                //Upload file.
                // Parse the request to get file items.
                List<FileItem> fileItems = upload.parseRequest(req);

                // Process the uploaded file items
                Iterator i = fileItems.iterator();
                while (i.hasNext()) {
                    FileItem fi = (FileItem)i.next();
                    // Process form file field (input type="file").
                    if (!fi.isFormField()) {
                        // Get the uploaded file parameters
                        String fieldName = fi.getFieldName();
                        mapFile.put(fieldName, fi);
                    } else{
                        // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                        // Get the uploaded file parameters
                        String fieldName = fi.getFieldName();
                        String value = fi.getString("utf-8");
                        params.put(fieldName, value);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
