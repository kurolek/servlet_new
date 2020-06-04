import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class IndexServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("init");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("service start");
        super.service(req, resp);
        System.out.println("service finish");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // super.doGet(req, resp);
        PrintWriter w = resp.getWriter();
        try {
            PrintWriter writer = w;
            writer.println("Hello Servlet!");
            req.getParameterMap().forEach((key, value)->{
                writer.println("Hello Servlet!");
            });
            System.out.println("doGet");
        } finally {
            w.close();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        System.out.println("destroy");
    }
}
