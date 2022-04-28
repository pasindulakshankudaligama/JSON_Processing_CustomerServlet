import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @author : Sanu Vithanage
 * @since : 0.1.0
 **/
@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    // Json Formats
    // String json="{id:C001,name:Dasun,address:Galle,salary:1000}"; //single customer info
    // String jsonSet="[{id:C001,name:Dasun,address:Galle,salary:1000},{id:C001,name:Dasun,address:Galle,salary:1000}]"; //multiple customer info
    //This method can be used to get customer information.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     /*   String customerID = req.getParameter("customerID"); // name value from the input field
        String customerName = req.getParameter("customerName");
        String customerAddress = req.getParameter("customerAddress");
        String salary = req.getParameter("customerSalary");
        System.out.println(customerID+" "+customerName+" "+customerAddress+" "+salary+" From Get");*/

        try {
            //The Media Type of the Content of the response
            resp.setContentType("application/json"); // MIME Types (Multipurpose Internet Mail Extensions)

            //meta data for response from headers
            resp.addHeader("Institute", "IJSE");
            resp.addHeader("Course", "GDSE");


            //Initialize the connection
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "ijse");
            ResultSet rst = connection.prepareStatement("select * from Customer").executeQuery();
            String allRecords = "";
            // Access the records and generate a json object
            while (rst.next()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

                JsonArrayBuilder object = Json.createArrayBuilder();

                object.add(Integer.parseInt("id"),rst.getString(1));
                object.add(Integer.parseInt("name"),rst.getString(2));
                object.add(Integer.parseInt("address"),rst.getString(3));
                object.add(Integer.parseInt("salary"),rst.getString(4));


               /* String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                double salary = rst.getDouble(4);*/

                //Convert one record for json
                /*String customer = "{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"address\":\"" + address + "\",\"salary\":" + salary + "},";
                allRecords = allRecords + customer;*/

                arrayBuilder.add(object.build());
                PrintWriter writer = resp.getWriter();
                writer.print(arrayBuilder.build());
            }
            //Output of allRecords for now
            //{id:C001,name:Dasun,address:Galle,salary:1000},{id:C001,name:Dasun,address:Galle,salary:1000},

            //How it should be formatted
            //[{id:C001,name:Dasun,address:Galle,salary:1000},{id:C001,name:Dasun,address:Galle,salary:1000}]

            //After last customer object, ',' should be removed
            String finalJson = "[" + allRecords.substring(0, allRecords.length() - 1) + "]";

            //Then print it as the response
            PrintWriter writer = resp.getWriter();
            writer.write(finalJson); //Possible response types -> //text //xml //html //json

            //            {
//                "data": "[{},{},{},{}]",
//                "message":"Done",
//                "status":"200"
//            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    //This method can be used to save a customer
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String customerID = req.getParameter("customerID"); // name value from the input field
        String customerName = req.getParameter("customerName");
        String customerAddress = req.getParameter("customerAddress");
        String salary = req.getParameter("customerSalary");
        System.out.println(customerID + " " + customerName + " " + customerAddress + " " + salary);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "ijse");

            PreparedStatement pstm = connection.prepareStatement("Insert into Customer values(?,?,?,?)");
            pstm.setObject(1, customerID);
            pstm.setObject(2, customerName);
            pstm.setObject(3, customerAddress);
            pstm.setObject(4, salary);
            boolean b = pstm.executeUpdate() > 0;
            PrintWriter writer = resp.getWriter();

            if (b) {
                writer.write("Customer Added");
            }
//            {
//                "data": "",
//                "message":"Succsefully Aded",
//                "status":"200"
//            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.sendError(500, e.getMessage());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(500, throwables.getMessage());
        }
    }

    //This method can be used to delete a customer.
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Request Received for delete");
        //if we send data from the application/x-www-form-urlencoded type, doDelete will not
        //catch values from req.getParameter(); that type is not supported with doDelete
        //but we can send data via Query String
        String customerID = req.getParameter("CusID");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "sanu");

            PreparedStatement pstm = connection.prepareStatement("Delete from Customer where id=?");
            pstm.setObject(1, customerID);

            boolean b = pstm.executeUpdate() > 0;
            PrintWriter writer = resp.getWriter();

            if (b) {
                writer.write("Customer Deleted");
            }

            //            {
//                "data": "",
//                "message":"Succsefully Deleted",
//                "status":"200"
//            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.sendError(500, e.getMessage());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(500, throwables.getMessage());
        }

    }

    //This method can be used to update a customer
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String customerID = req.getParameter("customerID"); // name value from the input field
        String customerName = req.getParameter("customerName");
        String customerAddress = req.getParameter("customerAddress");
        String customerSalary = req.getParameter("customerSalary");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "sanu");

            PreparedStatement pstm = connection.prepareStatement("Update Customer set name=?,address=?,salary=? where id=?");
            pstm.setObject(1, customerName);
            pstm.setObject(2, customerAddress);
            pstm.setObject(3, customerSalary);
            pstm.setObject(4, customerID);
            boolean b = pstm.executeUpdate() > 0;
            PrintWriter writer = resp.getWriter();

            if (b) {
                writer.write("Customer Updated");
            }
            //            {
//                "data": "",
//                "message":"Succsefully Updated",
//                "status":"200"
//            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.sendError(500, e.getMessage());
        } catch (SQLException throwables) {
            throwables.printStackTrace(); 
            resp.sendError(500, throwables.getMessage());
        }
    }
}
