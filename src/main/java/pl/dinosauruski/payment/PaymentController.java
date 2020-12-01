package pl.dinosauruski.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.payment.dto.PaymentDTO;
import pl.dinosauruski.student.StudentQueryService;
import pl.dinosauruski.teacher.dto.TeacherDTO;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/teacher/payments")
public class PaymentController {
    private PaymentQueryService paymentQueryService;
    private PaymentCommandService paymentCommandService;
    private StudentQueryService studentQueryService;

    @GetMapping
    String index(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO, Model model) {
        List<Payment> payments = paymentQueryService.getLastTenPaymentsByTeacherId(teacherDTO.getId());
        model.addAttribute("teacher", teacherDTO);
        model.addAttribute("payments", payments);
        return "payment/index";
    }

    @GetMapping("/new")
    String newPaymentForm(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO, Model model) {
        model.addAttribute("students", studentQueryService.getAllByTeacherId(teacherDTO.getId()));
        model.addAttribute("payment", new PaymentDTO());
        return "payment/new";
    }

    @PostMapping("/new")
    String newPayment(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                      Model model,
                      @Valid @ModelAttribute("payment") PaymentDTO paymentDTO,
                      BindingResult result
                      ) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentQueryService.getAllByTeacherId(teacherDTO.getId()));
            return "payment/new";
        }
        paymentCommandService.create(paymentDTO, teacherDTO.getId());
        return "redirect:/teacher/payments";
    }

//    @GetMapping("/edit/{id}")
//    String editPaymentForm(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
//                           @PathVariable Long id,
//
//                           Model model) {
//        PaymentDTO paymentDTO = paymentQueryService.getOneDtoByIdOrThrow(id);
//        model.addAttribute("students", studentQueryService.getAllByTeacherId(teacherDTO.getId()));
//        model.addAttribute("payment", paymentDTO);
//        return "payment/edit";
//    }
//
//    @PatchMapping("/edit")
//    String editPayment(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
//                       @Valid @ModelAttribute("payment") PaymentDTO paymentDTO,
//                       BindingResult result) {
//        if (result.hasErrors()) {
//            return "payment/edit";
//        }
//        paymentCommandService.updateByDto(paymentDTO);
//        return "redirect:/teacher/payments";
//    }

    @GetMapping("/submit/{id}")
    String submitDeletingPaymentForm(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                                     @PathVariable Long id,
                                     Model model) {
        PaymentDTO paymentDTO = paymentQueryService.getOneDtoByIdOrThrow(id);
        model.addAttribute("payment", paymentDTO);
        return "payment/submit";
    }

    @DeleteMapping("/delete/{id}")
    String deletePaymentForm(@SessionAttribute("loggedTeacher") TeacherDTO teacherDTO,
                             @PathVariable Long id) {
        paymentCommandService.delete(id);
        return "redirect:/teacher/payments";
    }

//    @GetMapping("/pdf")
//    @ResponseBody
//    String generatePdf() throws IOException, DocumentException {
//      //  paymentQueryService.generatePdfFromHtml("thymeleaf_template");
//        paymentQueryService.generatePdfFromHtml(paymentQueryService.parseThymeleafTemplate());
//        return "thymeleaf_template";
//    }

}
