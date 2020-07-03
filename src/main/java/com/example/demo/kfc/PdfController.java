package com.example.demo.kfc;

import com.example.demo.Utils;
import com.example.demo.repository.*;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.example.demo.Utils.KFC_AGE_THRESHOLD;
import static com.example.demo.Utils.SR_KFC_AGE_THRESHOLD;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

@Controller
@RequestMapping(PdfController.CONTROLLER_URL)
public class PdfController {
    private static final String CONTROLLER_PATH = "kfc/report";
    public static final String CONTROLLER_URL = "/" + CONTROLLER_PATH;

    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    ChildRepository childRepository;
    @Autowired
    ParentRepository parentRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    EventChildRepository eventChildRepository;

    @RequestMapping(value = {"", "/", "/{eventName}"}, method = RequestMethod.GET)
    public String kfcReportSummary(Model model, @PathVariable Optional<String> eventName) {
        List<KfcReport> kfcReportList = new LinkedList();
        String selectedKfcEvent = "";
        for (Child child : getKfcKids()) {
            if (eventName.isPresent()) {
                selectedKfcEvent = eventName.get();
                Event event = eventRepository.findByName(selectedKfcEvent);
                EventChild eventChild = eventChildRepository.findByPtrChildAndPtrEvent(child.getId(), event.getId());
                if (eventChild != null) {
                    addChildToKfcList(kfcReportList, child);
                }
            } else {
                addChildToKfcList(kfcReportList, child);
            }
        }

        model.addAttribute("kfcEvents", eventRepository.findKfcEvents());
        model.addAttribute("selectedKfcEvent", selectedKfcEvent);
        model.addAttribute("reportList", kfcReportList);

        return CONTROLLER_PATH + "/pdfreport";
    }

    private void addChildToKfcList(List<KfcReport> kfcReportList, Child child) {
        KfcReport kfcReport = new KfcReport();
        kfcReport.setChildId(child.getId());
        kfcReport.setChildFullname(child.getFull_name());
        kfcReport.setChildPreferredName(child.getPreferred_name());
        kfcReport.setChildBirthdate(child.getBirthDateHumanReadable());

        kfcReport.setChildAge(Utils.getAge(child));

        Optional<Parent> optionalParent = parentRepository.findById(child.getPtrParent());
        String parentsName = "";
        String parentsEmail = "";
        if (optionalParent.isPresent()) {
            parentsName = optionalParent.get().getFullName();
            parentsEmail = optionalParent.get().getEmail();
        }
        kfcReport.setParentsName(parentsName);
        kfcReport.setParentsEmail(parentsEmail);

        kfcReportList.add(kfcReport);
    }


    private List<Child> getKfcKids() {
        return StreamSupport.stream(childRepository.findAll().spliterator(), false)
                .filter(child -> Utils.getAge(child) <= KFC_AGE_THRESHOLD)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/pdfreport/{childId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> kfcReport(@PathVariable int childId) {

        GeneratePdfReport generatePdfReport = new GeneratePdfReport();
        ByteArrayInputStream bis = generatePdfReport.generateKfcReport(resourceLoader, childId, childRepository, parentRepository);

        HttpHeaders headers = new HttpHeaders();
        String pattern = "yyyyMMddHHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String now = simpleDateFormat.format(new Date());
        headers.add("Content-Disposition", "inline; filename=kfcReport" + now + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
