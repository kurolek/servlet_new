import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"second-servlet"})
@MultipartConfig(location = "C:\\Users\\Poi\\IdeaProjects\\java-servlet-maven")
public class SecondServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Model model = new Model();
        for (Part part : req.getParts()) {


            if (part.getName().equals("user_name")) {
                InputStream inputStream = part.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputStream);

                model.userName = new BufferedReader(isr)
                        .lines()
                        .collect(Collectors.joining("\n"));

                log("Место 1");
                log(model.userName);

            } else if (part.getName().equals("image")) {


                model.image = UUID.randomUUID().toString() + part.getSubmittedFileName();
                part.write(model.image);
            }

//            InputStream inputStream = part.getInputStream();
//            InputStreamReader isr = new InputStreamReader(inputStream);
//            String userName = new BufferedReader(isr)
//                    .lines()
//                    .collect(Collectors.joining("\n"));
//            model.userName = userName;
            log("Место 2");
            log(model.userName);
            log("Место 3");



        }
        Storage.modelList.add(new Model(model.userName, model.image));
        log("Место 4");
        log(Storage.modelList.get(0).userName);
        log(Storage.modelList.get(0).image);

        resp.sendRedirect("/single-servlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      //http://127.0.0.1:8888/single-servlet/second-servlet?action=getFileNames
        //получить список имен файлов ранее созраненных на сервере
        if (req.getParameter("action").equals("getFileNames")) {
            Storage.modelList.forEach(model -> {
                try {
//                    resp.getWriter().println(model.image);
                    if(model.image !=null ) {
                        log("getFileNames:");
                        log("model name" + model.userName);
                        log("model img" + model.image);
                        resp.getWriter().println("<a href='http://127.0.0.1:8888/single-servlet/second-servlet?action=showPicture&username="+model.userName+"'>"+model.userName+"</a>\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            // http://127.0.0.1:8888/single-servlet/second-servlet?action=getFile&filename=file_name
            //получить файл по иего имени, переданному от клиента
        } else if (req.getParameter("action").equals("getFile")) {
            resp.setContentType("image/jpeg");
            ServletOutputStream out = resp.getOutputStream();
            FileInputStream fin = new FileInputStream("C:\\Users\\Poi\\IdeaProjects\\java-servlet-maven\\" + req.getParameter("filename"));
            BufferedInputStream bin = new BufferedInputStream(fin);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            int ch =0;
            while((ch=bin.read())!=-1)
            {
                bout.write(ch);
            }

            bin.close();
            fin.close();
            bout.close();
            out.close();
        } else if (req.getParameter("action").equals("showPicture")) {
            log("Im in showPicture");
            Optional<Model> modelOptional = Storage.modelList.stream().filter((model ->
                    model.userName.equals(req.getParameter("username")))).findFirst();
            if(modelOptional.isPresent()){
                Model model = modelOptional.get();
                    log("im inside isPresent");
                    resp.setContentType("text/html");
                    PrintWriter out = resp.getWriter();

                    out.print("<HTML>");
                    out.print("<HEAD><TITLE>Upload Image</TITLE></HEAD>");
                    out.print("<BODY>");

                    out.print("<h1>"+ model.userName + "</h1>");
                    out.print("<img src = \"http://127.0.0.1:8888/single-servlet/second-servlet?action=getFile&filename=" + model.image + "\"/>");

                    out.print("</BODY>");
                    out.print("</HTML>");
                    out.close();

            }
            // http://127.0.0.1:8888/single-servlet/second-servlet?action=showPicture&username=user_name
            //user_name - динамичекки подставленное имя файла
            //по имени пользователя из списка моделей одну модель
            //writer.println
            //веб-страница для клиента
            //h2-пользователь
            //img-пикча
//            RequestDispatcher rd = req.getRequestDispatcher("static/picturePage.html");
//            rd.forward(req, resp);

        }
    }
}
