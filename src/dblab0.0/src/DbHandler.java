import java.sql.*;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import java.util.Date;


/* FUNCTIONALITIES
	1) AUTHENTICATION
	2) REGISTRATION
	3) UPDATE PASSWORD
	4) GET OVERALL CLUBS
	5) GET RESPECTIVE EVENTS
	6) GET COMMENTS
	7) WRITE COMMENTS
	8) PARTICIPATE IN GROUPS
	9) GET ONLY MY EVENTS
   10) LIKE AN EVENT
   11) CREATE AN EVENT

*/

public class DbHandler {
	// connection strings
	private static String connString = "jdbc:postgresql://localhost:5432/dblab0.0";
	private static String userName = "postgres";
	private static String passWord = "srinath";
	private static String key = "INSTIVENT!123456"; // 128 bit key
    private static String initVector = "RandomInitVector"; // 16 bytes IV
	
    
    /* Encryptor.encrypt(key, initVector, string) */
    /* Encryptor.decrypt(key, initVector,string) */
    
	/* For User Authentication */
	public static JSONObject authenticate(String id, String password){		
		JSONObject obj = new JSONObject();
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select count(*) from password where id=? and password=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			preparedStmt.setString(2, password);
			ResultSet result =  preparedStmt.executeQuery();
			result.next();
			boolean ans = (result.getInt(1) > 0); 
			/* Get Name of the person */
			String query1 = "select first_name from student where id = ?;";
			preparedStmt = conn.prepareStatement(query1);
			preparedStmt.setString(1, id);
			result =  preparedStmt.executeQuery();
			result.next();
			String name = result.getString(1);
			preparedStmt.close();
			conn.close();
			if(ans==true){
				obj.put("status",true);				
				obj.put("data", Encryptor.encrypt(key, initVector, id));
				obj.put("name",name);
				
			}
			else{						
					obj.put("status",false);
					obj.put("message", "Authentication Failed");					
			}			
		} 
		catch(Exception e){
			System.out.println(e);
		}
		return obj;
	}
	
	/* For Registering a candidate*/
	public static JSONObject register(String id, String first_name, String last_name){
		JSONObject obj = new JSONObject();		
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			/* Check if this person already registered */
			String query = "select count(*) from student where id=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			ResultSet result =  preparedStmt.executeQuery();
			result.next();
			boolean ans = (result.getInt(1) == 0); 
			preparedStmt.close();
			if(ans==true){
				/* Add this person to  'student' */
				String insert = "insert into student values(?,?,?);";
				PreparedStatement preparedStmt1 = conn.prepareStatement(insert);
				preparedStmt1.setString(1, id);
				preparedStmt1.setString(2, first_name);
				preparedStmt1.setString(3, last_name);
				preparedStmt1.executeUpdate();
				preparedStmt1.close();
				conn.close();

				obj.put("status",true);				
				obj.put("message", "Sucessfully Registered");
			}
			else{						
					obj.put("status",false);
					obj.put("message", "Already Registered");	
					conn.close();
			}			
		} 
		catch(Exception e){
			System.out.println(e);
		}
		
		return obj;
	}
	
	/* Set Password given id, password : (not update) */
	public static boolean putPassword(String id,String password){
		boolean rtn = true;
		id = Encryptor.decrypt(key, initVector, id);
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String insert = "insert into password values(?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(insert);
			preparedStmt.setString(1, id);
			preparedStmt.setString(2, password);
			preparedStmt.executeUpdate();
			preparedStmt.close();
			conn.close();	
		} 
		catch(Exception e){
			rtn = false;
			System.out.println(e);
		}
		return rtn;
	}
	
	/* Return club names and id's */
	public static JSONObject getClubs(String s_id){
		JSONObject obj = new JSONObject();
		JSONArray jsonarr = new JSONArray();
		s_id = Encryptor.decrypt(key, initVector, s_id);
		System.out.println("Inside Database of Clubs "+ s_id);
		try{
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select c_id,name,focus,img_url,role from member,club where c_id = id and  s_id = ?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, s_id);
			ResultSet result =  preparedStmt.executeQuery();
			jsonarr = ResultSetConverter(result);
			obj.put("clubs", jsonarr);
			String query1 = "select count(*) from club;";
			preparedStmt = conn.prepareStatement(query1);
			result =  preparedStmt.executeQuery();
			result.next();
			Integer count = result.getInt(1);
			obj.put("count", count);
			preparedStmt.close();
			conn.close();
			 
		} catch(Exception e){
			System.out.println(e);
		}
		
		return obj;
	}
	
	/* Return events of given club in order of time stamp */
	public static JSONObject getEvents(String c_id, String s_id){
		JSONObject obj = new JSONObject();
		JSONArray jsonarr = new JSONArray();
		s_id = Encryptor.decrypt(key, initVector, s_id);
		System.out.println("Inside getEvents "+s_id);
		try{
			Connection conn = DriverManager.getConnection(connString,userName,passWord);
			String query = "with a as (select ev_id,count(*) as likes from likes group by ev_id),"+
					"d as (select ev_id,count(*) as comments from comments group by ev_id),"+
					 "b as (select organises.ev_id,c_id,name,image_url,event_date,num_participants,created_time from organises,event where organises.ev_id = event.id  and organises.c_id = ?),"+
					"c as (select * from b natural left  join a natural left join d),"+
					 "e as (select * from c natural join event_venue natural join venue),"+
					"f as (select ev_id,1 as status from likes where s_id =?)"+
					"SELECT ev_id,name,image_url,room,num_participants,"+
					"event_date::date as event_date,to_char(event_date, 'HH24:MI') as event_time,coalesce(likes,0) as likes,coalesce(comments,0) as comments,coalesce(status,0) as status "+
					"FROM e natural left join f order by created_time;";
					//System.out.println(query);
								PreparedStatement PreparedStmt = conn.prepareStatement(query);
								PreparedStmt.setString(1, c_id);
								PreparedStmt.setString(2, s_id);
								ResultSet rs = PreparedStmt.executeQuery();
								jsonarr = ResultSetConverter(rs);
								obj.put("events", jsonarr);
								PreparedStmt.close();
								conn.close();
			
		} catch(Exception e){
			System.out.println(e);
		}
		
		return obj;
	}

	/* Return comments of given event in order of time stamp */
	public static JSONObject getComments(String ev_id,String s_id){
		JSONObject obj = new JSONObject();
		JSONArray jsonarr = new JSONArray();
		s_id = Encryptor.decrypt(key,initVector,s_id);
		try{
			Connection conn = DriverManager.getConnection(connString,userName,passWord);
		String query = "select comment_id,ev_id,comment,first_name from comments left join student on student.id = comments.s_id where ev_id =?;";
			//System.out.println(query);
			PreparedStatement PreparedStmt = conn.prepareStatement(query);
			PreparedStmt.setString(1, ev_id);
			ResultSet rs = PreparedStmt.executeQuery();
			jsonarr = ResultSetConverter(rs);
			obj.put("Comments", jsonarr);
			PreparedStmt.close();
			conn.close();
			
		} catch(Exception e){
			System.out.println(e);
		}
		
		return obj;
	}

	/* Write comment to event*/
		public static JSONObject writeComment(String ev_id,String s_id,String comment){
			JSONObject obj = new JSONObject();
			s_id = Encryptor.decrypt(key,initVector,s_id);
			try{
				Connection conn = DriverManager.getConnection(connString,userName,passWord);
				String query = "select coalesce(max(comment_id),0) as max from comments;";
				//System.out.println(query);
				PreparedStatement PreparedStmt1 = conn.prepareStatement(query);
				ResultSet rs = PreparedStmt1.executeQuery();
				
				rs.next();
				//System.out.println(rs.getInt(1));
				Integer max = rs.getInt(1);
				PreparedStmt1.close();
				String query1 = "insert into comments values(?,?,?,?,?);";
				PreparedStatement preparedStmt = conn.prepareStatement(query1);
				preparedStmt.setInt(1,max+1);
				preparedStmt.setString(2,s_id);
				preparedStmt.setString(3,ev_id);
				preparedStmt.setString(4,comment);
			    Calendar calendar = Calendar.getInstance();
			    java.sql.Timestamp curr_timestamp = new java.sql.Timestamp(calendar.getTime().getTime());
			    preparedStmt.setTimestamp(5,curr_timestamp);
			    
				preparedStmt.executeUpdate();
				preparedStmt.close();
				obj.put("status",true);				
				obj.put("message", "Sucessfully Commented");
				conn.close();
				
			} catch(Exception e){
				System.out.println(e);
			}
			
			return obj;
		}

	/* Participates for group formation*/
		public static JSONObject participates(String ev_id,String stu_id,String[] s_id,Integer num_participants,String g_name){
			JSONObject obj = new JSONObject();
			stu_id = Encryptor.decrypt(key,initVector,stu_id);
			try{
				Connection conn = DriverManager.getConnection(connString,userName,passWord);
				String query = "select max(to_number(g_id,'99999')) from groups;";
				//System.out.println(query);
				PreparedStatement PreparedStmt1 = conn.prepareStatement(query);
				ResultSet rs = PreparedStmt1.executeQuery();
				rs.next();
				//System.out.println(rs.getInt(1));
				String max = rs.getString(1);
				//System.out.println(max);
				Integer x =1;
				if(max!=null)
				{
					x=Integer.valueOf(max)+1;
				} 
				String  y = String.valueOf(x);
				
				PreparedStmt1.close();
				String query1 = "insert into groups values(?,?,?);";
				PreparedStatement preparedStmt = conn.prepareStatement(query1);
				preparedStmt.setString(1,y);
				preparedStmt.setString(2,ev_id);
				preparedStmt.setString(3,g_name);
				preparedStmt.executeUpdate();
				preparedStmt.close();
				
					/* for inserting student who is creating groups */
					String query20 = "insert into participates values(?,?);";
					PreparedStatement preparedStmt20 = conn.prepareStatement(query20);
					preparedStmt20.setString(1,stu_id);
					preparedStmt20.setString(2,y);
					preparedStmt20.executeUpdate();
					preparedStmt20.close();

				for(int i=0;i<num_participants;i++)
				{
					String query2 = "insert into participates values(?,?);";
					PreparedStatement preparedStmt2 = conn.prepareStatement(query2);
					preparedStmt2.setString(1,s_id[i]);
					preparedStmt2.setString(2,y);
					preparedStmt2.executeUpdate();
					preparedStmt2.close();
				}
				obj.put("status",true);				
				obj.put("message", "Sucessfully Added");
				conn.close();
				
			} catch(Exception e){
				System.out.println(e);
			}
			
			return obj;
		}

	/*To retrive my events*/
		public static JSONObject myEvents(String s_id){
			JSONObject obj = new JSONObject();
			JSONArray jsonarr = new JSONArray();
			s_id = Encryptor.decrypt(key,initVector,s_id);
			System.out.println("Inside myEvents "+s_id);
			try{
				Connection conn = DriverManager.getConnection(connString,userName,passWord);
				String query = "with a as (select * from participates  natural join groups where s_id=?),"+
				"b as (select ev_id,event.name,event_date,g_id,a.name as g_name from a  join event on a.ev_id=event.id),"+
				"c as (select ev_id,name,event_date,g_id,room,g_name from b natural join event_venue natural join venue),"+
				"d as (SELECT g_id,string_agg(first_name, '-') AS s_name FROM   participates join student on participates.s_id=student.id GROUP  BY g_id)"+
				"select ev_id,name,event_date::date as event_date,to_char(event_date, 'HH24:MM') as event_time,room as venue,g_name,s_name as participants from d natural join c ;";
				PreparedStatement PreparedStmt = conn.prepareStatement(query);
				PreparedStmt.setString(1, s_id);
				ResultSet rs = PreparedStmt.executeQuery();
				jsonarr = ResultSetConverter(rs);
				obj.put("my_events", jsonarr);
				PreparedStmt.close();
				conn.close();
				
			} catch(Exception e){
				System.out.println(e);
			}
			
			return obj;
		}

	/* Write like to event*/
		public static JSONObject writeLike(String ev_id,String s_id){
			JSONObject obj = new JSONObject();
			s_id = Encryptor.decrypt(key,initVector,s_id);
			try{
				Connection conn = DriverManager.getConnection(connString,userName,passWord);
				
				String query1 = "insert into likes values(?,?);";
				PreparedStatement preparedStmt = conn.prepareStatement(query1);
				preparedStmt.setString(1,s_id);
				preparedStmt.setString(2,ev_id);
				
				preparedStmt.executeUpdate();
				preparedStmt.close();
				obj.put("status",true);				
				obj.put("message", "Sucessfully Liked");
				conn.close();
				
			} catch(Exception e){
				System.out.println(e);
			}
			
			return obj;
		}
	
	/* creates new event in a club when called by a club admin*/
	public static JSONObject getCreate_Event(String s_id,String c_id,String ev_name,String venue, String time, String img_name ,String num_participants){		
		JSONObject obj = new JSONObject();
		Calendar calendar = Calendar.getInstance();
	    java.sql.Timestamp curr_timestamp = new java.sql.Timestamp(calendar.getTime().getTime());
	    s_id = Encryptor.decrypt(key, initVector, s_id);
	    System.out.println("Inside getCreate_Event "+s_id);
	    String s = time+":00";
	    Timestamp tme = Timestamp.valueOf(s); 
	try{
		Connection conn = DriverManager.getConnection(connString,userName,passWord);
		/* Check if the user has Admin previlages */
		String qry = "select role from member where s_id = ? and c_id = ?;";
		PreparedStatement preparedStt = conn.prepareStatement(qry);
		preparedStt.setString(1,s_id);
		preparedStt.setString(2,c_id);
		ResultSet rslt =  preparedStt.executeQuery();
		rslt.next();
		String  role = rslt.getString(1);
		preparedStt.close();
		
		System.out.println(" His Previlage is "+ role);
		if(!role.equalsIgnoreCase("A")){
			conn.close();
			obj.put("status",false);				
			obj.put("message", "Event Cannot be Created");
			return obj;
										}
		/* Check if the event  can be created or not(already created or not) */
		String query = "select  max(id) from event;";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		ResultSet result =  preparedStmt.executeQuery();
		result.next();
		int e=result.getInt(1)+1;
		preparedStmt.close();
		String e_id = String.valueOf(e);
		/* Get Venue Id of currently given venue */
		String query1 = "select  v_id from venue where room = ?;";
		PreparedStatement preparedStmt0 = conn.prepareStatement(query1);
		preparedStmt0.setString(1, venue);
		ResultSet result0 =  preparedStmt0.executeQuery();
		result0.next();
		String v_id = result0.getString(1);
		preparedStmt0.close();
	// id, ev_name, img_url,v_date, num_participants, creatd_time,
			/* Add this event to  'event' */			
			String insert1 = "insert into event values (?,?,?,?,?,?);";
			PreparedStatement preparedStmt1 = conn.prepareStatement(insert1);
			preparedStmt1.setString(1, e_id);
			preparedStmt1.setString(2, ev_name);
			preparedStmt1.setString(3, img_name);
			preparedStmt1.setTimestamp(4, tme);
			preparedStmt1.setInt(5, Integer.parseInt(num_participants));
			preparedStmt1.setTimestamp(6, curr_timestamp);
			preparedStmt1.executeUpdate();
			preparedStmt1.close();
			/*Add this event to  'organises' */
			String insert2 = "insert into organises values (?,?);";
			PreparedStatement preparedStmt2 = conn.prepareStatement(insert2);
			preparedStmt2.setString(1, e_id);
			preparedStmt2.setString(2, c_id);
			preparedStmt2.executeUpdate();
			preparedStmt2.close();
			
			/*Set Venue to  this event in EventVenue */
			String insert3 = "insert into event_venue values (?,?);";
			PreparedStatement preparedStmt3 = conn.prepareStatement(insert3);
			preparedStmt3.setString(1, e_id);
			preparedStmt3.setString(2, v_id);
			preparedStmt3.executeUpdate();
			preparedStmt3.close();
			
			conn.close();
			
			
			obj.put("status",true);				
			obj.put("message", "Event Sucessfully Created");
					
	} 
	catch(Exception e){
		System.out.println(e);
	}
	
	return obj;
	}
	
	/* Result set converter */
	private static JSONArray ResultSetConverter(ResultSet rs) throws SQLException, JSONException {
		
		// TODO Auto-generated method stub
		JSONArray json = new JSONArray();
	    ResultSetMetaData rsmd = rs.getMetaData();
	    while(rs.next()) {
	        int numColumns = rsmd.getColumnCount();
	        JSONObject obj = new JSONObject();

	        for (int i=1; i<numColumns+1; i++) {
	          String column_name = rsmd.getColumnName(i);

	          if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
	           obj.put(column_name, rs.getArray(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
	           obj.put(column_name, rs.getBoolean(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
	           obj.put(column_name, rs.getBlob(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
	           obj.put(column_name, rs.getDouble(column_name)); 
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
	           obj.put(column_name, rs.getFloat(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
	           obj.put(column_name, rs.getNString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
	           obj.put(column_name, rs.getString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
	           obj.put(column_name, rs.getDate(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
	          obj.put(column_name, rs.getTimestamp(column_name));   
	          }
	          else{
	           obj.put(column_name, rs.getObject(column_name));
	          }
	        }

	        json.put(obj);
	      }
	    return json;
	}
	
}