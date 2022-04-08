package com.atguigu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.soap.AddressingFeature.Responses;

/**
 * 秒杀案例
 */
public class SecKillServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SecKillServlet() {
        super();
    }

	/**
	 * 获取form表单信息
	 * 关闭该web工程,显示有线程结束不了
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	//生成用户id随机数(bound为上限,不包含bound)
		//真实场景中,用户id应该也是从redis中获取的吧??(redis中保存会话session,session中包含用户信息id)
		String userid = new Random().nextInt(50000) +"" ;
		//获取商品id
		String prodid =request.getParameter("prodid");

		//判断验证结果成功否
		//秒杀过程(没有 乐观锁 和 事务)
		//boolean isSuccess= SecKill_redis.doSecKillNoWatchMulti(userid,prodid);

		//乐观锁 和 事务
		//boolean isSuccess=SecKill_redis.doSecKill(userid,prodid);

		//LUA脚本
		boolean isSuccess= SecKill_redisByScript.doSecKill(userid,prodid);
		//向前端返回,结果
		response.getWriter().print(isSuccess);
	}

}
