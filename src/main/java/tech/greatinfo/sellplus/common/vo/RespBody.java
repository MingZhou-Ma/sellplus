/**
 * <html>
 * <body>
 *  <P> Copyright China GuangZhou WanGuo tech-info co,ltd</p>
 *  <p> All rights reserved.</p>
 *  <p> Created by wubin@wanguo.com</p>
 *  </body>
 * </html>
 */
package tech.greatinfo.sellplus.common.vo;


import java.io.Serializable;

/**
* @Package：tech.greatinfo.sellplus.common.vo   
* @ClassName：RespBody   
* @Description：   <p> api接口响应 </p>
* @Author： - Jason   
* @CreatTime：2018年8月14日 下午5:17:33   
* @Modify By：   
* @ModifyTime：  2018年8月14日
* @Modify marker：   
* @version    V1.0
 */
public class RespBody implements Serializable{

	private static final long serialVersionUID = -6599447507957097341L;
	
	/**
	 * 状态
	 */
	private Status status;
	
	/**
	 * 状态码
	 */
	private Integer code;
	
	/**
	 * 结果
	 */
	private Object data;
	
	/**
	 * 消息描述
	 */
	private String msg;

	public RespBody() {
		super();
	}

	public RespBody(Status status) {
		super();
		this.status = status;
	}

	public RespBody(Status status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public RespBody(Status status, Object data) {
		super();
		this.status = status;
		this.data = data;
	}

	public RespBody(Status status, Object data, String msg) {
		super();
		this.status = status;
		this.data = data;
		this.msg = msg;
	}
	
	/**  
	* RespBody. 
	* @param status
	* @param code
	* @param msg  
	*/  
	public RespBody(Status status, Integer code, String msg) {
		super();
		this.status = status;
		this.code = code;
		this.msg = msg;
	}


	/**
	 * 结果类型信息
	 */
	public enum Status {
		OK, ERROR, FAIL,EXCEPTION
	}

	/**
	 * 添加成功结果信息
	 */
	public void addOK(String msg) {
		this.msg = msg;
		this.status = Status.OK;
	}

	/**
	 * 添加成功结果信息
	 */
	public void addOK(Object data, String msg) {
		this.code=200;
		this.msg = msg;
		this.status = Status.OK;
		this.data = data;
	}

	/**
	 * 添加错误消息
	 */
	public void addError(String msg) {
		this.msg = msg;
		this.status = Status.ERROR;
	}
	
	/**
	 * @Description: 异常信息
	 * @param msg
	 * @param code void
	 * @Autor: Jason
	 */
	public void addError(String msg,Integer code) {
		this.msg = msg;
		this.code = code; 
		this.status = Status.ERROR;
	}
	
	/**
	 * @Description: 添加异常信息
	 * @param msg 
	 * @Autor: Jason
	 */
	public void addException(String msg) {
		this.msg = msg;
		this.status = Status.EXCEPTION;
	}
	
	/**
	 * @Description: 异常处理
	 * @param msg
	 * @param code 
	 * @Autor: Jason
	 */
	public void addException(String msg,Integer code) {
		this.msg = msg;
		this.code = code;
		this.status = Status.EXCEPTION;
	}
	
	public void addFail(String msg) {
		this.msg = msg;
		this.status = Status.ERROR;
	}
	
	public void addFail(String msg,Integer code) {
		this.msg = msg;
		this.code = code;
		this.status = Status.ERROR;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Object getdata() {
		return data;
	}

	public void setdata(Object data) {
		this.data = data;
	}

	public String getmsg() {
		return msg;
	}

	public void setmsg(String msg) {
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}

	
}