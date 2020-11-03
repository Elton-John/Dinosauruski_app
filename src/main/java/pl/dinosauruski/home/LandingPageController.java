package pl.dinosauruski.home;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.dinosauruski.dayName.DayNameService;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class LandingPageController {
    private final DayNameService dayNameService;

    @GetMapping
    public String homePage(){
        return "home/landingPage";
    }

    @GetMapping("/testday")
    @ResponseBody
    public String showDays(){
        dayNameService.markAsWorkDay(1);
        dayNameService.markAsWorkDay(3);
        System.out.println(dayNameService.showAllDays().toString());
       System.out.println(dayNameService.showAllWorkDays().toString());
   return "poniedziałek";
    }
}
