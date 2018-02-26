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

import hapax.Template;
import hapax.TemplateCache;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Feb 23, 2018
 */
public abstract class BaseHandler extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(BaseHandler.class);
    public static final TemplateLoader Loader = TemplateCache.create("./views");
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp){
        try {
            /*
             * forward resquest
             */
            if ("GET".equals(req.getMethod())) {
                this.doGet(req, resp);
            } else if ("POST".equals(req.getMethod())) {
                this.doPost(req, resp);
            }
        } catch (Exception ex) {
            logger.error("BaseHandler: " + ex);
        }
    }
    
    public boolean checkParameter(Map<String, Object> paramValues, List<String> params) {
		if(paramValues == null) {
			return false;
		}

		boolean isValid = true;
		for(String param : params) {
			if(paramValues.get(param) == null) {
				isValid = false;
				break;
			}
		}
		return isValid;
	}
    
    protected void print(Object obj, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "Close");
            response.setStatus(HttpServletResponse.SC_OK);
            out = response.getWriter();
            out.print(obj);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected void printJS(Object obj, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "Close");
            response.setStatus(HttpServletResponse.SC_OK);
            out = response.getWriter();
            out.println("<script type=\"text/javascript\">");
            out.print(obj);
            out.println("</script>");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    protected void printStrJSON(Object json, HttpServletResponse resp) {
		PrintWriter out = null;
		try {
			resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
			out = resp.getWriter();
			out.print(json);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

    protected void printXML(Object obj, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/xml;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "Close");
            response.setStatus(HttpServletResponse.SC_OK);
            out = response.getWriter();
            out.print(obj);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected void printText(String str, HttpServletResponse resp) {
        PrintWriter out = null;
        try {
            resp.setContentType("text/plain; charset=utf-8");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            out = resp.getWriter();
            out.println(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    protected void printFile(HttpServletResponse response, ByteArrayOutputStream bigfile, String fileName, String mimeType) throws IOException {
		ServletOutputStream stream = null;
		BufferedInputStream buf = null;
		try {
			stream = response.getOutputStream();

			// set response headers
			response.setBufferSize(1024 * 1024 * 6); // 6M
			response.setContentType(mimeType + ";charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Length", String.valueOf(bigfile.size()));
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			// the contentlength
			response.setContentLength(bigfile.size());

			InputStream input = new ByteArrayInputStream(bigfile.toByteArray());
			buf = new BufferedInputStream(input);

			// read from the file; write to the ServletOutputStream
			byte[] bb = new byte[1024 * 1024 * 5]; // 5M
			int readByte;
			while ((readByte = buf.read(bb, 0, bb.length)) != -1) {
				stream.write(bb, 0, readByte);
			}
			// System.out.println("Complete send file media. Done!!!...");
		} catch (Exception e) {
			logger.error("printFile: ", e);
		} finally {
			if (stream != null) {
				stream.flush();
				stream.close();
			}
			if (buf != null) {
				buf.close();
			}
			if (bigfile != null) {
				bigfile.close();
			}
		}
	}
    
    protected TemplateDataDictionary getDictionary() {
        TemplateDataDictionary dic = TemplateDictionary.create();
        return dic;
    }
    
    protected String getContentTemplate(String tplName, HttpServletRequest req){
        try {
            TemplateDataDictionary dic = getDictionary();
//            TemplateLoader templateLoader = TemplateCache.create("./views"); //TemplateResourceLoader.create("views/");
//            Template template = templateLoader.getTemplate(tplName);
            
            Template template = Loader.getTemplate(tplName);
//            dic.setVariable("domain", Configuration.APP_DOMAIN);
//            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            if (template != null) {
                return template.renderToString(dic);
            }
        } catch (TemplateException ex) {
            logger.error("getContentTemplate: " + ex);
        }
        return "";
    }

    protected String applyTemplate(TemplateDataDictionary dic, String tplName, HttpServletRequest req){
        try {
//            TemplateLoader templateLoader = TemplateCache.create("./views"); //TemplateResourceLoader.create("views/");
//            Template template = templateLoader.getTemplate(tplName);
            
            Template template = Loader.getTemplate(tplName);
//            dic.setVariable("domain", Configuration.APP_DOMAIN);
//            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            if (template != null) {
                return template.renderToString(dic);
            }
            
        } catch (TemplateException ex) {
            logger.error("applyTemplate: " + ex);
        }
        return "";
    }
    
    protected String applyTemplateLayoutMain(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp){
        try {
//            TemplateLoader templateLoader = TemplateCache.create("./views"); //TemplateResourceLoader.create("views/");
//            Template template = templateLoader.getTemplate("layout.xtm");
            
            Template template = Loader.getTemplate("layout.xtm");
//            dic.setVariable("domain", Configuration.APP_DOMAIN);
//            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            if (template != null) {
                return template.renderToString(dic);
            }
            
        } catch (TemplateException ex) {
            logger.error("applyTemplateLayoutMain: " + ex);
        }
        return "";
    }
    
    public void renderPageNotFound(HttpServletRequest req, HttpServletResponse resp){
        try {
            TemplateDataDictionary dic = getDictionary();

            dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "error404.xtm", req));
            print(applyTemplateLayoutMain(dic, req, resp), resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    public void renderDenyPage(HttpServletRequest req, HttpServletResponse resp){
        try {
            TemplateDataDictionary dic = getDictionary();
            TemplateLoader templateLoader = TemplateCache.create("./views"); //TemplateResourceLoader.create("./views");
            Template template = templateLoader.getTemplate("deny.xtm");
            
//            dic.setVariable("domain", Configuration.APP_DOMAIN);
//            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            String content = "";
            if (template != null) {
                content = template.renderToString(dic);
            }
            print(content, resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    
}
