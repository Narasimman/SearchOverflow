package ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import search.Retriever;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final String NOT_FOUND = "Best Answer Not Found";
  private static final String indexPath = "/data/ns3184/index";
  private static final String dbPath = "/data/ns3184/full_so_dump.db";

  /**
   * @see HttpServlet#HttpServlet()
   */
  public SearchServlet() {
    super();
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      String query = request.getParameter("q");
      String res = NOT_FOUND;

      Retriever retriever = new Retriever(dbPath);
      String result = retriever.retrieve(indexPath, query);
      //String result = query;

      if(result != null) {
        res = result;
      }

      response.setContentType("text/html; charset=UTF-8");

      response.setContentLength(res.length());
      response.getOutputStream().write(res.getBytes());
      response.getOutputStream().flush();
      response.getOutputStream().close();      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}
