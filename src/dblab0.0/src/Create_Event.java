import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Servlet implementation class Create_Event
 */
@WebServlet("/Create_Event")
public class Create_Event extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Create_Event() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String c_id = request.getParameter("c_id");
		String ev_name = request.getParameter("name");
		String venue = request.getParameter("venue");
		String time = request.getParameter("time");
		String img_name = request.getParameter("img_name");
		String num_participants = request.getParameter("num_participants");
		String s_id = request.getParameter("s_id");
		System.out.println("Inside post Create_Event "+s_id+" c-id:"+c_id+"ev-name:"+ev_name+"venue:"+venue);
		
		JSONObject obj = DbHandler.getCreate_Event(s_id,c_id,ev_name,venue,time,img_name,num_participants);

		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    PrintWriter out = response.getWriter();
	    out.println(obj);			
		out.close();
	}

}
