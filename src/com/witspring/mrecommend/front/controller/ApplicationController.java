package com.witspring.mrecommend.front.controller;

import java.util.Map;

import com.witspring.net.rest.RestController;
import com.witspring.net.rest.RestParamErrorType;
import com.witspring.net.rest.exception.RestParamException;
import com.witspring.net.rest.resource.Get;
import com.witspring.net.rest.rule.RestParamRuleChecker;

/**
 * TODO Put here a description of what this class does.
 *
 * @author renhao.cao.
 *         Created 2014-9-25.
 */
public class ApplicationController extends RestController {
	
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @throws Exception
	 */
	public void setUp() throws Exception {
		
	}
	
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @throws Exception
	 */
	public void index() throws Exception {
		responseMessage("app.index");
	}
	
	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @throws Exception
	 */
	@Get
	public void welcome() throws Exception {
		responseMessage("app.welcome");
	}

	/**
	 * 统一检查参数
	 * @param checker 参数检查器
	 * @throws Exception
	 */
	protected void checkParam(RestParamRuleChecker checker) throws Exception {
		Map<String, RestParamErrorType> errorMap = checker.check(this.params);
		if(errorMap != null && !errorMap.isEmpty())
			throw new RestParamException(errorMap);
	}
}
