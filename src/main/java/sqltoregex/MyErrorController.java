package sqltoregex;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping(path="/error", method = {RequestMethod.GET})
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String imgSRC = "https://http.cat/"+status.toString()+".jpg";
        model.addAttribute("imgsrc", imgSRC);
        model.addAttribute("title", "sql2regex - ERROR");
        return "error";
    }
}