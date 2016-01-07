package org.jschropf.edu.pia.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jschropf.edu.pia.dao.UserDao;
import org.jschropf.edu.pia.domain.User; 

public class Friends extends HttpServlet{
	 @EJB
	 private UserDao userDao;

	 public Friends(UserDao userDao){
		 this.userDao = userDao;
	 }
	    /** 
	     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	     * @param request servlet request
	     * @param response servlet response
	     * @throws ServletException if a servlet-specific error occurs
	     * @throws IOException if an I/O error occurs
	     */
	    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        response.setContentType("text/html;charset=UTF-8");
		ServletContext ctx = getServletConfig().getServletContext();
	        HttpSession session = request.getSession(true);
	        Long personId = (Long)session.getAttribute("personId");
		if(request.getParameter("personId") != null) {
			personId = Long.parseLong(request.getParameter("personId"));
		}
	        PrintWriter out = response.getWriter();
	        try {    



		    List<User> friends = null;
		    String orderParam = request.getParameter("order");
		    boolean order = (orderParam == null || !orderParam.equals("DESC"));
		    String orderBy = request.getParameter("orderBy");
		    if(orderBy != null && orderBy.equals("dateOfBirth")) {
			friends = userDao.friendsSortedByDateOfBirth(personId, order);
		    } else {
			friends = userDao.friendsSortedByName(personId, order);
		    }

	            List<User> nonFriends = userDao.nonFriendsFor(personId);
		    //request.setAttribute("friends", friends);
		    //request.setAttribute("nonFriends", nonFriends);
	            // ctx.getRequestDispatcher("/friends.jsp").forward(request, response);
	            request.setAttribute("people", friends);
	            
	            ctx.getRequestDispatcher("/friendColumn.jsp").forward(request, response);
	            /* TODO output your page here
	            out.println("<html>");
	            out.println("<head>");
	            out.println("<title>Servlet FriendServlet</title>");  
	            out.println("</head>");
	            out.println("<body>");
	            out.println("<h1>Servlet FriendServlet at " + request.getContextPath () + "</h1>");
	            out.println("</body>");
	            out.println("</html>");
	             */
	        } finally {            
	            out.close();
	        }
	    }

	    /** 
	     * Handles the HTTP <code>GET</code> method.
	     * @param request servlet request
	     * @param response servlet response
	     * @throws ServletException if a servlet-specific error occurs
	     * @throws IOException if an I/O error occurs
	     */
	    @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        processRequest(request, response);
	    }

	    /** 
	     * Handles the HTTP <code>POST</code> method.
	     * @param request servlet request
	     * @param response servlet response
	     * @throws ServletException if a servlet-specific error occurs
	     * @throws IOException if an I/O error occurs
	     */
	    @Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        processRequest(request, response);
	    }

	    /** 
	     * Returns a short description of the servlet.
	     * @return a String containing servlet description
	     */
	    @Override
	    public String getServletInfo() {
	        return "Short description";
	    }
}
