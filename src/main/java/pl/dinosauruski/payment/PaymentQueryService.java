package pl.dinosauruski.payment;

import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import pl.dinosauruski.models.Payment;
import pl.dinosauruski.payment.dto.PaymentDTO;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PaymentQueryService {

    private PaymentRepository paymentRepository;


    public List<Payment> getAllPaymentsByTeacherId(Long id) {
        return paymentRepository.findAllByTeacherId(id);
    }

    public List<Payment> getLastTenPaymentsByTeacherId(Long id) {
        List<Payment> allPayments = paymentRepository.findAllByTeacherId(id);
        List<Payment> lastTen = allPayments.stream().limit(10).collect(Collectors.toList());
        return lastTen;
    }

    public PaymentDTO getOneDtoByIdOrThrow(Long id) {
        return paymentRepository.findOneDtoById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Payment getOneByIdOrThrow(Long id) {
        return paymentRepository.findOneById(id).orElseThrow(EntityNotFoundException::new);
    }

    public String parseThymeleafTemplate() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("to", "Baeldung");

        return templateEngine.process("thymeleaf_template", context);
    }

    public void generatePdfFromHtml(String html) throws IOException, DocumentException {
        String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
        OutputStream outputStream = new FileOutputStream(outputFolder);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();

    }


}
