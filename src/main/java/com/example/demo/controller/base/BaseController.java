package com.example.demo.controller.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class BaseController {

	private static Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	protected HttpServletRequest request;


	public void error(Exception e) {
		logger.error(getClass().getName() + ":", e.getMessage());
		e.printStackTrace();
	}

	public void warn(Exception e) {
		logger.warn(getClass().getName() + ":", e.getMessage());
		e.printStackTrace();
	}

	/**
	 * 返回接口处理结果
	 * 
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @return
	 */
	public String error(int code, String msg) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", code);
			map.put("msg", msg);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return null;
		}
	}

	/**
	 * 接口处理成功
	 * @param msg
	 * @return
	 */
	public String success(String msg) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", 200);
			map.put("msg", msg);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return null;
		}
	}

	public String write(int code, String msg) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", code);
			map.put("msg", msg);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return null;
		}
	}

	/**
	 * 返回接口处理结果
	 * 
	 * 
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @return
	 */
	public String write(int code, String msg, String key, List<?> list) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", code);
			map.put("msg", msg);
			map.put(key, list);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	/**
	 * 返回接口处理结果
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @param value 结果数据
	 * @return
	 */
	public String write(int code, String msg, String key, JSONObject value) {
		try {
			JSONObject json = new JSONObject();
			json.put("status", code);
			json.put("msg", msg);
			json.put(key, value);
			return json.toString();
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}
	
	/**
	 * 返回接口处理结果
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @param value 结果数据
	 * @return
	 */
	public String write(int code, String msg, String key, JSONArray value) {
		try {
			JSONObject json = new JSONObject();
			json.put("status", code);
			json.put("msg", msg);
			json.put(key, value);
			return json.toString();
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	/**
	 * 返回接口处理结果
	 *
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @param total 总数
	 * @param value 结果数据
	 * @return
	 */
	public String write(int code, String msg, int total, String key, JSONArray value) {
		try {
			JSONObject json = new JSONObject();
			json.put("status", code);
			json.put("msg", msg);
			json.put("total", total);
			json.put(key, value);
			return json.toString();
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	/**
	 * 返回接口处理结果
	 * 
	 * 
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @param value 结果数据
	 * @return
	 */
	public String write(int code, String msg, String key, Object value) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", code);
			map.put("msg", msg);
			map.put(key, value);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	/**
	 * 返回接口处理结果
	 * 
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @return
	 */
	public String write(int code, String msg, String key, Page<?> list) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", code);
			map.put("msg", msg);
			// 是否为第一页
			map.put("isFirst", list.isFirst());
			// 是否为最后一页
			map.put("isLast", list.isLast());
			// 总条数
			map.put("total", list.getTotalElements());
			// 总页数
			map.put("totalPages", list.getTotalPages());
			map.put(key, list.getContent());
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	/**
	 * 返回接口处理结果
	 *
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @return
	 */
	public String write(int code, String msg, String key, Page<?> page, JSONArray array) {
		try {
			JSONObject json = new JSONObject();
			json.put("status", code);
			json.put("msg", msg);
			// 是否为第一页
			json.put("isFirst", page.isFirst());
			// 是否为最后一页
			json.put("isLast", page.isLast());
			// 总条数
			json.put("total", page.getTotalElements());
			// 总页数
			json.put("totalPages", page.getTotalPages());
			json.put(key, array);
			return json.toString();
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	/**
	 * 返回接口处理结果
	 * 
	 * 
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @param value 结果数据
	 * @return
	 */
	public String write(int code, String msg, String key, Map<?, ?> value) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", code);
			map.put("msg", msg);
			map.put(key, value);
			System.out.println(mapper.writeValueAsString(map));
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	/**
	* 返回接口处理结果
	*
	* @param code 结果码，成功为200
	* @param msg 结果提示信息
	* @param value 结果数据
	* @return
	*/
	public String write(int code, String msg, String key, String value) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("status", code);
			map.put("msg", msg);
			map.put(key, value);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}


	/**
	 * 返回接口处理结果
	 * 
	 * 
	 * @param code 结果码，成功为200
	 * @param msg 结果提示信息
	 * @return
	 */
	public String write(int code, String msg, boolean isFirst, boolean isLast, long total, int totalPages, String key, Object values) {
		try {
			JSONObject json = new JSONObject();
			json.put("status", code);
			json.put("msg", msg);
			// 是否为第一页
			json.put("isFirst", isFirst);
			// 是否为最后一页
			json.put("isLast", isLast);
			// 总条数
			json.put("total", total);
			// 总页数
			json.put("totalPages", totalPages);
			json.put(key, values);
			return json.toString();
		} catch (Exception e) {
			logger.error("BaseController:", e.getMessage());
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

	public String trimEnd(String param, String trimChars) {
		if (param.endsWith(trimChars)) {
			param = param.substring(0, param.length() - trimChars.length());
		}
		return param;
	}

	/**
	 * 无效用户消息返回
	 * @param e
	 * @param defaultCode
	 * @param defaultMsg
	 * @return
	 */
	public String invalidUserException(Exception e, int defaultCode, String defaultMsg) {
		try {
			return error(defaultCode, defaultMsg);
		} catch (Exception e2) {
			return null;
		}
	}

	/**
	 * 返回表格列表数据
	 * @param code 状态码：0成功，非0失败
	 * @param errorMsg 错误消息
	 * @param page 当前页码
	 * @param rows 分页大小
	 * @param list 查询的结果集
	 * @return
	 */
	public String write(int code, String errorMsg, int page, int rows, Page<?> list) {
		try {
			JSONObject object = new JSONObject();
			ObjectMapper mapper = new ObjectMapper();
			object.put("successFlg", code == 0);
			object.put("errorMsg", errorMsg);
			// 是否为第一页
			object.put("errorCode", code);
			// 是否为最后一页
			object.put("currPage", page);
			// 分页大小
			object.put("pageSize", rows);
			// 总条数
			object.put("totalCount", list.getTotalElements());
			// 总页数
			object.put("totalPage", list.getTotalPages());
			// 结果集
			object.put("detailModelList", list.getContent());
			 return object.toString();
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}
	
	public String write(int code, String errorMsg, int page, int rows, List<?> list) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("successFlg", code == 0);
			map.put("errorMsg", errorMsg);
			// 是否为第一页
			map.put("errorCode", code);
			// 是否为最后一页
			map.put("currPage", page);
			// 分页大小
			map.put("pageSize", rows);
			// 总条数
			map.put("totalCount", list.size());
			// 总页数
			map.put("totalPage", 1);
			// 结果集
			map.put("detailModelList", list);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}
	
	public String error(int code, String errorMsg, int page, int rows) {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			ObjectMapper mapper = new ObjectMapper();
			map.put("successFlg", code == 0);
			map.put("errorMsg", errorMsg);
			// 是否为第一页
			map.put("errorCode", code);
			// 是否为最后一页
			map.put("currPage", page);
			// 分页大小
			map.put("pageSize", rows);
			// 总条数
			map.put("totalCount", 0);
			// 总页数
			map.put("totalPage", 0);
			// 结果集
			map.put("detailModelList", null);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			error(e);
			return error(-1, "服务器异常，请稍候再试！");
		}
	}

    //json串转集合
    public <T> Collection<T> jsonToEntities(String jsonDate, Collection<T> targets, Class<T> targetCls) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List modelList = objectMapper.readValue(jsonDate,List.class);
            Iterator ex = modelList.iterator();
            while(ex.hasNext()) {
                Object aModelList = ex.next();
                String objJsonData = objectMapper.writeValueAsString(aModelList);
                T model = objectMapper.readValue(objJsonData, targetCls);
                targets.add(model);
            }
            return targets;
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }


    public String write(int code, String errorMsg, int page, int rows, long total, List<?> list) {
        try {
            Map<Object, Object> map = new HashMap<Object, Object>();
            ObjectMapper mapper = new ObjectMapper();
            map.put("successFlg", code == 0);
            map.put("errorMsg", errorMsg);
            // 是否为第一页
            map.put("errorCode", code);
            // 是否为最后一页
            map.put("currPage", page);
            // 分页大小
            map.put("pageSize", rows);
            // 总条数
            map.put("totalCount", total);
            // 总页数
            map.put("totalPage", Math.ceil((double)total/(rows)));
            // 结果集
            map.put("detailModelList", list);
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            error(e);
            return error(-1, "服务器异常，请稍候再试！");
        }
    }


}
