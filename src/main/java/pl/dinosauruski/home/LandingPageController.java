package pl.dinosauruski.home;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class LandingPageController {

    @GetMapping
    public String homePage() {
        return "home/landingPage";
    }
}
