/*
 * Copyright 2018 nghiatc.
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

package com.ntc.graphql.handlers;

import com.eclipsesource.json.JsonObject;
import com.ntc.graphql.utils.HttpHelper;
import com.ntc.graphql.utils.UploadFormUtil;
import hapax.TemplateDataDictionary;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Feb 23, 2018
 */
public class HomeHandler extends BaseHandler {
    private static Logger logger = LoggerFactory.getLogger(HomeHandler.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateDataDictionary dic = getDictionary();
            
            //renderPageHome(dic, req, resp);
            //dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "home.xtm", req));

            print(applyTemplateLayoutMain(dic, req, resp), resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            JsonObject result = new JsonObject();
            result.set("err", -1);
            result.set("msg", "Execute fail. Please try again.");
            
            String action = "";
            String callback = "";
            if(HttpHelper.isMultipartRequest(req)){ // process request multipart form data.
                Map<String, FileItem> mapFile = new HashMap<String, FileItem>();
                Map<String, String> params = new HashMap<String, String>();
                UploadFormUtil.getInstance().getMapFormUpload(req, mapFile, params);
                if(params != null && !params.isEmpty()){
                    System.out.println("multipart params: " + params);
                    System.out.println("multipart mapFile: " + mapFile);
                    callback = params.containsKey("callback") ? params.get("callback") : "";
                    action = params.containsKey("action") ? params.get("action") : "";
                    if(action != null && !action.isEmpty()) {
                        // process action...
                    }
                }
            } else{ // process request nomal.
                action = req.getParameter("action");
                callback = req.getParameter("callback");
                if(action != null && !action.isEmpty()) {
                    // process action...
                }
            }
            
            //render JsonObject.
            if(action != null && !action.isEmpty()) {
                if (HttpHelper.isAjaxRequest(req)) {
                    if (callback != null && !callback.isEmpty()) {
                        printStrJSON(callback + "(" + result.toString() + ")", resp);
                    } else {
                        printStrJSON(result.toString(), resp);
                    }
                } else {
                    TemplateDataDictionary dic = getDictionary();
                    dic.setVariable("callback", callback);
                    dic.setVariable("data", result.toString());
                    print(applyTemplate(dic, "iframe_callback", req), resp);
                }
            } else{
                printStrJSON(result.toString(), resp);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
