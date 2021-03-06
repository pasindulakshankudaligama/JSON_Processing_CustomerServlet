import javax.json.*;
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

        try {
            String option = req.getParameter("option");
            //The Media Type of the Content of the response
            resp.setContentType("application/json"); // MIME Types (Multipurpose Internet Mail Extensions)

            //Initialize the connection
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "ijse");
            PrintWriter writer = resp.getWriter();

        switch (option){
            case "SEARCH":



                break;
            case "GETALL":
            ResultSet rst = connection.prepareStatement("select * from Customer").executeQuery();
            String allRecords = "";

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            // Access the records and generate a json object
            while (rst.next()) {


                String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                double salary = rst.getDouble(4);

                JsonObjectBuilder object = Json.createObjectBuilder();

                object.add("id", id);
                object.add("name", name);
                object.add("address", address);
                object.add("salary", salary);


               /* String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                double salary = rst.getDouble(4);*/

                //Convert one record for json
                /*String customer = "{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"address\":\"" + address + "\",\"salary\":" + salary + "},";
                allRecords = allRecords + customer;*/

                arrayBuilder.add(object.build());

            }




            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("status", "200");
            response.add("message", "done");
            response.add("data", arrayBuilder.build());

            writer.print(response.build());
            //Output of allRecords for now
            //{id:C001,name:Dasun,address:Galle,salary:1000},{id:C001,name:Dasun,address:Galle,salary:1000},

            //How it should be formatted
            //[{id:C001,name:Dasun,address:Galle,salary:1000},{id:C001,name:Dasun,address:Galle,salary:1000}]

            //After last customer object, ',' should be removed
            String finalJson = "[" + allRecords.substring(0, allRecords.length() - 1) + "]";

            //Then print it as the response
            // PrintWriter writer = resp.getWriter();
            writer.write(finalJson); //Possible response types -> //text //xml //html //json

            //            {
//                "data": "[{},{},{},{}]",
//                "message":"Done",
//                "status":"200"
//            }


                break;
        }

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
        PrintWriter writer = resp.getWriter();
        System.out.println(customerID + " " + customerName + " " + customerAddress + " " + salary);

        resp.setContentType("application/json"); // MIME Types (Multipurpose Internet Mail Extensions)
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "ijse");

            PreparedStatement pstm = connection.prepareStatement("Insert into Customer values(?,?,?,?)");
            pstm.setObject(1, customerID);
            pstm.setObject(2, customerName);
            pstm.setObject(3, customerAddress);
            pstm.setObject(4, salary);


            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder response = Json.createObjectBuilder();
                resp.setStatus(HttpServletResponse.SC_CREATED);
                response.add("status", 200);
                response.add("message", "Successfully Added");
                response.add("data", "");
                writer.print(response.build());
            }
//            {
//                "data": "",
//                "message":"Succsefully Aded",
//                "status":"200"
//            }

        } catch (ClassNotFoundException e) {
            //resp.sendError(500, e.getMessage());
            JsonObjectBuilder response = Json.createObjectBuilder();
            resp.setStatus(HttpServletResponse.SC_OK);
            response.add("status", 400);
            response.add("message", "Error");
            response.add("data", e.getLocalizedMessage());

            e.printStackTrace();
        } catch (SQLException throwables) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            resp.setStatus(HttpServletResponse.SC_OK);
             response.add("status", 400);
            response.add("message", "Error");
            response.add("data", throwables.getLocalizedMessage());
            throwables.printStackTrace();
            //resp.sendError(500, throwables.getMessage());
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

        PrintWriter writer = resp.getWriter();

        resp.setContentType("application/json");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "sanu");

            PreparedStatement pstm = connection.prepareStatement("Delete from Customer where id=?");
            pstm.setObject(1, customerID);



            if ( pstm.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("data","");
                objectBuilder.add("message", "Successfully Deleted");
                objectBuilder.add("status", 200);
                writer.print(objectBuilder.build());
            }else{
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", 400);
                objectBuilder.add("data","Wrong Id Inserted");
                objectBuilder.add("message", "");

                writer.print(objectBuilder.build());

            }

            //            {
//                "data": "",
//                "message":"Succsefully Deleted",
//                "status":"200"
//            }
        } catch (ClassNotFoundException e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            resp.setStatus(HttpServletResponse.SC_OK);
            response.add("status", 400);
            response.add("message", "Error");
            response.add("data", e.getLocalizedMessage());
            e.printStackTrace();

            writer.print(response.build());

          //  resp.sendError(500, e.getMessage());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JsonObjectBuilder response = Json.createObjectBuilder();
            resp.setStatus(HttpServletResponse.SC_OK);
            response.add("status", 400);
            response.add("message", "Error");
            response.add("data", throwables.getLocalizedMessage());
           // resp.sendError(500, throwables.getMessage());
        }

    }

    //This method can be used to update a customer
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      /*  String customerID = req.getParameter("customerID"); // name value from the input field
        String customerName = req.getParameter("customerName");
        String customerAddress = req.getParameter("customerAddress");
        String customerSalary = req.getParameter("customerSalary");*/

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String customerID = jsonObject.getString("id");
        String customerName = jsonObject.getString("name");
        String customerAddress = jsonObject.getString("address");
        String customerSalary = jsonObject.getString("salary");

        PrintWriter writer = resp.getWriter();

        resp.setContentType("application/json");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "sanu");

            PreparedStatement pstm = connection.prepareStatement("Update Customer set name=?,address=?,salary=? where id=?");
            pstm.setObject(1, customerName);
            pstm.setObject(2, customerAddress);
            pstm.setObject(3, customerSalary);
            pstm.setObject(4, customerID);



            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status",200);
                objectBuilder.add("message","Successfully Updated");
                objectBuilder.add("data","");
                writer.print(objectBuilder.build());
            }else {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status",400);
                objectBuilder.add("message","Update Failed");
                objectBuilder.add("data","");
                writer.print(objectBuilder.build());
            }
            //            {
//                "data": "",
//                "message":"Succsefully Updated",
//                "status":"200"
//            }

        } catch (ClassNotFoundException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status",500);
            objectBuilder.add("message","Update Failed");
            objectBuilder.add("data",e.getLocalizedMessage());
            writer.print(objectBuilder.build());
            e.printStackTrace();
         /*   resp.sendError(500, e.getMessage());*/

        } catch (SQLException throwables) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("status",500);
            objectBuilder.add("message","Update Failed");
            objectBuilder.add("data",throwables.getLocalizedMessage());
            writer.print(objectBuilder.build());
            throwables.printStackTrace();
           // resp.sendError(500, throwables.getMessage());
        }
    }
}
