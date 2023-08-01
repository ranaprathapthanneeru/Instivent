import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Servlet implementation class Participates
 */
@WebServlet("/Participates")
public class Participates extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Participates() {
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
		String ev_id = request.getParameter("ev_id");
		String s_id = request.getParameter("s_id");
		String participants = request.getParameter("students_list");
		String k = request.getParameter("num_participants");
		String g_name = request.getParameter("g_name");
		Integer num_participants = Integer.parseInt(k);
		String[] s = participants.split("-");
		System.out.println("Requested to Participate "+s_id+" eid:"+ev_id+" gn:"+g_name+" np:"+num_participants);

		JSONObject obj = DbHandler.participates(ev_id,s_id,s,num_participants,g_name);

		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    PrintWriter out = response.getWriter();
	    out.println(obj);			
		out.close();
	}

}
