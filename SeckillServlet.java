package com.example.Seckill;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

@WebServlet("/seckill")
public class SeckillServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userid = new Random().nextInt(5000) + "";
        String prodid = req.getParameter("prodid");

        //boolean isSuccess = SeckillRedis.doSeckill(userid,prodid);

        boolean isSuccess = SeckillRedisByScript.doSecKill(userid,prodid);

        resp.getWriter().print(isSuccess);
    }
}
