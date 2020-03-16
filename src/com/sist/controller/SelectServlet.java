package com.sist.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sist.model.BoardDAO;
import com.sist.model.BoardDTO;

@WebServlet("/select.do")
public class SelectServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    public SelectServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BoardDAO dao = BoardDAO.getInstance();
		List<BoardDTO> board = dao.getList();
		
		req.setAttribute("List", board);
		
		//페이지 이동
		RequestDispatcher rd = req.getRequestDispatcher("board_list.jsp");
		rd.forward(req, resp);
		
	}
}
