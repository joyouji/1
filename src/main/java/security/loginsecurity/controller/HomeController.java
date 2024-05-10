package security.loginsecurity.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import security.loginsecurity.home.service.CalendarService;

import java.time.LocalDate;

@Controller
public class HomeController {
    @Autowired
    private CalendarService calendarService;

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("/home");
        return modelAndView;
    }

    @GetMapping("/memo")
    public String showMemo(@RequestParam(value = "date", required = false) String date, Model model) {
        if (date == null) {
            date = LocalDate.now().toString();  // 파라미터가 없는 경우 오늘 날짜를 문자열로 변환하여 사용
        }
        model.addAttribute("date", date);
        return "memo";  // Thymeleaf 템플릿 이름 반환
    }



    @GetMapping("/aiDiary")
    public String redirectToAiDiary() {
        return "redirect:/aiDiary.html";
    }

    @GetMapping("/pTest")
    public String redirectToPTest() {
        return "redirect:/pTest.html";
    }
}
