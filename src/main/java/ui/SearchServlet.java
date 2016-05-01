package ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final String indexPath = "index";
  private static final String dbPath = "so-dump.db";

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

      // #TODO
      //Retriever retriever = new Retriever(dbPath);
      //String result = retriever.retrieve(indexPath, query);

      String result = query;
      response.setContentType("text/html; charset=UTF-8");
      response.setContentLength(result.length());
      response.getOutputStream().write(result.getBytes());
      response.getOutputStream().flush();
      response.getOutputStream().close();
      //request.setAttribute("res", result);
      //request.getRequestDispatcher("/result.jsp").forward(request, response);
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
