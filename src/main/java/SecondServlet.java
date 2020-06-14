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
        //region Прокручиваем данные которые приняли
        for (Part part : req.getParts()) {
            //что делать если данные - имя пользователя (картинки)
            if (part.getName().equals("user_name")) {
                InputStream inputStream = part.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputStream);
                //зависываем в переменную
                model.userName = new BufferedReader(isr)
                        .lines()
                        .collect(Collectors.joining("\n"));
                //что делать если данные - картинка
            } else if (part.getName().equals("image")) {
                //добавляем случайные символы + имя файла
                model.image = UUID.randomUUID().toString() + part.getSubmittedFileName();
                //записываем в переменную
                part.write(model.image);
            }
        }
        //endregion

        //добавляем объект с данными в список файлов
        Storage.modelList.add(new Model(model.userName, model.image));

        log(Storage.modelList.get(0).userName);



        resp.sendRedirect("/single-servlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      //http://127.0.0.1:8888/single-servlet/second-servlet?action=getFileNames
        //получить список имен файлов ранее созраненных на сервере
        if (req.getParameter("action").equals("getFileNames")) {
            //выводим на экран ссылки на изображения
            Storage.modelList.forEach(model -> {
                try {
                    if(model.image !=null ) {
                        resp.getWriter().println("<p><a href='http://127.0.0.1:8888/single-servlet/second-servlet?action=showPicture&username="+model.userName+"'>"+model.userName+"</a></p>\n");
                        resp.getWriter().println();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //ссылка на индекс
            try {
                resp.getWriter().println("<p><a href=http://127.0.0.1:8888/single-servlet/> Back to index</a></p>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

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

            //вывод на экран хтмл страницы с изобрежнием
        } else if (req.getParameter("action").equals("showPicture")) {
            Optional<Model> modelOptional = Storage.modelList.stream().filter((model ->
                    model.userName.equals(req.getParameter("username")))).findFirst();
            if(modelOptional.isPresent()){
                Model model = modelOptional.get();
                    resp.setContentType("text/html");
                    PrintWriter out = resp.getWriter();

                    out.print("<HTML>");
                    out.print("<HEAD><TITLE>Upload Image</TITLE></HEAD>");
                    out.print("<BODY>");
                    out.print("<h1>"+ model.userName + "</h1>");
                    out.print("<img src = \"http://127.0.0.1:8888/single-servlet/second-servlet?action=getFile&filename=" + model.image + "\" width = \"16.8%\" />");
                    out.print("<p><a href=http://127.0.0.1:8888/single-servlet/second-servlet?action=getFileNames>Back to file list</a></p>");
                    out.print("<p><a href=http://127.0.0.1:8888/single-servlet/>Back to index</a></p>");
                    out.print("</BODY>");
                    out.print("</HTML>");
                    out.close();
            }
        }
    }
}
